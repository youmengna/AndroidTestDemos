package com.youmengna.byr.bean;

import java.io.Serializable;

/**
 * 分页结构体
 *
 * @author dss886
 * @since 2014-9-7
 */
public class Pagination implements Serializable {

    /**
     * 总页数
     */
    private int page_all_count;
    /**
     * 当前页数
     */
    private int page_current_count;
    /**
     * 每页元素个数
     */
    private int item_page_count;
    /**
     * 所有元素个数
     */
    private int item_all_count;

    public int getPage_all_count() {
        return page_all_count;
    }

    public void setPage_all_count(int page_all_count) {
        this.page_all_count = page_all_count;
    }

    public int getPage_current_count() {
        return page_current_count;
    }

    public void setPage_current_count(int page_current_count) {
        this.page_current_count = page_current_count;
    }

    public int getItem_page_count() {
        return item_page_count;
    }

    public void setItem_page_count(int item_page_count) {
        this.item_page_count = item_page_count;
    }

    public int getItem_all_count() {
        return item_all_count;
    }

    public void setItem_all_count(int item_all_count) {
        this.item_all_count = item_all_count;
    }
}
