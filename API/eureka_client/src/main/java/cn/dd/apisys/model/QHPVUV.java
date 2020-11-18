package cn.dd.apisys.model;

/**
 * FileName: QHPVUV.java
 * CreateTime: 2020/6/28 18:02.
 * Version: V1.0
 * Author: ChengTao
 * Description: 启航新起PVUV统计模块实体类（供Mybitas使用）
 */
public class QHPVUV {
    private long id;
    private String tenantid;
    private int pv;
    private int uv;
    private int opv;
    private int ouv;
    private int markDate;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTenantid() {
        return tenantid;
    }

    public void setTenantid(String tenantid) {
        this.tenantid = tenantid;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public int getUv() {
        return uv;
    }

    public void setUv(int uv) {
        this.uv = uv;
    }

    public int getMarkDate() {
        return markDate;
    }

    public void setMarkDate(int markDate) {
        this.markDate = markDate;
    }

    public int getOpv() {
        return opv;
    }

    public void setOpv(int opv) {
        this.opv = opv;
    }

    public int getOuv() {
        return ouv;
    }

    public void setOuv(int ouv) {
        this.ouv = ouv;
    }
}
