// 센서 데이터로 계산한 수조 건강 상태를 표현하는 enum 파일이다.
package com.example.dodam.domain.model;

public enum AquariumStatus {
    NORMAL,
    WARNING,
    DANGER,
    STALE,
    EMPTY,
    ERROR
}
