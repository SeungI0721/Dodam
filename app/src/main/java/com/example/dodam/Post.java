package com.example.dodam;

public class Post {
    private  String userPhoto;
    private  String userName;
    private  String time;
    private  String noticeTitle;

    public Post(){}

    public String getUserPhoto() {
        return userPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public String getTime() {
        return time;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
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
}
