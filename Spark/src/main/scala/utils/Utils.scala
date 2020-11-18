package utils

import java.io.InputStream

import com.alibaba.fastjson.JSONArray
import org.apache.commons.lang3.StringUtils

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.io.Source
import scala.util.matching.Regex
import org.apache.http.HttpStatus
import org.apache.http.client.methods.{CloseableHttpResponse, HttpPost, HttpPut}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{HttpClientBuilder, HttpClients}
import org.apache.http.util.EntityUtils
import org.apache.commons.io.IOUtils
import org.apache.spark.sql.DataFrame

import scala.collection.mutable
import scala.util.parsing.json.JSONObject

/**
  * FileName: Utils.java
  * CreateTime: 2020/5/14 18:45.
  * Version: V1.0
  * Author: ChengTao
  * Description: 
  */
object Utils {
  private val pattern4Date = new Regex("""^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2},\d{3}""")
  private val pattern4Table = "(from|FROM|join|JOIN|upsert into|UPSERT INTO)[\\s+](\\S+[\\.\\:]\\S+)".r

  def main(args: Array[String]): Unit = {

    val taskMsg = "[{\"entId\":\"企业1id\",\"entStatus\":\"1\"},{\"entId\":\"企业2id\",\"entStatus\":\"0\"}]"
    getProtectStatusInfo(taskMsg).foreach{case (entId, entStatus)=>println("[RES]"+entId+":"+entStatus)}

    val sqlStr = "UPSERT INTO DTC.T_DTC_SUBJECT_201_20200521 SELECT ent_id as subject_id,1 as idx,modify_time as modify_time,create_time as create_time FROM EDS.T_EDS_ENT_BASE_INFO;";
    val ps: (Set[String], String, String) = specialHandleSql(sqlStr)
    var df1: DataFrame = null
    var simpleName: String = null

    for (tab <- ps._1) {
      simpleName = tab.split("\\.")(1)
      println("[" + simpleName + "][" + tab + "]")
      //ss.read.format(Comm.CFT).options(Map("table" -> tab, "zkUrl" -> Comm.CZK)).load().createOrReplaceTempView(simpleName)

    }
    println("[]" + ps._2)
    println("[]" + ps._3)

  }

  /**
    * 判断有效日志开始格式<br>
    * 0000-00-00 00:00:00,000
    */
  def isValidBegin(str: String): Boolean = {
    pattern4Date.findFirstIn(str).isDefined
  }


  def readFile(file: String): String = {
    val in: InputStream = Utils.getClass.getClassLoader.getResourceAsStream(file)
    val s = Source.fromInputStream(in).mkString
    in.close()
    s
  }

  def readFileToLine(file: String): ListBuffer[String] = {
    val in: InputStream = Utils.getClass.getClassLoader.getResourceAsStream("sqls/" + file)
    val s = Source.fromInputStream(in).getLines()
    val rowList = ListBuffer[String]()
    var t: String = null
    while (s.hasNext) {
      t = s.next()
      if (StringUtils.isNotEmpty(t)) {
        rowList += t
      }
    }

    in.close()
    rowList
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
          tmp = tmp.replace(tab, tab.split("\\.|\\:")(1))

        }
      }
    }

    (array.toSet, tmp, intoTable)
  }


  def http(url: String, json: String): String = {
    val client = HttpClientBuilder.create().build()
    val post = new HttpPost(url)
    var result = ""
    try {
      val s = new StringEntity(json)
      s.setContentEncoding("UTF-8")
      s.setContentType("application/json")
      post.setEntity(s)
      val res = client.execute(post)
      if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        result = EntityUtils.toString(res.getEntity())
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      client.close()
    }

    result
  }


  def httpPut(url: String, json: String, headers: Map[String, String]): String = {
    val client = HttpClients.createDefault()
    val httpPut = new HttpPut(url)
    httpPut.setHeader("Content-type", "application/json")

    val stringEntity = new StringEntity(json, Comm.ENCODE)
    httpPut.setEntity(stringEntity)
    var result: String = null
    var httpResponse: CloseableHttpResponse = null
    try {
      //响应信息
      httpResponse = client.execute(httpPut)
      val entity = httpResponse.getEntity()
      result = EntityUtils.toString(entity, Comm.ENCODE)
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      IOUtils.closeQuietly(httpResponse)
      IOUtils.closeQuietly(client)
    }
    result
  }
  /**
    * 格式化待同步信息
    * */
  def getProtectStatusInfo(taskMsg:String): Map[String, Int] = {
    var dataSourceMap: Map[String, Int] = Map()
    import com.alibaba.fastjson.{JSON, JSONException, JSONObject}
    val parseObject: JSONArray = JSON.parseArray(taskMsg)
    for(i <- 0 until parseObject.size()){
      println(i+":"+parseObject.get(i))
      val data: JSONObject = JSON.parseObject(parseObject.get(i).toString)
      println(i+"[]"+data.get("entId").toString+" : "+data.get("entStatus").toString.toInt)
      dataSourceMap+=(data.get("entId").toString -> data.get("entStatus").toString.toInt)
    }
    dataSourceMap
  }


}
