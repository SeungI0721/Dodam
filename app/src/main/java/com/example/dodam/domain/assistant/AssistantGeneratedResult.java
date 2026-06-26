// 선택적 생성형 AI 답변 결과를 표현하는 모델 파일이다.
package com.example.dodam.domain.assistant;

// 실제 AI 생성 여부와 생성된 문장을 함께 보관한다.
public class AssistantGeneratedResult {
    private final String text;
    private final boolean generated;

    // 생성 결과 문장과 외부 AI 사용 여부를 받는다.
    public AssistantGeneratedResult(String text, boolean generated) {
        this.text = text;
        this.generated = generated;
    }

    public String getText() {
        return text;
    }

    public boolean isGenerated() {
        return generated;
    }
}
