// 실제 하드웨어 없이 수조 센서 데이터를 시뮬레이션하는 Demo 장치 게이트웨이 파일이다.
package com.example.dodam.data.device;

import android.os.Handler;
import android.os.Looper;

import com.example.dodam.domain.model.CommandResult;
import com.example.dodam.domain.model.CommandStatus;
import com.example.dodam.domain.model.CommandType;
import com.example.dodam.domain.model.ControlCommand;
import com.example.dodam.domain.model.ControlMode;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.example.dodam.domain.model.SensorSnapshot;

import java.util.Random;

public class DemoDeviceGateway implements DeviceGateway {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private SensorListener listener;
    private String deviceId = "tank-demo-001";
    private boolean running;
    private boolean heaterOn;
    private boolean lightOn = true;
    private double temperatureC = 25.8;
    private double turbidity = 2.3;
    private int waterLevelMm = 580;

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            if (!running || listener == null) {
                return;
            }
            updateDemoValues();
            listener.onSensorSnapshot(new SensorSnapshot(
                    deviceId,
                    System.currentTimeMillis(),
                    temperatureC,
                    turbidity,
                    waterLevelMm,
                    heaterOn,
                    lightOn,
                    ControlMode.MANUAL
            ));
            handler.postDelayed(this, 3_000L);
        }
    };

    @Override
    public void connect(String deviceId, SensorListener listener) {
        this.deviceId = deviceId == null || deviceId.trim().isEmpty() ? this.deviceId : deviceId;
        this.listener = listener;
        this.running = true;
        listener.onConnectionStateChanged(DeviceConnectionState.CONNECTING);
        handler.postDelayed(() -> {
            if (running && this.listener != null) {
                this.listener.onConnectionStateChanged(DeviceConnectionState.CONNECTED);
                tick.run();
            }
        }, 500L);
    }

    @Override
    public void disconnect() {
        running = false;
        handler.removeCallbacks(tick);
        if (listener != null) {
            listener.onConnectionStateChanged(DeviceConnectionState.OFFLINE);
        }
        listener = null;
    }

    @Override
    public void sendCommand(ControlCommand command, CommandCallback callback) {
        handler.postDelayed(() -> {
            if (!running) {
                callback.onResult(new CommandResult(command.getCommandId(), CommandStatus.TIMEOUT,
                        command.getValue(), System.currentTimeMillis(), "장치가 오프라인입니다."));
                return;
            }
            if (command.getType() == CommandType.SET_HEATER) {
                heaterOn = command.getValue();
            } else if (command.getType() == CommandType.SET_LIGHT) {
                lightOn = command.getValue();
            }
            callback.onResult(new CommandResult(command.getCommandId(), CommandStatus.APPLIED,
                    command.getValue(), System.currentTimeMillis(), "데모 장치 ACK"));
            if (listener != null) {
                listener.onSensorSnapshot(new SensorSnapshot(deviceId, System.currentTimeMillis(),
                        temperatureC, turbidity, waterLevelMm, heaterOn, lightOn, ControlMode.MANUAL));
            }
        }, 700L);
    }

    private void updateDemoValues() {
        temperatureC += (random.nextDouble() - 0.45) * 0.3;
        if (heaterOn) {
            temperatureC += 0.1;
        }
        turbidity = Math.max(1.8, Math.min(3.2, turbidity + (random.nextDouble() - 0.5) * 0.08));
        waterLevelMm = Math.max(450, Math.min(620, waterLevelMm + random.nextInt(7) - 3));
    }
}
