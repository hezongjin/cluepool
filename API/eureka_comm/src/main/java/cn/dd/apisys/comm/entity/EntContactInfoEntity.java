package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: EntContactInfoEntity.java
 * CreateTime: 2020/6/18 14:49.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntContactInfoEntity extends BaseEntity implements Serializable {

    //    编外字段
    private String idx;
    //    联系人ID
    private String contactId;
    //    企业ID
    private String ent_id;
    //    数据来源
    private String sourceId;
    //    联系人姓名
    private String contactName;
    //    英文姓名
    private String contactEn;
    //    联系人称谓
    private String contactTitle;
    //    性别
    private String gender;
    //    国籍
    private String nationality;
    //    民族
    private String ethnicity;
    //    婚姻状况
    private String maritalStatus;
    //    首次参加工作时间
    private String firstJobTime;
    //    工龄
    private String workAge;
    //    户口所在地
    private String householdPlace;
    //    所在省份
    private String provinceCode;
    //    所在城市
    private String cityCode;
    //    所在区县
    private String countyCode;
    //    通讯地址
    private String mailingAddr;
    //    职位	position
    private String position;
    //    生育状况
    private String fertilityStatus;
    //    外语语种
    private String foreignLanguages;
    //    外语等级
    private String languageRank;
    //    血型
    private String bloodType;
    //    身高
    private String height;
    //    体重
    private String weight;
    //    来源二级
    private String sourceSub;
    //    记录更新时间
    private String modifyTime;
    //    记录创建时间
    private String createTime;

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getEnt_id() {
        return ent_id;
    }

    public void setEnt_id(String ent_id) {
        this.ent_id = ent_id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEn() {
        return contactEn;
    }

    public void setContactEn(String contactEn) {
        this.contactEn = contactEn;
    }

    public String getContactTitle() {
        return contactTitle;
    }

    public void setContactTitle(String contactTitle) {
        this.contactTitle = contactTitle;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getFirstJobTime() {
        return firstJobTime;
    }

    public void setFirstJobTime(String firstJobTime) {
        this.firstJobTime = firstJobTime;
    }

    public String getWorkAge() {
        return workAge;
    }

    public void setWorkAge(String workAge) {
        this.workAge = workAge;
    }

    public String getHouseholdPlace() {
        return householdPlace;
    }

    public void setHouseholdPlace(String householdPlace) {
        this.householdPlace = householdPlace;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getMailingAddr() {
        return mailingAddr;
    }

    public void setMailingAddr(String mailingAddr) {
        this.mailingAddr = mailingAddr;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFertilityStatus() {
        return fertilityStatus;
    }

    public void setFertilityStatus(String fertilityStatus) {
        this.fertilityStatus = fertilityStatus;
    }

    public String getForeignLanguages() {
        return foreignLanguages;
    }

    public void setForeignLanguages(String foreignLanguages) {
        this.foreignLanguages = foreignLanguages;
    }

    public String getLanguageRank() {
        return languageRank;
    }

    public void setLanguageRank(String languageRank) {
        this.languageRank = languageRank;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSourceSub() {
        return sourceSub;
    }

    public void setSourceSub(String sourceSub) {
        this.sourceSub = sourceSub;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
