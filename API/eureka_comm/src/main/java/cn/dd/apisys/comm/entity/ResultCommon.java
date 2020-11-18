package cn.dd.apisys.comm.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * FileName: ResultCommon.java
 * CreateTime: 2020/6/3 17:08.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class ResultCommon implements Serializable {
    private int result_code;
    private String msg;
    private List<Map<String, Object>> dataList;

    public ResultCommon(int result_code, String msg, List<Map<String, Object>> dataList) {
        this.result_code = result_code;
        this.msg = msg;
        this.dataList = dataList;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public List<Map<String, Object>> getList() {
        return dataList;
    }

    public void setList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}