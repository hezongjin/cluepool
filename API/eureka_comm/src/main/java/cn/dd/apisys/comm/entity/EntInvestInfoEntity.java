package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: EntInvestInfoEntity.java
 * CreateTime: 2020/6/19 20:50.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntInvestInfoEntity extends BaseEntity implements Serializable {
    //企业ID，用户统一的id
    private String ent_id;

    //企业ID，该业务对应的Id
    private String subject_main_id;

    public String getEnt_id() {
        return ent_id;
    }

    public void setEnt_id(String ent_id) {
        this.ent_id = ent_id;
    }

    public String getSubject_main_id() {
        return subject_main_id;
    }

    public void setSubject_main_id(String subject_main_id) {
        this.subject_main_id = subject_main_id;
    }
}
