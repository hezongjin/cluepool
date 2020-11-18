package cn.dd.apisys.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangHao
 * @version 1.0.0.1
 * @description: ongodb 连接数据库工具类2.6版本
 * @date 2019/7/2 15:27
 */
public class MongoDBUtil {


    //需要密码认证方式连接操作2.6版本
    public static MongoClient getConnect() {
        PropertyUtil propertyUtil = new PropertyUtil();
        //获取mongo地址
        String url = "bj-zw-boss-master-mongo-node1.online.local,bj-zw-boss-slave-mongo-node1.online.local";//propertyUtil.getValueByKey("conf/mongo.properties", "url");
        String port = "27018";//propertyUtil.getValueByKey("conf/mongo.properties", "port");
        String username = "monbossxg";//propertyUtil.getValueByKey("conf/mongo.properties", "username");
        String dbname = "MONCMA";//propertyUtil.getValueByKey("conf/mongo.properties", "dbname");
        String password = "bossxg@11194";//propertyUtil.getValueByKey("conf/mongo.properties", "password");

        String[] urlArr = url.split(",");
        List<ServerAddress> adds = new ArrayList<>();
        for (int i = 0; i < urlArr.length; i++) {
            //ServerAddress()两个参数分别为 服务器地址 和 端口
            ServerAddress serverAddress = null;
            try {
                serverAddress = new ServerAddress(urlArr[i], Integer.valueOf(port));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            adds.add(serverAddress);
        }
        List<MongoCredential> credentials = new ArrayList<>();
        MongoCredential mongoCredential = MongoCredential.createMongoCRCredential(username, dbname, password.toCharArray());
        credentials.add(mongoCredential);

        //通过连接认证获取MongoDB连接
        MongoClient mongoClient = new MongoClient(adds, credentials);
        //连接到数据库
        //DB db = mongoClient.getDB(dbname);

        //返回连接数据库对象
        return mongoClient;
    }

    /**
     * 使用完销毁数据库连接
     */
    public static void globalDestroy(MongoClient mongoClient) {
        mongoClient.close();
    }


}
