package utils

import java.io.{File, FileInputStream, InputStream, InputStreamReader}
import java.text.SimpleDateFormat
import java.util
import java.util.{Date, Properties}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.matching.Regex
import org.apache.spark.sql.Row

import scala.collection.mutable
import scala.io.BufferedSource

/**
  * FileName: DataFormat.java
  * CreateTime: 2020/4/16 18:25.
  * Version: V1.0
  * Author: ChengTao
  * Description: 数据转化辅助类
  */
object DataFormat {
  private val pattern4Date = new Regex("""^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2},\d{3}""")
  private val pattern4Table = "(from|FROM|join|JOIN|upsert into|UPSERT INTO)[\\s+](\\S+\\.\\S+)".r
  private val SPLIT_KEY: String = "#-#";
  private val PATTERN = "[0-9.]{1,}".r

  def main(args: Array[String]): Unit = {
    println("[CD]" + getCurrentDate())
    println("[CC]" + getOriSign("15321336337"))
    val tagCount: Int = 3
    val tagData: String = "普通#-#普通#-#普通"
    val scoreData: String = "0.71428573#-#0#-#1"
    val feedData: String = "447456755635453953,447456755635453955,447456758974119938,447456758978314241,447456758990897154,447456758995091457,447456759007674370,447456759011868673,447456760161107968,447692460110381057#-#447456756633698305,456526921941909504#-#447456755635453953,447456755635453955,447456758974119938,447456758978314241,447456758990897154,447456758995091457,447456759007674370,447456759011868673,447456760161107968,447692460110381057"
    println("[DD]" + getTagRes(tagCount, tagData, scoreData, feedData, SPLIT_KEY))
    println("[1]" + getLastModifyTime(List("2020-05-28 19:19:19", "2020-05-27 19:09:19", "2020-05-27 19:59:19", "2020-05-27 19:49:19", "2020-05-27 19:39:19", "2020-05-27 19:29:19")))
  }

  /**
    * 根据多表修改日期获取最新修改日期
    **/
  def getLastModifyTime(data: List[String]): String = {
    val cs = data.sortWith(_ < _).reverse
    cs(0).toString
  }

  /**
    * 读取配置
    *
    * @return
    */
  def readConf(configFile: String): Properties = {
    val in: InputStream = DataFormat.getClass.getClassLoader.getResourceAsStream(configFile)
    val props = new Properties()
    props.load(new InputStreamReader(in, "UTF-8"))
    in.close()
    props
  }

  /**
    *
    * @param str
    * @return
    */
  def specialHandleSql(str: String): Tuple3[Set[String], String, String] = {
    val array = ArrayBuffer[String]()
    var tmp: String = str
    var intoTable: String = null
    for (pattern4Table(flag, tab) <- pattern4Table.findAllIn(str)) {
      if (flag.toUpperCase.startsWith("UPSERT")) {
        intoTable = tab
        tmp = tmp.replaceFirst(tab, "").replace(flag, "")
      } else if (!tab.startsWith("(")) {
        if (tab.startsWith("!")) {
          tmp = tmp.replace(tab, tab.substring(1))
        } else {
          array += tab
          tmp = tmp.replace(tab, tab.split("\\.")(1))
        }
      }
    }
    (array.toSet, tmp, intoTable)
  }

  /**
    * 获取当前时间
    **/
  def getCurrentDate(): String = {
    new SimpleDateFormat("yyyy-MM-dd").format(new Date())
  }

  /**
    * 标签计算函数
    **/
  def getTagRes(tagCount: Int, tagData: String, scoreData: String, feedData: String, splitKey: String): String = {
    val tagArr = tagData.split("#-#")
    val scoreArr = scoreData.split("#-#")
    val feedIdArr = feedData.split("#-#")
    var tagSign = ""
    //获取各标签对应feedID的个数
    val tagFeedRelCount: Array[Int] = new Array[Int](tagCount)
    var tagFeedIdMaxCount: Int = 0
    var allTagStr: String = ""
    for (i <- 0 to (tagCount - 1)) {
      tagFeedRelCount.update(i, feedIdArr(i).split(",").length)
      if (tagFeedIdMaxCount <= feedIdArr(i).split(",").length) {
        tagFeedIdMaxCount = feedIdArr(i).split(",").length
      }
      for (k <- 0 to feedIdArr(i).split(",").length - 1) {
        allTagStr += "," + feedIdArr(i).split(",")(k)
      }
    }
    //println("[INFO][MAX]"+tagFeedIdMaxCount)
    //println("[INFO][DETAIL]"+tagFeedRelCount.toBuffer)
    if (tagArr.length == tagCount && tagArr.length == scoreArr.length && tagArr.length == feedIdArr.length) {
      var stepNum = 0
      for (j <- 0 to tagFeedIdMaxCount) {
        for (i <- 0 to tagCount - 1) {
          if (scoreArr(i).toDouble > 0.5) {
            if (feedIdArr(i).split(",").length > 1 && stepNum < tagFeedIdMaxCount - 1 && stepNum < tagFeedRelCount(i) - 1) {
              tagSign = tagSign + "|" + feedIdArr(i).split(",")(stepNum) + "|" + feedIdArr(i).split(",")(stepNum + 1)
            } else if (feedIdArr(i).split(",").length == 1 && stepNum < tagFeedIdMaxCount && stepNum < tagFeedRelCount(i)) {
              tagSign = tagSign + "|" + feedIdArr(i).split(",")(0)
            }
            stepNum += 2
          } else {
            if (feedIdArr(i).split(",").length >= 1 && stepNum < tagFeedIdMaxCount && stepNum < tagFeedRelCount(i)) {
              tagSign = tagSign + "|" + feedIdArr(i).split(",")(stepNum)
            }
            stepNum += 1
          }
        }
      }
    }
    val allTagArr: Array[String] = allTagStr.substring(1).split(",")
    var tagEndStr: String = "";
    for (i <- 0 to allTagArr.length - 1) {
      if (tagSign.indexOf(allTagArr(i)) < 0) {
        tagEndStr += "|" + allTagArr(i)
      }
    }
    if (tagEndStr.length > 0) {
      (tagSign + "|" + tagEndStr.substring(1)).substring(1).split("\\|").distinct.mkString(",").toString
    } else {
      (tagSign).substring(1).split("\\|").distinct.mkString(",").toString
    }

  }

  /**
    * 用户手机号加密函数
    **/
  def getOriSign(codes: String): String = {
    return "12312" //StringCodecUtil.encode(codes,"CRM","F71BC468701B11E7","UTF-8")
  }

  /**
    * 增量任务解析函数
    **/
  def getTaskInfo(taskOri: String, taskSQL: String): Tuple6[String, String, String, String, String, String] = {
    val taskInfo: Array[String] = taskOri.split("\\.")
    val taskNo: String = taskInfo(1).toString
    val taskName: String = taskInfo(3).toString
    val taskDetail: Array[String] = taskInfo(2).split("_")
    val taskDB: String = taskDetail(1).toString
    val taskType: String = taskDetail(2).toString
    val taskOptType: String = taskDetail(0).toString
    (taskNo, taskName, taskDB, taskType, taskOptType, taskSQL)
  }

  /**
    * 注册资金单位转换函数
    */
  def parseFunction(it: Row): Array[String] = {
    val resultList = ListBuffer[String]()

    try {
      val row: Row = it

      val ent_id: String = getVal(row, "ent_id")
      val reg_caps: String = trim(getVal(row, "reg_caps"))
      val reg_caps_unit: String = trim(getVal(row, "reg_caps_unit")).replaceAll("!|-", "")
      //
      var reg_caps3 = reg_caps //金额
      var reg_caps_unit3 = reg_caps_unit //单位
      //
      val capUnit = extract(reg_caps3) // 本来是数字
      val capUnit2 = extract(reg_caps_unit3) // 本来是单位

      if (capUnit._2.length > 0) {
        reg_caps3 = capUnit._1
        reg_caps_unit3 = capUnit._2
      }

      if (capUnit2._2.length > 0) { // 提取单位
        reg_caps_unit3 = capUnit2._2
      }

      if (!capUnit2._1.equals("0.0") && reg_caps3.equals("0.0")) { // 提取数字
        reg_caps3 = capUnit2._1
      }

      //
      val radio = getRatio(reg_caps_unit3)
      if (reg_caps3.equals("")) {
        reg_caps3 = "0.0"
      }

      // 转化
      val reg_caps2 = reg_caps3.toDouble * radio._1
      val reg_caps_unit2 = "万元人民币"

      // 处理
      resultList += (ent_id //
        + Comm.SEP + reg_caps + Comm.SEP + reg_caps_unit //
        + Comm.SEP + reg_caps2.formatted("%.2f") + Comm.SEP + reg_caps_unit2 //
        + Comm.SEP + radio._2
        )
    } catch {
      case e: Exception => {
        println("---RunERROR -> " + e)
      }
    }

    resultList.toArray
  }


  /**
    * 获取汇率
    *
    * @param str
    * @return
    */
  def getRatio(str: String): Tuple2[Double, String] = {
    var mulriple: Double = 1d
    var unit = "人民币"

    if (str == null || str.equals("")) {
      return (mulriple, unit)
    }

    // 无万时
    if (!str.contains("万")) {
      mulriple = 0.0001
    }

    if (str.contains("亿")) {
      mulriple = 10000
    }

    if (str.contains("人民币") || str.equals("元")) {
      //  mulriple = mulriple
    } else if (str.contains("港")) {
      mulriple = mulriple * 0.855 //香港元	0.855
      unit = "港"
    } else if (str.contains("澳门")) {
      mulriple = mulriple * 0.83 //澳门元	0.83
      unit = "澳门"
    } else if (str.contains("澳")) {
      mulriple = mulriple * 4.792 //澳元/澳大利亚元	4.792
      unit = "澳"
    } else if (str.contains("德")) {
      mulriple = mulriple * 11.5302 //德国马克	11.5302
      unit = "德"
    } else if (str.contains("加")) {
      mulriple = mulriple * 5.019 //加元/加拿大元	5.019
      unit = "加"
    } else if (str.contains("泰")) {
      mulriple = mulriple * 0.21 //泰国铢	0.21
      unit = "泰"
    } else if (str.contains("日")) {
      mulriple = mulriple * 0.059 //日元	0.059
      unit = "日"
    } else if (str.contains("莱")) {
      mulriple = mulriple * 4.947 //文莱元	4.947
      unit = "莱"
    } else if (str.contains("富")) {
      mulriple = mulriple * 0.0865 //阿富汗尼人民币	0.0865
      unit = "富"
    } else if (str.contains("芬")) {
      mulriple = mulriple * 1.2423 //芬兰马克	1.2423
      unit = "芬"
    } else if (str.contains("奥地利")) {
      mulriple = mulriple * 0.5368 //奥地利先令	0.5368
      unit = "奥地利"
    } else if (str.contains("哥伦")) {
      mulriple = mulriple * 0.002133 //哥伦比亚比索	0.002133
      unit = "哥伦"
    } else if (str.contains("瓦")) {
      mulriple = mulriple * 0.5477 //新克瓦查	0.5477
      unit = "瓦"
    } else if (str.contains("比")) {
      mulriple = mulriple * 0.1831 //比利时法郎	0.1831
      unit = "比"
    } else if (str.contains("美")) {
      mulriple = mulriple * 6.71 //美元	6.71
      unit = "美"
    } else if (str.contains("CFP")) {
      mulriple = mulriple * 0.0632 //CFP法郎	0.0632
      unit = "CFP"
    } else if (str.contains("瓜")) {
      mulriple = mulriple * 0.00108 //瓜拉尼	0.00108
      unit = "瓜"
    } else if (str.contains("瑞典")) {
      mulriple = mulriple * 0.721 //瑞典 克朗/瑞典 克郎	0.721
      unit = "瑞典"
    } else if (str.contains("马")) {
      mulriple = mulriple * 1.622 //马来西亚林吉特	1.622
      unit = "马"
    } else if (str.contains("丹")) {
      mulriple = mulriple * 1.01 //丹麦 克朗/丹麦 克郎	1.01
      unit = "丹"
    } else if (str.contains("台")) {
      mulriple = mulriple * 0.217 //新台湾元/新台币	0.217
      unit = "台"
    } else if (str.contains("塞")) {
      mulriple = mulriple * 12.734 //塞浦路斯镑	12.734
      unit = "塞"
    } else if (str.contains("智")) {
      mulriple = mulriple * 0.01 //智利比索	0.01
      unit = "智"
    } else if (str.contains("普")) {
      mulriple = mulriple * 0.633 //普拉	0.633
      unit = "普"
    } else if (str.contains("韩")) {
      mulriple = mulriple * 0.00588 //韩国元/韩元	0.00588
      unit = "韩"
    } else if (str.toLowerCase.contains("mvdol")) {
      mulriple = mulriple * 0.97 //Mvdol/玻利维亚	0.97
      unit = "Mvdol"
    } else if (str.contains("英镑")) {
      mulriple = mulriple * 8.722 //英镑	8.722
      unit = "英镑"
    } else if (str.contains("欧元")) {
      mulriple = mulriple * 7.546 //欧元	7.546
      unit = "欧元"
    } else if (str.contains("列克")) {
      mulriple = mulriple * 0.06091 //列克	0.06091
      unit = "列克"
    } else if (str.contains("瑞士")) {
      mulriple = mulriple * 6.6135 //瑞士 法郎	6.6135
      unit = "瑞士"
    } else if (str.contains("新加坡")) {
      mulriple = mulriple * 4.946 //新加坡元	4.946
      unit = "新加坡"
    } else if (str.contains("荷")) {
      mulriple = mulriple * 3.7455 //荷兰币	3.7455
      unit = "荷"
    } else if (str.contains("挪")) {
      mulriple = mulriple * 0.789 //挪威 克朗	0.789
      unit = "挪"
    } else if (str.contains("马来西亚")) {
      mulriple = mulriple * 1.622 //马来西亚币	1.622
      unit = "马来西亚"
    } else if (str.contains("法国")) {
      mulriple = mulriple * 6.6129 //法国 法郎	6.6129
      unit = "法国"
    } else if (str.contains("新西兰")) {
      mulriple = mulriple * 4.481 //新西兰元	4.481
      unit = "新西兰"
    } else if (str.contains("科摩罗")) {
      mulriple = mulriple * 0.0153 //科摩罗 法郎	0.0153
      unit = "科摩罗"
    } else if (str.contains("比塞塔")) {
      mulriple = mulriple * 0.045 //安道尔比塞塔	0.045
      unit = "比塞塔"
    } else if (str.contains("布隆迪")) {
      mulriple = mulriple * 0.003693 //布隆迪 法郎	0.003693
      unit = "布隆迪"
    } else if (str.contains("菲")) {
      mulriple = mulriple * 0.129 //菲律宾比索	0.129
      unit = "菲"
    } else {
      mulriple = 1
    }

    (mulriple, unit)
  }


  /**
    * 抽取 数字和单位
    *
    * @param str
    * @return
    */
  def extract(str: String): Tuple2[String, String] = {
    var num = PATTERN.findFirstIn(str).getOrElse("")
    if (num.equals("."))
      num = "0.0"
    (num, trimSpecialChar(PATTERN.replaceAllIn(str, "")))
  }

  /**
    * 去除空字符
    *
    * @param str
    * @return
    */
  def trimSpecialChar(str: String): String = {
    if (str == null) {
      ""
    } else {
      str.replaceAll(",|\\(|\\)|（|）", "")
    }
  }

  /**
    * 去除空字符
    *
    * @param str
    * @return
    */
  def trim(str: String): String = {
    if (str == null) {
      ""
    } else {
      str.replaceAll(" |　", "")
    }
  }

  /**
    * 获取字段值
    **/
  def getVal(row: Row, field: String): String = {
    val v: Object = row.getAs(field)
    if (v == null)
      ""
    else
      v.toString
  }

  /**
    * 格式化企业注册资金表字段
    **/
  def createEtl4CapitalSchema(): ListBuffer[String] = {
    var fieldList = new ListBuffer[String]()
    fieldList += "ent_id"
    fieldList += "reg_caps"
    fieldList += "reg_caps_unit"
    fieldList += "reg_caps2"
    fieldList += "reg_caps_unit2"
    fieldList += "simple_unit"
    fieldList
  }

  /**
    * 行数据转换
    **/
  def toRow(data: String): Row = {
    Row.fromSeq(data.split(Comm.SEP, -1))
  }

}
