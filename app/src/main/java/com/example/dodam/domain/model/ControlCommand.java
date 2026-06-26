// 앱에서 수조 장치 게이트웨이로 보내는 제어 명령 모델 파일이다.
package com.example.dodam.domain.model;

public class ControlCommand {
    private final String commandId;
    private final String deviceId;
    private final CommandType type;
    private final boolean value;
    private final long requestedAt;

    public ControlCommand(String commandId, String deviceId, CommandType type, boolean value, long requestedAt) {
        this.commandId = commandId;
        this.deviceId = deviceId;
        this.type = type;
        this.value = value;
        this.requestedAt = requestedAt;
    }

    public String getCommandId() {
        return commandId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public CommandType getType() {
        return type;
    }

    public boolean getValue() {
        return value;
    }

    public long getRequestedAt() {
        return requestedAt;
    }
}
