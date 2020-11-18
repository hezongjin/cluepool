package utils

/**
  * FileName: RedisUtil.java
  * CreateTime: 2020/4/22 19:36.
  * Version: V1.0
  * Author: ChengTao
  * Description: 
  */
import java.util

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.{Jedis, JedisPool, Response}
import redis.clients.util.Pool

object RedisUtil  {
  private[this] var jedisPool: Pool[Jedis] = _

  def main(args: Array[String]): Unit = {
    val password = "h-{<Fc!yJL87_Zkc8S"
    val host = "192.168.1.30"
    val port = 80
    val timeout = 1000
    RedisUtil.init(host, port, timeout, password, 0)
    RedisUtil.set("Time".getBytes(), "2018-09-03 09:00:00".getBytes())
    val result = RedisUtil.get("Time".getBytes())
    println(new String(result))
    //val map = Map("name"-> "zhangsan","age"-> "21", "gender"-> "male", "id"-> "519099386")
    //RedisUtil.setCols("hash",map)

    // val result = RedisUtil.getCols("hash", Array("name", "age", "xx")).map(x => (x._1, new String(x._2)))
    // logger.info(result)
    //val result = RedisUtil.bulkGetCols(Array("hash", "ss"))
  }

  def init(host: String, port: Int, timeout: Int, password: String, database: Int = 0): Unit = {
    jedisPool = new JedisPool(new GenericObjectPoolConfig, host, port, timeout)
  }
  def connRedis(host: String, port: Int, timeout: Int):Jedis={
    jedisPool = new JedisPool(new GenericObjectPoolConfig, host, port, timeout)
    jedisPool.getResource
  }

  def get(key: Array[Byte]): Array[Byte] = {
    val jedis = jedisPool.getResource
    val result: Array[Byte] = jedis.get(key)
    jedis.close()
    result
  }

  def close():Unit={
    jedisPool.close()
  }


  def hset(key:String,field:String,value:String):Boolean={
    try {
      val jedis = jedisPool.getResource
      jedis.hset(key, field,value)
      jedis.close()
      true
    } catch {
      case e: Exception => {
        false
      }
    }  }
  def set(key: Array[Byte], value: Array[Byte]): Boolean = {
    try {
      val jedis = jedisPool.getResource
      jedis.set(key, value)
      jedis.close()
      true
    } catch {
      case e: Exception => {
        false
      }
    }
  }


  def getCols(key: String,
              cols: Array[String] = Array.empty
             ): Map[String, Array[Byte]] = {
    import scala.collection.JavaConverters._
    val jedis = jedisPool.getResource
    var map = Map.empty[String, Array[Byte]]
    if (cols.length > 0) {
      val pipe = jedis.pipelined()
      val response = pipe.hmget(key.getBytes(), cols.map(_.getBytes()): _*)
      pipe.sync()
      map = cols.zip(response.get.asScala).toMap.filter(x => x._2 != null)
      pipe.close()
    } else {
      val tmpMap: util.Map[Array[Byte], Array[Byte]] = jedis.hgetAll(key.getBytes())
      map = tmpMap.asScala.toMap.map(x => (new String(x._1), x._2))
    }
    jedis.close
    map
  }

  def getCols2(
                key: String,
                cols: Array[String] = Array.empty
              ): Map[String, Array[Byte]] = {
    val jedis = jedisPool.getResource
    var map = Map.empty[String, Array[Byte]]
    if (cols.length > 0) {
      for (col <- cols) {
        val value: Array[Byte] = jedis.hget(key.getBytes(), col.getBytes())
        if (null != value) {
          map = map + (col -> value)
        }
      }
    } else {
      val tmpMap: util.Map[Array[Byte], Array[Byte]] = jedis.hgetAll(key.getBytes())
      import scala.collection.JavaConverters._
      map = tmpMap.asScala.toMap.map(x => (new String(x._1), x._2))
    }
    jedis.close
    map
  }

  def bulkGetCols(keys: Array[String],
                  cols: Array[String] = Array.empty
                 ): Map[String, Map[String, Array[Byte]]] = {
    import scala.collection.JavaConverters._
    var result: Map[String, Map[String, Array[Byte]]] = Map.empty
    val jedis = jedisPool.getResource
    val pipe = jedis.pipelined
    if (cols.length > 0) {
      val data = keys.map(x => {
        pipe.hmget(x.getBytes(), cols.map(_.getBytes()): _*)
      })

      pipe.sync
      pipe.close
      jedis.close

      result = keys.zip(data.map(_.get().asScala.toArray).map(cols.zip(_).toMap.filter(null != _._2)))
        .toMap.filter(_._2.nonEmpty)
    } else {
      val data: Array[Response[util.Map[Array[Byte], Array[Byte]]]] = keys.map(x => {
        pipe.hgetAll(x.getBytes())
      })
      pipe.sync
      pipe.close
      jedis.close

      result = keys.zip(data.map(_.get().asScala.map(x => (new String(x._1), x._2)).toMap))
        .toMap.filter(_._2.nonEmpty)
    }
    result
  }

  def bulkGetCols2(rowkeys: Array[String],
                   cols: Array[String] = Array.empty
                  ): Map[String, Map[String, Array[Byte]]] = {
    val jedis = jedisPool.getResource
    var map = Map.empty[String, Map[String, Array[Byte]]]
    import scala.collection.JavaConverters._
    for (rowkey <- rowkeys) {
      var cellMap = Map.empty[String, Array[Byte]]
      if (cols.length > 0) {
        for (col <- cols) {
          val value = jedis.hget(rowkey.getBytes(), col.getBytes())
          if (null != value) {
            cellMap = cellMap + (col -> value)
          }
        }
      } else {
        val tmpMap = jedis.hgetAll(rowkey.getBytes())
        cellMap = tmpMap.asScala.toMap.map(x => (new String(x._1), x._2))
      }
      if (cellMap.nonEmpty) {
        map = map + (rowkey -> cellMap)
      }
    }
    jedis.close
    map
  }

  def setCols(
               key: String,
               fieldValues: Map[String, String]
             ): Unit = {
    import scala.collection.JavaConverters._
    val data = fieldValues.map(element => {
      (element._1.getBytes(), element._2.getBytes())
    }).asJava
    val jedis = jedisPool.getResource
    jedis.hmset(key.getBytes(), data)
    jedis.close()
  }

}