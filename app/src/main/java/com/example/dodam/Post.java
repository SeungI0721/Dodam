// Firebase Realtime Database에 저장되는 커뮤니티 게시글 모델 파일이다.
package com.example.dodam;

public class Post {
    private  String userName;
    private  String noticeTitle;
    private  String noticeContent;
    private String postId;
    private String authorUid;
    private String category;
    private long createdAt;

    public Post(){}

    public Post(String userName, String noticeTitle, String noticeContent) {
        this.userName = userName;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
    }

    public Post(String postId, String authorUid, String userName, String category,
                String noticeTitle, String noticeContent, long createdAt) {
        this.postId = postId;
        this.authorUid = authorUid;
        this.userName = userName;
        this.category = category;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {

        this.noticeTitle = noticeTitle;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {

        this.noticeContent = noticeContent;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public  String toString() {
        return "Post{" +
                "noticeContent='" + noticeContent + '\'' +
                ", noticeTitle='" + noticeTitle + '\'' +
                ", userName=" + userName + '\'' +
                '}';
    }

}
