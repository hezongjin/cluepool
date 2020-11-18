package cn.dd.apisys.comm.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * FileName: EntCommonEntity.java
 * CreateTime: 2020/6/3 17:10.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntCommonEntity extends BaseEntity implements Serializable {
    private JSONObject data;

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}