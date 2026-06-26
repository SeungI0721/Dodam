// 대시보드에 표시할 장치 연결 상태를 표현하는 enum 파일이다.
package com.example.dodam.domain.model;

public enum DeviceConnectionState {
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    OFFLINE,
    ERROR
}
