// 사용자별 수조 자동화 설정과 알림 설정을 저장하는 로컬 설정 Repository 파일이다.
package com.example.dodam.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dodam.domain.model.AlertSettings;
import com.example.dodam.domain.model.AutomationSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsRepository {
    private static final String FILE_NAME = "dodam_settings";
    private final SharedPreferences preferences;

    public SettingsRepository(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public AutomationSettings loadAutomationSettings() {
        String prefix = prefix();
        return new AutomationSettings(
                preferences.getBoolean(prefix + "heater_auto", false),
                Double.longBitsToDouble(preferences.getLong(prefix + "target_temp", Double.doubleToLongBits(26.0))),
                Double.longBitsToDouble(preferences.getLong(prefix + "heater_on_below", Double.doubleToLongBits(24.5))),
                Double.longBitsToDouble(preferences.getLong(prefix + "heater_off_above", Double.doubleToLongBits(27.0))),
                preferences.getBoolean(prefix + "light_auto", false),
                preferences.getString(prefix + "light_on", "08:00"),
                preferences.getString(prefix + "light_off", "20:00")
        );
    }

    public AlertSettings loadAlertSettings() {
        String prefix = prefix();
        return new AlertSettings(
                preferences.getBoolean(prefix + "dashboard_alerts", true),
                preferences.getBoolean(prefix + "notification_alerts", true),
                Double.longBitsToDouble(preferences.getLong(prefix + "low_temp", Double.doubleToLongBits(23.0))),
                Double.longBitsToDouble(preferences.getLong(prefix + "high_temp", Double.doubleToLongBits(29.0))),
                Double.longBitsToDouble(preferences.getLong(prefix + "turbidity_warning", Double.doubleToLongBits(2.7))),
                preferences.getInt(prefix + "low_water_level", 500)
        );
    }

    public void save(AutomationSettings automationSettings, AlertSettings alertSettings) {
        String prefix = prefix();
        preferences.edit()
                .putBoolean(prefix + "heater_auto", automationSettings.isHeaterAutoMode())
                .putLong(prefix + "target_temp", Double.doubleToLongBits(automationSettings.getTargetTemperatureC()))
                .putLong(prefix + "heater_on_below", Double.doubleToLongBits(automationSettings.getHeaterOnBelowC()))
                .putLong(prefix + "heater_off_above", Double.doubleToLongBits(automationSettings.getHeaterOffAboveC()))
                .putBoolean(prefix + "light_auto", automationSettings.isLightAutoMode())
                .putString(prefix + "light_on", automationSettings.getLightOnTime())
                .putString(prefix + "light_off", automationSettings.getLightOffTime())
                .putBoolean(prefix + "dashboard_alerts", alertSettings.isDashboardAlertsEnabled())
                .putBoolean(prefix + "notification_alerts", alertSettings.isNotificationAlertsEnabled())
                .putLong(prefix + "low_temp", Double.doubleToLongBits(alertSettings.getLowTemperatureC()))
                .putLong(prefix + "high_temp", Double.doubleToLongBits(alertSettings.getHighTemperatureC()))
                .putLong(prefix + "turbidity_warning", Double.doubleToLongBits(alertSettings.getTurbidityWarning()))
                .putInt(prefix + "low_water_level", alertSettings.getLowWaterLevelMm())
                .apply();
    }

    private String prefix() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user == null ? "demo-user" : user.getUid();
        return uid + "_demo-aquarium_";
    }
}
