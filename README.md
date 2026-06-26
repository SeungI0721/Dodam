# 도담도담 Android 스마트 수조 관리 앱

도담도담은 2021년에 Java와 XML로 개발된 Android 기반 스마트 수조 관리 앱이다. 현재 저장소는 기존 앱 구조를 유지하면서 2026년 작업으로 Firebase 인증, 수조 관리, Demo 장치 연동, 대시보드 상태 갱신, 설정 저장, 알림, 기록, 커뮤니티 보강, 빌드 복구, 테스트 검증을 추가한 상태다.

챗봇 화면은 이번 IoT/SW 구현 범위에서 제외했다. 기존 `Chatbot.java`, `Dodam_Cat.java`, 관련 XML은 빌드 호환을 위해 유지한다.

## 현재 상태

| 구분 | 상태 |
| --- | --- |
| Android 언어/화면 | Java, XML |
| 빌드 도구 | Gradle 8.7, Android Gradle Plugin 8.6.1 |
| SDK | compileSdk 35, targetSdk 35, minSdk 24 |
| Firebase | Google Services Plugin 4.5.0, Firebase BoM 34.15.0 |
| 인증 | Firebase Authentication 이메일 로그인/회원가입 |
| 데이터 | Firebase Realtime Database, Firestore SDK 준비 |
| 장치 연동 | 실제 장치 대신 `DemoDeviceGateway`로 동작 검증 |
| 최종 판정 | 실제 Android 기기와 실제 수조 장치 통합 검증 필요 |

## 2021년 기존 앱 범위

2021년 원본 앱은 다음 기능을 중심으로 구성되어 있었다.

- Java `Activity`와 XML 레이아웃 중심 구조
- 로그인, 회원가입, 메인 수조 화면
- 수온, 수질, 수위 표시 이미지
- 히터와 조명 ON/OFF 버튼
- 설정 화면
- Firebase Realtime Database 기반 게시글 조회 일부
- 게시글 작성 화면 일부
- 도담이 캐릭터 화면과 WebView 챗봇 화면
- PHP/MySQL 서버와 통신하는 Request 클래스
- PNG 배경 이미지 기반 고정 배치 UI

기존 구조의 주요 한계는 다음과 같았다.

- Gradle, Android Gradle Plugin, Firebase 의존성이 오래됨
- Firebase 플러그인과 BoM 선언이 중복되거나 정리되지 않음
- 로그인/회원가입에 PHP HTTP 요청 클래스가 남아 있음
- 평문 비밀번호 저장 가능성이 있음
- 센서값과 제어 상태가 하드코딩 문자열 또는 로컬 boolean에 가까움
- Activity 내부에 UI, 상태, 통신, 검증 로직이 집중됨
- 단위 테스트와 최종 검증 문서가 부족함

## 2026년 변경 범위

2026년 작업에서는 기존 Java/XML 앱을 완전히 갈아엎지 않고, 실행 가능한 Android 앱으로 복구하면서 SW로 구현 가능한 기능을 보강했다.

### 빌드와 Firebase

- Gradle Wrapper 8.7 적용
- Android Gradle Plugin 8.6.1 적용
- Google Services Gradle Plugin 4.5.0 적용
- Firebase BoM 34.15.0 적용
- Firebase Analytics, Authentication, Firestore, Realtime Database SDK 추가
- 실제 Firebase 프로젝트용 `app/google-services.json` 유지

### 인증

- PHP HTTP 로그인/회원가입 요청 제거
- Firebase Authentication 이메일 로그인/회원가입 적용
- 평문 비밀번호 로컬 저장 제거
- 로그인/회원가입 중복 클릭 방지
- 로그인 세션 유지

### 수조와 장치 구조

- 수조 등록, 수정, 삭제 화면 추가
- 사용자별 수조와 장치 ID 저장 구조 추가
- 장치 ID 형식 검증과 중복 등록 방지
- 실제 장치가 없어도 앱이 동작하는 `DemoDeviceGateway` 추가
- 장치 통신 인터페이스 `DeviceGateway` 추가
- 장치 명령 결과를 표현하는 Command/ACK 모델 추가

### 대시보드

- `Repository`, `ViewModel`, `UiState`, `Domain Model` 구조 추가
- 하드코딩 센서값 제거
- Demo 장치 센서값을 주기적으로 표시
- 수온, 탁도, 수위, 연결 상태, 마지막 업데이트 표시
- 히터와 조명 명령을 Repository를 통해 전달

### 설정, 알림, 기록

- 사용자별 수조 설정 저장소 추가
- 목표 수온과 조명 스케줄 검증 추가
- Android 13 이상 알림 권한 요청
- 알림 채널 생성
- 최근 센서, 명령, 알림, 설정 이벤트 기록 저장
- 기록 화면 추가

### 커뮤니티

- 게시글 작성자 UID, 카테고리, 작성 시각 저장
- 공지, Q&A, 자유게시판 카테고리 필터 보강
- 비로그인 게시글 작성 차단
- 본인 게시글 삭제 기능 추가

### 정리된 파일

최종 실행에 필요 없는 항목은 제거했다.

- `LoginRequest.java`
- `RegisterRequest.java`
- `UserTestRequest.java`
- `PostingRequest.java`
- `TestActivity.java`
- `activity_test.xml`
- `exexex.xml`
- `php_File/*.php`
- 미사용 Volley 의존성

## 2021년 코드와 2026년 코드 차이

| 구분 | 2021년 코드 | 2026년 변경 코드 |
| --- | --- | --- |
| 인증 | PHP/MySQL HTTP 요청 | Firebase Authentication |
| 비밀번호 저장 | 로컬 저장 가능성 있음 | 평문 저장 제거 |
| 센서 데이터 | 하드코딩 문자열 | `SensorSnapshot`, `DemoDeviceGateway` |
| 장치 제어 | 로컬 boolean 상태 변경 | `ControlCommand`, `CommandResult`, ACK 모델 |
| 구조 | Activity 중심 | Repository, ViewModel, UiState, Domain Model |
| 수조 관리 | 없음 | 수조 등록, 수정, 삭제 |
| 설정 저장 | 일부 텍스트 저장 | 사용자별 수조 설정 저장 |
| 알림 | 채널/권한 부족 | 알림 채널과 권한 요청 |
| 기록 | 없음 | 최근 이벤트 기록 |
| 커뮤니티 | 조회/작성 일부 | UID, 카테고리, 작성 시각, 삭제 보강 |
| 보안 | PHP HTTP 요청 클래스 존재 | 불필요 HTTP 요청 클래스 제거 |
| 테스트 | 기본 샘플 테스트 | 상태 판정/입력 검증 단위 테스트 |

## 프로젝트 구조

```text
app/src/main/java/com/example/dodam
├─ data/                 데이터 저장소와 장치 게이트웨이
├─ domain/               수조, 센서, 명령, 설정 도메인 모델과 유스케이스
├─ notification/         수조 알림 처리
├─ ui/dashboard/         메인 대시보드 ViewModel과 UiState
├─ MainActivity.java     로그인
├─ MainScreen.java       메인 대시보드
├─ Register.java         회원가입
├─ AquariumManageActivity.java
├─ ProfileActivity.java
├─ HistoryActivity.java
└─ Chatbot.java          기존 챗봇 화면, 이번 범위 제외
```

## 실행과 검증

Windows PowerShell 기준으로 다음 명령을 사용한다.

```powershell
.\gradlew.bat clean
.\gradlew.bat :app:assembleDebug
.\gradlew.bat test
.\gradlew.bat lint
```

Debug APK 위치:

```text
app/build/outputs/apk/debug/app-debug.apk
```

최근 검증 결과:

| 항목 | 결과 |
| --- | --- |
| Debug APK 생성 | PASS |
| Unit Test | PASS |
| Lint | PASS |
| Android 기기 설치/실행 | NOT VERIFIED |
| Raspberry Pi, Arduino, 센서 통합 | NOT VERIFIED |

Firebase BoM 34.15.0 의존성 일부는 lint 실행 시 Kotlin metadata 호환성 경고를 stderr에 출력할 수 있다. Gradle 태스크 결과는 `BUILD SUCCESSFUL`이다.

## 문서

자세한 변경 이력과 검증 기록은 `docs` 폴더에 정리했다.

- `docs/README.md`: 문서 목록
- `docs/phase0-audit.md`: 초기 감사 결과
- `docs/phase1-build-recovery.md`: 빌드 복구 결과
- `docs/phase2-to-phase7-implementation.md`: 기능 구현 보고서
- `docs/software-implementation-without-chatbot.md`: 챗봇 제외 구현 범위
- `docs/final-verification-report.md`: 최종 검증 보고서
- `docs/renamed-items.md`: 이름과 개념 정리

## 남은 작업

- 실제 Android 기기 또는 에뮬레이터 설치/화면 조작 검증
- Firebase Authentication 실제 계정 시나리오 검증
- Firebase Realtime Database Security Rules 작성 및 적용
- Raspberry Pi, Arduino, 센서 실장치 프로토콜 연결
- 실제 히터/조명 제어 ACK 검증
- 장기 이력 저장, 그래프, 관리 일정 알림 확장
