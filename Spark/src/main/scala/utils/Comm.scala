package utils

/**
  * FileName: Comm.java
  * CreateTime: 2020/5/14 18:44.
  * Version: V1.0
  * Author: ChengTao
  * Description: 
  */
object Comm {
  /** \001 */
  val SEP = String.valueOf(1.toChar)
  /** 默认编码 */
  val ENCODE = "UTF-8"
  val CFT = "org.apache.phoenix.spark"
  val CFT_REDIS = "org.apache.spark.sql.redis"
  val CZK = "server1,server2,tool:2181:/hbase-unsecure"
}