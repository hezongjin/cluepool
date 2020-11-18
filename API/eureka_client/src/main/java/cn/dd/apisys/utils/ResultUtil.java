package cn.dd.apisys.utils;

import cn.dd.apisys.entity.CluesPoolConstant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * FileName: ResultUtil.java
 * CreateTime: 2020/6/3 15:57.
 * Version: V1.0
 * Author: ChengTao
 * Description: 返回结果封装类
 */
public class ResultUtil {
    private Logger logger = null;

    public ResultUtil(Class clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }


    public JSONObject error(int code, String msg, long minTimes, Exception e) {
        /*
        BaseResultModel baseResultModel = new BaseResultModel(code, msg, null, String.valueOf(minTimes));
        logger.error(baseResultModel.getMsg(), e);
        */
        return (JSONObject) JSON.toJSON(code);
    }

    public JSONObject success(String msg, long minTimes, Object data) {
        /*
        BaseResultModel baseResultModel = new BaseResultModel(CluesPoolConstant.RESULT_CODE.SUCCESS_, msg, data, String.valueOf(minTimes));
        //logger.debug(baseResultModel.getMsg());
        */
        JSONObject jsonObject = (JSONObject) JSON.toJSON(msg);

        return jsonObject;
    }

    public JSONObject common(int result_code, String msg, long minTimes, Object data) {
        /*
        BaseResultModel baseResultModel = new BaseResultModel(result_code, msg, data, String.valueOf(minTimes));
        //logger.debug(baseResultModel.getMsg());
        */
        JSONObject jsonObject = (JSONObject) JSON.toJSON(result_code);
        return jsonObject;
    }

    public HashMap getDataList(List<Object> list) {
        return new HashMap<String, Object>() {
            {
                put("dataList", list);
            }
        };
    }


}