// 도담이 챗봇이 분류할 수 있는 사용자 질문 의도 enum 파일이다.
package com.example.dodam.domain.assistant;

// 상태 조회, FAQ, 관리 기록, 장치 제어 같은 대화 목적을 구분한다.
public enum AssistantIntent {
    CURRENT_STATUS,
    TEMPERATURE_STATUS,
    WATER_LEVEL_STATUS,
    TURBIDITY_STATUS,
    DEVICE_CONNECTION,
    HEATER_REASON,
    LIGHT_REASON,
    RECENT_ALERTS,
    NEXT_MAINTENANCE,
    APP_HELP,
    FAQ,
    DEVICE_CONTROL,
    UNKNOWN
}
