package utils

import com.alibaba.fastjson.JSONObject
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients

/**
  * FileName: HttpUtils.java
  * CreateTime: 2020/6/24 17:28.
  * Version: V1.0
  * Author: ChengTao
  * Description: Http请求类
  */
object HttpUtils {
  def main(args: Array[String]): Unit = {
    posetUpdateData2ES("1_201_02cf676d235d86cec1c8174b3d8ee573",7)
  }

  def posetUpdateData2ES(entId:String,entStatus:Int): Unit ={
    val client = HttpClients.createDefault()
    //val post: HttpPost = new HttpPost("http://10.12.52.174:9200/ent_base_info-2020-06-03/_doc/"+entId+"/_update")
    val post: HttpPost = new HttpPost("http://10.20.124.146:9200/ent_base_info/_doc/"+entId+"/_update")
    //设置提交参数为application/json
    post.addHeader("Content-Type", "application/json")
    val exData = "{\"doc\" : {\"ent_protect_status\":"+entStatus+"}}"
    post.setEntity(new StringEntity(exData))
    //执行请求
    client.execute(post)
    client.close()
  }
}
