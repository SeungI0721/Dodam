# Dodam Android

Dodam은 복구 및 현대화 작업을 거친 Android 기반 수족관 관리 앱입니다. Firebase Authentication과 Firebase 데이터 서비스를 사용하도록 구성되어 있으며, 현재 실제 하드웨어 대신 `DemoDeviceGateway` 기반 데모 장치 게이트웨이로 센서 상태와 히터/조명 제어 결과를 시뮬레이션합니다.

이 저장소는 포트폴리오 공개를 염두에 두고 정리된 Android/Firebase 프로젝트입니다. 다만 실제 Android 기기, 실제 Firebase 계정 시나리오, Raspberry Pi/Arduino/센서, 히터/조명 하드웨어 ACK까지 검증된 운영 배포 상태로 주장하지 않습니다.

## 현재 구현 범위

| 구분 | 상태 | 내용 |
| --- | --- | --- |
| Android 앱 구조 | 구현 | Java Activity, XML Layout, Repository, Domain Model, UseCase 구조 |
| Firebase 인증 | 부분 검증 | Firebase Authentication 이메일 로그인/회원가입 코드 구성. 실제 계정 시나리오는 미검증 |
| Firebase 데이터 서비스 | 부분 검증 | Firebase Realtime Database 및 Firestore SDK 구성. 실제 Firebase 데이터 흐름은 미검증 |
| 수족관 관리 | 부분 검증 | 수족관 이름과 장치 ID 등록, 수정, 삭제 흐름 구현. 실제 기기 화면 조작은 미검증 |
| 대시보드 | 부분 검증 | 데모 센서값 기반 수온, 탁도, 수위, 연결 상태 표시 |
| 장치 제어 | 부분 검증 | `DemoDeviceGateway`로 히터/조명 제어 ACK를 시뮬레이션. 실제 하드웨어 ACK는 미검증 |
| 설정 | 구현 | 목표 수온, 조명 시간, 알림/자동화 설정 저장 로직 |
| 기록 | 부분 검증 | 최근 센서, 명령, 알림, 관리 기록 표시 로직 |
| 커뮤니티 | 부분 검증 | 공지, Q&A, 자유게시판 목록/작성/삭제 흐름. 실제 Firebase 계정 기반 사용은 미검증 |
| 앱 내 도우미 | 구현 | 로컬 FAQ와 수족관 상태 기반 네이티브 도우미 화면 |

## 개발 환경

| 항목 | 버전 |
| --- | --- |
| Gradle Wrapper | 8.7 |
| Android Gradle Plugin | 8.6.1 |
| Gradle JDK | 17 이상 권장 |
| Java sourceCompatibility | 17 |
| Java targetCompatibility | 17 |
| compileSdk | 35 |
| targetSdk | 35 |
| minSdk | 24 |
| Firebase BoM | 34.15.0 |

Android Studio에서는 Gradle JVM을 17 이상으로 맞춥니다. 개인 PC의 JDK 경로, Firebase 설정, IDE 설정 파일은 저장소에 포함하지 않습니다.

## 프로젝트 구조

```text
Dodam/
├─ README.md
├─ app/                  Android 앱 모듈
├─ docs/                 구현, 복구, 검증 기록 문서
├─ gradle/wrapper/       Gradle Wrapper 실행 파일
├─ build.gradle          루트 Gradle 빌드 설정
├─ settings.gradle       Gradle 모듈 설정
└─ gradle.properties     Gradle 실행 옵션
```

주요 Java 소스 구조는 다음과 같습니다.

```text
app/src/main/java/com/example/dodam
├─ data/                 데이터 저장소, 로컬 저장소, 데모 장치 게이트웨이
├─ domain/               수족관, 센서, 명령, 도우미 도메인 모델과 UseCase
├─ notification/         수족관 상태 알림 처리
├─ ui/assistant/         네이티브 도우미 화면
├─ ui/dashboard/         메인 대시보드 상태 모델과 ViewModel
├─ MainActivity.java     로그인 화면
├─ Register.java         회원가입 화면
├─ MainScreen.java       메인 대시보드 화면
├─ Dodam_Cat.java        Dodam 캐릭터 화면
├─ Setting.java          수족관 자동화/알림 설정 화면
├─ Community.java        공지 게시판 화면
├─ community_q.java      Q&A 게시판 화면
├─ community_f.java      자유게시판 화면
└─ Commu_po.java         게시글 작성 화면
```

## 빌드 방법

Firebase Authentication과 Database SDK를 사용하므로 로컬 빌드에는 Firebase 설정 파일이 필요합니다. 공개 저장소에는 실제 `app/google-services.json`을 포함하지 않고, 형식 참고용 `app/google-services.example.json`만 포함합니다.

1. Firebase Console에서 Android 앱 패키지 이름을 `com.example.dodam`으로 등록합니다.
2. 내려받은 `google-services.json`을 `app/google-services.json` 위치에 둡니다.
3. Android Studio의 Gradle JVM 또는 로컬 JDK를 17 이상으로 맞춥니다.

Windows PowerShell 기준 빌드 명령:

```powershell
.\gradlew.bat :app:assembleDebug
```

테스트와 lint 확인:

```powershell
.\gradlew.bat test
.\gradlew.bat lint
```

Debug APK 생성 위치:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 검증 결과

문서화된 최종 검증일은 **2026-06-26**입니다. 상세 내용은 `docs/final-verification-report.md`에 기록되어 있습니다.

| 검증 항목 | 결과 | 근거 |
| --- | --- | --- |
| Gradle clean | PASS | `.\gradlew.bat clean` 결과 `BUILD SUCCESSFUL` |
| Debug APK build | PASS | `.\gradlew.bat :app:assembleDebug` 결과 Debug APK 생성 |
| Unit tests | PASS | `.\gradlew.bat test` 통과 |
| Lint | PASS | `.\gradlew.bat lint` 통과. Firebase/Google 라이브러리 Kotlin metadata 경고는 있었으나 Gradle 결과는 성공 |
| Real Android device execution | NOT VERIFIED | 연결된 실제 Android 기기/에뮬레이터에서 전체 화면 조작 미검증 |
| Real Firebase account scenario | NOT VERIFIED | 실제 Firebase 계정으로 로그인, 회원가입, 데이터 쓰기/읽기 시나리오 미검증 |
| Raspberry Pi / Arduino / sensor integration | NOT VERIFIED | 실제 Raspberry Pi, Arduino, 센서 연동 환경 없음 |
| Actual heater/light hardware ACK | NOT VERIFIED | 실제 히터/조명 장치 응답 미검증. 데모 ACK만 구현 |

검증 상태의 구분은 다음 기준을 따릅니다.

- `구현`: 코드와 앱 구조가 존재하고 빌드 대상에 포함됩니다.
- `부분 검증`: 코드, 빌드, 단위 테스트 또는 데모 게이트웨이 수준에서만 확인되었습니다.
- `미검증`: 실제 기기, 실제 Firebase 계정, 실제 하드웨어 환경이 필요하지만 아직 확인하지 못했습니다.

이 프로젝트는 포트폴리오용 복구 및 현대화 사례이며, 실제 서비스 운영 또는 하드웨어 제어 제품으로 production-ready 상태라고 주장하지 않습니다.

## 문서

상세 구현 기록은 `docs` 폴더에 정리되어 있습니다.

- `docs/README.md`: 문서 목록
- `docs/assistant-rebuild-plan.md`: 도우미 재구축 계획
- `docs/assistant-final-implementation.md`: 도우미 최종 구현 문서
- `docs/final-verification-report.md`: 최종 검증 기록
- `docs/phase0-audit.md`: 초기 감사 기록
- `docs/phase1-build-recovery.md`: 빌드 복구 기록
- `docs/phase2-to-phase7-implementation.md`: 단계별 구현 기록

## 공개 저장소 주의사항

다음 항목은 `.gitignore`로 제외되어야 하며, 현재 저장소 설정에 반영되어 있습니다.

- `app/google-services.json`
- `.env`, `.env.local`, `.env.development`, `.env.production`
- `*.key`, `*.pem`, `*.p12`, `*.jks`, `*.keystore`, `serviceAccountKey.json`
- `.idea/`, `.vscode/`, `*.iml`, `local.properties`
- `.gradle/`, `build/`, `app/build/`, `*.apk`, `*.aab` 등 빌드 출력물

실제 Firebase 인증 정보, 서비스 계정 키, 서명 키, 로컬 IDE 설정, 빌드 산출물은 공개 저장소에 커밋하지 않습니다.
