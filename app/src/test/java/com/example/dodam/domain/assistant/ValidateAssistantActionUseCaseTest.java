// 도담이 장치 제어 안전 검증 UseCase를 테스트하는 파일이다.
package com.example.dodam.domain.assistant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.example.dodam.domain.model.AlertSettings;
import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.AutomationSettings;
import com.example.dodam.domain.model.CommandType;
import com.example.dodam.domain.model.DeviceConnectionState;

import org.junit.Test;

import java.util.ArrayList;

// 장치 연결 상태에 따라 명령 허용 여부가 달라지는지 확인한다.
public class ValidateAssistantActionUseCaseTest {
    private final ValidateAssistantActionUseCase useCase = new ValidateAssistantActionUseCase();

    @Test
    // 오프라인 장치에는 명령을 전송하지 않도록 거부하는지 확인한다.
    public void rejectsCommandWhenDeviceIsOffline() {
        AssistantContext context = context(DeviceConnectionState.OFFLINE);

        String result = useCase.validate(new PendingCommand(CommandType.SET_HEATER, true, "confirm"), context);

        assertFalse(result.isEmpty());
    }

    @Test
    // 연결된 장치에는 명령 검증을 통과시키는지 확인한다.
    public void acceptsCommandWhenDeviceIsConnected() {
        AssistantContext context = context(DeviceConnectionState.CONNECTED);

        String result = useCase.validate(new PendingCommand(CommandType.SET_LIGHT, false, "confirm"), context);

        assertEquals("", result);
    }

    // 테스트에 필요한 최소 AssistantContext를 만든다.
    private AssistantContext context(DeviceConnectionState state) {
        return new AssistantContext("user", "tank", null, state, AquariumStatus.EMPTY,
                AlertSettings.defaults(), AutomationSettings.defaults(),
                new ArrayList<>(), new ArrayList<>(), 1_000L);
    }
}
