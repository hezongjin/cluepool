package cn.dd.apisys.entity;

import java.io.Serializable;

/**
 * FileName: PvuvEntity.java
 * CreateTime: 2020/6/29 18:04.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class PvuvEntity implements Serializable {
    //租户ID
    private String tenantId;
    //统计日期
    private String markDate;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getMarkDate() {
        return markDate;
    }

    public void setMarkDate(String markDate) {
        this.markDate = markDate;
    }
}