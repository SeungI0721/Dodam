// 수조 상태 판정 규칙을 확인하는 단위 테스트 파일이다.
package com.example.dodam.domain.usecase;

import static org.junit.Assert.assertEquals;

import com.example.dodam.domain.model.AlertSettings;
import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.ControlMode;
import com.example.dodam.domain.model.SensorSnapshot;

import org.junit.Test;

public class AquariumStatusEvaluatorTest {
    private final AquariumStatusEvaluator evaluator = new AquariumStatusEvaluator();
    private final AlertSettings settings = AlertSettings.defaults();
    private final long now = 1_780_000_000_000L;

    @Test
    public void evaluate_returnsNormalForValuesInsideThresholds() {
        SensorSnapshot snapshot = snapshot(25.8, 2.2, 580, now);

        AquariumStatus status = evaluator.evaluate(snapshot, settings, now);

        assertEquals(AquariumStatus.NORMAL, status);
    }

    @Test
    public void evaluate_returnsWarningForLowTemperature() {
        SensorSnapshot snapshot = snapshot(23.0, 2.2, 580, now);

        AquariumStatus status = evaluator.evaluate(snapshot, settings, now);

        assertEquals(AquariumStatus.WARNING, status);
    }

    @Test
    public void evaluate_returnsDangerForVeryLowWaterLevel() {
        SensorSnapshot snapshot = snapshot(25.8, 2.2, 390, now);

        AquariumStatus status = evaluator.evaluate(snapshot, settings, now);

        assertEquals(AquariumStatus.DANGER, status);
    }

    @Test
    public void evaluate_returnsStaleForOldData() {
        SensorSnapshot snapshot = snapshot(25.8, 2.2, 580, now - 61_000L);

        AquariumStatus status = evaluator.evaluate(snapshot, settings, now);

        assertEquals(AquariumStatus.STALE, status);
    }

    @Test
    public void evaluate_returnsErrorForNaN() {
        SensorSnapshot snapshot = snapshot(Double.NaN, 2.2, 580, now);

        AquariumStatus status = evaluator.evaluate(snapshot, settings, now);

        assertEquals(AquariumStatus.ERROR, status);
    }

    private SensorSnapshot snapshot(double temperatureC, double turbidity, int waterLevelMm, long timestamp) {
        return new SensorSnapshot("tank-test", timestamp, temperatureC, turbidity, waterLevelMm,
                false, true, ControlMode.MANUAL);
    }
}
