package cn.dd.apisys.utils;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * FileName: DataFormat.java
 * CreateTime: 2020/6/28 18:40.
 * Version: V1.0
 * Author: ChengTao
 * Description: 数据转换辅助类
 */
public class DataFormat {
    //定义分区数（为保证离散度，采用可控的大质数）
    private static int regionCount = 23;

    /**
     * 单元测试类
     */
    public static void main(String[] args) {
        System.out.println("[formatRowKey]" + formatRowKey("1307441301_9_20200623"));
    }

    /**
     * 求商（保留两位小数）
     */
    public static double getNum2Point(int num1, int num2) {
        BigDecimal bg = new BigDecimal(Double.valueOf(num1) * 100 / num2);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * 求商（保留两位小数,被除数是小数）
     */
    public static double getNum2PointByDouble(double num1, int num2) {
        BigDecimal bg = new BigDecimal(num1 / num2);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * 求商（保留两位小数,被除数和除数都是小数）
     */
    public static double getNum2PointByDoubles(double num1, double num2) {
        BigDecimal bg = new BigDecimal(num1 / num2);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * 数据格式转换（保留两位小数）
     */
    public static double getNum2PointByString(String val) {
        BigDecimal bg = new BigDecimal(Double.valueOf(val));
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * 字符串生成离散RowKey（供门户翻译状态RowKey生成和解析使用）
     * 解析规则：根据可定长数字部分，截取后诸位求和，然后关于23求模，再加100（用于补齐），再拼接MD5后的原字符串，生成定长35位的
     */
    public static String formatRowKey(String oriKey) {
        String backRes = "";
        String[] keys = oriKey.split("_");
        try {
            int siteCount = 0;
            for (int i = 0; i < keys[0].length(); i++) {
                siteCount += Integer.valueOf(keys[0].charAt(i));
            }
            backRes = (100 + (siteCount % regionCount)) + str2md5(oriKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backRes;
    }

    /**
     * md5加密方法
     */
    public static String str2md5(String plainText) {
        //定义一个字节数组
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(plainText.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        //将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 获取当前时间戳所在的日期
     */
    public static String getDateByLong(long inputTime) {
        String backRes = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            backRes = sdf.format(inputTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backRes;
    }

    /**
     * 地区分布实时数据拼装
     *
     * @parama ipdatas: IP数据，List
     * @parama uiddatas: 用户ID数据，HashMap
     * @parama sidLsit: 设备ID数据，HashMap
     * @parama provinceName: 省份名称（为空时，统计到省维度），String
     */
    public static List<Object> getAreaAnaRes(Set<String> ipdatas, Map<String, String> uiddatas, Map<String, String> siddatas, String provinceName, int searchType) {
        List<Object> backRes = new ArrayList<>();
        if ("".equals(provinceName)) {
            //查看省级维度分布
            //System.out.println("[-----------------------][Province]" + provinceName);
            //各省名称及PV数
            HashMap<String, Integer> pvTemp = new HashMap<>();
            //各省IP个数
            HashMap<String, Integer> ipTemp = new HashMap<>();
            for (String provinceInfo : ipdatas) {
                String proTmp = provinceInfo.split("#-#")[0].toString();
                if (ipTemp.containsKey(proTmp)) {
                    ipTemp.put(proTmp, (ipTemp.get(proTmp) + 1));
                } else {
                    ipTemp.put(proTmp, 1);
                }
            }
            //System.out.println("[Province][IP]" + JSON.toJSONString(ipTemp));
            //各省只访问过一个页面的用户个数
            HashMap<String, Integer> uidOnlyTemp = new HashMap<>();
            //各省用户设备个数
            HashMap<String, Integer> sidTemp = new HashMap<>();
            siddatas.forEach((key, val) -> {
                String proTmp = key.split("#-#")[0].toString();
                if (Integer.parseInt(val.toString()) == 1) {
                    if (uidOnlyTemp.containsKey(proTmp)) {
                        uidOnlyTemp.put(proTmp, (uidOnlyTemp.get(proTmp) + 1));
                    } else {
                        uidOnlyTemp.put(proTmp, 1);
                    }
                }
                if (sidTemp.containsKey(proTmp)) {
                    sidTemp.put(proTmp, (sidTemp.get(proTmp) + 1));
                } else {
                    sidTemp.put(proTmp, 1);
                }
            });
            //各省用户个数
            HashMap<String, Integer> uidTemp = new HashMap<>();
            uiddatas.forEach((key, val) -> {
                String proTmp = key.split("#-#")[0].toString();
                if (uidTemp.containsKey(proTmp)) {
                    pvTemp.put(proTmp, (pvTemp.get(proTmp) + Integer.parseInt(val.toString())));
                    uidTemp.put(proTmp, (uidTemp.get(proTmp) + 1));
                } else {
                    pvTemp.put(proTmp, Integer.parseInt(val.toString()));
                    uidTemp.put(proTmp, 1);
                }
            });
            //System.out.println("[Province][PV]" + JSON.toJSONString(pvTemp));
            //System.out.println("[Province][SV]" + JSON.toJSONString(sidTemp));
            //System.out.println("[Province][UV]" + JSON.toJSONString(uidTemp));
            //System.out.println("[Province][OUV]" + JSON.toJSONString(uidOnlyTemp));

            //省份数据拼装
            pvTemp.forEach((key, val) -> {
                //获取区域名称
                String areaKey = key.toString().trim();
                HashMap<String, Object> temRes = new HashMap<>();
                if (searchType == 1) {
                    //直接输出
                    temRes.put("name", key.toString().trim());//省份名称
                    temRes.put("pv", Integer.parseInt(pvTemp.get(areaKey).toString()));//PV
                    temRes.put("uv", Integer.parseInt(pvTemp.get(areaKey).toString()));//UV
                    //temRes.put("SU", Integer.parseInt(uidTemp.get(areaKey).toString()));//独立访客数
                    temRes.put("newuv", Integer.parseInt(uidTemp.get(areaKey).toString()));//新访客个数
                    temRes.put("ip", Integer.parseInt(sidTemp.get(areaKey).toString()));//独立IP个数
                    temRes.put("session", Integer.parseInt(sidTemp.get(areaKey).toString()));//访问次数
                    if (!uidOnlyTemp.containsKey(areaKey)) {
                        temRes.put("bounceRate", "100%");//跳出率
                        temRes.put("OSU", 0);//只访问过一次的用户数
                    } else {
                        temRes.put("bounceRate", getNum2Point(Integer.parseInt(uidOnlyTemp.get(areaKey).toString()) , Integer.parseInt(sidTemp.get(areaKey).toString()))+"%");//跳出率
                        temRes.put("OSU", Integer.parseInt(uidOnlyTemp.get(areaKey).toString()));//只访问过一次的用户数
                    }
                    temRes.put("avgDepth", getNum2PointByDouble(Double.valueOf(pvTemp.get(areaKey).toString()), Integer.parseInt(sidTemp.get(areaKey).toString())));//访问深度
                    temRes.put("avgPage", getNum2PointByDouble(Double.valueOf(pvTemp.get(areaKey).toString()), Integer.parseInt(uidTemp.get(areaKey).toString())));//人均浏览页数
                    temRes.put("avgTime", 0);//平均访问时长，暂时无法计算，均取0
                } else {
                    temRes.put("AreaName", key.toString().trim());//省份名称
                    temRes.put("PV", Integer.parseInt(pvTemp.get(areaKey).toString()));//PV
                    temRes.put("UV", Integer.parseInt(pvTemp.get(areaKey).toString()));//UV
                    temRes.put("SU", Integer.parseInt(uidTemp.get(areaKey).toString()));//独立访客数
                    temRes.put("NVC", Integer.parseInt(uidTemp.get(areaKey).toString()));//新访客个数
                    temRes.put("IP", Integer.parseInt(sidTemp.get(areaKey).toString()));//独立IP个数
                    temRes.put("VT", Integer.parseInt(sidTemp.get(areaKey).toString()));//访问次数
                    if (!uidOnlyTemp.containsKey(areaKey)) {
                        temRes.put("VTR", 100.00);//跳出率
                        temRes.put("OSU", 0);//只访问过一次的用户数
                    } else {
                        temRes.put("VTR", getNum2Point(Integer.parseInt(uidOnlyTemp.get(areaKey).toString()) * 100, Integer.parseInt(sidTemp.get(areaKey).toString())));//跳出率
                        temRes.put("OSU", Integer.parseInt(uidOnlyTemp.get(areaKey).toString()));//只访问过一次的用户数
                    }
                    temRes.put("VD", getNum2Point(Integer.parseInt(pvTemp.get(areaKey).toString()), Integer.parseInt(sidTemp.get(areaKey).toString())));//访问深度
                    temRes.put("AVP", getNum2Point(Integer.parseInt(pvTemp.get(areaKey).toString()), Integer.parseInt(uidTemp.get(areaKey).toString())));//人均浏览页数
                    temRes.put("VL", 0);//平均访问时长，暂时无法计算，均取0
                }

                backRes.add(temRes);
            });
        } else {
            //查看市级维度分布
            //System.out.println("[-----------------------][City]" + provinceName);
            //相应省名称下城市及PV数
            HashMap<String, Integer> pvTemp = new HashMap<>();
            //指定省份下各城市IP个数
            HashMap<String, Integer> ipTemp = new HashMap<>();
            for (String provinceInfo : ipdatas) {
                String proTmp = provinceInfo.split("#-#")[0].toString();
                String cityTemp = provinceInfo.split("#-#")[1].toString();
                if (proTmp.equals(provinceName)) {
                    if (ipTemp.containsKey(cityTemp)) {
                        ipTemp.put(cityTemp, (ipTemp.get(cityTemp) + 1));
                    } else {
                        ipTemp.put(cityTemp, 1);
                    }
                }
            }
            //System.out.println("[City][IP]" + JSON.toJSONString(ipTemp));
            //指定省份下的城市中只访问过一个页面的用户个数
            HashMap<String, Integer> uidOnlyTemp = new HashMap<>();
            //各省用户设备个数
            HashMap<String, Integer> sidTemp = new HashMap<>();
            siddatas.forEach((key, val) -> {
                String proTmp = key.split("#-#")[0].toString();
                String cityTemp = key.split("#-#")[1].toString();
                if (proTmp.equals(provinceName)) {
                    if (Integer.parseInt(val.toString()) == 1) {
                        if (uidOnlyTemp.containsKey(cityTemp)) {
                            uidOnlyTemp.put(cityTemp, (uidOnlyTemp.get(cityTemp) + 1));
                        } else {
                            uidOnlyTemp.put(cityTemp, 1);
                        }
                    }
                    if (sidTemp.containsKey(cityTemp)) {
                        sidTemp.put(cityTemp, (sidTemp.get(cityTemp) + 1));
                    } else {
                        sidTemp.put(cityTemp, 1);
                    }
                }
            });
            //指定省分下各城市用户个数
            HashMap<String, Integer> uidTemp = new HashMap<>();
            uiddatas.forEach((key, val) -> {
                String proTmp = key.split("#-#")[0].toString();
                String cityTemp = key.split("#-#")[1].toString();
                if (proTmp.equals(provinceName)) {
                    if (uidTemp.containsKey(cityTemp)) {
                        pvTemp.put(cityTemp, (pvTemp.get(cityTemp) + Integer.parseInt(val.toString())));
                        uidTemp.put(cityTemp, (uidTemp.get(cityTemp) + 1));
                    } else {
                        pvTemp.put(cityTemp, Integer.parseInt(val.toString()));
                        uidTemp.put(cityTemp, 1);
                    }
                }
            });
            //System.out.println("[Province][PV]" + JSON.toJSONString(pvTemp));
            //System.out.println("[Province][SV]" + JSON.toJSONString(sidTemp));
            //System.out.println("[Province][UV]" + JSON.toJSONString(uidTemp));
            //System.out.println("[Province][OUV]" + JSON.toJSONString(uidOnlyTemp));

            //省份数据拼装
            pvTemp.forEach((key, val) -> {
                String areaKey = key.toString().trim();
                HashMap<String, Object> temRes = new HashMap<>();
                //获取区域名称
                if (searchType == 1) {
                    //直接输出
                    temRes.put("name", key.toString().trim());//省份名称
                    temRes.put("pv", Integer.parseInt(pvTemp.get(areaKey).toString()));//PV
                    temRes.put("uv", Integer.parseInt(pvTemp.get(areaKey).toString()));//UV
                    //temRes.put("SU", Integer.parseInt(uidTemp.get(areaKey).toString()));//独立访客数
                    temRes.put("newuv", Integer.parseInt(uidTemp.get(areaKey).toString()));//新访客个数
                    temRes.put("ip", Integer.parseInt(sidTemp.get(areaKey).toString()));//独立IP个数
                    temRes.put("session", Integer.parseInt(sidTemp.get(areaKey).toString()));//访问次数
                    if (!uidOnlyTemp.containsKey(areaKey)) {
                        temRes.put("bounceRate", "100%");//跳出率
                        temRes.put("OSU", 0);//只访问过一次的用户数
                    } else {
                        temRes.put("bounceRate", getNum2Point(Integer.parseInt(uidOnlyTemp.get(areaKey).toString()) , Integer.parseInt(sidTemp.get(areaKey).toString()))+"%");//跳出率
                        temRes.put("OSU", Integer.parseInt(uidOnlyTemp.get(areaKey).toString()));//只访问过一次的用户数
                    }
                    temRes.put("avgDepth", getNum2PointByDouble(Double.valueOf(pvTemp.get(areaKey).toString()), Integer.parseInt(sidTemp.get(areaKey).toString())));//访问深度
                    temRes.put("avgPage", getNum2PointByDouble(Double.valueOf(pvTemp.get(areaKey).toString()), Integer.parseInt(uidTemp.get(areaKey).toString())));//人均浏览页数
                    temRes.put("avgTime", 0);//平均访问时长，暂时无法计算，均取0
                } else {
                    temRes.put("AreaName", key.toString().trim());//省份名称
                    temRes.put("PV", Integer.parseInt(pvTemp.get(areaKey).toString()));//PV
                    temRes.put("UV", Integer.parseInt(pvTemp.get(areaKey).toString()));//UV
                    temRes.put("SU", Integer.parseInt(uidTemp.get(areaKey).toString()));//独立访客数
                    temRes.put("NVC", Integer.parseInt(uidTemp.get(areaKey).toString()));//新访客个数
                    temRes.put("IP", Integer.parseInt(sidTemp.get(areaKey).toString()));//独立IP个数
                    temRes.put("VT", Integer.parseInt(sidTemp.get(areaKey).toString()));//访问次数
                    if (!uidOnlyTemp.containsKey(areaKey)) {
                        temRes.put("VTR", 100.00);//跳出率
                        temRes.put("OSU", 0);//只访问过一次的用户数
                    } else {
                        temRes.put("VTR", getNum2Point(Integer.parseInt(uidOnlyTemp.get(areaKey).toString()) * 100, Integer.parseInt(sidTemp.get(areaKey).toString())));//跳出率
                        temRes.put("OSU", Integer.parseInt(uidOnlyTemp.get(areaKey).toString()));//只访问过一次的用户数
                    }
                    temRes.put("VD", getNum2Point(Integer.parseInt(pvTemp.get(areaKey).toString()), Integer.parseInt(sidTemp.get(areaKey).toString())));//访问深度
                    temRes.put("AVP", getNum2Point(Integer.parseInt(pvTemp.get(areaKey).toString()), Integer.parseInt(uidTemp.get(areaKey).toString())));//人均浏览页数
                    temRes.put("VL", 0);//平均访问时长，暂时无法计算，均取0

                }
                backRes.add(temRes);
            });
        }
        return backRes;
    }

}
