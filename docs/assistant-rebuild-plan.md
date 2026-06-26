# 도담이 챗봇 재구현 기준 문서

작성일: 2026-06-26

## 목적

기존 도담이 챗봇은 KoChat 서버를 Android `WebView`로 불러오는 구조였다. 새 도담이는 외부 챗봇 서버와 실제 라즈베리파이 또는 센서 하드웨어가 없어도 Android 앱 안에서 정상적으로 실행되는 네이티브 수조 관리 도우미로 재구현한다.

이 문서는 이후 챗봇 재구현의 기준 문서다. 구현할 때 확인되지 않은 실제 센서 사양, 장치 프로토콜, 데이터베이스 구조를 임의로 추측하지 않는다.

## 핵심 전제

- 실제 HW와 연결된 Raspberry Pi는 제공되지 않는다.
- 앱은 Android 단독으로 설치, 실행, 테스트할 수 있어야 한다.
- 실제 장치 연동 대신 현재 프로젝트의 `DemoDeviceGateway` 같은 시뮬레이션 계층을 사용한다.
- 센서값과 장치 상태는 앱 내부 Repository와 Demo Gateway가 제공하는 값만 사용한다.
- 생성형 AI 또는 RAG는 초기 필수 기능이 아니다.
- API key, Firebase 인증 토큰, 민감 정보는 앱 코드나 문서에 직접 넣지 않는다.
- 장치 제어 기능은 실제 HW가 없으므로 데모 명령/ACK 흐름으로만 검증한다.

## 기존 KoChat 및 WebView 구조 분석

현재 챗봇 관련 진입 흐름은 다음과 같다.

```text
MainScreen
  -> Dodam_Cat
      -> Chatbot
          -> WebView
              -> http://172.20.10.4:8080/
```

관련 파일:

- `app/src/main/java/com/example/dodam/MainScreen.java`
- `app/src/main/java/com/example/dodam/Dodam_Cat.java`
- `app/src/main/java/com/example/dodam/Chatbot.java`
- `app/src/main/res/layout/activity_chatbot.xml`

현재 `Chatbot.java`는 `WebView`를 초기화하고 고정 IP 서버를 로드한다. 이 구조는 다음 문제가 있다.

- KoChat 서버가 실행 중이어야만 챗봇이 동작한다.
- 서버 IP가 개발 환경에 고정되어 있다.
- 오프라인 상태에서 사용할 수 없다.
- 앱 내부 수조 상태, 센서값, 이벤트 기록과 직접 결합되어 있지 않다.
- 챗봇 UI/UX가 Android 네이티브 화면이 아니라 서버 HTML에 의존한다.

따라서 새 구현에서는 기존 `Chatbot.java`와 WebView 기반 구조를 사용하지 않는다. 기존 파일은 빌드 호환 또는 참고 자료로만 유지하거나, 필요하면 `legacy/kochat_2021`로 분리한다.

## 재사용 가능한 기존 코드

새 도담이는 기존 KoChat보다 현재 Android 앱의 도메인 계층을 우선 재사용한다.

| 목적 | 재사용 대상 |
| --- | --- |
| 현재 센서값 | `SensorSnapshot` |
| 수조 상태 판정 | `AquariumStatusEvaluator` |
| 현재 Repository 상태 | `AquariumRepositoryState` |
| 센서/장치 추상화 | `DeviceGateway` |
| Android 단독 데모 실행 | `DemoDeviceGateway` |
| 수조 선택 | `UserAquariumRepository` |
| 최근 이벤트 | `AquariumRepository.getEvents()`, `AquariumHistoryRepository.loadRecentEvents()` |
| 관리 기록 모델 | `MaintenanceRecord` |
| 설정 모델 | `AlertSettings`, `AutomationSettings` |

현재 저장소에는 실제 장치 대신 데모 장치가 이미 존재한다. 새 챗봇은 이 흐름을 이용해 실제 HW 없이도 수온, 탁도, 수위, 히터, 조명, 연결 상태에 대한 답변을 생성할 수 있다.

## 새 도담이의 기능 범위

초기 구현 범위는 Android 앱 단독 동작을 기준으로 한다.

### P0. 기존 챗봇 분리

- WebView 기반 `Chatbot.java`를 새 챗봇 진입점에서 제거한다.
- 기존 KoChat 서버는 새 구현에서 사용하지 않는다.
- `Dodam_Cat`에서 새 네이티브 도담이 화면으로 이동하도록 변경한다.
- 기존 WebView 파일은 삭제하지 않고 legacy 또는 참고 대상으로 둔다.

### P1. 네이티브 채팅 UI

- XML 기반 Android 채팅 화면을 만든다.
- 사용자 메시지와 도담이 응답을 RecyclerView로 표시한다.
- 입력창과 전송 버튼을 제공한다.
- 빠른 질문 버튼을 제공한다.
- 로딩, 빈 상태, 오류 상태를 처리한다.

빠른 질문 예시:

- 현재 수조 상태
- 최근 경고
- 히터 작동 이유
- 조명 상태
- 장치 연결 상태
- 다음 관리 일정
- 지난 24시간 변화
- 앱 사용 방법

### P2. 상태 기반 답변 엔진

생성형 AI 없이 규칙 기반으로 답변한다.

처리할 질문:

- 지금 수조 상태 어때?
- 수온은 정상인가?
- 물이 너무 탁한가?
- 수위가 낮아?
- 장치가 연결되어 있어?
- 마지막 데이터는 언제 들어왔어?
- 지금 위험한 상태야?
- 히터가 왜 켜졌어?
- 조명이 왜 켜졌어?
- 최근 경고 알려줘.

답변 규칙:

- 실제 Repository에 있는 값만 말한다.
- 센서값이 없으면 없다고 답한다.
- 오래된 데이터는 현재값처럼 말하지 않는다.
- 단위는 명확히 표시한다.
- 탁도만으로 전체 수질을 단정하지 않는다.
- 장치 ACK가 없으면 명령 성공으로 표시하지 않는다.

### P3. 로컬 FAQ

앱 사용법과 일반 안내는 로컬 FAQ로 처리한다.

FAQ 주제:

- 수조 등록 방법
- 장치 ID 등록 방법
- 센서 데이터가 안 들어올 때 확인할 것
- 목표 수온 설정 방법
- 알림 설정 방법
- 히터/조명 버튼 사용법
- 기록 화면 사용법
- 커뮤니티 사용법

FAQ 검색 결과가 없으면 추측하지 않고 다음처럼 답한다.

```text
현재 등록된 도움말만으로는 정확히 답하기 어렵습니다. 수조 정보, 장치 ID, 센서 연결 상태를 확인해 주세요.
```

### P4. 관리 기록 요약

관리 기록은 모델은 있으나 현재 저장 방식이 충분하지 않다. 초기에는 저장된 이벤트와 인메모리 또는 로컬 저장 기록만 요약한다.

지원할 답변:

- 최근 관리 기록
- 마지막 환수 기록
- 마지막 먹이 급여 기록
- 필터 청소 기록
- 다음 관리 일정 안내

단, 관리 주기와 사육 기준은 사용자가 설정했거나 앱에 검증된 기준으로 등록된 경우에만 사용한다.

### P5. 선택적 생성형 AI

초기 구현 이후 선택적으로 적용한다.

AI가 해도 되는 일:

- FAQ 검색 결과를 자연스럽게 재작성
- 여러 이벤트 요약
- 사용자 질문 의도 분류 보조
- 관리 기록을 읽기 쉬운 문장으로 정리

AI가 하면 안 되는 일:

- 존재하지 않는 센서값 생성
- 오래된 센서값을 현재값처럼 표현
- 장치 연결 성공 여부 추측
- 검증되지 않은 치료법 또는 약품 사용법 안내
- 사용자 확인 없이 히터나 조명 제어
- API key 또는 인증 토큰 노출

AI API를 붙일 경우 Android 앱에서 직접 호출하지 않고 인증된 백엔드 경유 구조를 사용한다.

## 권장 파일 구조

현재 프로젝트는 Java와 XML 기반이므로 Kotlin 예시가 아니라 Java/XML 구조로 시작한다.

```text
app/src/main/java/com/example/dodam/ui/assistant/
  AssistantActivity.java
  AssistantAdapter.java
  AssistantUiState.java
  ChatMessage.java
  QuickQuestion.java

app/src/main/java/com/example/dodam/domain/assistant/
  AssistantIntent.java
  AssistantAnswer.java
  AssistantContext.java
  AnswerSourceType.java
  AnswerReference.java
  ClassifyAssistantIntentUseCase.java
  BuildAquariumStatusAnswerUseCase.java
  SearchFaqUseCase.java
  BuildMaintenanceAnswerUseCase.java

app/src/main/java/com/example/dodam/data/assistant/
  AssistantRepository.java
  FaqRepository.java
  LocalFaqDataSource.java
  FaqDocument.java

app/src/main/res/layout/
  activity_assistant.xml
  item_assistant_message.xml
  item_quick_question.xml

app/src/main/res/raw/
  assistant_faq.json
```

## 주요 데이터 모델 초안

Java 기준으로 다음 모델을 둔다.

```java
public class AssistantContext {
    private final String userId;
    private final String aquariumId;
    private final SensorSnapshot sensorSnapshot;
    private final DeviceConnectionState connectionState;
    private final AquariumStatus aquariumStatus;
    private final AlertSettings alertSettings;
    private final AutomationSettings automationSettings;
    private final List<AquariumEvent> recentEvents;
    private final List<MaintenanceRecord> maintenanceRecords;
    private final long generatedAt;
}
```

```java
public enum AssistantIntent {
    CURRENT_STATUS,
    TEMPERATURE_STATUS,
    WATER_LEVEL_STATUS,
    TURBIDITY_STATUS,
    DEVICE_CONNECTION,
    HEATER_REASON,
    LIGHT_REASON,
    RECENT_ALERTS,
    NEXT_MAINTENANCE,
    APP_HELP,
    FAQ,
    DEVICE_CONTROL,
    UNKNOWN
}
```

```java
public class AssistantAnswer {
    private final String text;
    private final AnswerSourceType sourceType;
    private final List<AnswerReference> references;
    private final Long dataTimestamp;
    private final boolean requiresConfirmation;
    private final PendingCommand pendingCommand;
}
```

## 상태 기반 답변과 FAQ의 경계

상태 기반 답변은 실시간 또는 최근 저장 상태를 사용한다.

예:

- 현재 수조 상태
- 수온 정상 여부
- 탁도 상태
- 수위 상태
- 히터/조명 상태
- 장치 연결 상태
- 최근 경고

FAQ는 문서성 질문을 처리한다.

예:

- 수조 등록 방법
- 알림 설정 방법
- 장치 ID 확인 방법
- 앱 화면 사용 방법
- 센서 연결 문제 해결 순서

상태 질문에 FAQ만으로 답하지 않고, FAQ 질문에 센서값을 임의로 섞지 않는다.

## Android 단독 동작 방식

실제 Raspberry Pi와 센서가 없으므로 다음 방식으로 동작한다.

- `DemoDeviceGateway`가 주기적으로 센서값을 생성한다.
- `AquariumRepository`가 센서값을 받아 상태를 평가한다.
- `AssistantRepository`가 현재 `AquariumRepositoryState`를 읽어 `AssistantContext`를 만든다.
- 답변 엔진은 `AssistantContext`와 로컬 FAQ만 사용한다.
- 히터/조명 제어는 데모 명령으로 처리하고 `CommandResult`를 통해 결과를 표시한다.

이 구조는 실제 HW가 없어도 앱 화면, 채팅 UX, 상태 답변, 빠른 질문, 명령 확인 흐름을 검증할 수 있다.

## 장치 제어 정책

초기 버전에서는 자연어 장치 제어를 기본 비활성화한다. 구현하더라도 반드시 확인 단계를 둔다.

흐름:

```text
사용자 요청
-> 의도 분류
-> 현재 장치 상태 확인
-> 예상 영향 안내
-> 사용자 확인
-> 데모 명령 전송
-> CommandResult 확인
-> 최종 결과 안내
```

ACK 또는 `CommandResult`가 없으면 성공으로 말하지 않는다.

## 테스트 기준

단위 테스트:

- 질문 의도 분류
- 현재 상태 답변
- null 센서값 처리
- 오래된 센서값 처리
- 비정상 센서값 처리
- 히터/조명 이유 답변
- 최근 경고 요약
- FAQ 검색
- FAQ 결과 없음 처리
- 명령 확인 필요 여부

UI 테스트:

- 채팅 화면 진입
- 빠른 질문 선택
- 메시지 전송
- 답변 표시
- 로딩 상태
- 오류 상태
- 오프라인 FAQ 동작

안전 테스트:

- 센서값이 없을 때 임의값을 만들지 않는지 확인
- 오래된 값을 현재값처럼 말하지 않는지 확인
- 사용자 확인 없이 장치 명령을 실행하지 않는지 확인
- 다른 사용자의 수조 데이터에 접근하지 않는지 확인
- API key, 토큰, 비밀번호가 로그나 문서에 남지 않는지 확인

## 구현 순서

1. 이 문서를 기준으로 새 `AssistantActivity`와 XML 화면을 추가한다.
2. `Dodam_Cat` 또는 `MainScreen`의 챗봇 진입점을 새 Activity로 연결한다.
3. `ChatMessage`, `QuickQuestion`, `AssistantUiState`를 만든다.
4. `AssistantRepository`에서 현재 수조 상태와 이벤트를 수집한다.
5. `ClassifyAssistantIntentUseCase`를 만든다.
6. `BuildAquariumStatusAnswerUseCase`로 상태 기반 답변을 구현한다.
7. `assistant_faq.json`과 `LocalFaqDataSource`를 만든다.
8. 빠른 질문 버튼을 실제 답변 엔진에 연결한다.
9. 단위 테스트를 추가한다.
10. 빌드와 앱 실행을 검증한다.

## 이번 단계에서 하지 않을 일

- 실제 Raspberry Pi 연동
- 실제 센서 프로토콜 구현
- 실제 장치 ACK 프로토콜 구현
- KoChat 서버 복구
- WebView 챗봇 유지보수
- Android 앱에 AI API key 직접 포함
- 검증되지 않은 사육/치료 지식 추가

## 추가로 필요할 수 있는 자료

실제 HW 없이 Android 앱 단독으로 구현하는 데는 필수가 아니지만, 추후 실제 장치 연동을 하려면 다음 자료가 필요하다.

- Raspberry Pi 또는 Arduino 통신 방식
- 센서별 단위와 보정 기준
- 히터/조명 명령 포맷
- 장치 ACK 응답 포맷
- Firebase Realtime Database Rules
- 사용자별 수조 권한 정책
- 검증된 FAQ 원문

현재 단계에서는 위 자료가 없어도 데모 장치 기반 네이티브 챗봇을 구현할 수 있다.
