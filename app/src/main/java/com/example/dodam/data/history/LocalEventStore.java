// 최근 수조 이벤트 기록을 SharedPreferences에 저장하는 로컬 저장소 파일이다.
package com.example.dodam.data.history;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dodam.domain.model.AquariumEvent;

import java.util.ArrayList;
import java.util.List;

public class LocalEventStore {
    private static final String FILE_NAME = "dodam_event_history";
    private static LocalEventStore instance;
    private final SharedPreferences preferences;

    private LocalEventStore(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initialize(Context context) {
        if (instance == null) {
            instance = new LocalEventStore(context);
        }
    }

    public static synchronized LocalEventStore getInstanceOrNull() {
        return instance;
    }

    public void append(AquariumEvent event) {
        List<AquariumEvent> events = load();
        events.add(0, event);
        while (events.size() > 100) {
            events.remove(events.size() - 1);
        }
        StringBuilder builder = new StringBuilder();
        for (AquariumEvent item : events) {
            builder.append(escape(item.getEventId())).append('|')
                    .append(escape(item.getAquariumId())).append('|')
                    .append(item.getCreatedAt()).append('|')
                    .append(escape(item.getType())).append('|')
                    .append(escape(item.getMessage()))
                    .append('\n');
        }
        preferences.edit().putString("events", builder.toString()).apply();
    }

    public List<AquariumEvent> load() {
        String raw = preferences.getString("events", "");
        List<AquariumEvent> result = new ArrayList<>();
        if (raw.trim().isEmpty()) {
            return result;
        }
        for (String row : raw.split("\n")) {
            String[] parts = row.split("\\|", -1);
            if (parts.length == 5) {
                result.add(new AquariumEvent(unescape(parts[0]), unescape(parts[1]),
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
