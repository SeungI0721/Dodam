// 수조 자동화 설정과 알림 설정을 저장하는 Activity 파일이다.
package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dodam.data.aquarium.AquariumRepository;
import com.example.dodam.data.local.SettingsRepository;
import com.example.dodam.domain.model.AlertSettings;
import com.example.dodam.domain.model.AutomationSettings;
import com.example.dodam.domain.usecase.AquariumInputValidator;

public class Setting extends AppCompatActivity {

    private Button btu_bac;
    private Button btu_sa;
    private Button btu_heater;
    private Button btu_wq_m;
    private Button btu_wq_t;
    private Button btu_wl_m;
    private Button btu_wl_t;
    private EditText tv_suon;
    private EditText li_on;
    private EditText li_off;

    private boolean heaterAutoMode;
    private boolean turbidityDashboardAlert;
    private boolean turbidityNotificationAlert;
    private boolean waterLevelDashboardAlert;
    private boolean waterLevelNotificationAlert;
    private SettingsRepository settingsRepository;
    private final AquariumInputValidator validator = new AquariumInputValidator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 설정 화면 XML과 Activity를 연결한다.
        setContentView(R.layout.activity_setting);

        // 설정 화면의 입력칸과 버튼을 연결한다.
        btu_bac = findViewById(R.id.btu_bac);
        btu_sa = findViewById(R.id.btu_sa);
        btu_heater = findViewById(R.id.btu_heater);
        btu_wq_m = findViewById(R.id.btu_wq_m);
        btu_wq_t = findViewById(R.id.btu_wq_t);
        btu_wl_m = findViewById(R.id.btu_wl_m);
        btu_wl_t = findViewById(R.id.btu_wl_t);
        tv_suon = findViewById(R.id.tv_suon);
        li_on = findViewById(R.id.li_on);
        li_off = findViewById(R.id.li_off);

        // 저장된 설정값을 불러와 화면에 반영한다.
        settingsRepository = new SettingsRepository(this);
        AutomationSettings automationSettings = settingsRepository.loadAutomationSettings();
        AlertSettings alertSettings = settingsRepository.loadAlertSettings();
        heaterAutoMode = automationSettings.isHeaterAutoMode();
        turbidityDashboardAlert = alertSettings.isDashboardAlertsEnabled();
        turbidityNotificationAlert = alertSettings.isNotificationAlertsEnabled();
        waterLevelDashboardAlert = alertSettings.isDashboardAlertsEnabled();
        waterLevelNotificationAlert = alertSettings.isNotificationAlertsEnabled();

        tv_suon.setText(String.valueOf(automationSettings.getTargetTemperatureC()));
        li_on.setText(automationSettings.getLightOnTime());
        li_off.setText(automationSettings.getLightOffTime());
        renderButtonStates();

        // 뒤로가기 버튼을 누르면 메인 대시보드로 돌아간다.
        btu_bac.setOnClickListener(v -> {
            startActivity(new Intent(Setting.this, MainScreen.class));
            finish();
        });

        // 저장 버튼을 누르면 입력값을 검증한 뒤 설정을 저장한다.
        btu_sa.setOnClickListener(v -> saveSettings());

        // 설정 토글 버튼을 누르면 각 설정의 활성화 상태를 바꾼다.
        btu_heater.setOnClickListener(v -> {
            heaterAutoMode = !heaterAutoMode;
            renderButtonStates();
        });
        btu_wq_m.setOnClickListener(v -> {
            turbidityDashboardAlert = !turbidityDashboardAlert;
            renderButtonStates();
        });
        btu_wq_t.setOnClickListener(v -> {
            turbidityNotificationAlert = !turbidityNotificationAlert;
            renderButtonStates();
        });
        btu_wl_m.setOnClickListener(v -> {
            waterLevelDashboardAlert = !waterLevelDashboardAlert;
            renderButtonStates();
        });
        btu_wl_t.setOnClickListener(v -> {
            waterLevelNotificationAlert = !waterLevelNotificationAlert;
            renderButtonStates();
        });
    }

    private void saveSettings() {
        double targetTemperature = parseDouble(tv_suon.getText().toString(), -1);
        if (!validator.isValidTargetTemperature(targetTemperature)) {
            Toast.makeText(this, "목표 수온은 18~32°C 사이로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String lightOn = normalizeTime(li_on.getText().toString(), "08:00");
        String lightOff = normalizeTime(li_off.getText().toString(), "20:00");
        if (!validator.isValidLightSchedule(lightOn, lightOff)) {
            Toast.makeText(this, "조명 ON/OFF 시각은 HH:mm 형식이며 서로 달라야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        AutomationSettings automationSettings = new AutomationSettings(
                heaterAutoMode,
                targetTemperature,
                targetTemperature - 1.0,
                targetTemperature + 1.0,
                true,
                lightOn,
                lightOff
        );
        AlertSettings alertSettings = new AlertSettings(
                turbidityDashboardAlert || waterLevelDashboardAlert,
                turbidityNotificationAlert || waterLevelNotificationAlert,
                23.0,
                29.0,
                2.7,
                500
        );
        settingsRepository.save(automationSettings, alertSettings);
        AquariumRepository.getInstance().updateAlertSettings(alertSettings);
        Toast.makeText(this, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void renderButtonStates() {
        btu_heater.setBackgroundResource(heaterAutoMode ? R.drawable.bt_on : R.drawable.bt_off);
        btu_wq_m.setBackgroundResource(turbidityDashboardAlert ? R.drawable.bt_on : R.drawable.bt_off);
        btu_wq_t.setBackgroundResource(turbidityNotificationAlert ? R.drawable.bt_on : R.drawable.bt_off);
        btu_wl_m.setBackgroundResource(waterLevelDashboardAlert ? R.drawable.bt_on : R.drawable.bt_off);
        btu_wl_t.setBackgroundResource(waterLevelNotificationAlert ? R.drawable.bt_on : R.drawable.bt_off);
    }

    private double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String normalizeTime(String value, String fallback) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.matches("^([01]\\d|2[0-3]):[0-5]\\d$") ? trimmed : fallback;
    }
}
