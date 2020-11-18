package cn.dd.apisys.entity;

import java.io.Serializable;

/**
 * FileName: EntNetPopularizationEntity.java
 * CreateTime: 2020/6/19 18:15.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntNetPopularizationEntity extends BaseEntity implements Serializable {
    //    记录ID
    private String id;
    //    企业ID
    private String ent_id;
    //    推广类型
    private String popuType;
    //    推广平台
    private String popuPlat;
    //    推广投放时间	release_time
    private String releaseTime;
    //    推广关键词
    private String popuKeyword;
    //    创意方案
    private String creativeScheme;

    //数据库的index
    private String _index;

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

    public String getPopuType() {
        return popuType;
    }

    public void setPopuType(String popuType) {
        this.popuType = popuType;
    }

    public String getPopuPlat() {
        return popuPlat;
    }

    public void setPopuPlat(String popuPlat) {
        this.popuPlat = popuPlat;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getPopuKeyword() {
        return popuKeyword;
    }

    public void setPopuKeyword(String popuKeyword) {
        this.popuKeyword = popuKeyword;
    }

    public String getCreativeScheme() {
        return creativeScheme;
    }

    public void setCreativeScheme(String creativeScheme) {
        this.creativeScheme = creativeScheme;
    }

    public String get_index() {
        return _index;
    }

    public void set_index(String _index) {
        this._index = _index;
    }
}
