// 수조 상태 모니터링과 수동 제어를 담당하는 메인 대시보드 Activity 파일이다.
package com.example.dodam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.example.dodam.data.aquarium.UserAquariumRepository;
import com.example.dodam.domain.model.Aquarium;
import com.example.dodam.data.history.LocalEventStore;
import com.example.dodam.notification.AquariumAlertNotifier;
import com.example.dodam.ui.dashboard.DashboardUiState;
import com.example.dodam.ui.dashboard.DashboardViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainScreen extends AppCompatActivity {

    private Button btu_community, btu_setting, btu_chatbot, btu_heter, btu_light;
    private TextView tv_name, tv_point, tv_team, tv_dashboard_detail;
    private ImageView member_photo, Light_bt, tem_im, water_im;
    private DashboardViewModel viewModel;
    private AquariumAlertNotifier alertNotifier;
    private AquariumStatus lastDialogStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 메인 대시보드 화면 XML과 Activity를 연결한다.
        setContentView(R.layout.activity_main_screen);

        // 대시보드 ViewModel과 알림 도우미를 준비한다.
        LocalEventStore.initialize(this);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        alertNotifier = new AquariumAlertNotifier(this);
        alertNotifier.createChannels();
        alertNotifier.requestPermissionIfNeeded(this);

        // 메인 대시보드 화면의 버튼과 표시 영역을 연결한다.
        btu_community = findViewById(R.id.btu_community);
        btu_chatbot = findViewById(R.id.btu_chatbot);
        btu_setting = findViewById(R.id.btu_setting);
        btu_heter = findViewById(R.id.btu_heter);
        btu_light = findViewById(R.id.btu_light);

        tv_name = findViewById(R.id.tv_name);
        tv_point = findViewById(R.id.tv_point);
        tv_team = findViewById(R.id.tv_team);
        tv_dashboard_detail = findViewById(R.id.tv_dashboard_detail);

        member_photo = findViewById(R.id.member_photo);
        Light_bt = findViewById(R.id.Light_bt);
        tem_im = findViewById(R.id.tem_im);
        water_im = findViewById(R.id.water_im);

        // 로그인된 사용자 정보를 화면에 표시한다.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String displayName = user != null && user.getDisplayName() != null ? user.getDisplayName() : getIntent().getStringExtra("userName");
        tv_name.setText(displayName == null || displayName.trim().isEmpty() ? "도담 사용자" : displayName);
        tv_point.setText("0");
        member_photo.setImageResource(R.drawable.p_rofile1);
        member_photo.setOnClickListener(v -> startActivity(new Intent(MainScreen.this, ProfileActivity.class)));
        member_photo.setOnLongClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainScreen.this, MainActivity.class));
            finish();
            return true;
        });

        // 히터와 조명 수동 제어 명령을 ViewModel로 전달한다.
        btu_light.setOnClickListener(v -> viewModel.toggleLight());
        btu_heter.setOnClickListener(v -> viewModel.toggleHeater());

        // 하단 버튼으로 커뮤니티, 도담이, 설정, 기록, 수조 관리 화면을 이동한다.
        btu_community.setOnClickListener(v -> startActivity(new Intent(MainScreen.this, community_f.class)));
        btu_community.setOnLongClickListener(v -> {
            startActivity(new Intent(MainScreen.this, HistoryActivity.class));
            return true;
        });
        btu_chatbot.setOnClickListener(v -> startActivity(new Intent(MainScreen.this, Dodam_Cat.class)));
        btu_setting.setOnClickListener(v -> startActivity(new Intent(MainScreen.this, Setting.class)));
        btu_setting.setOnLongClickListener(v -> {
            startActivity(new Intent(MainScreen.this, AquariumManageActivity.class));
            return true;
        });

        // 대시보드 상태를 관찰하고 UI를 갱신한다.
        viewModel.getUiState().observe(this, this::renderState);
        Aquarium aquarium = new UserAquariumRepository(this).loadSelectedOrDefault();
        viewModel.start(aquarium.getDeviceId());
    }

    private void renderState(DashboardUiState state) {
        tv_team.setText(state.getTemperatureText());
        tv_point.setText(statusLabel(state));
        tv_dashboard_detail.setText("탁도 " + state.getTurbidityText()
                + " / 수위 " + state.getWaterLevelText()
                + "\n연결 " + state.getConnectionState().name()
                + " / 업데이트 " + state.getLastUpdatedText()
                + "\n홈 길게: 기록 / 설정 길게: 수조");

        updateTemperatureImage(state.getTemperatureText());
        updateTurbidityImage(state.getTurbidityText());
        btu_heter.setBackgroundResource(state.isHeaterOn() ? R.drawable.bt_heater_on : R.drawable.bt_heater_off);
        Light_bt.setImageResource(state.isLightOn() ? R.drawable.bt_light_on : R.drawable.bt_light_off);

        if (state.getCommandMessage() != null && !state.getCommandMessage().trim().isEmpty()) {
            Toast.makeText(this, state.getCommandMessage(), Toast.LENGTH_SHORT).show();
        }

        alertNotifier.notifyIfNeeded(state);
        showDashboardDialogIfNeeded(state);
    }

    private void updateTemperatureImage(String temperatureText) {
        double temperature = parseDouble(temperatureText, 0);
        if (temperature < 24.0) {
            tem_im.setImageResource(R.drawable.m_tem1);
        } else if (temperature < 28.0) {
            tem_im.setImageResource(R.drawable.m_tem2);
        } else {
            tem_im.setImageResource(R.drawable.m_tem3);
        }
    }

    private void updateTurbidityImage(String turbidityText) {
        double turbidity = parseDouble(turbidityText, 0);
        if (turbidity >= 2.7) {
            water_im.setImageResource(R.drawable.m_water3);
        } else if (turbidity >= 2.4) {
            water_im.setImageResource(R.drawable.m_water2);
        } else {
            water_im.setImageResource(R.drawable.m_water1);
        }
    }

    private void showDashboardDialogIfNeeded(DashboardUiState state) {
        if (state.getStatus() == lastDialogStatus) {
            return;
        }
        if (state.getStatus() == AquariumStatus.WARNING || state.getStatus() == AquariumStatus.DANGER
                || state.getConnectionState() == DeviceConnectionState.OFFLINE
                || state.getConnectionState() == DeviceConnectionState.ERROR) {
            lastDialogStatus = state.getStatus();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
            Dialog dialog = builder.setMessage(statusLabel(state)).setNegativeButton("확인", null).create();
            dialog.show();
        }
    }

    private String statusLabel(DashboardUiState state) {
        if (state.getConnectionState() == DeviceConnectionState.CONNECTING) {
            return "연결 중";
        }
        if (state.getConnectionState() == DeviceConnectionState.OFFLINE) {
            return "오프라인";
        }
        if (state.getConnectionState() == DeviceConnectionState.ERROR) {
            return "오류";
        }
        switch (state.getStatus()) {
            case NORMAL:
                return "정상";
            case WARNING:
                return "주의";
            case DANGER:
                return "위험";
            case STALE:
                return "지연";
            case EMPTY:
                return "대기";
            default:
                return "확인";
        }
    }

    private double parseDouble(String text, double fallback) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
