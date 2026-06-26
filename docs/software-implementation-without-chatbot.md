<!-- 챗봇을 제외하고 소프트웨어로 구현한 기능을 정리한 문서이다. -->
# 챗봇 제외 SW 구현 보고서

작성일: 2026-06-26

## 목적

이번 구현 범위는 스마트 수조 관리 앱의 핵심 SW 기능을 보강하는 것이다. 도담이 챗봇은 기존 파일을 유지하되, 이번 IoT 통합과 검증 범위에서는 제외했다.

## 구현한 기능

### 수조 관리

- `AquariumManageActivity` 추가
- 수조 이름과 장치 ID 등록, 수정, 삭제
- 사용자 UID 기준 로컬 저장
- Firebase `users/{uid}/aquariums/{aquariumId}` 동기화 구조 추가
- 장치 ID 형식 검증과 중복 등록 방지

### 프로필 관리

- `ProfileActivity` 추가
- Firebase Auth `displayName` 수정
- Firebase `users/{uid}/profile` 저장 구조 추가
- 메인 화면 프로필 이미지 클릭 시 진입
- 메인 화면 프로필 이미지 길게 누르면 로그아웃

### 대시보드 표시

- `DashboardViewModel`, `DashboardUiState` 추가
- 선택된 수조의 `deviceId`로 Demo 장치 시작
- 수온, 탁도, 수위, 연결 상태, 마지막 업데이트 시각 표시
- 히터와 조명 명령 결과를 UI 상태로 반영

### 기록

- `LocalEventStore` 추가
- 최근 센서 수신, 연결 상태, 명령, 설정 이벤트 저장
- `HistoryActivity` 추가
- 최근 이벤트 최대 100개까지 로컬 저장

### 커뮤니티

- 게시글에 `postId`, `authorUid`, `category`, `createdAt` 저장
- 공지, Q&A, 자유게시판 카테고리 필터 적용
- 로그인 사용자만 게시글 작성 가능
- 본인 게시글 길게 누르면 삭제 가능

### 설정과 알림

- 사용자별 수조 설정 저장
- 목표 수온과 조명 스케줄 입력 검증
- Android 13 이상 알림 권한 요청
- 알림 채널 생성
- 상태 변화 알림 중복 방지

## 제외한 범위

- `Chatbot.java` WebView 기반 챗봇
- `Dodam_Cat.java` 도담이 캐릭터 화면
- 외부 챗봇 API
- 오프라인 FAQ 챗봇

## 실제 하드웨어 연동 전 필요한 정보

- Raspberry Pi 또는 Arduino 통신 프로토콜
- 센서별 데이터 단위와 보정식
- 히터와 조명 제어 명령 형식
- 장치 ACK 응답 형식
- 안전 제한 기준
- 네트워크 연결 방식

## 남은 SW 개선 후보

- Room 기반 장기 이력 저장
- 센서 그래프와 기간별 통계
- 수조 목록 선택 UI 개선
- 게시글 수정 화면
- Firebase Security Rules 파일화
- 고정 PNG 기반 XML 레이아웃 반응형 개선
