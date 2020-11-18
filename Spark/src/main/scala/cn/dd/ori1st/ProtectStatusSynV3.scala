package cn.dd.ori1st

import java.io.Serializable

import com.alibaba.fastjson.JSONArray
import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import kafka.utils.{ZKGroupTopicDirs, ZkUtils}
import org.I0Itec.zkclient.ZkClient
import org.apache.http.impl.client.HttpClients
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark.rdd.EsSpark
import utils.{EsUtils, HttpUtils}

import scala.collection.mutable.ListBuffer

/**
  * FileName: ProtectStatusSyn.java
  * CreateTime: 2020/6/23 10:11.
  * Version: V1.0
  * Author: ChengTao
  * Description: 一号线索池企业保护状态同步(基于ESAPI的状态数据同步任务，已部署于线上)
  */
object ProtectStatusSynV3 {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().set("es.nodes", "10.20.124.146")
      .set("es.port","9200")
      .set("es.index.auto.create", "truw")
      .set("es.nodes.wan.only", "true")
      .set("dfs.client.socket-timeout", "300000")
      //.set("spark.sql.warehouse.dir", "file:///F://PRO_BIGDATA//SparkLearning")//本地调试使用，集群模式可删除

    conf.setAppName("ProtectStatusSynV3")
    //设置为本地调试模式
    //conf.setMaster("local[2]")
    //创建Spark执行的的入口
    val sc = new SparkContext(conf)


    //SparkStreaming的StreamContext是对SparkContext的封装，他对SparkContext包装一层后即可实现实时功能
    //创建StreamingContext,定义各计算批次的时间间隔为5秒,接下来就可以使用DStream了
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(5))
    //从Kafka中读取数据进行计算
    //定义broker地址、topic名称、消费者属组
    //一号线索池-线上环境
    val brokerList:String = "server1:6667,server2:6667,tool:6667";
    //val topic: String = "SEND_CLUESPOOL_ENTSTATUS"
    //val topic: String = "se-do-data-test2"
    val topic:String ="SEND_CLUESPOOL_ENTSTATUS_01";
    val topics: Set[String] = Set[String](topic)
    //SparkStreaming可以同时消费多个Topic，所以这里是集合
    val groupId: String = "ProtectStatusSyn"
    //定义zk相关信息
    val zkList: String = "server1:2181,server2:2181,tool:2181"
    //创建偏移量保存目录
    val topicDirs: ZKGroupTopicDirs = new ZKGroupTopicDirs(groupId, topic)
    val zkTopicPath: String = s"${topicDirs.consumerOffsetDir}"
    //准备Kafka参数
    val kafkaParams = Map(
      "metadata.broker.list" -> brokerList,
      "group.id" -> "spark",
      "auto.offset.reset" -> kafka.api.OffsetRequest.SmallestTimeString //从头开始消费数据
    )



    //实例化zkClient，更新偏移量
    val zkClient: ZkClient = new ZkClient(zkList)
    //查看路径下是否有偏移量节点记录
    val children: Int = zkClient.countChildren(zkTopicPath)
    var kafkaStream: InputDStream[(String, String)] = null
    //如果zk中保存了offset，就用这个offset，否则，从头读起
    var fromOffsets: Map[TopicAndPartition, Long] = Map()
    if (children > 0) {
      for (i <- 0 until children) {
        val partitionOffset: String = zkClient.readData[String](s"$zkTopicPath/${i}")
        val tp: TopicAndPartition = TopicAndPartition(topic, i)
        //将不同Partition对应的Offset增加到fromOffsets中
        fromOffsets += (tp -> partitionOffset.toLong)
      }
      //对Kafka的消息进行Transform，最终的数据会变成(topic_name,message)这样的元组
      val messageHandler = (mmd: MessageAndMetadata[String, String]) => (mmd.key(), mmd.message())
      //创建DStream
      //kafkaStream = KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder,(String,String)](ssc,kafkaParams,fromOffsets,messageHandler)
      kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, (String, String)](ssc, kafkaParams, fromOffsets, messageHandler)
    } else {
      //如果未保存，根据 kafkaParam 的配置使用最新(largest)或者最旧的（smallest） offset
      kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)
    }
    //偏移量的范围
    var offsetRanges = Array[OffsetRange]()

    //从kafka读取的消息，DStream的Transform方法可以将当前批次的RDD获取出来
    //该transform方法计算获取到当前批次RDD,然后将RDD的偏移量取出来，然后在将RDD返回到DStream
    var updateData: Iterator[LastModifyTime] = null
    val transform: DStream[(String, String)] = kafkaStream.transform { rdd =>
      //得到该 rdd 对应 kafka 的消息的 offset
      //该RDD是一个KafkaRDD，可以获得偏移量的范围
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      /*
      rdd.foreach(x=>{
        println("[sd]"+x._2)
        val taskTemp = getProtectStatusInfo(x._2)
        for((entId,entStatus)<- taskTemp){
          println("[RESID-A-]"+entId)
          println("[RESSTATUS-A-]"+entStatus)
          //val dataRdd = sc.parallelize(Seq(Map(entId->entStatus)))
          //EsUtils.updateDate(entId,entStatus.toInt)
          HttpUtils.posetUpdateData2ES(entId,entStatus)
          println("[UPDATE]"+"["+entId+"]["+entStatus+"]")

        }
      })
      */
      rdd
    }
    //var taskMap = ("1_201_73b8193e54b23e1bf3aafabbc14181de"->1)
    //sc.makeRDD(Seq(taskMap))
    //val parallelize: RDD[Map[String, Int]] = sc.parallelize(Seq(taskMap))
    //println("[1]"+updateData)
    //EsSpark.saveToEs(sc.makeRDD(Seq(updateData)), "ent_base_info-2020-06-03", Map("es.mapping.id" -> "ent_id", "es.write.operation" -> "update"))
    val messages: DStream[String] = transform.map(_._2)


    messages.foreachRDD { rdd =>

      //对RDD进行操作，触发Action
      rdd.foreachPartition(partition =>{
        partition.foreach(x=>{
          println("[MSG]"+x)
          try{
            val cc = getProtectStatusInfo(x)
            for((entId,entStatus)<- cc){
              println("[RESID-]"+entId)
              println("[RESSTATUS-]"+entStatus)
              //EsUtils.updateDate(entId,entStatus.toInt)
              HttpUtils.posetUpdateData2ES(entId,entStatus)
              println("[UPDATE]"+"["+entId+"]["+entStatus+"]")
            }
          }catch {
            case ex: Exception => {
              ex.printStackTrace() // 打印到标准err
              System.err.println("exception===>: ..."+ex)  // 打印到标准err
              System.err.println("MSG===>: ..."+x)  // 打印到标准err
            }
          }
        })

      })

      /*V1
      rdd.foreachPartition(partition =>
        partition.foreach(x => {
          println(x)
          getProtectStatusInfo(x).foreach{case (entId, entStatus)=>{
            println("[OPT]"+"[entId] "+entId+"[entStatus] "+entStatus)
            //LastModifyTime(entId,entStatus)
            //import org.elasticsearch.spark._
            taskMap = getProtectStatusInfo(x)

            //students.saveToEs("ent_base_info-2020-06-03", Map("es.mapping.id" -> "ent_id", "es.write.operation" -> "update"))
            //EsSpark.esRDD(students, "ent_base_info-2020-06-03", Map("es.mapping.id" -> "ent_id", "es.write.operation" -> "update"))
            //println("[JSON]"+ taskMap.toString())
            //taskMap+=(entId->entStatus)
          }}
          taskMap = getProtectStatusInfo(x)
          println("[JSON]"+taskMap)
          //var students:RDD[]
          //EsSpark.saveToEs(students, "ent_base_info-2020-06-03", Map("es.mapping.id" -> "ent_id", "es.write.operation" -> "update"))
        })


      )
      */
      //println("[RESEND]"+taskMap)
      //EsSpark.saveToEs(sc.parallelize(Seq(LastModifyTime("1_201_73b8193e54b23e1bf3aafabbc14181de",1))), "ent_base_info-2020-06-03", Map("es.mapping.id" -> "ent_id", "es.write.operation" -> "update"))





      for (o <- offsetRanges) {
        val zkPath = s"${topicDirs.consumerOffsetDir}/${o.partition}"
        ZkUtils.updatePersistentPath(zkClient, zkPath, o.untilOffset.toString)
      }
    }

    //开启实时计算
    ssc.start()
    //进入等待优雅退出模式
    ssc.awaitTermination()
  }

  /**
    * 格式化待同步信息
    * */
  def getProtectStatusInfo(taskMsg:String): Map[String, Int] = {
    var dataSourceMap: Map[String, Int] = Map()
    import com.alibaba.fastjson.{JSON, JSONObject}
    val parseObject: JSONArray = JSON.parseArray(taskMsg)
    for(i <- 0 until parseObject.size()){
      //println(i+":"+parseObject.get(i))
      val data: JSONObject = JSON.parseObject(parseObject.get(i).toString)
      //println(i+"[]"+data.get("entId").toString+" : "+data.get("entStatus").toString.toInt)
      dataSourceMap+=(data.get("entId").toString->data.get("entStatus").toString.toInt)
//      dataSourceMap+=("ent_id" -> data.get("entId").toString)
//      dataSourceMap+=("status" -> data.get("entStatus").toString)
    }
    dataSourceMap
  }

  def iteratorFunctionLT(id:String,status:Int): Iterator[LastModifyTime] = {
    val res = ListBuffer[LastModifyTime]()
    res+= LastModifyTime(id, status)
    res.iterator
  }


  case class LastModifyTime(ent_id: String, ent_protect_status: Int) extends  Serializable
}
