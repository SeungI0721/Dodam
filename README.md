# 도담도담 Android 앱

도담도담은 수조 상태 확인, 설정 관리, 커뮤니티, 기록 조회, 도담이 챗봇을 제공하는 Android 앱입니다. 현재 저장소는 실제 라즈베리파이 또는 센서 하드웨어 없이도 Android 앱 단독으로 빌드하고 실행할 수 있도록 정리되어 있습니다.

## 현재 구현 범위

| 구분 | 내용 |
| --- | --- |
| 앱 구조 | Java Activity, XML 레이아웃, Repository, Domain Model, UseCase |
| 인증 | Firebase Authentication 이메일 로그인/회원가입 |
| 데이터 | Firebase Realtime Database, Firestore SDK 준비 |
| 수조 관리 | 수조 이름과 장치 ID 등록, 수정, 삭제 |
| 대시보드 | 데모 센서값 기반 수온, 탁도, 수위, 연결 상태 표시 |
| 장치 제어 | 실제 HW 대신 `DemoDeviceGateway`로 히터와 조명 제어 결과 시뮬레이션 |
| 설정 | 목표 수온, 조명 시간, 알림/자동화 설정 저장 |
| 기록 | 최근 센서, 명령, 알림, 관리 기록 표시 |
| 커뮤니티 | 공지, Q&A, 자유게시판 목록/작성/삭제 |
| 챗봇 | 로컬 FAQ와 수조 상태 기반 도담이 네이티브 챗봇 |

## 개발 환경

| 항목 | 버전 |
| --- | --- |
| Gradle Wrapper | 8.7 |
| Android Gradle Plugin | 8.6.1 |
| Gradle JDK | 17 |
| Java sourceCompatibility | 17 |
| Java targetCompatibility | 17 |
| compileSdk | 35 |
| targetSdk | 35 |
| minSdk | 24 |
| Firebase BoM | 34.15.0 |

프로젝트에는 `tools/jdk-17` 경로의 로컬 JDK 17을 우선 사용하도록 Gradle 설정이 들어 있습니다. Android Studio에서 열 때도 Gradle JVM을 17로 맞추면 됩니다.

## 프로젝트 구조

```text
app/src/main/java/com/example/dodam
├─ data/                 데이터 저장소, 로컬 저장소, 데모 장치 게이트웨이
├─ domain/               수조, 센서, 명령, 챗봇 도메인 모델과 UseCase
├─ notification/         수조 상태 알림 처리
├─ ui/assistant/         네이티브 도담이 챗봇 화면
├─ ui/dashboard/         메인 대시보드 상태 모델과 ViewModel
├─ MainActivity.java     로그인 화면
├─ Register.java         회원가입 화면
├─ MainScreen.java       메인 대시보드 화면
├─ Dodam_Cat.java        도담이 캐릭터 화면
├─ Setting.java          수조 자동화/알림 설정 화면
├─ Community.java        공지 게시판 화면
├─ community_q.java      Q&A 게시판 화면
├─ community_f.java      자유게시판 화면
└─ Commu_po.java         게시글 작성 화면
```

## 빌드 방법

Windows PowerShell 기준으로 다음 명령을 실행합니다.

```powershell
.\gradlew.bat :app:assembleDebug
```

단위 테스트와 lint까지 확인하려면 다음 명령을 사용합니다.

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:lintDebug
```

Debug APK 생성 위치는 다음과 같습니다.

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 최근 검증 결과

| 검증 항목 | 결과 |
| --- | --- |
| Debug APK 빌드 | 성공 |
| 단위 테스트 | 성공 |
| Lint | 성공 |
| Android Studio 실제 기기/에뮬레이터 실행 | 별도 실행 검증 필요 |
| 실제 라즈베리파이/HW 연동 | 구현 범위 제외 |

lint 실행 중 Firebase/Google 라이브러리의 Kotlin metadata 버전 경고가 stderr에 출력될 수 있지만, Gradle 작업 결과는 `BUILD SUCCESSFUL`입니다.

## 문서

상세 구현 기록은 `docs` 폴더에 정리되어 있습니다.

- `docs/README.md`: 문서 목록
- `docs/assistant-rebuild-plan.md`: 챗봇 재구현 계획
- `docs/assistant-final-implementation.md`: 챗봇 최종 구현 문서
- `docs/final-verification-report.md`: 최종 검증 기록
- `docs/phase0-audit.md`: 초기 감사 기록
- `docs/phase1-build-recovery.md`: 빌드 복구 기록
- `docs/phase2-to-phase7-implementation.md`: 단계별 구현 기록
