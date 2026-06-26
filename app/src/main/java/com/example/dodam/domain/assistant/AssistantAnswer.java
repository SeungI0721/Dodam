// 도담이 챗봇이 사용자에게 반환할 최종 답변 정보를 표현하는 모델 파일이다.
package com.example.dodam.domain.assistant;

import java.util.ArrayList;
import java.util.List;

// 답변 문장, 출처, 참조 문서, 데이터 시각, 확인 필요 명령을 한 번에 담는다.
public class AssistantAnswer {
    private final String text;
    private final AnswerSourceType sourceType;
    private final List<AnswerReference> references;
    private final Long dataTimestamp;
    private final boolean requiresConfirmation;
    private final PendingCommand pendingCommand;

    // 답변 객체를 직접 구성할 때 사용하는 생성자다.
    public AssistantAnswer(String text, AnswerSourceType sourceType, List<AnswerReference> references,
                           Long dataTimestamp, boolean requiresConfirmation, PendingCommand pendingCommand) {
        this.text = text;
        this.sourceType = sourceType;
        this.references = references == null ? new ArrayList<>() : new ArrayList<>(references);
        this.dataTimestamp = dataTimestamp;
        this.requiresConfirmation = requiresConfirmation;
        this.pendingCommand = pendingCommand;
    }

    // 확인 절차나 참조 문서가 필요 없는 단순 답변을 빠르게 만든다.
    public static AssistantAnswer simple(String text, AnswerSourceType sourceType, Long dataTimestamp) {
        return new AssistantAnswer(text, sourceType, new ArrayList<>(), dataTimestamp, false, null);
    }

    public String getText() {
        return text;
    }

    public AnswerSourceType getSourceType() {
        return sourceType;
    }

    public List<AnswerReference> getReferences() {
        return new ArrayList<>(references);
    }

    public Long getDataTimestamp() {
        return dataTimestamp;
    }

    public boolean isRequiresConfirmation() {
        return requiresConfirmation;
    }

    public PendingCommand getPendingCommand() {
        return pendingCommand;
    }
}
