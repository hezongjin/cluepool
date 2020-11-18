package cn.dd.apisys.entity;

import java.io.Serializable;

/**
 * FileName: EntTrademarkInfoEntity.java
 * CreateTime: 2020/6/19 20:28.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntTrademarkInfoEntity  extends BaseEntity implements Serializable {
    //    商标ID
    private String trademarkId;
    //    企业ID
    private String ent_id;
    //    商标注册号
    private String trademarkRegNo;
    //    商标名称
    private String trademarkName;
    //    商标图片
    private String trademarkLogo;
    //    有效期限
    private String validPeriod;
    //    商标状态
    private String trademarkStatus;
    //    申请时间
    private String applTime;
    //    申请人
    private String applUser;
    //    申请地址
    private String applAddr;
    //    申请流程状态
    private String applStatus;
    //    是否共有商标
    private String communityMarksFlag;
    //    商标类型
    private String trademarkType;
    //    代理机构
    private String agency;
    //    商标服务项目
    private String serviceItem;

    public String getTrademarkId() {
        return trademarkId;
    }

    public void setTrademarkId(String trademarkId) {
        this.trademarkId = trademarkId;
    }

    public String getEnt_id() {
        return ent_id;
    }

    public void setEnt_id(String ent_id) {
        this.ent_id = ent_id;
    }

    public String getTrademarkRegNo() {
        return trademarkRegNo;
    }

    public void setTrademarkRegNo(String trademarkRegNo) {
        this.trademarkRegNo = trademarkRegNo;
    }

    public String getTrademarkName() {
        return trademarkName;
    }

    public void setTrademarkName(String trademarkName) {
        this.trademarkName = trademarkName;
    }

    public String getTrademarkLogo() {
        return trademarkLogo;
    }

    public void setTrademarkLogo(String trademarkLogo) {
        this.trademarkLogo = trademarkLogo;
    }

    public String getValidPeriod() {
        return validPeriod;
    }

    public void setValidPeriod(String validPeriod) {
        this.validPeriod = validPeriod;
    }

    public String getTrademarkStatus() {
        return trademarkStatus;
    }

    public void setTrademarkStatus(String trademarkStatus) {
        this.trademarkStatus = trademarkStatus;
    }

    public String getApplTime() {
        return applTime;
    }

    public void setApplTime(String applTime) {
        this.applTime = applTime;
    }

    public String getApplUser() {
        return applUser;
    }

    public void setApplUser(String applUser) {
        this.applUser = applUser;
    }

    public String getApplAddr() {
        return applAddr;
    }

    public void setApplAddr(String applAddr) {
        this.applAddr = applAddr;
    }

    public String getApplStatus() {
        return applStatus;
    }

    public void setApplStatus(String applStatus) {
        this.applStatus = applStatus;
    }

    public String getCommunityMarksFlag() {
        return communityMarksFlag;
    }

    public void setCommunityMarksFlag(String communityMarksFlag) {
        this.communityMarksFlag = communityMarksFlag;
    }

    public String getTrademarkType() {
        return trademarkType;
    }

    public void setTrademarkType(String trademarkType) {
        this.trademarkType = trademarkType;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(String serviceItem) {
        this.serviceItem = serviceItem;
    }
}
