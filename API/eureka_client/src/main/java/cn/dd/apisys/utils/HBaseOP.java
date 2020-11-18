package cn.dd.apisys.utils;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ChengTao on 2018/5/14.
 */
public class HBaseOP {
    private static Configuration configuration;
    private static Connection conn;
    private static HashMap<String, String> _conf;
    private static HBaseOP hBaseOP;

    /**
     * 创建连接池并初始化环境配置
     */
    public void init() {
        //实例化HBase配置类
        if (configuration == null) {
            configuration = HBaseConfiguration.create();
        }
        try {
            configuration.set("zookeeper.znode.parent", "/hbase-unsecure");
            //zookeeper集群的URL配置信息
            //configuration.set("hbase.zookeeper.quorum", "saiserver01,saiserver02,saiserver03");//测试环境
            configuration.set("hbase.zookeeper.quorum", "qhbd01,qhbd02,qhbd03,qhbd04");//线上环境
            //客户端连接zookeeper端口
            configuration.set("hbase.zookeeper.property.clientPort", "2181");
            //HBase RPC请求超时时间，默认60s(60000)
            configuration.setInt("hbase.rpc.timeout", 3000);
            //客户端重试最大次数，默认35
            configuration.setInt("hbase.client.retries.number", 3);
            //客户端发起一次操作数据请求直至得到响应之间的总超时时间，可能包含多个RPC请求，默认为2min
            configuration.setInt("hbase.client.operation.timeout", 3000);
            //客户端发起一次scan操作的rpc调用至得到响应之间的总超时时间
            configuration.setInt("hbase.client.scanner.timeout.period", 3000);
            //获取hbase连接对象
            if (conn == null || conn.isClosed()) {
                conn = ConnectionFactory.createConnection(configuration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接池
     */
    public static void close() {
        try {
            if (conn != null) conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 私有无参构造方法
     */
    private HBaseOP() {
    }
    /**
     * 数据写入
     */
    public static void infoWrite(String tableName,String rowKey,String vals){
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes("transStatus"), Bytes.toBytes("ver1"), Bytes.toBytes(vals));
            table.put(put);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 数据写入（多字段）
     */
    public static void infoMutilWrite(String tableName,String rowKey,String familyName,HashMap<String,String> infoData){
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Put put = new Put(Bytes.toBytes(rowKey));
            infoData.forEach((key,val)->{
                if(val != null && val.length()>0){
                    put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(key), Bytes.toBytes(val));
                }
            });
            table.put(put);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 数据查询V2（根据RowKey查询）
     */
    public static HashMap<String,Object> infoSearchByRowKey(String tableName,String rowKey){
        HashMap<String,Object> backRes = new HashMap<>();
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Get get = new Get(rowKey.getBytes()); //根据主键查询
            Result res = table.get(get);
            for (KeyValue kv : res.raw()) {
                //backRes = Bytes.toString(CellUtil.cloneValue(kv));
                //System.out.println("["+Bytes.toString(CellUtil.cloneQualifier(kv))+"]"+Bytes.toString(CellUtil.cloneValue(kv)));
                backRes.put(Bytes.toString(CellUtil.cloneQualifier(kv)),Bytes.toString(CellUtil.cloneValue(kv)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return backRes;
    }
    /**
     * 数据查询V2（根据RowKey列表查询）
     */
    public static HashMap<String,Object> infoSearchByRowKeyList(String tableName,List<String> rowkeyList){
        HashMap<String,Object> backRes = new HashMap<>();
        List<Get> getList = new ArrayList();
        HashMap<String,String> keyRel = new HashMap<>();
        for (String rowkey : rowkeyList) {
            Get get = new Get(Bytes.toBytes(DataFormat.formatRowKey(rowkey.trim())));
            getList.add(get);
            keyRel.put(DataFormat.formatRowKey(rowkey.trim()),rowkey.trim());
        }
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Result res[] = table.get(getList);
            for (Result result : res) {
                for (Cell kv : result.rawCells()) {
                    HashMap<String,Object> tempData = new HashMap<>();
                    for (KeyValue ki : result.raw()){
                        tempData.put(Bytes.toString(CellUtil.cloneQualifier(ki)),Bytes.toString(CellUtil.cloneValue(ki)));
                    }
                    backRes.put(keyRel.get(Bytes.toString(CellUtil.cloneRow(kv))),tempData);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return backRes;
    }

    /**
     * 数据查询（根据RowKey查询）
     */
    public static String infoSearch(String tableName,String rowKey){
        String backRes = "-";
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Get get = new Get(rowKey.getBytes()); //根据主键查询
            Result res = table.get(get);
            for (KeyValue kv : res.raw()) {
                backRes = Bytes.toString(CellUtil.cloneValue(kv));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return backRes;
    }
    /**
     * 数据查询（根据RowKey列表查询）
     */
    public static HashMap<String,String> infoPSearch(String tableName,List<String> rowkeyList){
        HashMap<String,String> backRes = new HashMap<>();
        List<Get> getList = new ArrayList();
        HashMap<String,String> keyRel = new HashMap<>();
        for (String rowkey : rowkeyList) {
            Get get = new Get(Bytes.toBytes(DataFormat.formatRowKey(rowkey.trim())));
            getList.add(get);
            keyRel.put(DataFormat.formatRowKey(rowkey.trim()),rowkey.trim());
        }
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Result res[] = table.get(getList);
            for (Result result : res) {
                for (Cell kv : result.rawCells()) {
                    backRes.put(keyRel.get(Bytes.toString(CellUtil.cloneRow(kv))),Bytes.toString(CellUtil.cloneValue(kv)));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return backRes;
    }

    /**
     * 唯一实例，线程安全，保证连接池唯一
     *
     * @return
     */
    public static HBaseOP getInstance() {
        if (hBaseOP == null) {
            synchronized (HBaseOP.class) {
                if (hBaseOP == null) {
                    hBaseOP = new HBaseOP();
                    hBaseOP.init();
                }
            }
        }
        return hBaseOP;
    }
/*

    public HBaseOP(Object conf) {
        _conf = (HashMap<String, String>) conf;
        HashMap<String, Object> hbaseConnMap = new HashMap<>();

        if (null == conn) {
            HBaseOP.conf = HBaseConfiguration.create();
            HBaseOP.conf.set("hbase.zookeeper.quorum", _conf.get("serverHost").toString());
            HBaseOP.conf.set("hbase.zookeeper.property.clientPort", _conf.get("serverPort"));
            HBaseOP.conf.set("zookeeper.znode.parent", _conf.get("zkParent"));
            HBaseOP.conf.setLong("hbase.client.scanner.timeout.period", Integer.parseInt(_conf.get("clientTimeout")));

            HBaseOP.conf.set("hbase.client.pause", "100");
            HBaseOP.conf.set("hbase.client.retries.number", "3");
            HBaseOP.conf.set("hbase.rpc.timeout", "10000");
            HBaseOP.conf.set("hbase.client.operation.timeout", "3000");

            try {
                conn = HConnectionManager.createConnection(HBaseOP.conf);
                System.err.println("Create HBase Connection");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    */

    /**
     * 获取各时段范围内统计信息(定时任务汇总数据专用)
     */
    public static HashMap<String, List> getDataByRowKeyTimePartList(String tableName, List<String> rowkeyList, HashMap<String, Integer> partMap) {
        HashMap<String, List> backRes = new HashMap<>();
        try {
            List<Get> getList = new ArrayList();
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            for (String rowkey : rowkeyList) {
                Get get = new Get(Bytes.toBytes(rowkey));
                getList.add(get);
            }
            Result[] results = table.get(getList);//重点在这，直接查getList<Get>
            for (Result result : results) {//对返回的结果集进行操作
                for (Cell kv : result.rawCells()) {
                    String value = Bytes.toString(CellUtil.cloneValue(kv)) + "|" + Bytes.toString(CellUtil.cloneFamily(kv));
                    String siteKey = Bytes.toString(CellUtil.cloneRow(kv)).substring(8).replace("http://", "");
                    List<String> siteValue = new ArrayList<>();
                    if (partMap.containsKey(Bytes.toString(CellUtil.cloneQualifier(kv)))) {
                        if (backRes.containsKey(siteKey)) {
                            siteValue = backRes.get(siteKey);
                        }
                        siteValue.add(value);
                        backRes.put(siteKey, siteValue);
                    }
                }
            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return backRes;
    }

    /**
     * 根据RowKeyList进行数据检索
     */
    public static HashMap<String, Long> getInfoByRowKeyList(String tablename, List<String> rowkeyList) {
        HashMap<String, Long> backRes = new HashMap<>();
        try {
            Table table = conn.getTable(TableName.valueOf(tablename));// 获取表
            List<Get> getList = new ArrayList();
            for (String rowkey : rowkeyList) {
                Get get = new Get(Bytes.toBytes(rowkey));
                getList.add(get);
            }

            Result[] results = table.get(getList);//重点在这，直接查getList<Get>
            for (Result result : results) {//对返回的结果集进行操作
                for (Cell kv : result.rawCells()) {
                    long valuek = 0;
                    try {
                        valuek = Bytes.toLong(CellUtil.cloneValue(kv));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String mapKey = Bytes.toString(CellUtil.cloneRow(kv)) + "#" + Bytes.toString(CellUtil.cloneFamily(kv)) + "#" + Bytes.toString(CellUtil.cloneQualifier(kv));
                    if (!backRes.containsKey(mapKey)) {
                        backRes.put(mapKey, valuek);
                    } else {
                        backRes.put(mapKey, backRes.get(mapKey) + valuek);
                    }
                    //System.out.println("[KEY]" + Bytes.toString(CellUtil.cloneRow(kv)) + "[VAL]" + valuek+"[SP]"+Bytes.toString(CellUtil.cloneFamily(kv))+"[TIM]"+Bytes.toString(CellUtil.cloneQualifier(kv)));
                }
            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backRes;
    }

    /**
     * 根据Row获取网站数据
     */
    public static HashMap<String, HashMap<String, Long>> getVisitDetailByRowKey(String tableName, String rowKey) {
        HashMap<String, HashMap<String, Long>> backRes = new HashMap<>();
        try {
            System.out.println("TableName: " + tableName);
            System.out.println("RowKey: " + rowKey);
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Get get = new Get(rowKey.getBytes()); //根据主键查询
            Result r = table.get(get);
            for (KeyValue kv : r.raw()) {
                //System.out.println("KeyValue: "+kv);
                //System.out.println("key: "+kv.getKeyString());
                String mapKey = Bytes.toString(CellUtil.cloneRow(kv)) + "#" + Bytes.toString(CellUtil.cloneFamily(kv)) + "#" + Bytes.toString(CellUtil.cloneQualifier(kv));
                long valuek = 0;
                try {
                    valuek = Bytes.toLong(CellUtil.cloneValue(kv));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("mapKey: "+mapKey);
                //System.out.println("valuek: "+valuek);
                String resMapKey = Bytes.toString(CellUtil.cloneFamily(kv));
                if (!backRes.containsKey(resMapKey)) {
                    HashMap<String, Long> temp = new HashMap<>();
                    temp.put(Bytes.toString(CellUtil.cloneQualifier(kv)), Long.valueOf(valuek));
                    backRes.put(resMapKey, temp);
                } else {
                    HashMap<String, Long> temp = backRes.get(resMapKey);
                    temp.put(Bytes.toString(CellUtil.cloneQualifier(kv)), Long.valueOf(valuek));
                    backRes.put(resMapKey, temp);

                }
            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backRes;
    }

    /**
     * 根据ROWKEY获取访问城市
     */
    public static HashMap<String, Long> getVisitCityByRowKeyList(String tablename, List<String> rowkeyList) {
        HashMap<String, Long> backRes = new HashMap<>();
        try {
            Table table = conn.getTable(TableName.valueOf(tablename));// 获取表
            List<Get> getList = new ArrayList();
            for (String rowkey : rowkeyList) {
                Get get = new Get(Bytes.toBytes(rowkey));
                getList.add(get);
            }

            Result[] results = table.get(getList);//重点在这，直接查getList<Get>
            for (Result result : results) {//对返回的结果集进行操作
                for (Cell kv : result.rawCells()) {
                    long valuek = 0;
                    try {
                        valuek = Bytes.toLong(CellUtil.cloneValue(kv));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //String mapKey = Bytes.toString(CellUtil.cloneRow(kv))+"#"+Bytes.toString(CellUtil.cloneFamily(kv))+"#"+Bytes.toString(CellUtil.cloneQualifier(kv));
                    String mapKey = Bytes.toString(CellUtil.cloneQualifier(kv));
                    if ("pv".equals(Bytes.toString(CellUtil.cloneFamily(kv)))) {
                        if (!backRes.containsKey(mapKey)) {
                            backRes.put(mapKey, valuek);
                        } else {
                            backRes.put(mapKey, backRes.get(mapKey) + valuek);
                        }
                    }
                    //System.out.println("[KEY]" + Bytes.toString(CellUtil.cloneRow(kv)) + "[VAL]" + valuek+"[SP]"+Bytes.toString(CellUtil.cloneFamily(kv))+"[TIM]"+Bytes.toString(CellUtil.cloneQualifier(kv)));
                }
            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backRes;
    }

    /**
     * 根据ROWKEY获取访问数据明细（数据统计使用）
     */
    public static List<String> getVisitDetailByRowKeyList(String tablename, List<String> rowkeyList) {
        List<String> backRes = new ArrayList<>();
        try {
            Table table = conn.getTable(TableName.valueOf(tablename));// 获取表
            List<Get> getList = new ArrayList();
            for (String rowkey : rowkeyList) {
                Get get = new Get(Bytes.toBytes(rowkey));
                getList.add(get);
            }
            Result[] results = table.get(getList);//重点在这，直接查getList<Get>
            for (Result result : results) {//对返回的结果集进行操作
                for (Cell kv : result.rawCells()) {
                    long valuek = 0;
                    try {
                        valuek = Bytes.toLong(CellUtil.cloneValue(kv));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String mapKey = Bytes.toString(CellUtil.cloneRow(kv)) + "#" + Bytes.toString(CellUtil.cloneFamily(kv)) + "#" + Bytes.toString(CellUtil.cloneQualifier(kv));
                    //String mapKey = Bytes.toString(CellUtil.cloneQualifier(kv));
                    //System.out.println("[KEY]" + mapKey + "[VAL]" + valuek);
                    backRes.add(mapKey + "#" + valuek);
                }
            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backRes;
    }

    /**
     * 根据ROWKEY汇总数据（数据统计使用）
     */
    public static void updateDailyData(String tablename, List<String> dataList) {
        try {
            Table table = conn.getTable(TableName.valueOf(tablename));// 获取表
            for (String item : dataList) {
                String[] tempArr = item.split("#");
                String rowKey = tempArr[0].substring(0, 8) + tempArr[0].substring(11);
                String familyKey = tempArr[1].trim();
                String columnKey = tempArr[2].trim();
                long val = Integer.parseInt(tempArr[3]);
                Increment increment2 = new Increment(Bytes.toBytes(rowKey));
                increment2.addColumn(Bytes.toBytes(familyKey), Bytes.toBytes(columnKey), val);
                table.increment(increment2);
            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定统计项统计信息(定时任务汇总数据专用)
     */
    public static List<HashMap<String, HashMap<String, String>>> getDataByRowKeyCaseList(String tableName, List<String> rowkeyList, String graphTypeStr, int dateShowType) {
        List<HashMap<String, HashMap<String, String>>> backRes = new ArrayList<>();
        try {
            List<Get> getList = new ArrayList();
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            for (String rowkey : rowkeyList) {
                Get get = new Get(Bytes.toBytes(rowkey));
                getList.add(get);
            }
            Result[] results = table.get(getList);//重点在这，直接查getList<Get>
            for (Result result : results) {//对返回的结果集进行操作
                HashMap<String, HashMap<String, String>> temp = new HashMap<>();
                for (Cell kv : result.rawCells()) {
                    String mapKey = Bytes.toString(CellUtil.cloneRow(kv)) + "#" + Bytes.toString(CellUtil.cloneFamily(kv)) + "#" + Bytes.toString(CellUtil.cloneQualifier(kv));
                    long valuek = 0;
                    try {
                        valuek = Bytes.toLong(CellUtil.cloneValue(kv));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String dateKey = Bytes.toString(CellUtil.cloneRow(kv)).substring(0, 8);
                    if (dateShowType == 2) {
                        dateKey = Bytes.toString(CellUtil.cloneRow(kv)).substring(0, 10);
                    }
                    String colKey = Bytes.toString(CellUtil.cloneFamily(kv));
                    String cityKey = colKey + Bytes.toString(CellUtil.cloneQualifier(kv));
                    //if(graphTypeStr.equals(colKey)){
                    if (!temp.containsKey(dateKey)) {
                        HashMap<String, String> tempSon = new HashMap<>();
                        tempSon.put(cityKey, "" + valuek);
                        temp.put(dateKey, tempSon);
                    } else {
                        HashMap<String, String> tempSon = temp.get(dateKey);
                        tempSon.put(cityKey, "" + valuek);
                        temp.put(dateKey, tempSon);
                    }
                    //}
                    //System.out.println("WWWWWW"+mapKey);
                    //System.out.println("WWWWWW"+valuek);
                }
                if (!temp.isEmpty()) {
                    backRes.add(temp);
                }
            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return backRes;
    }


    /**
     * 获取异常网站
     */
    public static String getErrorSite(String tableName, String rowKey) {
        String backRes = "";
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Get get = new Get(rowKey.getBytes()); //根据主键查询
            Result r = table.get(get);
            int errorCount = 0;
            HashMap<String, Integer> temp = new HashMap<>();
            HashMap<String, String> tempSp = new HashMap<>();
            for (KeyValue kv : r.raw()) {
                //System.out.println("KeyValue: "+kv);
                //System.out.println("key: "+kv.getKeyString());

                String mapKey = Bytes.toString(CellUtil.cloneRow(kv)) + "#" + Bytes.toString(CellUtil.cloneFamily(kv)) + "#" + Bytes.toString(CellUtil.cloneQualifier(kv));
                String mapVal = Bytes.toString(CellUtil.cloneValue(kv)).split("\\|")[0];
                //System.out.println("mapKey: " + mapKey);
                //System.out.println("valuek: " + mapVal);

                if (Integer.parseInt(mapVal) >= 403 || Integer.parseInt(mapVal) <200) {
                    errorCount++;
                    if (!temp.containsKey(mapVal)) {
                        temp.put(mapVal, 1);
                    } else {
                        temp.put(mapVal, temp.get(mapVal) + 1);
                    }
                    if (!tempSp.containsKey(mapKey)) {
                        tempSp.put(mapVal, mapKey.split("#")[1] + "#" + Bytes.toString(CellUtil.cloneValue(kv)).split("\\|")[6] + "#" + mapVal);
                    } else {
                        String tempVal = tempSp.get(mapVal);
                        tempSp.put(mapVal, mapKey.split("#")[1] + "#" + Bytes.toString(CellUtil.cloneValue(kv)).split("\\|")[6] + "#" + mapVal + "," + tempVal);
                    }
                    //tempSp.put(mapVal,mapKey.split("#")[1]+"#"+Bytes.toString(CellUtil.cloneValue(kv)).split("\\|")[6]);
                }


            }
            //System.out.println(JSON.toJSONString(temp));
        /*
        if(errorCount<4){
            backRes = false;
        }
        */
            for (Map.Entry<String, Integer> entry : temp.entrySet()) {
                if (entry.getValue() > 4) {
                    backRes += "," + tempSp.get(entry.getKey());
                }
            }
            System.out.println(backRes);
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return backRes;
    }

    /**
     * 根据RowKey的范围查询HBase数据
     */
    public static List<String> getSiteTaskByRowKeyRange(String tableName, String startRow, String stopRow) {
        List<String> backRes = new ArrayList<>();
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(startRow));
            scan.setStopRow(Bytes.toBytes(stopRow));
            scan.setReversed(false);
            ResultScanner scanner = table.getScanner(scan);
            //int i = 0;
            for (Result result : scanner) {
                HashMap<String, String> itemMap = new HashMap<>();//主机监控项临时Map
                byte[] row = result.getRow();
                String dateValue = new String(row);//获取采集日期
                //System.out.println(dateValue);
                //itemMap.put(dateValue,);
                /*
                if(i<4000){
                    backRes.add(dateValue);
                }
                i++;
                */
                backRes.add(dateValue);
            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backRes;
    }

    /**
     * 根据Row获取网站采集数据
     */
    public static List<String> getSpiderDetailByRowKey(String tableName, String rowKey) {
        List<String> backRes = new ArrayList<>();
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Get get = new Get(rowKey.getBytes()); //根据主键查询
            Result r = table.get(get);
            for (KeyValue kv : r.raw()) {
                String mapKey = Bytes.toString(CellUtil.cloneRow(kv)) + "#" + Bytes.toString(CellUtil.cloneFamily(kv)) + "#" + Bytes.toString(CellUtil.cloneQualifier(kv));
                String valuek = Bytes.toString(CellUtil.cloneValue(kv));
                //System.out.println("mapKey: "+mapKey);
                //System.out.println("valuek: "+valuek);
                String itemVal = Bytes.toString(CellUtil.cloneFamily(kv)) + "#" + Bytes.toString(CellUtil.cloneQualifier(kv)) + "#" + valuek.split("\\|")[0] + "#" + valuek.split("\\|")[5];
                backRes.add(itemVal);
            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backRes;
    }

    /**
     * 根据RowKeyList获取当月的Sla平均值
     */
    public static String getSlaMonthlyBySite(String tableName, List<String> rowkeyList) {
        String backRes = "-";
        List<Get> getList = new ArrayList();
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            for (String rowkey : rowkeyList) {
                Get get = new Get(Bytes.toBytes(rowkey));
                getList.add(get);
            }
            Result[] results = table.get(getList);//重点在这，直接查getList<Get>
            int infoAllCount = 0;
            int infoOkCount = 0;
            for (Result result : results) {//对返回的结果集进行操作
                for (Cell kv : result.rawCells()) {
                    String mapKey = Bytes.toString(CellUtil.cloneRow(kv)) + "#" + Bytes.toString(CellUtil.cloneFamily(kv)) + "#" + Bytes.toString(CellUtil.cloneQualifier(kv));
                    String value = Bytes.toString(CellUtil.cloneValue(kv));
                    int codeTemp = Integer.parseInt(value.trim().split("\\|")[0]);
                    if (codeTemp < 303) {
                        infoOkCount = infoOkCount + 1;
                    }
                    infoAllCount = infoAllCount + 1;
                    //System.out.println("[KEY]"+mapKey+"[VAL]"+value);
                }
            }
            try {
                backRes = "" + new BigDecimal((float) infoOkCount * 100 / (float) infoAllCount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            } catch (Exception e) {

            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backRes;
    }
    /**
     * 根据域名和时间范围获取采集结果
     * */
    public static List<String> qurryDataBatch(String tableName,List<String> rowkeyList)  {
        List<String> backRes  = new ArrayList<>();
        List<Get> getList = new ArrayList();
        try{
            Table table = conn.getTable( TableName.valueOf(tableName));// 获取表
            for (String rowkey : rowkeyList){//把rowkey加到get里，再把get装到list中
                Get get = new Get(Bytes.toBytes(rowkey));
                getList.add(get);
            }
            Result[] results = table.get(getList);//重点在这，直接查getList<Get>
            for (Result result : results){//对返回的结果集进行操作
                for (Cell kv : result.rawCells()) {
                    String value = Bytes.toString(CellUtil.cloneValue(kv));
                    String backValue = Bytes.toString(CellUtil.cloneRow(kv)).substring(0,8)+"|"+Bytes.toString(CellUtil.cloneFamily(kv))+"|"+Bytes.toString(CellUtil.cloneQualifier(kv))+"|"+value;
                    //System.out.println("[KEY]"+Bytes.toString(CellUtil.cloneRow(kv))+"[VAL]"+value);
                    System.out.println("[KEY]"+backValue);
                    backRes.add(backValue);
                }
            }
            table.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        //this.conn.close();
        return backRes;
    }

    /**
     * 根据Row获取网站采集数据
     */
    public static List<String> getSpiderAllDetailByRowKey(String tableName, String rowKey) {
        List<String> backRes = new ArrayList<>();
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));// 获取表
            Get get = new Get(rowKey.getBytes()); //根据主键查询
            Result r = table.get(get);
            for (KeyValue kv : r.raw()) {
                String mapKey = Bytes.toString(CellUtil.cloneRow(kv)) + "#" + Bytes.toString(CellUtil.cloneFamily(kv)) + "#" + Bytes.toString(CellUtil.cloneQualifier(kv));
                String valuek = Bytes.toString(CellUtil.cloneValue(kv));
                //System.out.println("mapKey: "+mapKey);
                //System.out.println("valuek: "+valuek);
                String itemVal = Bytes.toString(CellUtil.cloneFamily(kv)) + "#" + Bytes.toString(CellUtil.cloneQualifier(kv)) + "#" + valuek;
                backRes.add(itemVal);
            }
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backRes;
    }


}
