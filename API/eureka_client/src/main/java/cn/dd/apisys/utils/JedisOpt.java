package cn.dd.apisys.utils;

import com.alibaba.fastjson.JSON;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
/**
 * FileName: JedisOpt.java
 * CreateTime: 2020/7/24 10:41.
 * Version: V1.0
 * Author: ChengTao
 * Description: Redis操作辅助类
 */
public class JedisOpt {
    private JedisCluster jedis;
    /**
     * 创建Redis连接
     * */
    public void connectRedis(HashMap<String, Object> conf) {
        //String hostList = "10.20.252.120,10.20.252.123,10.20.252.52,10.20.252.127,10.20.252.51,10.20.252.128";//online
        String hostList = "10.12.40.251,10.12.40.252,10.12.40.253";//dev
        int port = 7000;
        Set<HostAndPort> hostAndPortSet = new HashSet<>();
        for (String item : conf.get("ip").toString().split(",")) {
            HostAndPort hostAndPort = new HostAndPort(item, Integer.parseInt(conf.get("port").toString()));
            hostAndPortSet.add(hostAndPort);
        }
        //this.jedis = new JedisCluster(hostAndPortSet,300);

        // Jedis连接池配置
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲连接数, 默认8个
        jedisPoolConfig.setMaxIdle(Integer.parseInt(conf.get("maxIdle").toString()));
        // 最大连接数, 默认8个
        jedisPoolConfig.setMaxTotal(Integer.parseInt(conf.get("maxTotal").toString()));
        //最小空闲连接数, 默认0
        jedisPoolConfig.setMinIdle(Integer.parseInt(conf.get("minIdle").toString()));
        // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(conf.get("maxWait").toString())); // 设置2秒
        //对拿到的connection进行validateObject校验
        jedisPoolConfig.setTestOnBorrow(true);
        this.jedis = new JedisCluster(hostAndPortSet, jedisPoolConfig);
    }
    /**
     * hgetall
     */
    public Map<String, String> hgetAll(String key) {
        Map<String, String> backRes = new HashMap<>();
        try {
            backRes = this.jedis.hgetAll(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return backRes;
    }
    /**
     * get
     * */
    public String get(String key) {
        String backRes = "";
        try {
            backRes = this.jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return backRes;
    }
    /**
     * Smembers
     * */
    public Set<String> sMembers(String key) {
        Set<String> backRes = new TreeSet<>();
        try {
            backRes = this.jedis.smembers(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return backRes;
    }
    /**
     * Sismember
     * */
    public boolean SisMember(String key,String field){
        boolean backRes = false;
        try{
            backRes = this.jedis.sismember(key,field);
        }catch (Exception e){
            e.printStackTrace();
        }
        return backRes;
    }
    /**
     * 关闭链接
     * */
    public void closeDB(){
        try {
            this.jedis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
