// 인증과 사용자 정보 화면에서 사용하는 사용자 프로필 모델 파일이다.
package com.example.dodam.domain.model;

public class UserProfile {
    private final String uid;
    private final String email;
    private final String nickname;
    private final String photoName;

    public UserProfile(String uid, String email, String nickname, String photoName) {
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.photoName = photoName;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhotoName() {
        return photoName;
    }
}
