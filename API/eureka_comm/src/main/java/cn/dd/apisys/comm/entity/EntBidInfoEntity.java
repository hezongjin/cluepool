package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: EntBidInfoEntity.java
 * CreateTime: 2020/6/19 20:41.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntBidInfoEntity extends BaseEntity implements Serializable {
    //企业ID
    private String ent_id;

    public String getEnt_id() {
        return ent_id;
    }

    public void setEnt_id(String ent_id) {
        this.ent_id = ent_id;
    }
}
