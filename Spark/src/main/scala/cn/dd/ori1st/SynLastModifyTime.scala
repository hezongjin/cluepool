package cn.dd.ori1st

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.elasticsearch.spark.rdd.EsSpark
import utils.DataFormat
import org.apache.commons.lang3.StringUtils
import scala.collection.Map
import scala.collection.mutable.ListBuffer

/**
  * FileName: ToAll4Nested.java
  * CreateTime: 2020/5/15 22:29.
  * Version: V1.0
  * Author: ChengTao
  * Description: 
  */
object SynLastModifyTime {
  private val CONFIG_FILE: String = "confNew.properties"

  def main(args: Array[String]): Unit = {
    val _args: Array[String] = args

    //        val _args = Array("temp/test.sql")
    if (_args == null || _args.length == 0 || StringUtils.isEmpty(_args(0))) {
      println("参数为空")
      System.exit(0)
    }

    val dateIndex = "-" + _args(0).trim
    println("本次es index后缀:" + dateIndex)
    val pro = DataFormat.readConf(CONFIG_FILE)
    //读取并加载项目配置文件
    val confData: Properties = DataFormat.readConf(CONFIG_FILE)

    val conf = new SparkConf().setAppName(SynLastModifyTime.getClass.getName)
      //.setMaster("local[2]") //本地调试使用，集群模式可删除
      .set("es.nodes", confData.getProperty("es.nodes", ""))
      .set("es.port", confData.getProperty("es.port", ""))
      .set("es.index.auto.create", confData.getProperty("es.index.auto.create", "false"))
      .set("es.nodes.wan.only", confData.getProperty("es.nodes.wan.only", "false"))
      .set("dfs.client.socket-timeout", "300000")
      //.set("spark.sql.warehouse.dir", "file:///F://PRO_BIGDATA//SparkLearning")
    //本地调试使用，集群模式可删除
    val ss = SparkSession.builder().config(conf).getOrCreate()

    //
    val index = "ent_base_info-2020-05-30"
    val zkUrl = pro.getProperty("zkUrl", "")
    setTempView(ss, "DTC.T_DTC_LASTMODIFYTIME", "T_DTC_LASTMODIFYTIME", zkUrl)
    var df: DataFrame = null
    var sql = "select ENT_ID as ent_id,LMT01,LMT02,LMT03,LMT04,LMT05,LMT06 from T_DTC_LASTMODIFYTIME where LMT01 is not null and length(ent_id) = 38 and (LMT01>'2020-07-04' or LMT02>'2020-07-04' or LMT03>'2020-07-04' or LMT04>'2020-07-04' or LMT05>'2020-07-04' or LMT06>'2020-07-04')"
    if (sql != null && sql.length > 0) {
      df = ss.sql(sql)
      val rdd = df.rdd.mapPartitions(iteratorFunctionLT(_))
      df.show()
      println("to update index for nested,[%s]/[%s]".format(index, "last_modify_time"))
      EsSpark.saveToEs(rdd, index, Map("es.mapping.id" -> "ent_id", "es.write.operation" -> "update"))
    }
    ss.stop()
  }


  def getVal(row: Row, field: String): String = {
    val v: Object = row.getAs(field)
    if (v == null)
      ""
    else
      v.toString
  }



  def iteratorFunctionLT(it: Iterator[Row]): Iterator[LastModifyTime] = {
    val res = ListBuffer[LastModifyTime]()
    while (it.hasNext) {
      val row: Row = it.next
      res += getValLMT(row)
    }
    res.iterator
  }

  def getValLMT(row: Row): LastModifyTime = {
    val lt1 = getVal(row, "LMT01")
    val lt2 = getVal(row, "LMT02")
    val lt3 = getVal(row, "LMT03")
    val lt4 = getVal(row, "LMT04")
    val lt5 = getVal(row, "LMT05")
    val lt6 = getVal(row, "LMT06")
    val timeList = List(lt1,lt2,lt3,lt4,lt5,lt6)
    LastModifyTime(getVal(row, "ent_id"), DataFormat.getLastModifyTime(timeList))
  }

  def setTempView(ss: SparkSession, tName: String, tempView: String, zkUrl: String): Unit = {
    ss.read.format("org.apache.phoenix.spark") //
      .options(Map("table" -> tName, "zkUrl" -> zkUrl)) //
      .load().createOrReplaceTempView(tempView)
  }


  case class LastModifyTime(ent_id: String, last_modify_time: String)
}
