package cn.dd.apisys.comm.entity;

import java.io.Serializable;
import java.util.List;

/**
 * FileName: TranslateTaskMainEntity.java
 * CreateTime: 2020/7/21 14:41.
 * Version: V1.0
 * Author: ChengTao
 * Description: 全网门户外贸智能分站项目-翻译模块批量查询接参实体类
 */
public class TranslateTaskPSearchEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //租户ID列表
    private List<String> transIds;

    public List<String> getTransIds() {
        return transIds;
    }

    public void setTransIds(List<String> transIds) {
        this.transIds = transIds;
    }



}
