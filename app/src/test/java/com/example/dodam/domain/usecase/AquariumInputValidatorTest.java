// 수조와 설정 입력값 검증 규칙을 확인하는 단위 테스트 파일이다.
package com.example.dodam.domain.usecase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AquariumInputValidatorTest {
    private final AquariumInputValidator validator = new AquariumInputValidator();

    @Test
    public void isValidDeviceId_acceptsSafeDeviceIds() {
        assertTrue(validator.isValidDeviceId("tank-001"));
        assertTrue(validator.isValidDeviceId("tank_demo_01"));
    }

    @Test
    public void isValidDeviceId_rejectsEmptyShortAndUnsafeValues() {
        assertFalse(validator.isValidDeviceId(""));
        assertFalse(validator.isValidDeviceId("ab"));
        assertFalse(validator.isValidDeviceId("tank 001"));
        assertFalse(validator.isValidDeviceId("http://device"));
    }

    @Test
    public void isValidTargetTemperature_acceptsExpectedRangeOnly() {
        assertTrue(validator.isValidTargetTemperature(26.0));
        assertFalse(validator.isValidTargetTemperature(17.9));
        assertFalse(validator.isValidTargetTemperature(32.1));
        assertFalse(validator.isValidTargetTemperature(Double.NaN));
    }

    @Test
    public void isValidLightSchedule_requiresTimeFormatAndDifferentValues() {
        assertTrue(validator.isValidLightSchedule("08:00", "20:00"));
        assertFalse(validator.isValidLightSchedule("8:00", "20:00"));
        assertFalse(validator.isValidLightSchedule("08:00", "08:00"));
        assertFalse(validator.isValidLightSchedule("24:00", "20:00"));
    }
}
