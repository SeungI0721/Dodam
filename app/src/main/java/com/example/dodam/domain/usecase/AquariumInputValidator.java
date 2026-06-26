// 수조 등록과 설정 입력값을 검증하는 UseCase 파일이다.
package com.example.dodam.domain.usecase;

public class AquariumInputValidator {
    private static final String DEVICE_ID_REGEX = "^[A-Za-z0-9_-]{3,40}$";
    private static final String TIME_REGEX = "^([01]\\d|2[0-3]):[0-5]\\d$";

    public boolean isValidDeviceId(String deviceId) {
        return deviceId != null && deviceId.trim().matches(DEVICE_ID_REGEX);
    }

    public boolean isValidAquariumName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() <= 30;
    }

    public boolean isValidTargetTemperature(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value) && value >= 18.0 && value <= 32.0;
    }

    public boolean isValidLightSchedule(String onTime, String offTime) {
        return onTime != null && offTime != null
                && onTime.trim().matches(TIME_REGEX)
                && offTime.trim().matches(TIME_REGEX)
                && !onTime.trim().equals(offTime.trim());
    }
}
