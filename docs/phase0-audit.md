<!-- 도담도담 프로젝트의 초기 감사와 보안 위험을 정리한 문서이다. -->
# Phase 0 초기 감사 보고서

작성일: 2026-06-26

## 감사 목적

2021년에 작성된 Java/XML 기반 Android 앱을 최신 Android 빌드 환경에서 복구하기 전에, 구조적 위험과 보안 위험을 확인했다. 이 문서는 초기 상태를 기록하는 용도이며, 현재 저장소에는 일부 위험 항목이 이미 제거되어 있다.

## 초기 프로젝트 구성

- Android Java Activity와 XML 레이아웃 중심 구조
- Firebase Realtime Database 일부 사용
- PHP/MySQL 서버 예제 코드 포함
- 로그인, 회원가입, 메인 수조 화면, 설정 화면, 커뮤니티 화면, 챗봇 화면 포함
- PNG 배경 이미지와 고정 위치 기반 UI 다수

## 초기 위험 항목

| 항목 | 초기 상태 | 위험도 | 조치 결과 |
| --- | --- | --- | --- |
| PHP HTTP 로그인/회원가입 요청 | `LoginRequest`, `RegisterRequest`, `UserTestRequest` 존재 | 높음 | 제거 완료 |
| PHP HTTP 게시글 요청 | `PostingRequest` 존재 | 높음 | 제거 완료 |
| PHP 서버 예제 | `php_File/*.php` 존재 | 높음 | 제거 완료 |
| 평문 비밀번호 저장 가능성 | SharedPreferences 저장 흐름 존재 | 높음 | Firebase Auth 세션으로 교체 |
| Cleartext HTTP 허용 가능성 | 구형 통신 흐름에 필요 | 높음 | 앱 핵심 경로에서 제거 |
| 장치 제어 상태 | 로컬 boolean 중심 | 중간 | Command/ACK 모델로 보강 |
| 센서값 | 하드코딩 문자열 중심 | 중간 | `SensorSnapshot`과 Demo 장치로 교체 |
| 테스트 | 기본 샘플 수준 | 중간 | 단위 테스트 추가 |

## 구조적 문제

초기 앱은 Activity 내부에 UI 처리, 인증 요청, 상태 판단, 장치 제어 상태, 알림 로직이 섞여 있었다. 이 구조는 기능 추가 시 회귀 위험이 크기 때문에 다음 방향으로 분리했다.

- 화면 상태: `DashboardUiState`
- 화면 로직: `DashboardViewModel`
- 수조 데이터와 장치 명령: `AquariumRepository`
- 장치 통신 추상화: `DeviceGateway`
- 도메인 값: `domain/model`
- 상태 판정과 입력 검증: `domain/usecase`

## 보안 정리 방향

- 인증은 Firebase Authentication으로 통일
- 비밀번호는 앱 로컬 저장소에 저장하지 않음
- 사용자 데이터는 UID 기준으로 분리
- Firebase Security Rules는 콘솔 또는 별도 Rules 파일로 후속 적용 필요
- 챗봇은 이번 범위 제외로 기존 로컬 HTTP URL이 남아 있을 수 있음

## 현재 반영 상태

초기 감사에서 발견한 앱 핵심 경로의 PHP HTTP 요청 클래스와 PHP 예제 파일은 제거했다. 다만 실제 Firebase Rules, 실제 Android 기기 실행, 실제 Raspberry Pi/Arduino 연동은 환경이 없어 검증하지 못했다.
