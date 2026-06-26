// 도담이 질문 의도 분류 UseCase를 검증하는 단위 테스트 파일이다.
package com.example.dodam.domain.assistant;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

// 상태 질문, 제어 질문, FAQ 질문이 올바른 의도로 분류되는지 확인한다.
public class ClassifyAssistantIntentUseCaseTest {
    private final ClassifyAssistantIntentUseCase useCase = new ClassifyAssistantIntentUseCase();

    @Test
    // 현재 상태 질문이 CURRENT_STATUS로 분류되는지 확인한다.
    public void classifiesCurrentStatusQuestion() {
        assertEquals(AssistantIntent.CURRENT_STATUS, useCase.classify("지금 수조 상태 어때?"));
    }

    @Test
    // 히터 켜기 요청이 장치 제어 의도로 분류되는지 확인한다.
    public void classifiesHeaterControlQuestion() {
        assertEquals(AssistantIntent.DEVICE_CONTROL, useCase.classify("히터 켜줘"));
    }

    @Test
    // "켜졌어" 같은 단어가 있어도 이유 질문은 제어 명령으로 오분류하지 않는다.
    public void classifiesHeaterReasonBeforeControlWords() {
        assertEquals(AssistantIntent.HEATER_REASON, useCase.classify("히터가 왜 켜졌어?"));
    }

    @Test
    // 사용법 질문이 FAQ 의도로 분류되는지 확인한다.
    public void classifiesFaqQuestion() {
        assertEquals(AssistantIntent.FAQ, useCase.classify("수조 등록 방법 알려줘"));
    }
}
