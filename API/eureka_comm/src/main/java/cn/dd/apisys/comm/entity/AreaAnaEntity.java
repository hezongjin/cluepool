package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: AreaAnaEntity.java
 * CreateTime: 2020/7/17 13:09.
 * Version: V1.0
 * Author: ChengTao
 * Description: 启航地区分布新API查询接口接参实体类
 */
public class AreaAnaEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //租户ID
    private String tenantId;
    //检索起始日期
    private String startDate;
    //检索截止日期
    private String stopDate;
    //检索域名
    private String domainName;
    //检索省级粒度名称
    private String provName;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStopDate() {
        return stopDate;
    }

    public void setStopDate(String stopDate) {
        this.stopDate = stopDate;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getProvName() {
        return provName;
    }

    public void setProvName(String provName) {
        this.provName = provName;
    }
}
