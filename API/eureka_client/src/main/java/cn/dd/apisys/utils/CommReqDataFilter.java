package cn.dd.apisys.utils;

import cn.dd.apisys.comm.entity.TranslatePSearchEntity;
import cn.dd.apisys.comm.entity.TranslateTaskMainEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/**
 * FileName: CommReqDataFilter.java
 * CreateTime: 2020/7/21 15:09.
 * Version: V1.0
 * Author: ChengTao
 * Description: 通用接口接参过滤类
 */
public class CommReqDataFilter {

    /**
     * 单元测试方法
     * */
    public static void main(String[] args) {
        TranslateTaskMainEntity translatePSearchEntity = new TranslateTaskMainEntity();
//        translatePSearchEntity.setTransId("1");
//        translatePSearchEntity.setSyncDateTime("123");
        HashMap<String, Object> res = filterReqData(translatePSearchEntity, "transId,syncDateTime".split(","));
        System.out.println("[FR]" + JSON.toJSONString(res));
    }

    /**
     * 根据传参对向及属性值进行合法校验与过滤
     */
    public static HashMap<String, Object> filterReqData(Object reqData, String[] params) {
        HashMap<String, Object> backRes = new HashMap<>();
        backRes.put("code", 2000);
        backRes.put("msg", "请求参数正常");
        if (reqData == null) {
            backRes.put("code", 5000);
            backRes.put("msg", "请求参数不能为空");
            return backRes;
        }
        Field[] objField = reqData.getClass().getDeclaredFields();
        for (String item : params) {
            for (int i = 0; i < objField.length; i++) {
                String privateField = objField[i].getName().trim();
                if (item.equals(privateField)) {
                    //System.out.println("[FIELD][" + item + "]");
                    try {
                        String invMethod = "get"+item.substring(0,1).toUpperCase()+item.substring(1);
                        //System.out.println("[OD]"+reqData.getClass().getDeclaredMethod(invMethod).invoke(reqData));
                        if (reqData.getClass().getDeclaredMethod(invMethod).invoke(reqData).toString().length() < 1) {
                            backRes.put("code", 5000);
                            backRes.put("msg", "请求参数[" + item + "]不能为空");
                            return backRes;
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        backRes.put("code", 5000);
                        backRes.put("msg", "请求参数[" + item + "]不能为空");
                        return backRes;

                    }

                }
            }
        }
        return backRes;
    }
}
