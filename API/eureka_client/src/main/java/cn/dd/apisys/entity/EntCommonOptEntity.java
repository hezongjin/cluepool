package cn.dd.apisys.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * FileName: EntCommonOptEntity.java
 * CreateTime: 2020/7/27 4:30.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntCommonOptEntity extends BaseEntity implements Serializable {
    //用于存放field
    private Map<String, Object> map;

    //用于存放index
    private String index;

    //用于存放index的id
    private String indexId;


    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }
}
