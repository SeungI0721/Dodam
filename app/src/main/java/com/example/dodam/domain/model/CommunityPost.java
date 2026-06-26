// 작성자 UID와 게시판 분류를 포함한 커뮤니티 게시글 모델 파일이다.
package com.example.dodam.domain.model;

public class CommunityPost {
    private final String postId;
    private final String authorUid;
    private final String authorName;
    private final String category;
    private final String title;
    private final String content;
    private final long createdAt;

    public CommunityPost(String postId, String authorUid, String authorName, String category,
                         String title, String content, long createdAt) {
        this.postId = postId;
        this.authorUid = authorUid;
        this.authorName = authorName;
        this.category = category;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getPostId() {
        return postId;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
