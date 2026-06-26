// 도담이 상태 답변 생성 UseCase를 검증하는 단위 테스트 파일이다.
package com.example.dodam.domain.assistant;

import static org.junit.Assert.assertTrue;

import com.example.dodam.domain.model.AlertSettings;
import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.AutomationSettings;
import com.example.dodam.domain.model.ControlMode;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.example.dodam.domain.model.SensorSnapshot;

import org.junit.Test;

import java.util.ArrayList;

// 센서값 누락 처리와 실제 센서값 포함 여부를 확인한다.
public class BuildAquariumStatusAnswerUseCaseTest {
    private final BuildAquariumStatusAnswerUseCase useCase = new BuildAquariumStatusAnswerUseCase();

    @Test
    // 센서 스냅샷이 없을 때 임의 값을 만들지 않는지 확인한다.
    public void doesNotInventSensorValueWhenSnapshotIsMissing() {
        AssistantContext context = new AssistantContext("user", "tank", null,
                DeviceConnectionState.OFFLINE, AquariumStatus.EMPTY, AlertSettings.defaults(),
                AutomationSettings.defaults(), new ArrayList<>(), new ArrayList<>(), 1_000L);

        AssistantAnswer answer = useCase.build(AssistantIntent.CURRENT_STATUS, context);

        assertTrue(answer.getText().contains("센서 데이터가 없습니다"));
    }

    @Test
    // 실제 스냅샷 값이 현재 상태 답변에 포함되는지 확인한다.
    public void includesRealSensorValuesInCurrentStatus() {
        SensorSnapshot snapshot = new SensorSnapshot("tank-demo", 1_000L, 25.8, 2.2,
                580, true, false, ControlMode.MANUAL);
        AssistantContext context = new AssistantContext("user", "tank", snapshot,
                DeviceConnectionState.CONNECTED, AquariumStatus.NORMAL, AlertSettings.defaults(),
                AutomationSettings.defaults(), new ArrayList<>(), new ArrayList<>(), 2_000L);

        AssistantAnswer answer = useCase.build(AssistantIntent.CURRENT_STATUS, context);

        assertTrue(answer.getText().contains("25.8"));
        assertTrue(answer.getText().contains("580mm"));
    }
}
