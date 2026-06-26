// 도담이 답변에 사용된 FAQ나 문서 근거를 표현하는 모델 파일이다.
package com.example.dodam.domain.assistant;

// 답변 출처의 식별자와 제목을 보관한다.
public class AnswerReference {
    private final String id;
    private final String title;

    // 참조 문서의 ID와 사용자에게 보여줄 제목을 받는다.
    public AnswerReference(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
