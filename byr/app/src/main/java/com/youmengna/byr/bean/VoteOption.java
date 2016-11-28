package com.youmengna.byr.bean;

import java.io.Serializable;

/**
 * 投票选项结构体
 *
 * @author dss886
 * @since 2014-9-7
 */
public class VoteOption implements Serializable{

    /**
     * 投票选项标识id
     */
    private int viid;
    /**
     * 选项内容
     */
    private String label;
    /**
     * 改选项已投票数，如果设置投票后可见且还没投票这个值为-1
     */
    private int num;
    /**
     * 投票时间，未投票没有此项
     */
    private int time;

    /**
     * 选项是否选中，true选中，false未选中
     */
    private boolean isChecked;
    public int getViid() {
        return viid;
    }

    public void setViid(int viid) {
        this.viid = viid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
