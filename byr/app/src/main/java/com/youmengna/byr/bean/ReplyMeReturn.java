package com.youmengna.byr.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by youmengna0 on 2016/10/20.
 */
public class ReplyMeReturn implements Serializable {

    private String description;
    private List<Refer> article;
    private Pagination pagination;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Refer> getArticle() {
        return article;
    }

    public void setArticle(List<Refer> article) {
        this.article = article;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

}
