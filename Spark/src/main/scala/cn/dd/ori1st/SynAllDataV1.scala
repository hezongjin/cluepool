package cn.dd.ori1st

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}
import org.elasticsearch.hadoop.cfg.ConfigurationOptions.ES_MAPPING_ID
import org.elasticsearch.spark.rdd.EsSpark
import utils.{Comm, DataFormat, Utils}

import scala.collection.Map
import scala.collection.mutable.ListBuffer

/**
  * FileName: SynIncrement.java
  * CreateTime: 2020/5/20 16:28.
  * Version: V1.0
  * Author: ChengTao
  * Description: 全量数据更新集成程序(源数据转换（仅资金）+标签标定+ES数据同步)
  */
object SynAllDataV1 {
  //系统配置文件（存放于项目resources目录下）
  private val CONFIG_FILE: String = "syn_all_data_v1.conf"
  //设置Phoenix驱动
  private val CFT = "org.apache.phoenix.spark"

  /**
    * 服务启动函数
    **/
  def main(args: Array[String]): Unit = {
    //获取处理类型，TAG为计算标签，LOAD为同步数据
    val optType: String = args(0)

    //全量索引后缀
    val esIncEndness: String = "2020-11-13"
    //读取并加载项目配置文件
    val confData: Properties = DataFormat.readConf(CONFIG_FILE)
    println(confData.getProperty("es.nodes"))
    //加载增量处理任务
    val taskList: Array[AnyRef] = confData.keySet().toArray().filter(k => k.toString.startsWith("INC_TASK."))
      .sortWith((q, h) => q.toString.substring(9, 11) < h.toString.substring(9, 11))
    //设置SparkSQL配置（测试时，启用本地模式）
    val conf = new SparkConf().setAppName(SynAllDataV1.getClass.getName)
      //.setMaster("local[2]")//本地调试使用，集群模式可删除
      .set("es.nodes", confData.getProperty("es.nodes", ""))
      .set("es.port", confData.getProperty("es.port", ""))
      .set("es.index.auto.create", confData.getProperty("es.index.auto.create", "false"))
      .set("es.nodes.wan.only", confData.getProperty("es.nodes.wan.only", "false"))
      //.set("dfs.client.socket-timeout", "300000")
      .set("dfs.client.socket-timeout", "600000")
      .set("hbase.rpc.timeout", "6000000")
      .set("hbase.client.scanner.caching", "100000")
    //.set("spark.sql.warehouse.dir", "file:///F://PRO_BIGDATA//SparkLearning")//本地调试使用，集群模式可删除
    val zkUrl = confData.getProperty("zkUrl", "")
    //创建SparkSession
    //val ss = SparkSession.builder().config(conf).getOrCreate()//本地调试（不支持Hive）
    val ss = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate() //线上模式（支持Hive）
    //顺序执行任务（用于任务跟踪）
    println("[TASK Opt Type] [%s]".format(optType))

    taskList.map(x => {
      val taskName: String = x.toString
      val taskDetail: String = confData.getProperty(x.toString)
      val taskInfo: (String, String, String, String, String, String) = DataFormat.getTaskInfo(taskName, taskDetail)
      dataOptionDeal(taskInfo, ss, zkUrl, esIncEndness, optType)
    })

    ss.stop()

  }

  /**
    * 数据处理函数
    **/
  def dataOptionDeal(taskInfo: Tuple6[String, String, String, String, String, String], ss: SparkSession, zkUrl: String, esIncEndness: String, optType: String): Unit = {
    println("[TASK ID] [%s]".format(taskInfo._1))
    println("[TASK NAME] [%s] ".format(taskInfo._2))
    println("[TASK DB] [%s] ".format(taskInfo._3))
    println("[TASK TYPE] [%s] ".format(taskInfo._4))
    println("[TASK OPT TYPE] [%s] ".format(taskInfo._5))
    println("[TASK SQL] [%s] ".format(taskInfo._6))
    println("[TASK OPT] [START] %s".format(List.fill(200)("-").mkString))
    if (optType.endsWith("TAG")) {
      println("[OPT TYPE] [%s] ".format("标签计算"))
      if (taskInfo._5.equals("EXTO1")) {
        //源数据转换处理（暂限于 HBASE -> HBASE）
        if (taskInfo._2.equals("T_EDS_ENT_BASE_INFO")) {
          //企业注册资金单位换算清洗(Finished)
          etl4Capital(taskInfo._2, taskInfo._6, ss, zkUrl)
        }
      } else if (taskInfo._5.equals("SGTO") && taskInfo._1.toInt>24) {
        //打标签处理（暂限于 HBASE -> HBASE）
        tagOptionDeal(taskInfo._2, taskInfo._6, ss, zkUrl)
      }
    } else if (optType.endsWith("LOAD")) {
      println("[OPT TYPE] [%s] ".format("数据同步"))
      if (taskInfo._5.equals("ESTO")) {
        //索引数据同步处理（暂限于 HBASE -> ES）
        dataSyn2ES(taskInfo._2, taskInfo._6, ss, zkUrl, esIncEndness)
      } else if (taskInfo._5.equals("ESUP")) {
        //索引数据标签处理（更新标签 HBASE -> ES）
        dataSynUPES(taskInfo._2, taskInfo._6, ss, zkUrl, esIncEndness)
      }
    }

    println("[TASK OPT] [FINISH] %s".format(List.fill(200)("-").mkString))
  }


  /**
    * 企业注册资金单位换算清洗(CREATE+UPDATE)
    **/
  def etl4Capital(optTable: String, optSQL: String, ss: SparkSession, zkUrl: String): Unit = {
    val optSourceTable: String = "eds." + optTable
    val optOrderTable: String = "eds_bak.t_eds_ent_base_info_capital"
    ss.read.format(CFT).options(Map("table" -> optSourceTable, "zkUrl" -> zkUrl)).load().createOrReplaceTempView(optTable)
    val dfOri = ss.sql(optSQL)
    val dfMid = dfOri.rdd.flatMap(DataFormat.parseFunction(_)).filter(_ != null)
    val fieldList = DataFormat.createEtl4CapitalSchema()
    val schema = StructType(fieldList.map(f => StructField(f, StringType, true)))
    val dfRow: RDD[Row] = dfMid.map(line => DataFormat.toRow(line))
    val dfEnd = ss.createDataFrame(dfRow, schema)
    dfEnd.write.format(CFT).options(Map("table" -> optOrderTable, "zkUrl" -> zkUrl)).mode(SaveMode.Overwrite).save()
    println("[SAVED COUNT] [" + dfEnd.count() + "]")
    dfOri.unpersist()
  }

  /**
    * 企业打标(CREATE+UPDATE)
    **/
  def tagOptionDeal(optTable: String, optSQL: String, ss: SparkSession, zkUrl: String): Unit = {
    println("[ORI SQL]" + optSQL)
    val optParam: (Set[String], String, String) = Utils.specialHandleSql(optSQL)
    var dfOri: DataFrame = null
    var tempTable: String = null
    for (tab <- optParam._1) {
      tempTable = tab.split("\\.")(1)
      ss.read.format(Comm.CFT).options(Map("table" -> tab, "zkUrl" -> zkUrl)).load().createOrReplaceTempView(tempTable)
    }
    println("[OPT SQL]" + optParam._2)
    dfOri = ss.sql(optParam._2)
    val optCount = dfOri.count()
    println("[ORDER TABLE]" + optParam._3)
    if (optCount > 0) {
      dfOri.write.format(CFT).options(Map("table" -> optParam._3, "zkUrl" -> zkUrl)).mode(SaveMode.Overwrite).save()
    }
    println("[SAVED COUNT] [" + optCount + "]")
    dfOri.unpersist()
  }


  /**
    * ES数据同步入库
    **/
  def dataSyn2ES(optTable: String, optSQL: String, ss: SparkSession, zkUrl: String, esIncEndness: String): Unit = {
    val trueIndex = optTable + "-" + esIncEndness
    val optParam: (Set[String], String, String) = Utils.specialHandleSql(optSQL)
    var tempTable: String = null
    for (tab <- optParam._1) {
      tempTable = tab.split("\\.")(1)
      println("[" + tab + "][" + tempTable + "]")
      ss.read.format(CFT).options(Map("table" -> tab, "zkUrl" -> zkUrl)).load().createOrReplaceTempView(tempTable)
    }
    val idName = optParam._2.substring(optParam._2.indexOf(" "), optParam._2.indexOf(",")).trim
    println("to update index[%s], alias[%s], id field[%s], handle sql:[%s]\n".format(trueIndex, optTable, idName, optParam._2))
    //用于代码跟踪，上线时请删除
    val dfOri = ss.sql(optParam._2).cache()
    //将查询结果转为DataFrame
    val c = dfOri.count()
    println("\tcount:" + c) //用于代码跟踪，上线时请删除

    //ES数据写入

    if (c > 0) {
      dfOri.write.format("es").mode("append").options(Map(ES_MAPPING_ID -> idName)).save(trueIndex) //写入ES，并更新数据（更新完后的，需要人为触发索引别名飘逸工作，将API检索时所需要的索引别名进行绑定）
    }
    dfOri.unpersist()
  }

  /**
    * 索引数据标签处理
    **/
  def dataSynUPES(optTable: String, optSQL: String, ss: SparkSession, zkUrl: String, esIncEndness: String): Unit = {
    val tagHead = optTable.substring(0, 4)
    val trueIndex = optTable.substring(4) + "-" + esIncEndness
    val optParam: (Set[String], String, String) = Utils.specialHandleSql(optSQL)
    var tempTable: String = null
    for (tab <- optParam._1) {
      tempTable = tab.split("\\.")(1)
      println("[" + tab + "][" + tempTable + "]")
      ss.read.format(CFT).options(Map("table" -> tab, "zkUrl" -> zkUrl)).load().createOrReplaceTempView(tempTable)
    }
    val dfOri = ss.sql(optParam._2)
    if (tagHead.equals("101_")) {
      val rdd = dfOri.rdd.mapPartitions(iteratorFunction101(_))
      println("to update index for nested,[%s]/[%s][%s]".format(trueIndex, "dtc_101", tagHead))
      EsSpark.saveToEs(rdd, trueIndex, Map("es.mapping.id" -> "ent_id", "es.write.operation" -> "update"))
    } else if (tagHead.equals("201_")) {
      val rdd = dfOri.rdd.mapPartitions(iteratorFunction201(_))
      println("to update index for nested,[%s]/[%s][%s]".format(trueIndex, "dtc_201_advertise_notice", tagHead))
      EsSpark.saveToEs(rdd, trueIndex, Map("es.mapping.id" -> "ent_id", "es.write.operation" -> "update"))
    } else if (tagHead.equals("401_")) {
      val rdd = dfOri.rdd.mapPartitions(iteratorFunction401(_))
      println("to update index for nested,[%s]/[%s][%s]".format(trueIndex, "dtc_401", tagHead))
      EsSpark.saveToEs(rdd, trueIndex, Map("es.mapping.id" -> "ent_id", "es.write.operation" -> "update"))
    }
    dfOri.unpersist()
  }
  def iteratorFunction101(it: Iterator[Row]): Iterator[Nested101] = {
    val res = ListBuffer[Nested101]()
    while (it.hasNext) {
      val row: Row = it.next
      res += getValue101(row)
    }
    res.iterator
  }

  def getValue101(row: Row): Nested101 = {
    val vals = getVal(row, "vals").split("\\|")
    var nList = new ListBuffer[Bean5]()
    for (v <- vals) {
      val vs = v.split(",")
      nList += Bean5(vs(0).toShort, vs(1).toShort, vs(2).toShort, vs(3).toShort, vs(4).toShort)
    }
    Nested101(getVal(row, "ent_id"), nList.toArray)
  }

  def getVal(row: Row, field: String): String = {
    val v: Object = row.getAs(field)
    if (v == null)
      ""
    else
      v.toString
  }

  def iteratorFunction401(it: Iterator[Row]): Iterator[Nested401] = {
    val res = ListBuffer[Nested401]()
    while (it.hasNext) {
      val row: Row = it.next
      res += getValue401(row)
    }
    res.iterator
  }

  def getValue401(row: Row): Nested401 = {
    val vals = getVal(row, "vals").split("\\|")
    var nList = new ListBuffer[Bean5]()
    for (v <- vals) {
      val vs = v.split(",")
      nList += Bean5(vs(0).toShort, vs(1).toShort, vs(2).toShort, vs(3).toShort, vs(4).toShort)
    }
    Nested401(getVal(row, "ent_id"), nList.toArray)
  }

  def iteratorFunction201(it: Iterator[Row]): Iterator[Nested201] = {
    val res = ListBuffer[Nested201]()
    while (it.hasNext) {
      val row: Row = it.next
      res += getValue201(row)
    }
    res.iterator
  }

  def getValue201(row: Row): Nested201 = {
    val vals = getVal(row, "vals").split("\\|")
    var nList = new ListBuffer[Bean2]()
    for (v <- vals) {
      val vs = v.split(",")
      nList += Bean2(vs(0).toShort, vs(1).toShort)
    }
    Nested201(getVal(row, "ent_id"), nList.toArray)
  }

  def setTempView(ss: SparkSession, tName: String, tempView: String, zkUrl: String): Unit = {
    ss.read.format("org.apache.phoenix.spark") //
      .options(Map("table" -> tName, "zkUrl" -> zkUrl)) //
      .load().createOrReplaceTempView(tempView)
  }

  case class Bean2(t1: Short, t2: Short)

  case class Bean5(t1: Short, t2: Short, t3: Short, t4: Short, t5: Short)

  case class Nested201(ent_id: String, dtc_201_advertise_notice: Array[Bean2])

  case class Nested101(ent_id: String, dtc_101: Array[Bean5])

  case class Nested401(ent_id: String, dtc_401: Array[Bean5])
}
