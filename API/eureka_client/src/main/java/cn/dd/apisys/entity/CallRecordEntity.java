package cn.dd.apisys.entity;

import java.io.Serializable;

/**
 * FileName: CallRecordEntity.java
 * CreateTime: 2020/6/9 13:32.
 * Version: V1.0
 * Author: ChengTao
 * Description: 呼叫记录查询接参实体类
 */
public class CallRecordEntity  extends BaseEntity implements Serializable  {
    private String phoneNum;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
