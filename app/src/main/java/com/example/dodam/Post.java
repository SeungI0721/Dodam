package com.example.dodam;

public class Post {
    private  String userName;
    private  String noticeTitle;
    private  String noticeContent;

    public Post(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle() {

        this.noticeTitle = noticeTitle;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {

        this.noticeContent = noticeContent;
    }

    public  String toString() {
        return "Post{" +
                "noticeContent='" + noticeContent + '\'' +
                ", noticeTitle='" + noticeTitle + '\'' +
                ", userName=" + userName + '\'' +
                '}';
    }

}
