# 도담이 챗봇 최종 구현 보고서

작성일: 2026-06-26

## 목적

이 문서는 기존 KoChat/WebView 기반 챗봇을 대체하기 위해 구현한 Android 네이티브 도담이 챗봇의 최종 구현 내용을 정리한다.

구현 기준은 `assistant-rebuild-plan.md`이며, 실제 Raspberry Pi, Arduino, 센서 HW, 외부 AI 서버 없이 Android 앱 단독으로 실행되는 것을 목표로 했다.

## 최종 결론

도담이 챗봇은 이제 외부 KoChat 서버나 WebView에 의존하지 않는다. 사용자는 기존 도담이 캐릭터 화면에서 새 네이티브 챗봇 화면으로 진입하고, 앱 내부 Demo 장치와 Repository 상태를 기준으로 수조 상태, FAQ, 관리 기록, 데모 장치 제어 안내를 받을 수 있다.

실제 HW가 없기 때문에 센서값과 ACK는 `DemoDeviceGateway`가 제공하는 데모 데이터와 데모 명령 결과를 사용한다.

## 구현 범위 요약

| Phase | 구현 결과 |
| --- | --- |
| Phase 0 | 기존 WebView 챗봇 진입 제거, 새 `AssistantActivity`로 진입 전환 |
| Phase 1 | RecyclerView 기반 네이티브 채팅 UI 구현 |
| Phase 2 | Repository 상태 기반 규칙 답변 엔진 구현 |
| Phase 3 | `assistant_faq.json` 기반 로컬 FAQ 검색 구현 |
| Phase 4 | 관리 기록 로컬 저장소와 요약 답변 구현 |
| Phase 5 | 생성형 AI 직접 호출 대신 안전한 인터페이스와 로컬 폴백 구현 |
| Phase 6 | 사용자 확인 기반 자연어 데모 장치 제어 구현 |
| Phase 7 | 빌드, 단위 테스트, lint 검증 수행 |

## 진입 흐름

기존 흐름:

```text
MainScreen
  -> Dodam_Cat
      -> Chatbot
          -> WebView
              -> KoChat server
```

현재 흐름:

```text
MainScreen
  -> Dodam_Cat
      -> AssistantActivity
          -> AssistantRepository
              -> AquariumRepository
              -> Local FAQ
              -> Local maintenance records
              -> DemoDeviceGateway
```

관련 파일:

- `app/src/main/java/com/example/dodam/Dodam_Cat.java`
- `app/src/main/java/com/example/dodam/ui/assistant/AssistantActivity.java`
- `app/src/main/AndroidManifest.xml`

기존 `Chatbot.java`와 `activity_chatbot.xml`은 삭제하지 않았다. 빌드 호환 또는 legacy 참고 대상으로 남아 있지만, 새 챗봇 진입 흐름에서는 사용하지 않는다.

## UI 구현

새 챗봇 화면은 XML과 Java 기반으로 구현했다.

주요 파일:

- `app/src/main/java/com/example/dodam/ui/assistant/AssistantActivity.java`
- `app/src/main/java/com/example/dodam/ui/assistant/AssistantAdapter.java`
- `app/src/main/java/com/example/dodam/ui/assistant/ChatMessage.java`
- `app/src/main/java/com/example/dodam/ui/assistant/QuickQuestion.java`
- `app/src/main/res/layout/activity_assistant.xml`
- `app/src/main/res/layout/item_assistant_message.xml`

제공 기능:

- 사용자 메시지 표시
- 도담이 답변 표시
- 자유 질문 입력
- 빠른 질문 버튼
- 명령 확인 대화상자
- 데모 장치 ACK 결과 표시

빠른 질문 예시:

- 현재 상태
- 최근 경고
- 히터 이유
- 조명 상태
- 장치 연결
- 관리 일정
- 앱 사용법
- 히터 켜기

## 상태 기반 답변

상태 기반 답변은 생성형 AI 없이 규칙 기반으로 처리한다.

주요 파일:

- `app/src/main/java/com/example/dodam/domain/assistant/ClassifyAssistantIntentUseCase.java`
- `app/src/main/java/com/example/dodam/domain/assistant/BuildAquariumStatusAnswerUseCase.java`
- `app/src/main/java/com/example/dodam/data/assistant/AssistantRepository.java`

답변에 사용하는 데이터:

- `AquariumRepositoryState`
- `SensorSnapshot`
- `DeviceConnectionState`
- `AquariumStatus`
- `AlertSettings`
- `AutomationSettings`
- `AquariumEvent`
- `MaintenanceRecord`

처리 가능한 질문 유형:

- 현재 수조 상태
- 수온 상태
- 탁도 상태
- 수위 상태
- 장치 연결 상태
- 히터 작동 이유
- 조명 상태
- 최근 경고와 이벤트
- 다음 관리 일정 또는 관리 기록

안전 규칙:

- 센서값이 없으면 임의값을 만들지 않는다.
- 오래된 데이터는 현재값처럼 단정하지 않는다.
- 탁도값만으로 전체 수질을 단정하지 않는다.
- 실제 HW 연결 상태를 추측하지 않는다.
- 데모 장치 ACK 없이는 명령 성공으로 말하지 않는다.

## 로컬 FAQ

FAQ는 앱 내부 raw JSON 파일에 저장한다.

주요 파일:

- `app/src/main/res/raw/assistant_faq.json`
- `app/src/main/java/com/example/dodam/data/assistant/FaqDocument.java`
- `app/src/main/java/com/example/dodam/data/assistant/LocalFaqDataSource.java`
- `app/src/main/java/com/example/dodam/data/assistant/FaqRepository.java`
- `app/src/main/java/com/example/dodam/domain/assistant/SearchFaqUseCase.java`

현재 포함된 FAQ 주제:

- 수조 등록 방법
- 센서 데이터가 없을 때
- 목표 수온과 알림 기준 설정
- 히터와 조명 버튼 사용법
- 기록 화면 사용법
- 커뮤니티 사용법
- 앱 사용 방법

FAQ 검색 결과가 없으면 추측 답변을 하지 않고, 등록된 도움말만으로는 정확히 답하기 어렵다고 안내한다.

## 관리 기록

관리 기록은 `LocalMaintenanceStore`를 통해 SharedPreferences에 로컬 저장한다.

주요 파일:

- `app/src/main/java/com/example/dodam/data/history/LocalMaintenanceStore.java`
- `app/src/main/java/com/example/dodam/data/history/AquariumHistoryRepository.java`

지원 기록:

- 환수
- 먹이 급여
- 필터 청소
- 약품 투여

사용 예:

```text
환수 기록해줘
먹이 급여 기록해줘
필터 청소 기록해줘
```

저장된 기록은 이후 관리 기록 질문에서 요약 대상으로 사용된다.

## 생성형 AI/RAG 처리

현재 구현은 외부 생성형 AI API를 호출하지 않는다.

추가한 인터페이스:

- `app/src/main/java/com/example/dodam/domain/assistant/AssistantLanguageModel.java`
- `app/src/main/java/com/example/dodam/domain/assistant/AssistantGeneratedResult.java`
- `app/src/main/java/com/example/dodam/data/assistant/LocalOnlyAssistantLanguageModel.java`

현재 `LocalOnlyAssistantLanguageModel`은 외부 AI가 연결되지 않았음을 명확히 알리는 폴백 구현이다.

이 구조를 둔 이유:

- Android 앱에 API key를 직접 포함하지 않기 위해
- 외부 AI 장애가 있어도 로컬 답변과 FAQ가 유지되도록 하기 위해
- 추후 인증된 백엔드 AI/RAG를 붙일 수 있는 경계를 만들기 위해

AI가 추후 연결되더라도 다음 행위는 금지된다.

- 존재하지 않는 센서값 생성
- 오래된 센서값을 현재값처럼 표현
- 검증되지 않은 사육/치료 지식 안내
- 사용자 확인 없는 장치 제어
- API key, 인증 토큰, 개인정보 노출

## 자연어 장치 제어

자연어 장치 제어는 실제 HW가 아니라 데모 장치에만 적용된다.

주요 파일:

- `app/src/main/java/com/example/dodam/domain/assistant/PendingCommand.java`
- `app/src/main/java/com/example/dodam/domain/assistant/ValidateAssistantActionUseCase.java`
- `app/src/main/java/com/example/dodam/data/assistant/AssistantRepository.java`
- `app/src/main/java/com/example/dodam/data/aquarium/AquariumRepository.java`
- `app/src/main/java/com/example/dodam/data/device/DemoDeviceGateway.java`

흐름:

```text
사용자: 히터 켜줘
-> AssistantRepository가 PendingCommand 생성
-> AssistantActivity가 확인 대화상자 표시
-> 사용자가 실행 선택
-> AquariumRepository가 DemoDeviceGateway에 명령 전송
-> CommandResult ACK 확인
-> 최종 결과 메시지 표시
```

지원 예:

- 히터 켜줘
- 히터 꺼줘
- 조명 켜줘
- 조명 꺼줘

장치가 `CONNECTED` 상태가 아니면 명령을 보내지 않는다.

## 보안과 개인정보

현재 구현은 다음 정보를 문서나 코드에 새로 추가하지 않았다.

- API key
- Firebase 인증 토큰
- 비밀번호
- 전체 Firebase 응답 원문
- 외부 AI 요청용 민감 데이터

대화 답변은 앱 내부 센서 상태, 로컬 FAQ, 로컬 이벤트/관리 기록만 사용한다.

## 테스트

추가한 테스트:

- `app/src/test/java/com/example/dodam/domain/assistant/ClassifyAssistantIntentUseCaseTest.java`
- `app/src/test/java/com/example/dodam/domain/assistant/BuildAquariumStatusAnswerUseCaseTest.java`
- `app/src/test/java/com/example/dodam/domain/assistant/ValidateAssistantActionUseCaseTest.java`

검증 항목:

- 현재 상태 질문 의도 분류
- 히터 제어 질문 의도 분류
- 히터 이유 질문이 제어 명령으로 오분류되지 않는지 확인
- FAQ 질문 의도 분류
- 센서값 누락 시 임의값을 만들지 않는지 확인
- 실제 센서값을 답변에 포함하는지 확인
- 장치 오프라인 상태에서 명령을 거부하는지 확인
- 장치 연결 상태에서 명령 검증을 통과하는지 확인

## 실행 검증 결과

다음 명령을 실행했다.

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:lint
```

결과:

| 항목 | 결과 |
| --- | --- |
| Debug APK 빌드 | PASS |
| Debug unit test | PASS |
| Lint | PASS |
| 실제 Android 기기 수동 실행 | NOT VERIFIED |
| 실제 Raspberry Pi/HW 연동 | 대상 아님 |
| 외부 생성형 AI/RAG | 대상 아님 |

lint 실행 중 Firebase 의존성의 Kotlin metadata 경고가 stderr에 출력된다. Gradle 태스크 결과는 `BUILD SUCCESSFUL`이며, 기존 프로젝트 문서에 기록된 Firebase 의존성 경고와 같은 계열이다.

## 남은 제약

- 실제 Android 기기에서 UI를 직접 조작해 확인하지는 못했다.
- 실제 센서/HW가 없으므로 모든 센서값과 ACK는 데모 장치 기준이다.
- 관리 기록은 Room 또는 Firebase가 아니라 SharedPreferences 기반 로컬 저장이다.
- 생성형 AI/RAG는 인터페이스만 있고 실제 외부 연동은 없다.
- FAQ는 로컬 JSON에 등록된 내용만 답변한다.

## 이후 확장 방향

1. 실제 기기에서 `AssistantActivity` 화면 조작 검증
2. 관리 기록 입력 UI 분리
3. FAQ 데이터 확대
4. Room 기반 장기 기록 저장
5. 인증된 백엔드 기반 AI/RAG 연결
6. 실제 장치 프로토콜 확보 시 `DemoDeviceGateway` 대신 실제 Gateway 구현
7. Firebase Security Rules와 사용자별 수조 접근 격리 검증

현재 기준으로 도담이 챗봇은 Android 앱 단독 데모 환경에서 Phase 0부터 Phase 7까지 구현 완료 상태다.
