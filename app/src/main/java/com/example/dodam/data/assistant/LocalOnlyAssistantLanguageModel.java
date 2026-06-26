// 외부 생성형 AI 없이 로컬 폴백 답변만 제공하는 언어 모델 파일이다.
package com.example.dodam.data.assistant;

import com.example.dodam.domain.assistant.AssistantContext;
import com.example.dodam.domain.assistant.AssistantGeneratedResult;
import com.example.dodam.domain.assistant.AssistantLanguageModel;

import java.util.List;

// API key 없이 앱 단독 실행을 보장하기 위한 기본 AssistantLanguageModel 구현이다.
public class LocalOnlyAssistantLanguageModel implements AssistantLanguageModel {
    // 외부 AI를 호출하지 않고 로컬 전용 안내 문장을 반환한다.
    @Override
    public AssistantGeneratedResult generateAnswer(String question, AssistantContext context,
                                                   List<FaqDocument> retrievedDocuments) {
        return new AssistantGeneratedResult("현재 버전은 외부 생성형 AI 없이 로컬 상태 답변과 로컬 FAQ만 사용합니다.", false);
    }
}
