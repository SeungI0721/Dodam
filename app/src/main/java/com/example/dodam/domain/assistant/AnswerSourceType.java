// 도담이 답변이 어떤 근거에서 생성되었는지 구분하는 enum 파일이다.
package com.example.dodam.domain.assistant;

// 상태 답변, 로컬 FAQ, 관리 기록, 폴백 답변을 구분한다.
public enum AnswerSourceType {
    AQUARIUM_STATE,
    LOCAL_FAQ,
    MAINTENANCE_HISTORY,
    FALLBACK
}
