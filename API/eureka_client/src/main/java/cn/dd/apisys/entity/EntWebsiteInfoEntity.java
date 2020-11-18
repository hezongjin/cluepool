package cn.dd.apisys.entity;

import java.io.Serializable;

/**
 * FileName: EntWebsiteInfoEntity.java
 * CreateTime: 2020/6/19 20:34.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntWebsiteInfoEntity extends BaseEntity implements Serializable {
    //    网站ID
    private String websiteId;
    //    企业ID
    private String ent_id;
    //    网站类型
    private String siteType;
    //    网站名称
    private String siteName;
    //    网站备案号
    private String icpNo;
    //    备案时间
    private String icpDate;
    //    地址
    private String url;
    //    主页是否可访问
    private String accessFlag;
    //    是否官网
    private String officialFlag;
    //    是否适配移动端
    private String mobileAdaptFlag;
    //    是否使用SSL
    private String 	sslFlag;
    //    开发商
    private String developer;
    //    最近一次更新时间
    private String latestTime;
    //    建站语言
    private String developTools;

    public String getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
    }

    public String getEnt_id() {
        return ent_id;
    }

    public void setEnt_id(String ent_id) {
        this.ent_id = ent_id;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getIcpNo() {
        return icpNo;
    }

    public void setIcpNo(String icpNo) {
        this.icpNo = icpNo;
    }

    public String getIcpDate() {
        return icpDate;
    }

    public void setIcpDate(String icpDate) {
        this.icpDate = icpDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccessFlag() {
        return accessFlag;
    }

    public void setAccessFlag(String accessFlag) {
        this.accessFlag = accessFlag;
    }

    public String getOfficialFlag() {
        return officialFlag;
    }

    public void setOfficialFlag(String officialFlag) {
        this.officialFlag = officialFlag;
    }

    public String getMobileAdaptFlag() {
        return mobileAdaptFlag;
    }

    public void setMobileAdaptFlag(String mobileAdaptFlag) {
        this.mobileAdaptFlag = mobileAdaptFlag;
    }

    public String getSslFlag() {
        return sslFlag;
    }

    public void setSslFlag(String sslFlag) {
        this.sslFlag = sslFlag;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(String latestTime) {
        this.latestTime = latestTime;
    }

    public String getDevelopTools() {
        return developTools;
    }

    public void setDevelopTools(String developTools) {
        this.developTools = developTools;
    }
}
