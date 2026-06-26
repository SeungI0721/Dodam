// 수조 장치의 센서 데이터 수신과 제어 명령 전송을 정의하는 인터페이스 파일이다.
package com.example.dodam.data.device;

import com.example.dodam.domain.model.CommandResult;
import com.example.dodam.domain.model.ControlCommand;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.example.dodam.domain.model.SensorSnapshot;

public interface DeviceGateway {
    interface SensorListener {
        void onSensorSnapshot(SensorSnapshot snapshot);

        void onConnectionStateChanged(DeviceConnectionState state);

        void onDeviceError(String message);
    }

    interface CommandCallback {
        void onResult(CommandResult result);
    }

    void connect(String deviceId, SensorListener listener);

    void disconnect();

    void sendCommand(ControlCommand command, CommandCallback callback);
}
