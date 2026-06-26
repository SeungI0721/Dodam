<!-- 챗봇을 제외한 도담도담 앱의 최종 검증 결과를 정리한 문서이다. -->
# 최종 검증 보고서

작성일: 2026-06-26

## 검증 환경

| 항목 | 값 |
| --- | --- |
| 운영체제 | Windows 11 |
| JDK | 21.0.2 |
| Gradle | 8.7 |
| Android Gradle Plugin | 8.6.1 |
| Google Services Gradle Plugin | 4.5.0 |
| Firebase BoM | 34.15.0 |
| compileSdk / targetSdk / minSdk | 35 / 35 / 24 |
| Firebase 프로젝트 | `dodam-bb28c` |
| 실제 Android 기기 | NOT VERIFIED |
| Raspberry Pi / Arduino / 센서 | NOT VERIFIED |
| Demo 장치 | `DemoDeviceGateway`로 검증 |

## 최종 요약

| 분류 | 개수 |
| --- | ---: |
| PASS | 11 |
| PARTIAL | 7 |
| FAIL | 0 |
| NOT VERIFIED | 6 |

`PASS`는 빌드, 단위 테스트, lint, 정적 검색, Demo 장치 수준에서 증거가 있는 항목만 의미한다. 실제 Firebase 계정 로그인, 실제 Android 설치 실행, 실제 Raspberry Pi/Arduino/센서 통합은 환경 부재로 `NOT VERIFIED`로 분류했다.

## 자동 검증 결과

| 명령 | 결과 | 비고 |
| --- | --- | --- |
| `.\gradlew.bat clean` | PASS | `BUILD SUCCESSFUL` |
| `.\gradlew.bat :app:assembleDebug` | PASS | Debug APK 생성 |
| `.\gradlew.bat test` | PASS | Unit Test 성공 |
| `.\gradlew.bat lint` | PASS | 중대 오류 없음 |
| `.\gradlew.bat connectedAndroidTest` | NOT VERIFIED | 연결된 기기 없음 |

Debug APK 위치:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Firebase BoM 34.15.0 의존성 일부는 lint 실행 시 Kotlin metadata 호환성 경고를 stderr에 출력한다. Gradle 태스크 결과는 `BUILD SUCCESSFUL`이다.

## 요구사항 추적표

| ID | 기능 | 검증 방법 | 결과 | 근거 |
| --- | --- | --- | --- | --- |
| BLD-01 | Clean Build | Gradle | PASS | `.\gradlew.bat clean` |
| BLD-02 | Debug APK | Gradle | PASS | `.\gradlew.bat :app:assembleDebug` |
| BLD-03 | Unit Test | Gradle | PASS | `.\gradlew.bat test` |
| BLD-04 | Lint | Gradle | PASS | `.\gradlew.bat lint` |
| BLD-05 | Connected Test | Android 기기 | NOT VERIFIED | 연결된 기기 없음 |
| AUTH-01 | 회원가입 입력 검증 | 코드/빌드 | PARTIAL | 실제 Firebase 계정 시나리오 미검증 |
| AUTH-02 | 로그인 중복 클릭 방지 | 코드/빌드 | PARTIAL | 실제 기기 UX 미검증 |
| AUTH-03 | 평문 비밀번호 저장 제거 | 정적 검색 | PASS | SharedPreferences 비밀번호 저장 제거 |
| USER-01 | 프로필 수정 | 코드/빌드 | PARTIAL | 실제 Firebase 실행 미검증 |
| AQ-01 | 수조 등록/수정/삭제 | 코드/단위 테스트 | PARTIAL | 실제 기기 화면 조작 미검증 |
| DEV-01 | Demo 장치 연결 | 코드/빌드 | PASS | `DemoDeviceGateway` |
| DEV-02 | 실제 장치 연결 | 하드웨어 | NOT VERIFIED | 장치 미연결 |
| SENSOR-01 | 상태 판정 | 단위 테스트 | PASS | `AquariumStatusEvaluatorTest` |
| DASH-01 | 대시보드 표시 | 코드/빌드 | PARTIAL | 실제 기기 화면 미검증 |
| CTRL-01 | 히터/조명 명령 | Demo 코드 | PASS | Demo ACK 구현 |
| CTRL-02 | 실제 히터/조명 ACK | 하드웨어 | NOT VERIFIED | 장치 미연결 |
| SET-01 | 설정 검증 | 단위 테스트 | PASS | `AquariumInputValidatorTest` |
| SET-02 | 설정 저장/복원 | 코드/빌드 | PARTIAL | 실제 기기 재실행 미검증 |
| NOTI-01 | 알림 채널/권한 | 코드/빌드 | PARTIAL | 실제 기기 권한 UX 미검증 |
| HIST-01 | 이벤트 기록 | 코드/빌드 | PARTIAL | 실제 장기 사용 미검증 |
| COMM-01 | 게시글 작성 제한 | 코드/빌드 | PARTIAL | 실제 Firebase 계정 미검증 |
| COMM-02 | 게시글 분류/삭제 | 코드/빌드 | PARTIAL | 실제 기기 조작 미검증 |
| SEC-01 | HTTP/PHP 제거 | 정적 검색 | PASS | 챗봇 제외 앱 경로에서 PHP 요청 제거 |
| SEC-02 | Firebase Rules | Firebase 콘솔 | NOT VERIFIED | 콘솔 환경 필요 |

## 발견한 결함과 조치

| ID | 심각도 | 영역 | 원인 | 조치 | 결과 |
| --- | --- | --- | --- | --- | --- |
| BUG-001 | High | Layout | 회원가입 layout 중복 id | id 고유화와 제약 추가 | PASS |
| BUG-002 | Medium | Layout | 대시보드/item layout 제약 누락 | Constraint 보강 | PASS |
| BUG-003 | High | Security | 미사용 PHP HTTP 요청 클래스 잔존 | 요청 클래스 삭제 | PASS |
| BUG-004 | Medium | Auth | 로그인/회원가입 중복 클릭 가능 | 버튼 비활성화와 완료 후 복구 | PASS |
| BUG-005 | Medium | Aquarium | 장치 ID 형식/중복 검증 부족 | 입력 검증과 중복 검사 추가 | PASS |
| BUG-006 | Medium | Community | 비로그인 게시글 작성 가능 | 로그인 사용자만 작성 허용 | PASS |

## 제거한 파일

- `LoginRequest.java`
- `RegisterRequest.java`
- `UserTestRequest.java`
- `PostingRequest.java`
- `TestActivity.java`
- `activity_test.xml`
- `exexex.xml`
- `php_File/*.php`

## 미검증 항목

- 실제 Android 기기 또는 에뮬레이터 설치와 화면 조작
- Firebase Auth 실제 계정 로그인/회원가입
- Firebase Realtime Database Security Rules
- 실제 수조 장치 데이터 수신
- 센서 단위와 보정값 검증
- 실제 히터/조명 제어 ACK
- 백그라운드/앱 종료 상태 알림 동작

## 최종 판정

**실제 장치 통합 검증 필요**

빌드, Debug APK 생성, 단위 테스트, lint, Demo 장치 기반 로직은 통과했다. 다만 실제 Android 기기와 Raspberry Pi, Arduino, 센서, 히터, 조명 장치가 연결되지 않았으므로 최종 배포 가능 상태로 판정하지 않는다.
