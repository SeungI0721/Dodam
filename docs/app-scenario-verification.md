# App Scenario Verification

검증일: 2026-07-09

이 문서는 Dodam 앱의 주요 사용자 동작을 화면 흐름 기준으로 나누어 검증한 기록이다. 실제 Android 기기/에뮬레이터 실행과 실제 Firebase 인증 계정 쓰기는 현재 환경에서 직접 완료하지 못했으므로, 코드/빌드/정적 매핑/원격 공개 읽기 검증과 분리해서 표시한다.

## 검증 범위

| 구분 | 결과 | 근거 |
| --- | --- | --- |
| XML ID 매핑 | PASS | 각 Activity의 `setContentView()` 레이아웃에 `findViewById()` 대상 ID가 존재함 |
| Activity Manifest 등록 | PASS | 주요 화면 Activity가 `AndroidManifest.xml`에 등록됨. `AssistantActivity`는 `.ui.assistant.AssistantActivity`로 등록됨 |
| Firebase Rules JSON | PASS | `database.rules.json`, `firebase.json` JSON 파싱 통과 |
| Firebase 공개 읽기 | PASS | `UserIds/__codex_check__`, `Post` REST 읽기 모두 `200 OK`, 본문 `null` |
| Firebase Auth signup | FAIL | REST 임시 회원가입 요청이 `CONFIGURATION_NOT_FOUND`를 반환함 |
| Debug APK build | PASS | `.\gradlew.bat :app:assembleDebug` 성공 |
| Lint | PASS | `.\gradlew.bat :app:lintDebug` 성공. Firebase/Google Kotlin metadata 경고가 출력되지만 Gradle 결과는 성공 |
| Unit test | FAIL | `.\gradlew.bat :app:testDebugUnitTest`에서 모든 테스트 클래스가 `ClassNotFoundException`으로 초기화 실패 |
| Android device/emulator run | NOT VERIFIED | 현재 환경에서 `adb` 명령을 찾을 수 없어 실기/에뮬레이터 조작 검증 불가 |
| Firebase authenticated write | NOT VERIFIED | 실제 앱 로그인 토큰으로 `UserIds`, `Users`, `Post` 쓰기까지는 직접 검증하지 못함 |

## 순서별 앱 동작 시나리오

| 순서 | 시나리오 | 예상 동작 | 검증 결과 |
| --- | --- | --- | --- |
| 1 | 앱 시작 | `MainActivity` 로그인 화면이 실행된다. | Manifest 등록 및 레이아웃 매핑 통과 |
| 2 | 로그인 입력 검증 | 이메일/비밀번호 공백, 이메일 형식 오류를 DB 접근 전에 차단한다. | 코드 확인 통과 |
| 3 | 로그인 성공 | Firebase Auth 로그인 성공 후 `MainScreen`으로 이동한다. | 코드/Manifest 매핑 통과. 실제 계정 로그인은 미검증 |
| 4 | 회원가입 화면 이동 | 로그인 화면의 회원가입 버튼으로 `Register`가 열린다. | 코드/Manifest/XML 매핑 통과 |
| 5 | 아이디 중복 확인 | `UserIds/{userId}`를 읽어 중복 여부를 판단한다. | 원격 공개 읽기 통과 |
| 6 | 회원가입 저장 | Auth 계정 생성 후 토큰 확인, `/UserIds/{userId}`, `/Users/{uid}` 저장을 수행한다. | 현재 원격 Auth 설정 실패. REST 임시 회원가입 요청이 `CONFIGURATION_NOT_FOUND`를 반환함 |
| 7 | 대시보드 표시 | 데모 장치 게이트웨이 상태를 기반으로 수온, 탁도, 수위, 연결 상태를 표시한다. | 빌드/레이아웃 매핑 통과. 실제 화면 렌더링은 미검증 |
| 8 | 히터/조명 제어 | 버튼 클릭 시 `DashboardViewModel`을 통해 데모 ACK를 표시한다. | 코드 매핑 통과. 실제 하드웨어 ACK는 미검증 |
| 9 | 설정 저장 | 목표 수온, 조명 시간, 알림/자동화 토글을 검증 후 저장한다. | 코드/레이아웃 매핑 통과 |
| 10 | 수조 저장/삭제 | 수조 이름과 장치 ID를 검증하고 로컬 저장소 및 로그인 시 `Users/{uid}/aquariums`에 반영한다. | 코드/Rules 매핑 통과. 실제 인증 쓰기는 미검증 |
| 11 | 프로필 저장 | 닉네임/사진명을 검증 후 `Users/{uid}/profile`에 저장한다. | 코드/Rules 매핑 통과. 실제 인증 쓰기는 미검증 |
| 12 | 커뮤니티 목록 | `Post`를 읽어 공지/Q&A/자유게시판 카테고리별 목록을 표시한다. | 원격 공개 읽기 통과 |
| 13 | 커뮤니티 작성 | 로그인 사용자가 `authorUid`를 포함해 `Post/{postId}`에 게시글을 저장한다. | 코드/Rules 매핑 통과. 실제 인증 쓰기는 미검증 |
| 14 | 커뮤니티 삭제 | 앱과 Rules 모두 작성자 UID 기준으로 삭제를 제한한다. | 코드/Rules 매핑 통과. 실제 인증 삭제는 미검증 |
| 15 | 도담이/도우미 | `Dodam_Cat`에서 네이티브 `AssistantActivity`로 이동하고 로컬 FAQ 기반 응답을 표시한다. | Manifest/XML 매핑 통과 |
| 16 | 기록 화면 | 대시보드 버튼 길게 누르기로 기록 화면에 이동한다. | 코드/Manifest/XML 매핑 통과 |
| 17 | 로그아웃 | 프로필 이미지 길게 누르기로 Firebase Auth 로그아웃 후 로그인 화면으로 돌아간다. | 코드/Manifest 매핑 통과. 실제 Auth 세션은 미검증 |

## 발견된 오류와 미검증 항목

1. 단위 테스트 실행 오류
   - 증상: `:app:testDebugUnitTest`에서 `ExampleUnitTest` 포함 6개 테스트 클래스가 모두 `ClassNotFoundException`으로 실패한다.
   - 관찰: 테스트 `.class` 파일은 생성되어 있어 테스트 소스 누락은 아니다.
   - 판단: 현재 Windows 한글 프로젝트 경로 또는 Gradle 테스트 런처 클래스패스 문제 가능성이 높다.

2. 실기/에뮬레이터 실행 미검증
   - 증상: `adb devices` 실행 시 `adb` 명령을 찾을 수 없다.
   - 영향: 실제 화면 렌더링, 버튼 클릭, Firebase Auth 세션 유지, 실제 Toast/Dialog 흐름은 이 환경에서 확인하지 못했다.

3. Firebase 인증 쓰기 미검증
   - 공개 읽기 Rules는 원격에서 통과했다.
   - 로그인된 실제 사용자의 `/UserIds`, `/Users`, `/Post` 쓰기는 실제 앱 인증 토큰으로만 확인 가능하다.

4. Firebase Authentication 설정 오류
   - 증상: Firebase Auth REST 임시 회원가입 요청이 `CONFIGURATION_NOT_FOUND`를 반환한다.
   - 영향: 앱의 아이디 중복 확인은 통과해도 `createUserWithEmailAndPassword()` 단계에서 회원가입이 실패한다.
   - 필요한 조치: Firebase Console에서 Authentication을 시작하고 Email/Password 로그인 제공자를 활성화해야 한다.

## 보수적 결론

현재 코드 기준으로 주요 화면/XML/Manifest/DB 경로 매핑은 정리되어 있고, debug APK 빌드와 lint는 통과했다. 다만 Firebase Authentication 설정이 `CONFIGURATION_NOT_FOUND` 상태라 회원가입은 원격 Auth 설정 전까지 정상 동작할 수 없다. 단위 테스트 런처 오류와 실기/에뮬레이터 부재도 남아 있으므로 모든 앱 동작이 실제 기기에서 정상이라고 단정할 수는 없다.
