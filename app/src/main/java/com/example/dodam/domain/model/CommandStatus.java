// 장치로 보낸 명령의 처리 상태를 표현하는 enum 파일이다.
package com.example.dodam.domain.model;

public enum CommandStatus {
    PENDING,
    APPLIED,
    FAILED,
    TIMEOUT
}
