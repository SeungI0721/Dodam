// 수조 주의, 위험, 명령, 연결 상태 알림을 처리하는 알림 도우미 파일이다.
package com.example.dodam.notification;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.dodam.R;
import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.example.dodam.ui.dashboard.DashboardUiState;

public class AquariumAlertNotifier {
    private static final String CHANNEL_STATUS = "aquarium_status";
    private static final int REQUEST_NOTIFICATIONS = 7001;
    private static final long COOLDOWN_MS = 5 * 60 * 1000L;

    private final Context context;
    private AquariumStatus lastStatus;
    private DeviceConnectionState lastConnectionState;
    private long lastNotificationAt;

    public AquariumAlertNotifier(Context context) {
        this.context = context.getApplicationContext();
    }

    public void createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_STATUS,
                "수조 상태 알림",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("수온, 탁도, 수위, 장치 연결 상태 알림");
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    public void requestPermissionIfNeeded(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATIONS);
        }
    }

    public void notifyIfNeeded(DashboardUiState state) {
        if (!shouldNotify(state)) {
            return;
        }
        String title = buildTitle(state);
        String content = buildContent(state);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_STATUS)
                .setSmallIcon(R.drawable.p_rofile1)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(context).notify(1001, builder.build());
            lastNotificationAt = System.currentTimeMillis();
            lastStatus = state.getStatus();
            lastConnectionState = state.getConnectionState();
        }
    }

    private boolean shouldNotify(DashboardUiState state) {
        boolean statusNeedsAlert = state.getStatus() == AquariumStatus.WARNING
                || state.getStatus() == AquariumStatus.DANGER
                || state.getStatus() == AquariumStatus.STALE
                || state.getStatus() == AquariumStatus.ERROR;
        boolean connectionNeedsAlert = state.getConnectionState() == DeviceConnectionState.OFFLINE
                || state.getConnectionState() == DeviceConnectionState.ERROR;
        if (!statusNeedsAlert && !connectionNeedsAlert) {
            return false;
        }
        boolean changed = state.getStatus() != lastStatus || state.getConnectionState() != lastConnectionState;
        boolean cooledDown = System.currentTimeMillis() - lastNotificationAt > COOLDOWN_MS;
        return changed || cooledDown;
    }

    private String buildTitle(DashboardUiState state) {
        if (state.getConnectionState() == DeviceConnectionState.OFFLINE
                || state.getConnectionState() == DeviceConnectionState.ERROR) {
            return "수조 장치 연결 확인 필요";
        }
        if (state.getStatus() == AquariumStatus.DANGER) {
            return "수조 상태 위험";
        }
        return "수조 상태 주의";
    }

    private String buildContent(DashboardUiState state) {
        return "수온 " + state.getTemperatureText() + "°C, 탁도 "
                + state.getTurbidityText() + ", 수위 " + state.getWaterLevelText();
    }
}
