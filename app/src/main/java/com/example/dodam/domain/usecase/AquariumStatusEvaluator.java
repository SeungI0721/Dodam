// 센서값과 기준값으로 수조 상태를 판정하는 UseCase 파일이다.
package com.example.dodam.domain.usecase;

import com.example.dodam.domain.model.AlertSettings;
import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.SensorSnapshot;

public class AquariumStatusEvaluator {
    private static final long STALE_DATA_MS = 60_000L;

    public AquariumStatus evaluate(SensorSnapshot snapshot, AlertSettings settings, long now) {
        if (snapshot == null) {
            return AquariumStatus.EMPTY;
        }
        if (!isFinite(snapshot.getTemperatureC()) || !isFinite(snapshot.getTurbidity())
                || snapshot.getWaterLevelMm() <= 0) {
            return AquariumStatus.ERROR;
        }
        if (now - snapshot.getTimestamp() > STALE_DATA_MS) {
            return AquariumStatus.STALE;
        }
        if (snapshot.getTemperatureC() <= settings.getLowTemperatureC() - 2
                || snapshot.getTemperatureC() >= settings.getHighTemperatureC() + 2
                || snapshot.getWaterLevelMm() < settings.getLowWaterLevelMm() - 100) {
            return AquariumStatus.DANGER;
        }
        if (snapshot.getTemperatureC() <= settings.getLowTemperatureC()
                || snapshot.getTemperatureC() >= settings.getHighTemperatureC()
                || snapshot.getTurbidity() >= settings.getTurbidityWarning()
                || snapshot.getWaterLevelMm() < settings.getLowWaterLevelMm()) {
            return AquariumStatus.WARNING;
        }
        return AquariumStatus.NORMAL;
    }

    private boolean isFinite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }
}
