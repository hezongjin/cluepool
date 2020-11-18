package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: PvuvEntity.java
 * CreateTime: 2020/6/28 18:09.
 * Version: V1.0
 * Author: ChengTao
 * Description: PVUV查询接口参数接收实体类
 */
public class PvuvEntity extends BaseEntity implements Serializable {
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
