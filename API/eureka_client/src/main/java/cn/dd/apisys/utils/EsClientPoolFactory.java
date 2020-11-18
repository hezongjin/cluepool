package cn.dd.apisys.utils;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

/**
 * FileName: EsClientPoolFactory.java
 * CreateTime: 2020/6/3 17:15.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EsClientPoolFactory implements PooledObjectFactory<RestHighLevelClient> {

    private static final Logger logger = LoggerFactory.getLogger(EsClientPoolFactory.class);

    @Override
    public void activateObject(PooledObject<RestHighLevelClient> arg0) throws Exception {
        // logger.info("进入activateObject");
    }

    /**
     * 销毁对象
     */
    @Override
    public void destroyObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
        RestHighLevelClient highLevelClient = pooledObject.getObject();
        highLevelClient.close();
    }

    /**
     * 生产对象
     */
    @Override
    public PooledObject<RestHighLevelClient> makeObject() throws Exception {
        // Settings settings = Settings.builder().put("cluster.name","elasticsearch").build();
        RestHighLevelClient client = null;
        try {
            // client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
            PropertyUtil propertyUtil = new PropertyUtil();
            //获取集群地址
            //String address = propertyUtil.getValueByKey("conf/elasticSearch.properties", "elasticSeatch.address");
            //HttpHost[] hosts = new HttpHost[1];
            //hosts[0] = new HttpHost("10.20.124.146", 9200, "http");
            //client = new RestHighLevelClient(RestClient.builder(hosts));
            //String address = "10.12.52.174:9200,10.12.52.175:9200,10.12.52.176:9200";
            String address = "10.20.124.146:9200,10.20.124.147:9200,10.20.124.148:9200";
            if (!"".equals(address) && address != null) {
                String addressArr[] = address.split(",");
                HttpHost[] hosts = new HttpHost[addressArr.length];

                for (int i = 0; i < addressArr.length; i++) {
                    String hostArr[] = addressArr[i].split(":");
                    hosts[i] = new HttpHost(hostArr[0], Integer.valueOf(hostArr[1]), "http");
                }

                //RestHighLevelClient实例需要低级客户端构建器来构建
                client = new RestHighLevelClient(RestClient.builder(hosts));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultPooledObject<RestHighLevelClient>(client);
    }

    @Override
    public void passivateObject(PooledObject<RestHighLevelClient> arg0) throws Exception {
        // logger.info("进入passivateObject");
    }

    @Override
    public boolean validateObject(PooledObject<RestHighLevelClient> arg0) {
        return true;
    }

}