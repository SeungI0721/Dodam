// 수조 센서 상태, 장치 명령, 이벤트 기록을 조율하는 Repository 파일이다.
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

// DemoDeviceGateway를 통해 앱 단독 수조 상태와 명령 ACK를 관리한다.
public class AquariumRepository {
    // 수조 상태 변경을 화면이나 ViewModel에 전달하는 관찰자다.
    public interface StateListener {
        void onStateChanged(AquariumRepositoryState state);
    }

    // 장치 명령이 끝났을 때 결과를 호출자에게 전달하는 콜백이다.
    public interface CommandCompletionListener {
        void onCommandCompleted(CommandResult result);
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

    // 앱 전체에서 하나의 수조 Repository 인스턴스를 공유한다.
    public static synchronized AquariumRepository getInstance() {
        if (instance == null) {
            instance = new AquariumRepository(new DemoDeviceGateway());
        }
        return instance;
    }

    // 상태 관찰자를 등록하고 현재 상태를 즉시 전달한다.
    public void observe(StateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
        listener.onStateChanged(state);
    }

    public void removeObserver(StateListener listener) {
        listeners.remove(listener);
    }

    public AquariumRepositoryState getState() {
        return state;
    }

    // 실제 HW 대신 데모 장치 연결을 시작하고 센서값을 수신한다.
    public void startDemoDevice(String deviceId) {
        gateway.connect(deviceId, new DeviceGateway.SensorListener() {
            @Override
            public void onSensorSnapshot(SensorSnapshot snapshot) {
                AquariumStatus status = evaluator.evaluate(snapshot, alertSettings, System.currentTimeMillis());
                state = state.withSnapshot(snapshot, status);
                addEvent("SENSOR", "Sensor data received: " + formatTemperature(snapshot.getTemperatureC()));
                notifyListeners();
            }

            @Override
            public void onConnectionStateChanged(DeviceConnectionState connectionState) {
                state = state.withConnectionState(connectionState);
                addEvent("CONNECTION", "Device connection state: " + connectionState.name());
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
        addEvent("SETTINGS", "Alert settings saved.");
        notifyListeners();
    }

    // 현재 히터 상태를 반대로 바꾸는 데모 명령을 보낸다.
    public void toggleHeater() {
        SensorSnapshot snapshot = state.getSnapshot();
        sendBooleanCommand(CommandType.SET_HEATER, snapshot == null || !snapshot.isHeaterOn(), null);
    }

    // 현재 조명 상태를 반대로 바꾸는 데모 명령을 보낸다.
    public void toggleLight() {
        SensorSnapshot snapshot = state.getSnapshot();
        sendBooleanCommand(CommandType.SET_LIGHT, snapshot == null || !snapshot.isLightOn(), null);
    }

    // 히터를 명시한 상태로 변경하고 ACK 결과를 콜백으로 전달한다.
    public void setHeater(boolean enabled, CommandCompletionListener listener) {
        sendBooleanCommand(CommandType.SET_HEATER, enabled, listener);
    }

    // 조명을 명시한 상태로 변경하고 ACK 결과를 콜백으로 전달한다.
    public void setLight(boolean enabled, CommandCompletionListener listener) {
        sendBooleanCommand(CommandType.SET_LIGHT, enabled, listener);
    }

    public List<AquariumEvent> getEvents() {
        return new ArrayList<>(events);
    }

    // boolean 기반 장치 명령을 만들고 DemoDeviceGateway에 전송한다.
    private void sendBooleanCommand(CommandType type, boolean value, CommandCompletionListener listener) {
        if (commandInFlight) {
            state = state.withCommandMessage("이전 명령 처리 중입니다.");
            notifyListeners();
            if (listener != null) {
                listener.onCommandCompleted(new CommandResult("none", CommandStatus.FAILED, value,
                        System.currentTimeMillis(), "Previous command is still in flight."));
            }
            return;
        }
        commandInFlight = true;
        String commandId = "cmd-" + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.KOREA).format(new Date())
                + "-" + UUID.randomUUID().toString().substring(0, 4);
        String deviceId = state.getSnapshot() == null ? "tank-demo-001" : state.getSnapshot().getDeviceId();
        ControlCommand command = new ControlCommand(commandId, deviceId, type, value, System.currentTimeMillis());
        state = state.withCommandMessage("명령 전송 중: " + commandId);
        addEvent("COMMAND", type.name() + " requested");
        notifyListeners();

        gateway.sendCommand(command, result -> {
            commandInFlight = false;
            handleCommandResult(type, result);
            if (listener != null) {
                listener.onCommandCompleted(result);
            }
        });
    }

    // 장치 명령 결과를 Repository 상태와 이벤트 기록에 반영한다.
    private void handleCommandResult(CommandType type, CommandResult result) {
        String message = result.getStatus() == CommandStatus.APPLIED ? "명령 적용 완료" : "명령 실패";
        state = state.withCommandResult(result, message + ": " + result.getMessage());
        addEvent("COMMAND_RESULT", type.name() + " " + result.getStatus().name());
        notifyListeners();
    }

    // 센서, 연결, 명령 이벤트를 메모리와 로컬 저장소에 기록한다.
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
