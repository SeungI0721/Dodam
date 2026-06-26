<!-- 도담도담 프로젝트의 Phase 2부터 Phase 7까지 구현 내용을 정리한 문서이다. -->
# Phase 2~7 구현 보고서

작성일: 2026-06-26

## 완료 범위

기존 Java/XML 앱 구조를 유지하면서 스마트 수조 관리 앱으로 동작하는 데 필요한 핵심 구조와 기능을 추가했다. 실제 Raspberry Pi/Arduino 프로토콜은 제공되지 않았으므로 장치 통신은 `DemoDeviceGateway`로 구현했다.

## Phase 2 구조 개선

### 추가한 패키지

```text
com.example.dodam.domain.model
com.example.dodam.domain.usecase
com.example.dodam.data.aquarium
com.example.dodam.data.device
com.example.dodam.data.history
com.example.dodam.data.local
com.example.dodam.notification
com.example.dodam.ui.dashboard
```

### 핵심 변경

- Activity에 집중되어 있던 상태와 판단 로직을 Repository, ViewModel, Domain Model로 분리
- 센서값을 문자열이 아닌 `SensorSnapshot`으로 표현
- 수조 상태를 `AquariumStatusEvaluator`에서 판정
- 입력값 검증을 `AquariumInputValidator`로 분리

## Phase 3 장치 통신

### 구현 항목

- `DeviceGateway` 인터페이스 추가
- `DemoDeviceGateway` 추가
- 장치 명령 모델 `ControlCommand` 추가
- 장치 명령 결과 모델 `CommandResult` 추가
- 장치 연결 상태 모델 `DeviceConnectionState` 추가

### 동작 방식

`DemoDeviceGateway`는 실제 하드웨어 없이 3초 주기로 수온, 탁도, 수위, 히터, 조명 상태를 갱신한다. 히터와 조명 명령은 즉시 성공으로 처리하지 않고 ACK 형태의 결과를 반환하도록 구성했다.

실제 장치 연동 시에는 `DeviceGateway` 구현체만 교체하면 된다.

## Phase 4 대시보드와 제어

### 구현 항목

- `MainScreen`을 `DashboardViewModel`과 연결
- 하드코딩 센서 문자열 제거
- 수온, 탁도, 수위, 연결 상태, 마지막 업데이트 표시
- 히터와 조명 버튼을 Repository 명령으로 연결
- 상태 판정 결과를 UI에 반영

### 판정 상태

| 상태 | 의미 |
| --- | --- |
| `NORMAL` | 정상 |
| `WARNING` | 주의 |
| `DANGER` | 위험 |
| `STALE` | 센서 데이터 지연 |
| `ERROR` | 오류 |

## Phase 5 설정과 자동화

### 구현 항목

- `SettingsRepository` 추가
- 사용자 UID와 수조 ID 기준 설정 저장
- 목표 수온 저장
- 조명 자동 모드 저장
- 조명 ON/OFF 시간 저장
- 알림 활성화 설정 저장
- 설정값 입력 검증 추가

자동 제어 실행은 앱이 아니라 장치 또는 Raspberry Pi 쪽에서 수행해야 하므로, 앱에서는 설정 저장과 전달 구조까지만 구현했다.

## Phase 6 알림과 기록

### 알림

- `AquariumAlertNotifier` 추가
- Android 알림 채널 생성
- Android 13 이상 `POST_NOTIFICATIONS` 권한 요청
- 상태 변화 기준 중복 알림 방지

### 기록

- `LocalEventStore` 추가
- `AquariumHistoryRepository` 추가
- `HistoryActivity` 추가
- 센서 수신, 연결 상태, 명령 결과, 설정 변경 이벤트 저장

현재 기록은 SharedPreferences 기반 최근 기록이다. 장기 이력과 그래프는 Room 또는 서버 저장소로 확장하는 것이 적합하다.

## Phase 7 인증과 사용자 데이터

### 인증

- `MainActivity`를 Firebase Authentication 이메일 로그인으로 전환
- `Register`를 Firebase Authentication 이메일 회원가입으로 전환
- 평문 비밀번호 저장 제거
- 로그인 세션 유지
- 중복 클릭 방지

### 사용자 데이터

- `ProfileActivity` 추가
- Firebase Auth displayName 수정
- 사용자 프로필 저장 구조 추가
- 사용자별 수조 저장 구조 추가

### 커뮤니티

- 게시글 작성자 UID 저장
- 게시글 카테고리 저장
- 게시글 작성 시각 저장
- 비로그인 작성 차단
- 본인 게시글 삭제 기능 추가

## 제거한 구형 코드

다음 항목은 최종 실행에 필요 없고 보안상 유지할 이유가 없어 제거했다.

- `LoginRequest.java`
- `RegisterRequest.java`
- `UserTestRequest.java`
- `PostingRequest.java`
- `TestActivity.java`
- `activity_test.xml`
- `exexex.xml`
- `php_File/*.php`
- Volley 의존성

## 테스트

추가한 주요 테스트:

- `AquariumStatusEvaluatorTest`
- `AquariumInputValidatorTest`

검증 명령:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat test
.\gradlew.bat lint
```

결과:

```text
BUILD SUCCESSFUL
```

## 남은 작업

- 실제 Android 기기 또는 에뮬레이터에서 화면 조작 검증
- Firebase Authentication 실제 계정 로그인/회원가입 검증
- Firebase Realtime Database Security Rules 적용
- Raspberry Pi/Arduino 실제 프로토콜 구현
- 실제 센서 데이터 수신
- 실제 히터/조명 ACK 검증
- 장기 이력, 그래프, 관리 일정 알림 구현
