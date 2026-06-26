// 대시보드 ViewModel에서 사용하는 불변 Repository 상태 모델 파일이다.
package com.example.dodam.data.aquarium;

import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.CommandResult;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.example.dodam.domain.model.SensorSnapshot;

public class AquariumRepositoryState {
    private final SensorSnapshot snapshot;
    private final DeviceConnectionState connectionState;
    private final AquariumStatus status;
    private final CommandResult lastCommandResult;
    private final String commandMessage;
    private final String errorMessage;

    public AquariumRepositoryState(SensorSnapshot snapshot, DeviceConnectionState connectionState,
                                   AquariumStatus status, CommandResult lastCommandResult,
                                   String commandMessage, String errorMessage) {
        this.snapshot = snapshot;
        this.connectionState = connectionState;
        this.status = status;
        this.lastCommandResult = lastCommandResult;
        this.commandMessage = commandMessage;
        this.errorMessage = errorMessage;
    }

    public static AquariumRepositoryState initial() {
        return new AquariumRepositoryState(null, DeviceConnectionState.OFFLINE, AquariumStatus.EMPTY,
                null, "", "");
    }

    public AquariumRepositoryState withSnapshot(SensorSnapshot snapshot, AquariumStatus status) {
        return new AquariumRepositoryState(snapshot, connectionState, status, lastCommandResult,
                commandMessage, "");
    }

    public AquariumRepositoryState withConnectionState(DeviceConnectionState connectionState) {
        return new AquariumRepositoryState(snapshot, connectionState, status, lastCommandResult,
                commandMessage, errorMessage);
    }

    public AquariumRepositoryState withStatus(AquariumStatus status) {
        return new AquariumRepositoryState(snapshot, connectionState, status, lastCommandResult,
                commandMessage, errorMessage);
    }

    public AquariumRepositoryState withError(String errorMessage) {
        return new AquariumRepositoryState(snapshot, DeviceConnectionState.ERROR, AquariumStatus.ERROR,
                lastCommandResult, commandMessage, errorMessage);
    }

    public AquariumRepositoryState withCommandMessage(String commandMessage) {
        return new AquariumRepositoryState(snapshot, connectionState, status, lastCommandResult,
                commandMessage, errorMessage);
    }

    public AquariumRepositoryState withCommandResult(CommandResult result, String commandMessage) {
        return new AquariumRepositoryState(snapshot, connectionState, status, result, commandMessage,
                errorMessage);
    }

    public SensorSnapshot getSnapshot() {
        return snapshot;
    }

    public DeviceConnectionState getConnectionState() {
        return connectionState;
    }

    public AquariumStatus getStatus() {
        return status;
    }

    public CommandResult getLastCommandResult() {
        return lastCommandResult;
    }

    public String getCommandMessage() {
        return commandMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
