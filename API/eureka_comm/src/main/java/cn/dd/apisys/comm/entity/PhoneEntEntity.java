package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: PhoneEntEntity.java
 * CreateTime: 2020/6/8 13:16.
 * Version: V1.0
 * Author: ChengTao
 * Description: 手机好检索实体类
 */
public class PhoneEntEntity extends BaseEntity implements Serializable {
    //手机号
    private String phoneCode;

    public String getTableHeadList() {
        return tableHeadList;
    }

    public void setTableHeadList(String tableHeadList) {
        this.tableHeadList = tableHeadList;
    }

    //表头集合
    private String tableHeadList;

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }
}
