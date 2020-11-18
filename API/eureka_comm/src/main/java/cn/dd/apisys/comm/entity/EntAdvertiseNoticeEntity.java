package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: EntAdvertiseNoticeEntity.java
 * CreateTime: 2020/6/19 18:41.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntAdvertiseNoticeEntity extends BaseEntity implements Serializable {
    //    记录ID
    private String id;
    //    企业ID
    private String ent_id;
    //    招聘平台ID
    private String platformId;
    //    招聘平台名称
    private String platformName;
    //    招聘职位名称
    private String jobName;
    //    招聘职位类型
    private String jobType;
    //    岗位招聘人数
    private String neededNum;
    //    招聘岗位薪资
    private String salary;
    //    招聘职位经验要求
    private String expReq;
    //    岗位学历要求
    private String eduReq;
    //    招聘地区
    private String workingPlace;
    //    职位发布时间
    private String jobEeleaseDate;
    //    职位到期时间
    private String jobExpireDate;
    //    修改时间
    private String updateTime;
    //    招聘详情页面地址
    private String jobDetailUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnt_id() {
        return ent_id;
    }

    public void setEnt_id(String ent_id) {
        this.ent_id = ent_id;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getNeededNum() {
        return neededNum;
    }

    public void setNeededNum(String neededNum) {
        this.neededNum = neededNum;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getExpReq() {
        return expReq;
    }

    public void setExpReq(String expReq) {
        this.expReq = expReq;
    }

    public String getEduReq() {
        return eduReq;
    }

    public void setEduReq(String eduReq) {
        this.eduReq = eduReq;
    }

    public String getWorkingPlace() {
        return workingPlace;
    }

    public void setWorkingPlace(String workingPlace) {
        this.workingPlace = workingPlace;
    }

    public String getJobEeleaseDate() {
        return jobEeleaseDate;
    }

    public void setJobEeleaseDate(String jobEeleaseDate) {
        this.jobEeleaseDate = jobEeleaseDate;
    }

    public String getJobExpireDate() {
        return jobExpireDate;
    }

    public void setJobExpireDate(String jobExpireDate) {
        this.jobExpireDate = jobExpireDate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getJobDetailUrl() {
        return jobDetailUrl;
    }

    public void setJobDetailUrl(String jobDetailUrl) {
        this.jobDetailUrl = jobDetailUrl;
    }
}
