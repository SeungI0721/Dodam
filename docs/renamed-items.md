<!-- 도담도담 프로젝트 구조 개선 과정에서 변경된 이름과 개념을 정리한 문서이다. -->
# 이름과 개념 정리

작성일: 2026-06-26

## 변경한 주요 개념

| 기존 이름/개념 | 새 이름/개념 | 이유 |
| --- | --- | --- |
| `Temperature` 문자열 | `SensorSnapshot.temperatureC` | 수온 단위가 명확한 센서값으로 표현 |
| `Water` 문자열 | `SensorSnapshot.turbidity` | 수질을 탁도 센서값으로 표현 |
| `Depth` 문자열 | `SensorSnapshot.waterLevelMm` | 수위 단위를 mm로 명시 |
| `heter` | `heaterOn` | 오타와 의미 불명확성 제거 |
| `LED` | `lightOn` | 조명 상태를 boolean으로 표현 |
| Activity 내부 상태 로직 | `DeviceGateway`, `AquariumRepository`, `DashboardViewModel` | UI, 데이터, 장치 통신 책임 분리 |
| 로컬 boolean 버튼 상태 | `ControlCommand`, `CommandResult` | 명령 전송과 ACK 결과 분리 |
| `SAVE_LOGIN_DATA`, `PWD` 저장 | Firebase Auth session | 평문 비밀번호 저장 제거 |

## 유지한 이름

기존 XML id와 Activity 이름은 레이아웃 연결 안정성을 위해 대부분 유지했다.

- `MainActivity`
- `MainScreen`
- `Setting`
- `Register`
- `Commu_po`
- `Post`
- `activity_main_screen.xml`
- `activity_setting.xml`

## 향후 정리 후보

다음 UI 정리 단계에서는 Activity와 XML 이름을 더 명확하게 바꿀 수 있다.

| 현재 이름 | 후보 이름 |
| --- | --- |
| `MainScreen` | `DashboardActivity` |
| `Setting` | `SettingsActivity` |
| `Commu_po` | `PostEditorActivity` |
| `community_f` | `FreeBoardActivity` |
| `community_q` | `QuestionBoardActivity` |
