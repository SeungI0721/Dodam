// 최근 수조 센서, 명령, 알림, 설정 이벤트 기록을 표시하는 Activity 파일이다.
package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.dodam.data.history.AquariumHistoryRepository;
import com.example.dodam.data.history.LocalEventStore;
import com.example.dodam.domain.model.AquariumEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 기록 화면 XML과 Activity를 연결한다.
        setContentView(R.layout.activity_history);
        LocalEventStore.initialize(this);

        TextView history = findViewById(R.id.tv_history);
        Button back = findViewById(R.id.btn_back);
        back.setOnClickListener(v -> finish());

        List<AquariumEvent> events = new AquariumHistoryRepository().loadRecentEvents();
        if (events.isEmpty()) {
            history.setText("아직 기록이 없습니다.");
            return;
        }
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.KOREA);
        for (AquariumEvent event : events) {
            builder.append(format.format(new Date(event.getCreatedAt())))
                    .append(" [")
                    .append(event.getType())
                    .append("] ")
                    .append(event.getMessage())
                    .append('\n');
        }
        history.setText(builder.toString());
    }
}
