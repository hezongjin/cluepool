package cn.dd.apisys.utils;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileName: ElasticSearchPoolUtil.java
 * CreateTime: 2020/6/3 17:12.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class ElasticSearchPoolUtil {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchPoolUtil.class);

    // 对象池配置类，不写也可以，采用默认配置
    private static GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
    // 要池化的对象的工厂类，这个是我们要实现的类
    private static EsClientPoolFactory esClientPoolFactory = new EsClientPoolFactory();
    // 利用对象工厂类和配置类生成对象池
    private static GenericObjectPool<RestHighLevelClient> clientPool = new GenericObjectPool<>(esClientPoolFactory, poolConfig);

    // 采用默认配置maxTotal是8，池中有8个client
    {
        PropertyUtil propertyUtil = new PropertyUtil();
        //获取同一时刻可分配最大连接数：默认值 8 ，设置为负数时不做限制
        String maxTotal = "8";//propertyUtil.getValueByKey("conf/elasticSearch.properties", "elasticSeatch.maxTotal");
        //最大空闲连接，默认值 8 ，超出连接将被释放
        String maxIdle = "8";//propertyUtil.getValueByKey("conf/elasticSearch.properties", "elasticSeatch.maxIdle");
        //最小空闲连接数，默认值 0
        String minIdle = "0";//propertyUtil.getValueByKey("conf/elasticSearch.properties", "elasticSeatch.minIdle");

        poolConfig.setMaxTotal(Integer.valueOf(maxTotal));
        poolConfig.setMaxIdle(Integer.valueOf(maxIdle));
        poolConfig.setMinIdle(Integer.valueOf(minIdle));
    }

    /**
     * 获得对象
     *
     * @return
     * @throws Exception
     */
    public static RestHighLevelClient getClient() {
        // 从池中取一个对象
        RestHighLevelClient client = null;
        try {
            client = clientPool.borrowObject();
        } catch (Exception e) {
            logger.error("从对象池中获取ES实例异常", e);
        }
        return client;
    }

    /**
     * 归还对象
     *
     * @param client
     */
    public static void returnClient(RestHighLevelClient client) {
        // 使用完毕之后，归还对象
        clientPool.returnObject(client);
    }

}