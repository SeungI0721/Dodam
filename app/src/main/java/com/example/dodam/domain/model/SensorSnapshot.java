// 수조 장치에서 수신한 센서 측정값 묶음을 표현하는 모델 파일이다.
package com.example.dodam.domain.model;

public class SensorSnapshot {
    private final String deviceId;
    private final long timestamp;
    private final double temperatureC;
    private final double turbidity;
    private final int waterLevelMm;
    private final boolean heaterOn;
    private final boolean lightOn;
    private final ControlMode controlMode;

    public SensorSnapshot(String deviceId, long timestamp, double temperatureC, double turbidity,
                          int waterLevelMm, boolean heaterOn, boolean lightOn, ControlMode controlMode) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.temperatureC = temperatureC;
        this.turbidity = turbidity;
        this.waterLevelMm = waterLevelMm;
        this.heaterOn = heaterOn;
        this.lightOn = lightOn;
        this.controlMode = controlMode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getTemperatureC() {
        return temperatureC;
    }

    public double getTurbidity() {
        return turbidity;
    }

    public int getWaterLevelMm() {
        return waterLevelMm;
    }

    public boolean isHeaterOn() {
        return heaterOn;
    }

    public boolean isLightOn() {
        return lightOn;
    }

    public ControlMode getControlMode() {
        return controlMode;
    }
}
