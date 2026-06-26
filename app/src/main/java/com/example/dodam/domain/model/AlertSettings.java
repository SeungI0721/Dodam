// 수조 모니터링 알림 기준값을 표현하는 모델 파일이다.
package com.example.dodam.domain.model;

public class AlertSettings {
    private final boolean dashboardAlertsEnabled;
    private final boolean notificationAlertsEnabled;
    private final double lowTemperatureC;
    private final double highTemperatureC;
    private final double turbidityWarning;
    private final int lowWaterLevelMm;

    public AlertSettings(boolean dashboardAlertsEnabled, boolean notificationAlertsEnabled,
                         double lowTemperatureC, double highTemperatureC,
                         double turbidityWarning, int lowWaterLevelMm) {
        this.dashboardAlertsEnabled = dashboardAlertsEnabled;
        this.notificationAlertsEnabled = notificationAlertsEnabled;
        this.lowTemperatureC = lowTemperatureC;
        this.highTemperatureC = highTemperatureC;
        this.turbidityWarning = turbidityWarning;
        this.lowWaterLevelMm = lowWaterLevelMm;
    }

    public static AlertSettings defaults() {
        return new AlertSettings(true, true, 23.0, 29.0, 2.7, 500);
    }

    public boolean isDashboardAlertsEnabled() {
        return dashboardAlertsEnabled;
    }

    public boolean isNotificationAlertsEnabled() {
        return notificationAlertsEnabled;
    }

    public double getLowTemperatureC() {
        return lowTemperatureC;
    }

    public double getHighTemperatureC() {
        return highTemperatureC;
    }

    public double getTurbidityWarning() {
        return turbidityWarning;
    }

    public int getLowWaterLevelMm() {
        return lowWaterLevelMm;
    }
}
