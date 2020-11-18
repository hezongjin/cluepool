package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: EntSubscriptionsEntity.java
 * CreateTime: 2020/6/19 18:49.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntSubscriptionsEntity extends BaseEntity implements Serializable {
    //    信息ID
    private String id;
    //    企业ID
    private String ent_id;
    //    微信账号
    private String wechatAccount;
    //    公众号名称
    private String subscName;
    //    公众号LOGO
    private String subscLogo;
    //    公众号头像
    private String subscAvatar;
    //    简介
    private String subscDesc;

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

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }

    public String getSubscName() {
        return subscName;
    }

    public void setSubscName(String subscName) {
        this.subscName = subscName;
    }

    public String getSubscLogo() {
        return subscLogo;
    }

    public void setSubscLogo(String subscLogo) {
        this.subscLogo = subscLogo;
    }

    public String getSubscAvatar() {
        return subscAvatar;
    }

    public void setSubscAvatar(String subscAvatar) {
        this.subscAvatar = subscAvatar;
    }

    public String getSubscDesc() {
        return subscDesc;
    }

    public void setSubscDesc(String subscDesc) {
        this.subscDesc = subscDesc;
    }
}
