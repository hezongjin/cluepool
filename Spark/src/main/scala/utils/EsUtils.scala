package utils

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark.sql._
/**
  * FileName: EsUtils.java
  * CreateTime: 2020/5/20 11:30.
  * Version: V1.0
  * Author: ChengTao
  * Description: ES操作辅助类
  */
object EsUtils {

  def main(args: Array[String]): Unit = {
    updateDate("1_201_73b8193e54b23e1bf3aafabbc14181de",2)
    /*
    val spark = getSparkSession()
    //增
    val dataFrame = spark.createDataFrame(Seq(
      (1, 1, "2", "5"),
      (2, 2, "3", "6"),
      (3, 2, "36", "69")
    )).toDF("id", "label", "col1", "col2")
    dataFrame.saveToEs("test_index20200520",Map("es.mapping.id" -> "id"))

    //改
    val dataFrame1 = spark.createDataFrame(Seq(
      (3, 12)
    )).toDF("id", "label")
    dataFrame1.saveToEs("test_index20200520",Map("es.mapping.id" -> "id"))
    */
  }


  //配置spark
  def getSparkSession(): SparkSession = {
    val masterUrl = "local"
    val appName = "ESUTILS"
    val sparkconf = new SparkConf()
      .setMaster(masterUrl)
      .setAppName(appName)
      .set("es.nodes", "10.12.52.174")
      .set("es.port", "9200")
      //.set("spark.sql.warehouse.dir", "file:///F://PRO_BIGDATA//SparkLearning")//本地调试使用，集群模式可删除
    val Sparks = SparkSession.builder().config(sparkconf).getOrCreate()
    Sparks
  }

  //修改
  def updateDate(entId:String,entStatus:Int): Unit ={
    val session: SparkSession = getSparkSession()
    val dataFrame = session.createDataFrame(Seq(
      (entId->entStatus)
      //(data.get("ent_id").toString,data.get("status").toString.toInt)
    )).toDF("ent_id", "ent_protect_status")
    dataFrame.saveToEs("ent_base_info-2020-06-03",Map("es.mapping.id" -> "ent_id", "es.write.operation" -> "update"))
    session.stop()
  }




}
