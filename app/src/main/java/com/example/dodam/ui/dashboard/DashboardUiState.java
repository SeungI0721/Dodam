// 기존 MainScreen 대시보드에 표시할 UI 상태 모델 파일이다.
package com.example.dodam.ui.dashboard;

import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.DeviceConnectionState;

public class DashboardUiState {
    private final String temperatureText;
    private final String turbidityText;
    private final String waterLevelText;
    private final boolean heaterOn;
    private final boolean lightOn;
    private final String lastUpdatedText;
    private final DeviceConnectionState connectionState;
    private final AquariumStatus status;
    private final String commandMessage;
    private final String errorMessage;

    public DashboardUiState(String temperatureText, String turbidityText, String waterLevelText,
                            boolean heaterOn, boolean lightOn, String lastUpdatedText,
                            DeviceConnectionState connectionState, AquariumStatus status,
                            String commandMessage, String errorMessage) {
        this.temperatureText = temperatureText;
        this.turbidityText = turbidityText;
        this.waterLevelText = waterLevelText;
        this.heaterOn = heaterOn;
        this.lightOn = lightOn;
        this.lastUpdatedText = lastUpdatedText;
        this.connectionState = connectionState;
        this.status = status;
        this.commandMessage = commandMessage;
        this.errorMessage = errorMessage;
    }

    public String getTemperatureText() {
        return temperatureText;
    }

    public String getTurbidityText() {
        return turbidityText;
    }

    public String getWaterLevelText() {
        return waterLevelText;
    }

    public boolean isHeaterOn() {
        return heaterOn;
    }

    public boolean isLightOn() {
        return lightOn;
    }

    public String getLastUpdatedText() {
        return lastUpdatedText;
    }

    public DeviceConnectionState getConnectionState() {
        return connectionState;
    }

    public AquariumStatus getStatus() {
        return status;
    }

    public String getCommandMessage() {
        return commandMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
