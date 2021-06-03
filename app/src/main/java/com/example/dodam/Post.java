package com.example.dodam;

public class Post {
    private  String userName;
    private  String time;
    private  String noticeTitle;
    private  String noticeContent;

    public Post(){}

    public String getUserName() {
        return userName;
    }

    public String getTime() {
        return time;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public void setNoticeContent(String noticeContent) { this.noticeContent = noticeContent; }

    public String getNoticeContent() { return noticeContent; }
}
