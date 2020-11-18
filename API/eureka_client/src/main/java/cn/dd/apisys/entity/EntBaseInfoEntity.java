package cn.dd.apisys.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * FileName: EntBaseInfoEntity.java
 * CreateTime: 2020/6/3 16:40.
 * Version: V1.0
 * Author: ChengTao
 * Description: 清单列表参数接收类
 */
public class EntBaseInfoEntity extends BaseEntity implements Serializable {
    //企业ID
    private String ent_id;
    //企业名称
    private String ent_name;
    //行业代码1
    private String cneiCodeList;
    //行业代码2
    private String cneiSecCodeList;
    //行业代码3
    private String cneiThrCodeList;
    //专享区域编码
    private String exclusiveAreaCodeList;
    //共享区域编码
    private String shareAreaCodeList;
    //共享城市CODE集合
    private String shareCityCodeList;
    //专属城市CODE集合
    private String exclusiveCityCodeList;
    //是否有联系方式标记,0没有1有
    private String contactInfoSign;
    //注册时间
    private String estimateDate;
    //表头集合
    private String tableHeadList;
    //标签集合
    private List<Map<String, String>> labelList;

    //使用ent_name2用于精确查询
    private String ent_name2;

    //是否过滤区域编码，0为过滤，1为不过滤，默认为0(2020-11-10添加)
    private String filterArea;

    public String getFilterArea() {
        return filterArea;
    }

    public void setFilterArea(String filterArea) {
        this.filterArea = filterArea;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    //最新更新时间
    private String lastModifyTime;

    public String getEstimateDate() {
        return estimateDate;
    }

    public void setEstimateDate(String estimateDate) {
        this.estimateDate = estimateDate;
    }

    public String getContactInfoSign() {
        return contactInfoSign;
    }

    public void setContactInfoSign(String contactInfoSign) {
        this.contactInfoSign = contactInfoSign;
    }

    public String getEnt_id() {
        return ent_id;
    }

    public void setEnt_id(String ent_id) {
        this.ent_id = ent_id;
    }

    public String getEnt_name() {
        return ent_name;
    }

    public void setEnt_name(String ent_name) {
        this.ent_name = ent_name;
    }

    public String getCneiCodeList() {
        return cneiCodeList;
    }

    public void setCneiCodeList(String cneiCodeList) {
        this.cneiCodeList = cneiCodeList;
    }

    public String getCneiSecCodeList() {
        return cneiSecCodeList;
    }

    public void setCneiSecCodeList(String cneiSecCodeList) {
        this.cneiSecCodeList = cneiSecCodeList;
    }

    public String getCneiThrCodeList() {
        return cneiThrCodeList;
    }

    public void setCneiThrCodeList(String cneiThrCodeList) {
        this.cneiThrCodeList = cneiThrCodeList;
    }

    public String getExclusiveAreaCodeList() {
        return exclusiveAreaCodeList;
    }

    public void setExclusiveAreaCodeList(String exclusiveAreaCodeList) {
        this.exclusiveAreaCodeList = exclusiveAreaCodeList;
    }
    public String getShareAreaCodeList() {
        return shareAreaCodeList;
    }

    public void setShareAreaCodeList(String shareAreaCodeList) {
        this.shareAreaCodeList = shareAreaCodeList;
    }

    public String getTableHeadList() {
        return tableHeadList;
    }

    public void setTableHeadList(String tableHeadList) {
        this.tableHeadList = tableHeadList;
    }

    public List<Map<String, String>> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<Map<String, String>> labelList) {
        this.labelList = labelList;
    }

    public String getShareCityCodeList() {
        return shareCityCodeList;
    }

    public void setShareCityCodeList(String shareCityCodeList) {
        this.shareCityCodeList = shareCityCodeList;
    }

    public String getExclusiveCityCodeList() {
        return exclusiveCityCodeList;
    }

    public void setExclusiveCityCodeList(String exclusiveCityCodeList) {
        this.exclusiveCityCodeList = exclusiveCityCodeList;
    }

    public String getEnt_name2() {
        return ent_name2;
    }

    public void setEnt_name2(String ent_name2) {
        this.ent_name2 = ent_name2;
    }
}