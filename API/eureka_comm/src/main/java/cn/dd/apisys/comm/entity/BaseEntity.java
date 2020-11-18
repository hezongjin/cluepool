package cn.dd.apisys.comm.entity;

import java.io.Serializable;

/**
 * FileName: BaseEntity.java
 * CreateTime: 2020/6/3 16:38.
 * Version: V1.0
 * Author: ChengTao
 * Description: 参数接收基础类
 */
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private long maxRequest = 10000;
    private int pageNum = 1;
    private int pageSize = 10;
    private long total = 0;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getMaxRequest() {
        return maxRequest;
    }

    public void setMaxRequest(long maxRequest) {
        this.maxRequest = maxRequest;
    }
}