// 수조 센서 상태, 제어 명령 상태, 이벤트 기록을 조율하는 Repository 파일이다.
package com.example.dodam.data.aquarium;

import com.example.dodam.data.device.DemoDeviceGateway;
import com.example.dodam.data.device.DeviceGateway;
import com.example.dodam.data.history.LocalEventStore;
import com.example.dodam.domain.model.AlertSettings;
import com.example.dodam.domain.model.AquariumEvent;
import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.CommandResult;
import com.example.dodam.domain.model.CommandStatus;
import com.example.dodam.domain.model.CommandType;
import com.example.dodam.domain.model.ControlCommand;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.example.dodam.domain.model.SensorSnapshot;
import com.example.dodam.domain.usecase.AquariumStatusEvaluator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AquariumRepository {
    public interface StateListener {
        void onStateChanged(AquariumRepositoryState state);
    }

    private static AquariumRepository instance;

    private final DeviceGateway gateway;
    private final AquariumStatusEvaluator evaluator = new AquariumStatusEvaluator();
    private final List<StateListener> listeners = new ArrayList<>();
    private final List<AquariumEvent> events = new ArrayList<>();
    private AlertSettings alertSettings = AlertSettings.defaults();
    private AquariumRepositoryState state = AquariumRepositoryState.initial();
    private boolean commandInFlight;

    private AquariumRepository(DeviceGateway gateway) {
        this.gateway = gateway;
    }

    public static synchronized AquariumRepository getInstance() {
        if (instance == null) {
            instance = new AquariumRepository(new DemoDeviceGateway());
        }
        return instance;
    }

    public void observe(StateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
        listener.onStateChanged(state);
    }

    public void removeObserver(StateListener listener) {
        listeners.remove(listener);
    }

    public void startDemoDevice(String deviceId) {
        gateway.connect(deviceId, new DeviceGateway.SensorListener() {
            @Override
            public void onSensorSnapshot(SensorSnapshot snapshot) {
                AquariumStatus status = evaluator.evaluate(snapshot, alertSettings, System.currentTimeMillis());
                state = state.withSnapshot(snapshot, status);
                addEvent("SENSOR", "센서 데이터 수신: " + formatTemperature(snapshot.getTemperatureC()));
                notifyListeners();
            }

            @Override
            public void onConnectionStateChanged(DeviceConnectionState connectionState) {
                state = state.withConnectionState(connectionState);
                addEvent("CONNECTION", "장치 연결 상태: " + connectionState.name());
                notifyListeners();
            }

            @Override
            public void onDeviceError(String message) {
                state = state.withError(message);
                addEvent("ERROR", message);
                notifyListeners();
            }
        });
    }

    public void stopDevice() {
        gateway.disconnect();
    }

    public void updateAlertSettings(AlertSettings alertSettings) {
        this.alertSettings = alertSettings;
        SensorSnapshot snapshot = state.getSnapshot();
        AquariumStatus status = evaluator.evaluate(snapshot, alertSettings, System.currentTimeMillis());
        state = state.withStatus(status);
        addEvent("SETTINGS", "알림 기준이 저장되었습니다.");
        notifyListeners();
    }

    public void toggleHeater() {
        SensorSnapshot snapshot = state.getSnapshot();
        sendBooleanCommand(CommandType.SET_HEATER, snapshot == null || !snapshot.isHeaterOn());
    }

    public void toggleLight() {
        SensorSnapshot snapshot = state.getSnapshot();
        sendBooleanCommand(CommandType.SET_LIGHT, snapshot == null || !snapshot.isLightOn());
    }

    public List<AquariumEvent> getEvents() {
        return new ArrayList<>(events);
    }

    private void sendBooleanCommand(CommandType type, boolean value) {
        if (commandInFlight) {
            state = state.withCommandMessage("이전 명령 처리 중입니다.");
            notifyListeners();
            return;
        }
        commandInFlight = true;
        String commandId = "cmd-" + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.KOREA).format(new Date())
                + "-" + UUID.randomUUID().toString().substring(0, 4);
        String deviceId = state.getSnapshot() == null ? "tank-demo-001" : state.getSnapshot().getDeviceId();
        ControlCommand command = new ControlCommand(commandId, deviceId, type, value, System.currentTimeMillis());
        state = state.withCommandMessage("명령 전송 중: " + commandId);
        addEvent("COMMAND", type.name() + " 요청");
        notifyListeners();

        gateway.sendCommand(command, result -> {
            commandInFlight = false;
            handleCommandResult(type, result);
        });
    }

    private void handleCommandResult(CommandType type, CommandResult result) {
        String message = result.getStatus() == CommandStatus.APPLIED ? "명령 적용 완료" : "명령 실패";
        state = state.withCommandResult(result, message + ": " + result.getMessage());
        addEvent("COMMAND_RESULT", type.name() + " " + result.getStatus().name());
        notifyListeners();
    }

    private void addEvent(String type, String message) {
        AquariumEvent event = new AquariumEvent(UUID.randomUUID().toString(), "demo-aquarium",
                System.currentTimeMillis(), type, message);
        events.add(0, event);
        if (events.size() > 100) {
            events.remove(events.size() - 1);
        }
        LocalEventStore store = LocalEventStore.getInstanceOrNull();
        if (store != null) {
            store.append(event);
        }
    }

    private void notifyListeners() {
        for (StateListener listener : new ArrayList<>(listeners)) {
            listener.onStateChanged(state);
        }
    }

    private String formatTemperature(double value) {
        return String.format(Locale.KOREA, "%.1f°C", value);
    }
}
