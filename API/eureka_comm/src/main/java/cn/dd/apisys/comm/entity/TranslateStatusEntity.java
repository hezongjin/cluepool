package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: PvuvEntity.java
 * CreateTime: 2020/7/08 18:09.
 * Version: V1.0
 * Author: ChengTao
 * Description: 门户翻译状态查询接口参数接收实体类
 */
public class TranslateStatusEntity {
    //租户ID
    private String transId;
    //统计日期
    private String transStatus;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }
}
