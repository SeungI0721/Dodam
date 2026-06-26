// 도담이 관리 기록을 SharedPreferences에 저장하는 로컬 저장소 파일이다.
package com.example.dodam.data.history;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dodam.domain.model.MaintenanceRecord;

import java.util.ArrayList;
import java.util.List;

// 환수, 먹이 급여, 필터 청소 같은 관리 기록을 앱 내부에 최대 100개까지 보관한다.
public class LocalMaintenanceStore {
    private static final String FILE_NAME = "dodam_maintenance_history";
    private static LocalMaintenanceStore instance;
    private final SharedPreferences preferences;

    // Application Context 기준으로 SharedPreferences를 연다.
    private LocalMaintenanceStore(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    // 앱 어디서든 같은 저장소 인스턴스를 쓰도록 초기화한다.
    public static synchronized void initialize(Context context) {
        if (instance == null) {
            instance = new LocalMaintenanceStore(context);
        }
    }

    // 초기화된 저장소 인스턴스를 반환하고, 없으면 null을 반환한다.
    public static synchronized LocalMaintenanceStore getInstanceOrNull() {
        return instance;
    }

    // 새 관리 기록을 맨 앞에 추가하고 오래된 기록은 100개 이후부터 버린다.
    public void append(MaintenanceRecord record) {
        List<MaintenanceRecord> records = load();
        records.add(0, record);
        while (records.size() > 100) {
            records.remove(records.size() - 1);
        }
        StringBuilder builder = new StringBuilder();
        for (MaintenanceRecord item : records) {
            builder.append(escape(item.getRecordId())).append('|')
                    .append(escape(item.getAquariumId())).append('|')
                    .append(item.getCreatedAt()).append('|')
                    .append(escape(item.getCategory())).append('|')
                    .append(escape(item.getMemo()))
                    .append('\n');
        }
        preferences.edit().putString("records", builder.toString()).apply();
    }

    // SharedPreferences에 저장된 문자열을 관리 기록 목록으로 복원한다.
    public List<MaintenanceRecord> load() {
        String raw = preferences.getString("records", "");
        List<MaintenanceRecord> result = new ArrayList<>();
        if (raw.trim().isEmpty()) {
            return result;
        }
        for (String row : raw.split("\n")) {
            String[] parts = row.split("\\|", -1);
            if (parts.length == 5) {
                result.add(new MaintenanceRecord(unescape(parts[0]), unescape(parts[1]),
                        parseLong(parts[2]), unescape(parts[3]), unescape(parts[4])));
            }
        }
        return result;
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("|", " ").replace("\n", " ");
    }

    private String unescape(String value) {
        return value == null ? "" : value;
    }
}
