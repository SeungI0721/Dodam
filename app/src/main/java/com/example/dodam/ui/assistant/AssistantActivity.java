// 도담이 네이티브 챗봇 화면을 담당하는 Activity 파일이다.
package com.example.dodam.ui.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dodam.Dodam_Cat;
import com.example.dodam.R;
import com.example.dodam.data.assistant.AssistantRepository;
import com.example.dodam.domain.assistant.AssistantAnswer;
import com.example.dodam.domain.assistant.PendingCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 빠른 질문, 자유 입력, 확인 기반 데모 장치 제어를 처리한다.
public class AssistantActivity extends AppCompatActivity {
    private final List<ChatMessage> messages = new ArrayList<>();
    private AssistantAdapter adapter;
    private AssistantRepository repository;
    private RecyclerView messageList;
    private EditText inputMessage;

    // 사용자가 자주 묻는 질문을 버튼으로 제공한다.
    private final List<QuickQuestion> quickQuestions = Arrays.asList(
            new QuickQuestion("현재 상태", "지금 수조 상태 어때?"),
            new QuickQuestion("최근 경고", "최근 경고 알려줘"),
            new QuickQuestion("히터 이유", "히터가 왜 켜졌어?"),
            new QuickQuestion("조명 상태", "조명 상태 알려줘"),
            new QuickQuestion("장치 연결", "장치가 연결되어 있어?"),
            new QuickQuestion("관리 일정", "다음 관리 일정 알려줘"),
            new QuickQuestion("앱 사용법", "앱 사용 방법 알려줘"),
            new QuickQuestion("히터 켜기", "히터 켜줘")
    );

    @Override
    // 화면을 초기화하고 데모 장치 기반 도담이 Repository를 시작한다.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);

        repository = new AssistantRepository(this);
        repository.startDemoIfNeeded();

        adapter = new AssistantAdapter();
        messageList = findViewById(R.id.assistant_message_list);
        inputMessage = findViewById(R.id.assistant_input);
        Button sendButton = findViewById(R.id.assistant_send);
        Button backButton = findViewById(R.id.assistant_back);
        LinearLayout quickContainer = findViewById(R.id.quick_question_container);

        messageList.setLayoutManager(new LinearLayoutManager(this));
        messageList.setAdapter(adapter);

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(AssistantActivity.this, Dodam_Cat.class));
            finish();
        });
        sendButton.setOnClickListener(v -> sendCurrentInput());
        inputMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendCurrentInput();
                return true;
            }
            return false;
        });

        renderQuickQuestions(quickContainer);
        addAssistantMessage("안녕하세요. 도담이입니다. 실제 라즈베리파이 없이 앱의 데모 장치 상태를 기준으로 수조 상태와 앱 사용법을 안내할게요.");
    }

    // 빠른 질문 목록을 가로 스크롤 버튼으로 그린다.
    private void renderQuickQuestions(LinearLayout container) {
        container.removeAllViews();
        for (QuickQuestion quickQuestion : quickQuestions) {
            Button button = new Button(this);
            button.setText(quickQuestion.getLabel());
            button.setAllCaps(false);
            button.setTextSize(13f);
            button.setBackgroundResource(R.drawable.bg_assistant_quick_question);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, dp(8), 0);
            button.setLayoutParams(params);
            button.setOnClickListener(v -> sendQuestion(quickQuestion.getQuestion()));
            container.addView(button);
        }
    }

    // 입력창의 질문을 검증한 뒤 도담이에게 전달한다.
    private void sendCurrentInput() {
        String question = inputMessage.getText().toString().trim();
        if (question.isEmpty()) {
            Toast.makeText(this, "질문을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        inputMessage.setText("");
        sendQuestion(question);
    }

    // 사용자 질문을 메시지로 추가하고 Repository 답변을 화면에 표시한다.
    private void sendQuestion(String question) {
        addUserMessage(question);
        AssistantAnswer answer = repository.answer(question);
        addAssistantMessage(answer.getText());
        if (answer.isRequiresConfirmation()) {
            showCommandConfirmation(answer.getPendingCommand());
        }
    }

    // 장치 제어 요청은 바로 실행하지 않고 사용자 확인을 먼저 받는다.
    private void showCommandConfirmation(PendingCommand command) {
        if (command == null) {
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(command.getConfirmationText())
                .setNegativeButton("취소", (dialog, which) -> addAssistantMessage("명령을 취소했습니다."))
                .setPositiveButton("실행", (dialog, which) -> {
                    addAssistantMessage("명령을 전송했습니다. 데모 장치 ACK를 기다리는 중입니다.");
                    repository.executePendingCommand(command, answer -> runOnUiThread(() -> addAssistantMessage(answer.getText())));
                })
                .show();
    }

    // 사용자 메시지를 목록에 추가한다.
    private void addUserMessage(String text) {
        messages.add(new ChatMessage(text, true, System.currentTimeMillis()));
        renderMessages();
    }

    // 도담이 답변 메시지를 목록에 추가한다.
    private void addAssistantMessage(String text) {
        messages.add(new ChatMessage(text, false, System.currentTimeMillis()));
        renderMessages();
    }

    // Adapter에 메시지를 전달하고 마지막 메시지로 스크롤한다.
    private void renderMessages() {
        adapter.submitMessages(messages);
        if (!messages.isEmpty()) {
            messageList.scrollToPosition(messages.size() - 1);
        }
    }

    // 코드에서 만든 버튼 여백을 dp 단위로 변환한다.
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
