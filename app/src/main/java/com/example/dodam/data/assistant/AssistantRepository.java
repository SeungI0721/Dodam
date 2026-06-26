// 도담이 챗봇의 질문 처리, FAQ, 상태 답변, 데모 명령을 조율하는 Repository 파일이다.
package com.example.dodam.data.assistant;

import android.content.Context;

import com.example.dodam.data.aquarium.AquariumRepository;
import com.example.dodam.data.aquarium.AquariumRepositoryState;
import com.example.dodam.data.aquarium.UserAquariumRepository;
import com.example.dodam.data.history.AquariumHistoryRepository;
import com.example.dodam.data.history.LocalEventStore;
import com.example.dodam.data.history.LocalMaintenanceStore;
import com.example.dodam.domain.assistant.AssistantAnswer;
import com.example.dodam.domain.assistant.AssistantContext;
import com.example.dodam.domain.assistant.AssistantIntent;
import com.example.dodam.domain.assistant.AnswerSourceType;
import com.example.dodam.domain.assistant.BuildAquariumStatusAnswerUseCase;
import com.example.dodam.domain.assistant.ClassifyAssistantIntentUseCase;
import com.example.dodam.domain.assistant.PendingCommand;
import com.example.dodam.domain.assistant.SearchFaqUseCase;
import com.example.dodam.domain.assistant.ValidateAssistantActionUseCase;
import com.example.dodam.domain.model.AlertSettings;
import com.example.dodam.domain.model.Aquarium;
import com.example.dodam.domain.model.AutomationSettings;
import com.example.dodam.domain.model.CommandResult;
import com.example.dodam.domain.model.CommandStatus;
import com.example.dodam.domain.model.CommandType;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;

// UI와 도메인/데이터 계층 사이에서 챗봇 응답 생성을 담당한다.
public class AssistantRepository {
    // 확인 후 실행한 데모 장치 명령의 최종 답변을 UI에 돌려주는 콜백이다.
    public interface AssistantCommandCallback {
        void onCompleted(AssistantAnswer answer);
    }

    private final Context context;
    private final AquariumRepository aquariumRepository = AquariumRepository.getInstance();
    private final AquariumHistoryRepository historyRepository = new AquariumHistoryRepository();
    private final ClassifyAssistantIntentUseCase classifyUseCase = new ClassifyAssistantIntentUseCase();
    private final BuildAquariumStatusAnswerUseCase statusAnswerUseCase = new BuildAquariumStatusAnswerUseCase();
    private final ValidateAssistantActionUseCase validateActionUseCase = new ValidateAssistantActionUseCase();
    private final SearchFaqUseCase searchFaqUseCase;
    private Aquarium selectedAquarium;

    // 로컬 이벤트, 관리 기록, FAQ 저장소를 초기화한다.
    public AssistantRepository(Context context) {
        this.context = context.getApplicationContext();
        LocalEventStore.initialize(this.context);
        LocalMaintenanceStore.initialize(this.context);
        this.searchFaqUseCase = new SearchFaqUseCase(new FaqRepository(new LocalFaqDataSource(this.context)));
    }

    // 실제 HW가 없으므로 선택된 수조의 장치 ID로 데모 장치를 시작한다.
    public void startDemoIfNeeded() {
        selectedAquarium = new UserAquariumRepository(context).loadSelectedOrDefault();
        aquariumRepository.startDemoDevice(selectedAquarium.getDeviceId());
    }

    // 사용자 질문을 받아 관리 기록, FAQ, 상태 답변, 장치 제어 답변 중 하나로 처리한다.
    public AssistantAnswer answer(String question) {
        AssistantAnswer recordAnswer = tryRecordMaintenance(question);
        if (recordAnswer != null) {
            return recordAnswer;
        }

        AssistantIntent intent = classifyUseCase.classify(question);
        if (intent == AssistantIntent.FAQ || intent == AssistantIntent.APP_HELP || intent == AssistantIntent.UNKNOWN) {
            AssistantAnswer faqAnswer = searchFaqUseCase.search(question);
            if (faqAnswer.getSourceType() != AnswerSourceType.FALLBACK || intent == AssistantIntent.FAQ) {
                return faqAnswer;
            }
        }

        if (intent == AssistantIntent.DEVICE_CONTROL) {
            PendingCommand command = parsePendingCommand(question);
            if (command == null) {
                return AssistantAnswer.simple("제어할 장치와 켜기/끄기 방향을 명확히 말해 주세요. 예: 히터 켜줘, 조명 꺼줘",
                        AnswerSourceType.FALLBACK, null);
            }
            return new AssistantAnswer(command.getConfirmationText(), AnswerSourceType.AQUARIUM_STATE,
                    new ArrayList<>(), null, true, command);
        }

        return statusAnswerUseCase.build(intent, buildContext());
    }

    // 사용자 확인이 끝난 PendingCommand를 검증한 뒤 데모 장치에 전송한다.
    public void executePendingCommand(PendingCommand command, AssistantCommandCallback callback) {
        AssistantContext assistantContext = buildContext();
        String validationMessage = validateActionUseCase.validate(command, assistantContext);
        if (!validationMessage.isEmpty()) {
            callback.onCompleted(AssistantAnswer.simple(validationMessage, AnswerSourceType.FALLBACK, null));
            return;
        }

        AquariumRepository.CommandCompletionListener listener =
                result -> callback.onCompleted(commandResultAnswer(command, result));

        if (command.getCommandType() == CommandType.SET_HEATER) {
            aquariumRepository.setHeater(command.getValue(), listener);
        } else if (command.getCommandType() == CommandType.SET_LIGHT) {
            aquariumRepository.setLight(command.getValue(), listener);
        } else {
            callback.onCompleted(AssistantAnswer.simple("지원하지 않는 명령입니다.", AnswerSourceType.FALLBACK, null));
        }
    }

    // DemoDeviceGateway의 ACK 결과를 사용자에게 보여줄 답변으로 바꾼다.
    private AssistantAnswer commandResultAnswer(PendingCommand command, CommandResult result) {
        String target = command.getCommandType() == CommandType.SET_HEATER ? "히터" : "조명";
        if (result.getStatus() == CommandStatus.APPLIED) {
            return AssistantAnswer.simple("데모 장치 ACK를 확인했습니다. " + target + "가 "
                            + (result.getActualValue() ? "켜진" : "꺼진") + " 상태입니다.",
                    AnswerSourceType.AQUARIUM_STATE, result.getAppliedAt());
        }
        return AssistantAnswer.simple("장치 응답을 성공으로 확인하지 못했습니다. " + result.getMessage(),
                AnswerSourceType.FALLBACK, result.getAppliedAt());
    }

    // 자연어 질문에서 히터/조명과 켜기/끄기 방향을 추출한다.
    private PendingCommand parsePendingCommand(String question) {
        String text = question == null ? "" : question.toLowerCase(Locale.KOREA);
        boolean turnOn = text.contains("켜") || text.contains("on");
        boolean turnOff = text.contains("꺼") || text.contains("off");
        if (turnOn == turnOff) {
            return null;
        }
        boolean value = turnOn;
        if (text.contains("히터") || text.contains("heater")) {
            return new PendingCommand(CommandType.SET_HEATER, value,
                    "히터를 " + (value ? "켜" : "꺼") + "도 될까요? 실제 HW가 아니라 앱의 데모 장치에 명령을 보냅니다.");
        }
        if (text.contains("조명") || text.contains("라이트") || text.contains("light") || text.contains("불")) {
            return new PendingCommand(CommandType.SET_LIGHT, value,
                    "조명을 " + (value ? "켜" : "꺼") + "도 될까요? 실제 HW가 아니라 앱의 데모 장치에 명령을 보냅니다.");
        }
        return null;
    }

    // "환수 기록해줘" 같은 관리 기록 요청을 로컬 저장소에 저장한다.
    private AssistantAnswer tryRecordMaintenance(String question) {
        String text = question == null ? "" : question.toLowerCase(Locale.KOREA);
        if (!text.contains("기록")) {
            return null;
        }

        String category = "";
        if (text.contains("환수")) {
            category = "환수";
        } else if (text.contains("먹이") || text.contains("급여")) {
            category = "먹이 급여";
        } else if (text.contains("필터") || text.contains("청소")) {
            category = "필터 청소";
        } else if (text.contains("약품")) {
            category = "약품 투여";
        }

        if (category.isEmpty()) {
            return null;
        }

        String aquariumId = selectedAquarium == null ? "demo-aquarium" : selectedAquarium.getAquariumId();
        historyRepository.addMaintenanceRecord(aquariumId, category, question);
        return AssistantAnswer.simple(category + " 기록을 저장했습니다. 이 기록은 앱 로컬 저장소에 보관됩니다.",
                AnswerSourceType.MAINTENANCE_HISTORY, System.currentTimeMillis());
    }

    // 현재 Repository 상태를 도담이 답변 생성용 AssistantContext로 묶는다.
    private AssistantContext buildContext() {
        AquariumRepositoryState state = aquariumRepository.getState();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user == null ? "local-demo-user" : user.getUid();
        String aquariumId = selectedAquarium == null ? "demo-aquarium" : selectedAquarium.getAquariumId();
        return new AssistantContext(
                uid,
                aquariumId,
                state.getSnapshot(),
                state.getConnectionState() == null ? DeviceConnectionState.OFFLINE : state.getConnectionState(),
                state.getStatus(),
                AlertSettings.defaults(),
                AutomationSettings.defaults(),
                historyRepository.loadRecentEvents(),
                historyRepository.loadMaintenanceRecords(),
                System.currentTimeMillis()
        );
    }
}
