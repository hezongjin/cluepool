package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: TranslateTaskMainEntity.java
 * CreateTime: 2020/7/21 14:41.
 * Version: V1.0
 * Author: ChengTao
 * Description: 全网门户外贸智能分站项目-翻译模块子任务接参实体类
 */
public class TranslateTaskSubEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //翻译任务ID
    private String transId;
    //翻译任务预翻译语言种类
    private String toLanguage;
    //翻译任务主任务RowKey
    private String mainRowkey;
    //翻译任务所属数据ID
    private String dataId;
    //翻译任务来源语言种类
    private String fromLanguage;
    //翻译任务所属应用ID
    private String appId;
    //翻译任务所属租户ID
    private String tenantId;
    //翻译任务数据库表名
    private String tablename;
    //翻译子任务RowKey
    private String subRowkey;
    //翻译任务状态
    private String status;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getToLanguage() {
        return toLanguage;
    }

    public void setToLanguage(String toLanguage) {
        this.toLanguage = toLanguage;
    }

    public String getMainRowkey() {
        return mainRowkey;
    }

    public void setMainRowkey(String mainRowkey) {
        this.mainRowkey = mainRowkey;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getFromLanguage() {
        return fromLanguage;
    }

    public void setFromLanguage(String fromLanguage) {
        this.fromLanguage = fromLanguage;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getSubRowkey() {
        return subRowkey;
    }

    public void setSubRowkey(String subRowkey) {
        this.subRowkey = subRowkey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
