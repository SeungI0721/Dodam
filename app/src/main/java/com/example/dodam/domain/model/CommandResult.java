// 제어 명령에 대한 장치 ACK 결과를 표현하는 모델 파일이다.
package com.example.dodam.domain.model;

public class CommandResult {
    private final String commandId;
    private final CommandStatus status;
    private final boolean actualValue;
    private final long appliedAt;
    private final String message;

    public CommandResult(String commandId, CommandStatus status, boolean actualValue, long appliedAt, String message) {
        this.commandId = commandId;
        this.status = status;
        this.actualValue = actualValue;
        this.appliedAt = appliedAt;
        this.message = message;
    }

    public String getCommandId() {
        return commandId;
    }

    public CommandStatus getStatus() {
        return status;
    }

    public boolean getActualValue() {
        return actualValue;
    }

    public long getAppliedAt() {
        return appliedAt;
    }

    public String getMessage() {
        return message;
    }
}
