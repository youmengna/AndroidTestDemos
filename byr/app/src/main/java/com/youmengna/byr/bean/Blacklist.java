package com.youmengna.byr.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 黑名单结构体
 *
 * @author dss886
 * @since 2014-9-7
 */
public class Blacklist implements Serializable{

    /**
     * 黑名单用户的用户元数组
     */
    private List<User> user;
    /**
     * 黑名单列表分页信息
     */
    private Pagination pagination;

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
