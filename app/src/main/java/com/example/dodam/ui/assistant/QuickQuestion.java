// 도담이 채팅 화면의 빠른 질문 버튼 정보를 표현하는 UI 모델 파일이다.
package com.example.dodam.ui.assistant;

// 버튼에 보일 짧은 문구와 실제 전송할 질문 문장을 보관한다.
public class QuickQuestion {
    private final String label;
    private final String question;

    // 빠른 질문 버튼 하나를 구성한다.
    public QuickQuestion(String label, String question) {
        this.label = label;
        this.question = question;
    }

    public String getLabel() {
        return label;
    }

    public String getQuestion() {
        return question;
    }
}
