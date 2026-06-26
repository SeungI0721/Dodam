// 환수, 급여, 청소 같은 수조 관리 기록을 표현하는 모델 파일이다.
package com.example.dodam.domain.model;

public class MaintenanceRecord {
    private final String recordId;
    private final String aquariumId;
    private final long createdAt;
    private final String category;
    private final String memo;

    public MaintenanceRecord(String recordId, String aquariumId, long createdAt, String category, String memo) {
        this.recordId = recordId;
        this.aquariumId = aquariumId;
        this.createdAt = createdAt;
        this.category = category;
        this.memo = memo;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getAquariumId() {
        return aquariumId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getCategory() {
        return category;
    }

    public String getMemo() {
        return memo;
    }
}
