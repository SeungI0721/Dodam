// 수조 모니터링 알림 기준값을 표현하는 모델 파일이다.
package com.example.dodam.domain.model;

public class AlertSettings {
    private final boolean dashboardAlertsEnabled;
    private final boolean notificationAlertsEnabled;
    private final boolean turbidityDashboardAlertEnabled;
    private final boolean turbidityNotificationAlertEnabled;
    private final boolean waterLevelDashboardAlertEnabled;
    private final boolean waterLevelNotificationAlertEnabled;
    private final double lowTemperatureC;
    private final double highTemperatureC;
    private final double turbidityWarning;
    private final int lowWaterLevelMm;

    public AlertSettings(boolean dashboardAlertsEnabled, boolean notificationAlertsEnabled,
                         double lowTemperatureC, double highTemperatureC,
                         double turbidityWarning, int lowWaterLevelMm) {
        this.dashboardAlertsEnabled = dashboardAlertsEnabled;
        this.notificationAlertsEnabled = notificationAlertsEnabled;
        this.turbidityDashboardAlertEnabled = dashboardAlertsEnabled;
        this.turbidityNotificationAlertEnabled = notificationAlertsEnabled;
        this.waterLevelDashboardAlertEnabled = dashboardAlertsEnabled;
        this.waterLevelNotificationAlertEnabled = notificationAlertsEnabled;
        this.lowTemperatureC = lowTemperatureC;
        this.highTemperatureC = highTemperatureC;
        this.turbidityWarning = turbidityWarning;
        this.lowWaterLevelMm = lowWaterLevelMm;
    }

    public AlertSettings(boolean turbidityDashboardAlertEnabled, boolean turbidityNotificationAlertEnabled,
                         boolean waterLevelDashboardAlertEnabled, boolean waterLevelNotificationAlertEnabled,
                         double lowTemperatureC, double highTemperatureC,
                         double turbidityWarning, int lowWaterLevelMm) {
        this.dashboardAlertsEnabled = turbidityDashboardAlertEnabled || waterLevelDashboardAlertEnabled;
        this.notificationAlertsEnabled = turbidityNotificationAlertEnabled || waterLevelNotificationAlertEnabled;
        this.turbidityDashboardAlertEnabled = turbidityDashboardAlertEnabled;
        this.turbidityNotificationAlertEnabled = turbidityNotificationAlertEnabled;
        this.waterLevelDashboardAlertEnabled = waterLevelDashboardAlertEnabled;
        this.waterLevelNotificationAlertEnabled = waterLevelNotificationAlertEnabled;
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

    public boolean isTurbidityDashboardAlertEnabled() {
        return turbidityDashboardAlertEnabled;
    }

    public boolean isTurbidityNotificationAlertEnabled() {
        return turbidityNotificationAlertEnabled;
    }

    public boolean isWaterLevelDashboardAlertEnabled() {
        return waterLevelDashboardAlertEnabled;
    }

    public boolean isWaterLevelNotificationAlertEnabled() {
        return waterLevelNotificationAlertEnabled;
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
