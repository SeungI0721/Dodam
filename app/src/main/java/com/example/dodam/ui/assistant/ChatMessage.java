// 도담이 채팅 화면에 표시할 한 개의 메시지를 표현하는 UI 모델 파일이다.
package com.example.dodam.ui.assistant;

// 메시지 내용, 작성 주체, 생성 시각을 보관한다.
public class ChatMessage {
    private final String text;
    private final boolean fromUser;
    private final long createdAt;

    // 사용자 메시지인지 도담이 메시지인지 구분해서 생성한다.
    public ChatMessage(String text, boolean fromUser, long createdAt) {
        this.text = text;
        this.fromUser = fromUser;
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public boolean isFromUser() {
        return fromUser;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
