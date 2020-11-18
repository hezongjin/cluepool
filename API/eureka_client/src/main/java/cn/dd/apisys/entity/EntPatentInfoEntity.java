package cn.dd.apisys.entity;

import java.io.Serializable;

/**
 * FileName: EntPatentInfoEntity.java
 * CreateTime: 2020/6/19 20:23.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntPatentInfoEntity  extends BaseEntity implements Serializable {
    //    信息ID
    private String patentId;
    //    企业ID
    private String ent_id;
    //    申请号
    private String applNo;
    //    申请日期
    private String applDate;
    //    申请人
    private String applUser;
    //    公开号
    private String publicNo;
    //    公开日期
    private String publicDate;
    //    专利名称
    private String patentName;
    //    专利图片
    private String patentLogo;
    //    专利分类编码
    private String patentType;
    //    IPC分类编码列表
    private String patentIpcType;
    //    发明人
    private String inventor;
    //    代理机构
    private String agency;
    //    代理人
    private String agent;
    //    摘要
    private String summary;
    //    专利状态
    private String patentStatus;

    public String getPatentId() {
        return patentId;
    }

    public void setPatentId(String patentId) {
        this.patentId = patentId;
    }

    public String getEnt_id() {
        return ent_id;
    }

    public void setEnt_id(String ent_id) {
        this.ent_id = ent_id;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getApplDate() {
        return applDate;
    }

    public void setApplDate(String applDate) {
        this.applDate = applDate;
    }

    public String getApplUser() {
        return applUser;
    }

    public void setApplUser(String applUser) {
        this.applUser = applUser;
    }

    public String getPublicNo() {
        return publicNo;
    }

    public void setPublicNo(String publicNo) {
        this.publicNo = publicNo;
    }

    public String getPublicDate() {
        return publicDate;
    }

    public void setPublicDate(String publicDate) {
        this.publicDate = publicDate;
    }

    public String getPatentName() {
        return patentName;
    }

    public void setPatentName(String patentName) {
        this.patentName = patentName;
    }

    public String getPatentLogo() {
        return patentLogo;
    }

    public void setPatentLogo(String patentLogo) {
        this.patentLogo = patentLogo;
    }

    public String getPatentType() {
        return patentType;
    }

    public void setPatentType(String patentType) {
        this.patentType = patentType;
    }

    public String getPatentIpcType() {
        return patentIpcType;
    }

    public void setPatentIpcType(String patentIpcType) {
        this.patentIpcType = patentIpcType;
    }

    public String getInventor() {
        return inventor;
    }

    public void setInventor(String inventor) {
        this.inventor = inventor;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPatentStatus() {
        return patentStatus;
    }

    public void setPatentStatus(String patentStatus) {
        this.patentStatus = patentStatus;
    }
}
