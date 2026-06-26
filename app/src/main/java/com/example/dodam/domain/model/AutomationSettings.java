// 장치 제어기에 전달할 자동화 설정값을 표현하는 모델 파일이다.
package com.example.dodam.domain.model;

public class AutomationSettings {
    private final boolean heaterAutoMode;
    private final double targetTemperatureC;
    private final double heaterOnBelowC;
    private final double heaterOffAboveC;
    private final boolean lightAutoMode;
    private final String lightOnTime;
    private final String lightOffTime;

    public AutomationSettings(boolean heaterAutoMode, double targetTemperatureC, double heaterOnBelowC,
                              double heaterOffAboveC, boolean lightAutoMode, String lightOnTime, String lightOffTime) {
        this.heaterAutoMode = heaterAutoMode;
        this.targetTemperatureC = targetTemperatureC;
        this.heaterOnBelowC = heaterOnBelowC;
        this.heaterOffAboveC = heaterOffAboveC;
        this.lightAutoMode = lightAutoMode;
        this.lightOnTime = lightOnTime;
        this.lightOffTime = lightOffTime;
    }

    public static AutomationSettings defaults() {
        return new AutomationSettings(false, 26.0, 24.5, 27.0, false, "08:00", "20:00");
    }

    public boolean isHeaterAutoMode() {
        return heaterAutoMode;
    }

    public double getTargetTemperatureC() {
        return targetTemperatureC;
    }

    public double getHeaterOnBelowC() {
        return heaterOnBelowC;
    }

    public double getHeaterOffAboveC() {
        return heaterOffAboveC;
    }

    public boolean isLightAutoMode() {
        return lightAutoMode;
    }

    public String getLightOnTime() {
        return lightOnTime;
    }

    public String getLightOffTime() {
        return lightOffTime;
    }
}
