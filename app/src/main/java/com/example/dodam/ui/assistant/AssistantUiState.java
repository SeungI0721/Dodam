// 도담이 채팅 화면의 표시 상태를 표현하는 UI 상태 모델 파일이다.
package com.example.dodam.ui.assistant;

import java.util.ArrayList;
import java.util.List;

// 메시지 목록, 로딩 여부, 오류 문구를 한 객체로 묶는다.
public class AssistantUiState {
    private final List<ChatMessage> messages;
    private final boolean loading;
    private final String errorMessage;

    // 화면에 필요한 상태 값을 복사해서 보관한다.
    public AssistantUiState(List<ChatMessage> messages, boolean loading, String errorMessage) {
        this.messages = messages == null ? new ArrayList<>() : new ArrayList<>(messages);
        this.loading = loading;
        this.errorMessage = errorMessage;
    }

    public List<ChatMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public boolean isLoading() {
        return loading;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
