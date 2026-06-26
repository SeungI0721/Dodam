// 도담이의 수조 상태 기반 답변 문장을 생성하는 UseCase 파일이다.
package com.example.dodam.domain.assistant;

import com.example.dodam.domain.model.AquariumEvent;
import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.example.dodam.domain.model.MaintenanceRecord;
import com.example.dodam.domain.model.SensorSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// 센서값, 연결 상태, 이벤트, 관리 기록을 근거로 규칙 기반 답변을 만든다.
public class BuildAquariumStatusAnswerUseCase {
    private static final long STALE_MS = 60_000L;

    // 분류된 질문 의도에 맞는 세부 답변 생성 메서드로 분기한다.
    public AssistantAnswer build(AssistantIntent intent, AssistantContext context) {
        switch (intent) {
            case TEMPERATURE_STATUS:
                return temperature(context);
            case WATER_LEVEL_STATUS:
                return waterLevel(context);
            case TURBIDITY_STATUS:
                return turbidity(context);
            case DEVICE_CONNECTION:
                return connection(context);
            case HEATER_REASON:
                return heater(context);
            case LIGHT_REASON:
                return light(context);
            case RECENT_ALERTS:
                return recentAlerts(context);
            case NEXT_MAINTENANCE:
                return maintenance(context);
            case CURRENT_STATUS:
            default:
                return currentStatus(context);
        }
    }

    // 현재 수온, 탁도, 수위, 장치 상태를 한 번에 요약한다.
    private AssistantAnswer currentStatus(AssistantContext context) {
        SensorSnapshot snapshot = context.getSensorSnapshot();
        if (snapshot == null) {
            return fallback("아직 수신된 센서 데이터가 없습니다. 앱은 실행 중이지만 현재 수조 상태를 판단할 실제 값이 없습니다.");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("현재 수조 상태는 ").append(statusLabel(context.getAquariumStatus())).append("입니다. ");
        builder.append("수온은 ").append(formatTemperature(snapshot.getTemperatureC())).append(", ");
        builder.append("탁도는 ").append(formatTurbidity(snapshot.getTurbidity())).append(", ");
        builder.append("수위는 ").append(snapshot.getWaterLevelMm()).append("mm입니다. ");
        builder.append("히터는 ").append(onOff(snapshot.isHeaterOn())).append(", 조명은 ").append(onOff(snapshot.isLightOn())).append(" 상태입니다. ");
        builder.append(connectionSentence(context.getConnectionState())).append(" ");
        builder.append(timestampSentence(snapshot, context.getGeneratedAt()));
        return state(builder.toString(), snapshot.getTimestamp());
    }

    // 수온 값과 기본 알림 기준을 함께 안내한다.
    private AssistantAnswer temperature(AssistantContext context) {
        SensorSnapshot snapshot = context.getSensorSnapshot();
        if (snapshot == null) {
            return fallback("현재 수온 데이터가 수신되지 않아 정상 여부를 판단할 수 없습니다.");
        }
        String text = "마지막으로 수신된 수온은 " + formatTemperature(snapshot.getTemperatureC())
                + "입니다. 설정 기준은 기본값 기준 "
                + formatTemperature(context.getAlertSettings().getLowTemperatureC())
                + "부터 " + formatTemperature(context.getAlertSettings().getHighTemperatureC())
                + "까지입니다. " + timestampSentence(snapshot, context.getGeneratedAt());
        return state(text, snapshot.getTimestamp());
    }

    // 수위 값과 기본 주의 기준을 함께 안내한다.
    private AssistantAnswer waterLevel(AssistantContext context) {
        SensorSnapshot snapshot = context.getSensorSnapshot();
        if (snapshot == null) {
            return fallback("현재 수위 데이터가 수신되지 않았습니다.");
        }
        String text = "마지막으로 수신된 수위는 " + snapshot.getWaterLevelMm()
                + "mm입니다. 기본 주의 기준은 "
                + context.getAlertSettings().getLowWaterLevelMm()
                + "mm 미만입니다. " + timestampSentence(snapshot, context.getGeneratedAt());
        return state(text, snapshot.getTimestamp());
    }

    // 탁도 값을 안내하되 전체 수질 단정은 피한다.
    private AssistantAnswer turbidity(AssistantContext context) {
        SensorSnapshot snapshot = context.getSensorSnapshot();
        if (snapshot == null) {
            return fallback("현재 탁도 데이터가 수신되지 않았습니다.");
        }
        String text = "마지막으로 수신된 탁도는 " + formatTurbidity(snapshot.getTurbidity())
                + "입니다. 기본 주의 기준은 "
                + formatTurbidity(context.getAlertSettings().getTurbidityWarning())
                + " 이상입니다. 탁도값만으로 전체 수질을 단정하지는 않습니다. "
                + timestampSentence(snapshot, context.getGeneratedAt());
        return state(text, snapshot.getTimestamp());
    }

    // 장치 연결 상태와 마지막 센서 수신 시각을 안내한다.
    private AssistantAnswer connection(AssistantContext context) {
        String text = connectionSentence(context.getConnectionState());
        SensorSnapshot snapshot = context.getSensorSnapshot();
        if (snapshot != null) {
            text += " " + timestampSentence(snapshot, context.getGeneratedAt());
            return state(text, snapshot.getTimestamp());
        }
        return fallback(text + " 아직 수신된 센서 데이터는 없습니다.");
    }

    // 히터 상태와 현재 수온을 설명한다.
    private AssistantAnswer heater(AssistantContext context) {
        SensorSnapshot snapshot = context.getSensorSnapshot();
        if (snapshot == null) {
            return fallback("히터 상태를 설명할 센서 데이터가 아직 없습니다.");
        }
        String text = "현재 히터는 " + onOff(snapshot.isHeaterOn()) + " 상태입니다. "
                + "이 앱 단독 데모에서는 실제 라즈베리파이가 아니라 데모 장치 상태를 기준으로 표시합니다. "
                + "현재 수온은 " + formatTemperature(snapshot.getTemperatureC()) + "입니다. "
                + timestampSentence(snapshot, context.getGeneratedAt());
        return state(text, snapshot.getTimestamp());
    }

    // 조명 상태와 데모 장치 기준임을 설명한다.
    private AssistantAnswer light(AssistantContext context) {
        SensorSnapshot snapshot = context.getSensorSnapshot();
        if (snapshot == null) {
            return fallback("조명 상태를 설명할 센서 데이터가 아직 없습니다.");
        }
        String text = "현재 조명은 " + onOff(snapshot.isLightOn()) + " 상태입니다. "
                + "이 값은 실제 HW가 아니라 앱의 데모 장치 상태를 기준으로 합니다. "
                + timestampSentence(snapshot, context.getGeneratedAt());
        return state(text, snapshot.getTimestamp());
    }

    // 최근 이벤트를 최대 5개까지 요약한다.
    private AssistantAnswer recentAlerts(AssistantContext context) {
        List<AquariumEvent> events = context.getRecentEvents();
        if (events.isEmpty()) {
            return AssistantAnswer.simple("최근 저장된 경고나 이벤트가 없습니다.", AnswerSourceType.MAINTENANCE_HISTORY, null);
        }
        StringBuilder builder = new StringBuilder("최근 이벤트입니다.");
        int count = Math.min(5, events.size());
        for (int i = 0; i < count; i++) {
            AquariumEvent event = events.get(i);
            builder.append("\n- ")
                    .append(formatTime(event.getCreatedAt()))
                    .append(" ")
                    .append(event.getType());
        }
        return AssistantAnswer.simple(builder.toString(), AnswerSourceType.MAINTENANCE_HISTORY, null);
    }

    // 저장된 관리 기록을 최대 3개까지 요약한다.
    private AssistantAnswer maintenance(AssistantContext context) {
        List<MaintenanceRecord> records = context.getMaintenanceRecords();
        if (records.isEmpty()) {
            return AssistantAnswer.simple("저장된 관리 기록이 아직 없습니다. 환수, 먹이 급여, 필터 청소 같은 기록이 생기면 여기에서 요약할 수 있습니다.",
                    AnswerSourceType.MAINTENANCE_HISTORY, null);
        }
        StringBuilder builder = new StringBuilder("최근 관리 기록입니다.");
        int count = Math.min(3, records.size());
        for (int i = 0; i < count; i++) {
            MaintenanceRecord record = records.get(i);
            builder.append("\n- ")
                    .append(formatTime(record.getCreatedAt()))
                    .append(" ")
                    .append(record.getCategory())
                    .append(": ")
                    .append(record.getMemo());
        }
        return AssistantAnswer.simple(builder.toString(), AnswerSourceType.MAINTENANCE_HISTORY, null);
    }

    private AssistantAnswer state(String text, long timestamp) {
        return AssistantAnswer.simple(text, AnswerSourceType.AQUARIUM_STATE, timestamp);
    }

    private AssistantAnswer fallback(String text) {
        return AssistantAnswer.simple(text, AnswerSourceType.FALLBACK, null);
    }

    private String statusLabel(AquariumStatus status) {
        if (status == null) {
            return "확인 필요";
        }
        switch (status) {
            case NORMAL:
                return "정상";
            case WARNING:
                return "주의";
            case DANGER:
                return "위험";
            case STALE:
                return "데이터 지연";
            case EMPTY:
                return "데이터 없음";
            case ERROR:
            default:
                return "오류";
        }
    }

    private String connectionSentence(DeviceConnectionState state) {
        if (state == DeviceConnectionState.CONNECTED) {
            return "장치는 연결된 상태입니다.";
        }
        if (state == DeviceConnectionState.CONNECTING || state == DeviceConnectionState.RECONNECTING) {
            return "장치 연결을 확인하는 중입니다.";
        }
        if (state == DeviceConnectionState.ERROR) {
            return "장치 연결에 오류가 있습니다.";
        }
        return "장치는 오프라인 상태입니다.";
    }

    // 센서 데이터가 오래되었는지 판단해서 최신성 문장을 만든다.
    private String timestampSentence(SensorSnapshot snapshot, long now) {
        long ageMs = Math.max(0L, now - snapshot.getTimestamp());
        String time = formatTime(snapshot.getTimestamp());
        if (ageMs > STALE_MS) {
            return "마지막 데이터는 " + time + "에 수신되어 최신 상태로 단정할 수 없습니다.";
        }
        return "마지막 데이터는 " + time + "에 수신되었습니다.";
    }

    private String onOff(boolean value) {
        return value ? "켜짐" : "꺼짐";
    }

    private String formatTemperature(double value) {
        return String.format(Locale.KOREA, "%.1f°C", value);
    }

    private String formatTurbidity(double value) {
        return String.format(Locale.KOREA, "%.2f", value);
    }

    private String formatTime(long value) {
        return new SimpleDateFormat("HH:mm:ss", Locale.KOREA).format(new Date(value));
    }
}
