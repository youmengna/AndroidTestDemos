package com.youmengna.byr.bean;


/**
 * Created by youmengna0 on 2016/8/24.
 */
public class Bean {
    String inboxName;
    String inboxContent;
    String inboxDate;
    String inboxState;

    public Bean(String inboxName,String inboxContent,String inboxDate,String inboxState)
    {
        this.inboxName=inboxName;
        this.inboxContent=inboxContent;
        this.inboxDate=inboxDate;
        this.inboxState=inboxState;
    }
    public String getInboxName() {
        return inboxName;
    }

    public void setInboxName(String inboxName) {
        this.inboxName = inboxName;
    }

    public String getInboxContent() {
        return inboxContent;
    }

    public void setInboxContent(String inboxContent) {
        this.inboxContent = inboxContent;
    }

    public String getInboxDate() {
        return inboxDate;
    }

    public void setInboxDate(String inboxDate) {
        this.inboxDate = inboxDate;
    }

    public String getInboxState() {
        return inboxState;
    }

    public void setInboxState(String inboxState) {
        this.inboxState = inboxState;
    }
}
