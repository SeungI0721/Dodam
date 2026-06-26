// 도담이 장치 제어 명령을 전송해도 되는지 검사하는 UseCase 파일이다.
package com.example.dodam.domain.assistant;

import com.example.dodam.domain.model.DeviceConnectionState;

// 사용자 확인 이후에도 장치 연결 상태 같은 안전 조건을 다시 확인한다.
public class ValidateAssistantActionUseCase {
    // 명령과 현재 수조 컨텍스트를 검사하고, 문제가 있으면 사용자 안내 문구를 반환한다.
    public String validate(PendingCommand command, AssistantContext context) {
        if (command == null) {
            return "실행할 명령이 없습니다.";
        }
        if (context.getConnectionState() != DeviceConnectionState.CONNECTED) {
            return "장치가 연결된 상태가 아니어서 명령을 전송할 수 없습니다.";
        }
        return "";
    }
}
