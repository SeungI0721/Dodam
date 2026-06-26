// 로컬 FAQ 검색 결과를 도담이 답변으로 바꾸는 UseCase 파일이다.
package com.example.dodam.domain.assistant;

import com.example.dodam.data.assistant.FaqDocument;
import com.example.dodam.data.assistant.FaqRepository;

import java.util.Collections;

// FAQ 저장소에서 관련 문서를 찾아 답변과 참조 정보를 구성한다.
public class SearchFaqUseCase {
    private final FaqRepository repository;

    // 검색에 사용할 FAQ Repository를 주입받는다.
    public SearchFaqUseCase(FaqRepository repository) {
        this.repository = repository;
    }

    // 사용자 질문에 맞는 FAQ를 찾고, 없으면 추측하지 않는 안내를 반환한다.
    public AssistantAnswer search(String query) {
        FaqDocument document = repository.search(query);
        if (document == null) {
            return AssistantAnswer.simple("현재 등록된 도움말만으로는 정확히 답하기 어렵습니다. 수조 정보, 장치 ID, 센서 연결 상태를 확인해 주세요.",
                    AnswerSourceType.FALLBACK, null);
        }
        return new AssistantAnswer(document.getAnswer(), AnswerSourceType.LOCAL_FAQ,
                Collections.singletonList(new AnswerReference(document.getId(), document.getTitle())),
                null, false, null);
    }
}
