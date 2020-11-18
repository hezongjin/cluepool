package cn.dd.apisys.model;

import com.alibaba.fastjson.JSON;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: CallCenterRecord.java
 * CreateTime: 2020/6/9 13:25.
 * Version: V1.0
 * Author: ChengTao
 * Description: 呼叫中心实体类（供Mybitas使用）
 */
public class CallCenterRecord {
    private int id;
    private String phoneNum;
    private int callTimes;
    private int connectedTimes;

    public String getCallContext() {
        return callContext;
    }

    public void setCallContext(String callContext) {
        this.callContext = callContext;
    }

    private String callContext;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public int getCallTimes() {
        return callTimes;
    }

    public void setCallTimes(int callTimes) {
        this.callTimes = callTimes;
    }

    public int getConnectedTimes() {
        return connectedTimes;
    }

    public void setConnectedTimes(int connectedTimes) {
        this.connectedTimes = connectedTimes;
    }


}
