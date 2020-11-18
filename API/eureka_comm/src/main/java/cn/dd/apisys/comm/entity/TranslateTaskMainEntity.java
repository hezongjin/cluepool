package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: TranslateTaskMainEntity.java
 * CreateTime: 2020/7/21 14:41.
 * Version: V1.0
 * Author: ChengTao
 * Description: 全网门户外贸智能分站项目-翻译模块主任务接参实体类
 */
public class TranslateTaskMainEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //翻译任务ID
    private String transId;
    //翻译任务同步时间
    private String syncDateTime;
    //翻译任务创建时间
    private String strantsDate;
    //翻译任务状态
    private String status;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getSyncDateTime() {
        return syncDateTime;
    }

    public void setSyncDateTime(String syncDateTime) {
        this.syncDateTime = syncDateTime;
    }

    public String getStrantsDate() {
        return strantsDate;
    }

    public void setStrantsDate(String strantsDate) {
        this.strantsDate = strantsDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
