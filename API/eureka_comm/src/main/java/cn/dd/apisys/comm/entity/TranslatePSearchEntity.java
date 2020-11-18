package cn.dd.apisys.comm.entity;

import java.util.List;

/**
 * FileName: PvuvEntity.java
 * CreateTime: 2020/7/08 18:09.
 * Version: V1.0
 * Author: ChengTao
 * Description: 门户翻译状态批量查询接口参数接收实体类
 */
public class TranslatePSearchEntity {
    //租户ID列表
    private List<String> transIds;

    public List<String> getTransIds() {
        return transIds;
    }

    public void setTransIds(List<String> transIds) {
        this.transIds = transIds;
    }
}
