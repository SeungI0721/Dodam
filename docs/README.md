# 문서 목록

이 폴더는 Dodam Android 앱의 복구, 기능 구현, 도우미 화면 재구축, 검증 기록을 정리한 문서 모음입니다.

## 읽는 순서

1. `../README.md`
   - 프로젝트 개요, 개발 환경, 빌드 방법, 현재 구현 범위를 확인합니다.

2. `phase0-audit.md`
   - 초기 코드 감사 결과와 수정이 필요했던 위험 항목을 확인합니다.

3. `phase1-build-recovery.md`
   - Gradle, SDK, Firebase 빌드 복구 내용을 확인합니다.

4. `phase2-to-phase7-implementation.md`
   - 구조 개선, 데모 장치, 대시보드, 설정, 알림, 기록, 인증 구현 내용을 확인합니다.

5. `software-implementation-without-chatbot.md`
   - 실제 HW 없이 Android 앱 단독으로 구현한 SW 기능 범위를 확인합니다.

6. `assistant-rebuild-plan.md`
   - 기존 WebView 챗봇을 대체할 네이티브 도담이 도우미 화면 재구축 계획을 확인합니다.

7. `assistant-final-implementation.md`
   - Phase 0부터 Phase 7까지 구현된 도우미 구조, 기능, 검증 결과를 확인합니다.

8. `final-verification-report.md`
   - 최종 빌드, 테스트, lint, 미검증 항목을 확인합니다.

9. `renamed-items.md`
   - 코드 구조 개선 중 변경된 이름과 기존 이름을 확인합니다.

## 문서 작성 기준

- 실제 실행 가능한 상태와 아직 검증하지 못한 상태를 분리해서 기록합니다.
- 실제 라즈베리파이 또는 센서 HW가 없는 기능은 Android 앱 단독 데모 기준으로 범위를 명확히 적습니다.
- API key, 인증 토큰, 비밀번호 같은 민감 정보는 문서에 적지 않습니다.
- 공개 저장소에는 실제 `app/google-services.json` 대신 `app/google-services.example.json`만 포함합니다.
