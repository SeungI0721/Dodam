// 도담이 챗봇에서 선택적으로 사용할 생성형 AI 인터페이스 파일이다.
package com.example.dodam.domain.assistant;

import com.example.dodam.data.assistant.FaqDocument;

import java.util.List;

// Android 앱이 특정 AI 공급자에 직접 묶이지 않도록 추상화한다.
public interface AssistantLanguageModel {
    // 질문, 수조 상태, 검색된 FAQ를 바탕으로 자연어 답변을 생성한다.
    AssistantGeneratedResult generateAnswer(String question, AssistantContext context,
                                            List<FaqDocument> retrievedDocuments);
}
