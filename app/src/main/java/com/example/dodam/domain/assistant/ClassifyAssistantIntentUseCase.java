// 사용자 질문을 도담이 챗봇 의도로 분류하는 UseCase 파일이다.
package com.example.dodam.domain.assistant;

import java.util.Locale;

// 간단한 키워드 규칙으로 상태 조회, FAQ, 장치 제어 같은 목적을 구분한다.
public class ClassifyAssistantIntentUseCase {
    // 사용자 질문 문장을 하나의 AssistantIntent로 변환한다.
    public AssistantIntent classify(String question) {
        String text = normalize(question);
        if (text.isEmpty()) {
            return AssistantIntent.UNKNOWN;
        }
        if (containsAny(text, "히터", "heater", "온열", "가열")) {
            if (containsAny(text, "왜", "이유", "상태", "작동")) {
                return AssistantIntent.HEATER_REASON;
            }
            if (containsAny(text, "켜", "꺼", "on", "off", "제어", "바꿔")) {
                return AssistantIntent.DEVICE_CONTROL;
            }
            return AssistantIntent.HEATER_REASON;
        }
        if (containsAny(text, "조명", "불", "라이트", "light")) {
            if (containsAny(text, "왜", "이유", "상태", "작동")) {
                return AssistantIntent.LIGHT_REASON;
            }
            if (containsAny(text, "켜", "꺼", "on", "off", "제어", "바꿔")) {
                return AssistantIntent.DEVICE_CONTROL;
            }
            return AssistantIntent.LIGHT_REASON;
        }
        if (containsAny(text, "수온", "온도", "temperature")) {
            return AssistantIntent.TEMPERATURE_STATUS;
        }
        if (containsAny(text, "수위", "물높이", "물 높이", "water level")) {
            return AssistantIntent.WATER_LEVEL_STATUS;
        }
        if (containsAny(text, "탁도", "수질", "물 상태", "물상태", "turbidity")) {
            return AssistantIntent.TURBIDITY_STATUS;
        }
        if (containsAny(text, "연결", "오프라인", "온라인", "장치", "센서", "connection")) {
            return AssistantIntent.DEVICE_CONNECTION;
        }
        if (containsAny(text, "경고", "위험", "알림", "문제", "alert", "warning")) {
            return AssistantIntent.RECENT_ALERTS;
        }
        if (containsAny(text, "관리", "환수", "먹이", "급여", "필터", "청소", "일정")) {
            return AssistantIntent.NEXT_MAINTENANCE;
        }
        if (containsAny(text, "사용법", "방법", "도움말", "등록", "설정", "faq", "help")) {
            return AssistantIntent.FAQ;
        }
        if (containsAny(text, "상태", "지금", "어때", "정상", "현재", "status")) {
            return AssistantIntent.CURRENT_STATUS;
        }
        return AssistantIntent.UNKNOWN;
    }

    // 대소문자와 앞뒤 공백 차이를 줄이기 위해 질문을 정규화한다.
    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.KOREA);
    }

    // 질문에 지정한 키워드 중 하나라도 들어 있는지 확인한다.
    private boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
