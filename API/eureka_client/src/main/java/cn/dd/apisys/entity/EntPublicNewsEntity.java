package cn.dd.apisys.entity;

import java.io.Serializable;

/**
 * FileName: EntPublicNewsEntity.java
 * CreateTime: 2020/6/19 20:17.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EntPublicNewsEntity extends BaseEntity implements Serializable {
    //    信息ID
    private String id;
    //    企业ID
    private String ent_id;
    //    新闻标题
    private String newsTitle;
    //    来源
    private String newsSource;
    //    时间
    private String releaseTime;
    //    内容
    private String contents;
    //    发布者
    private String author;
    //    URL
    private String url;

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

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
