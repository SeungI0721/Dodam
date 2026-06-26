// 도담이 답변 생성에 필요한 현재 수조 상황을 모아두는 컨텍스트 모델 파일이다.
package com.example.dodam.domain.assistant;

import com.example.dodam.domain.model.AlertSettings;
import com.example.dodam.domain.model.AquariumEvent;
import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.AutomationSettings;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.example.dodam.domain.model.MaintenanceRecord;
import com.example.dodam.domain.model.SensorSnapshot;

import java.util.ArrayList;
import java.util.List;

// 사용자, 수조, 센서, 연결 상태, 이벤트, 관리 기록을 하나의 읽기 전용 객체로 전달한다.
public class AssistantContext {
    private final String userId;
    private final String aquariumId;
    private final SensorSnapshot sensorSnapshot;
    private final DeviceConnectionState connectionState;
    private final AquariumStatus aquariumStatus;
    private final AlertSettings alertSettings;
    private final AutomationSettings automationSettings;
    private final List<AquariumEvent> recentEvents;
    private final List<MaintenanceRecord> maintenanceRecords;
    private final long generatedAt;

    // 답변 생성 시점에 수집한 모든 상태 값을 컨텍스트로 묶는다.
    public AssistantContext(String userId, String aquariumId, SensorSnapshot sensorSnapshot,
                            DeviceConnectionState connectionState, AquariumStatus aquariumStatus,
                            AlertSettings alertSettings, AutomationSettings automationSettings,
                            List<AquariumEvent> recentEvents, List<MaintenanceRecord> maintenanceRecords,
                            long generatedAt) {
        this.userId = userId;
        this.aquariumId = aquariumId;
        this.sensorSnapshot = sensorSnapshot;
        this.connectionState = connectionState;
        this.aquariumStatus = aquariumStatus;
        this.alertSettings = alertSettings;
        this.automationSettings = automationSettings;
        this.recentEvents = recentEvents == null ? new ArrayList<>() : new ArrayList<>(recentEvents);
        this.maintenanceRecords = maintenanceRecords == null ? new ArrayList<>() : new ArrayList<>(maintenanceRecords);
        this.generatedAt = generatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getAquariumId() {
        return aquariumId;
    }

    public SensorSnapshot getSensorSnapshot() {
        return sensorSnapshot;
    }

    public DeviceConnectionState getConnectionState() {
        return connectionState;
    }

    public AquariumStatus getAquariumStatus() {
        return aquariumStatus;
    }

    public AlertSettings getAlertSettings() {
        return alertSettings;
    }

    public AutomationSettings getAutomationSettings() {
        return automationSettings;
    }

    public List<AquariumEvent> getRecentEvents() {
        return new ArrayList<>(recentEvents);
    }

    public List<MaintenanceRecord> getMaintenanceRecords() {
        return new ArrayList<>(maintenanceRecords);
    }

    public long getGeneratedAt() {
        return generatedAt;
    }
}
