// 센서, 명령, 알림, 관리 기록 이벤트를 표현하는 모델 파일이다.
package com.example.dodam.domain.model;

public class AquariumEvent {
    private final String eventId;
    private final String aquariumId;
    private final long createdAt;
    private final String type;
    private final String message;

    public AquariumEvent(String eventId, String aquariumId, long createdAt, String type, String message) {
        this.eventId = eventId;
        this.aquariumId = aquariumId;
        this.createdAt = createdAt;
        this.type = type;
        this.message = message;
    }

    public String getEventId() {
        return eventId;
    }

    public String getAquariumId() {
        return aquariumId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
