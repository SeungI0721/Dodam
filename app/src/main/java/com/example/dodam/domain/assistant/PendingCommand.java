// 사용자 확인을 기다리는 도담이 장치 제어 명령 모델 파일이다.
package com.example.dodam.domain.assistant;

import com.example.dodam.domain.model.CommandType;

// 실제 전송 전 명령 종류, 목표 값, 확인 문구를 보관한다.
public class PendingCommand {
    private final CommandType commandType;
    private final boolean value;
    private final String confirmationText;

    // 히터나 조명 제어 명령을 확인 대화상자에 전달할 형태로 만든다.
    public PendingCommand(CommandType commandType, boolean value, String confirmationText) {
        this.commandType = commandType;
        this.value = value;
        this.confirmationText = confirmationText;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public boolean getValue() {
        return value;
    }

    public String getConfirmationText() {
        return confirmationText;
    }
}
