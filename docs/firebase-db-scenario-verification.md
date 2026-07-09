# Firebase DB Scenario Verification

검증일: 2026-07-09

이 문서는 앱 코드에서 Firebase Realtime Database와 직접 연결되는 경로를 기준으로 작성한 시나리오 검증 기록이다. 실제 원격 Firebase 계정에 규칙을 배포하고 인증 계정으로 실행해야 하는 항목은 로컬 코드 검증과 분리해서 표시한다.

## DB 경로 인벤토리

| 경로 | 사용 화면/코드 | 목적 | 현재 검증 상태 |
| --- | --- | --- | --- |
| `UserIds/{userId}` | `Register` | 회원가입 전 아이디 중복 확인, 회원가입 후 아이디-UID 매핑 저장 | 코드 매핑 확인, 원격 읽기 통과, 실제 인증 쓰기는 미검증 |
| `Users/{uid}` | `Register`, `ProfileActivity`, `UserAquariumRepository` | 회원 기본 정보, 프로필, 수조 정보 저장 | 코드 매핑 확인, 실제 인증 계정 쓰기는 미검증 |
| `Post/{postId}` | `Community`, `community_q`, `community_f`, `Commu_po`, `CustomAdapter` | 커뮤니티 게시글 목록, 작성, 삭제 | 코드 매핑 확인, 실제 인증 계정 쓰기는 미검증 |

모든 앱 코드의 Realtime Database 인스턴스 생성은 `DodamFirebaseDatabase`를 통해 `https://dodam-bb28c-default-rtdb.firebaseio.com`로 통일했다.

## 순서별 검증 시나리오

| 순서 | 시나리오 | 예상 동작 | 검증 결과 |
| --- | --- | --- | --- |
| 1 | DB URL 설정 | 앱이 기본 DB URL을 명시적으로 사용한다. | 통과. `DodamFirebaseDatabase`에서 URL을 고정해 사용한다. 단, 로컬 `app/google-services.json`에는 `firebase_url`이 없어 Firebase Console에서 최신 파일을 다시 내려받는 것을 권장한다. |
| 2 | 회원가입 아이디 형식 검증 | `a-z`, `0-9`, `_` 조합의 4~20자만 DB 조회로 넘어간다. 대문자는 소문자로 정규화된다. | 통과. DB 호출 전에 로컬 검증이 수행된다. |
| 3 | 아이디 중복 확인 | `UserIds/{userId}`를 읽어 값이 없으면 사용 가능, 값이 있으면 중복으로 처리한다. | 코드 통과, 원격 읽기 통과. 2026-07-09 재확인 시 REST 읽기가 `200 OK`와 `null`을 반환했다. |
| 4 | 회원가입 저장 | Firebase Auth 계정 생성 후 인증 토큰을 확인하고 `/UserIds/{userId}`와 `/Users/{uid}`를 함께 저장한다. | Auth 단계 실패. 2026-07-09 REST 임시 회원가입 요청이 `CONFIGURATION_NOT_FOUND`를 반환했다. Firebase Console에서 Authentication 및 Email/Password 제공자 설정이 필요하다. |
| 5 | 로그인 | 로그인은 Firebase Auth만 사용하며 DB 직접 접근은 없다. | DB 시나리오 대상 아님. 빌드 통과. |
| 6 | 프로필 저장 | 로그인된 사용자의 `Users/{uid}/profile`에 프로필이 저장된다. | 코드 매핑 통과. 실제 원격 쓰기는 미검증이다. |
| 7 | 수조 저장/삭제 | 로그인된 사용자의 `Users/{uid}/aquariums/{aquariumId}`에 수조 정보가 저장/삭제된다. | 코드 매핑 통과. 실제 원격 쓰기는 미검증이다. |
| 8 | 커뮤니티 목록 | `Post`를 읽어 카테고리별 화면에 표시한다. | 코드 매핑 통과, 원격 읽기 통과. 2026-07-09 재확인 시 REST 읽기가 `200 OK`와 `null`을 반환했다. |
| 9 | 커뮤니티 작성 | 로그인 사용자가 `Post/{postId}`에 `authorUid`를 포함해 게시글을 저장한다. | 코드 매핑 통과. 실제 원격 쓰기는 미검증이다. |
| 10 | 커뮤니티 삭제 | 앱에서는 작성자만 삭제 메뉴가 열리고, DB Rules도 기존 `authorUid`와 로그인 UID가 같을 때만 삭제를 허용한다. | 코드/규칙 매핑 통과. 실제 원격 삭제는 미검증이다. |

## 현재 원격 Firebase 확인 결과

- `https://dodam-bb28c-default-rtdb.firebaseio.com/UserIds/__codex_check__.json` 읽기 요청 결과: `200 OK`, 본문 `null`
- `https://dodam-bb28c-default-rtdb.firebaseio.com/Post.json` 읽기 요청 결과: `200 OK`, 본문 `null`
- 따라서 공개 읽기 Rules는 현재 원격 DB에 적용된 상태로 확인된다.
- 회원가입 저장 실패 원인 확인: Firebase Auth REST 임시 회원가입 요청이 `CONFIGURATION_NOT_FOUND`를 반환했다. 현재는 `UserIds`/`Post` 공개 읽기보다 Firebase Authentication 설정이 먼저 해결되어야 한다.

## 실제 Firebase Console에서 필요한 확인 순서

1. Firebase Authentication을 시작하고 Email/Password 로그인이 활성화되어 있는지 확인한다.
2. Realtime Database가 `dodam-bb28c-default-rtdb` 인스턴스로 생성되어 있는지 확인한다.
3. Firebase Console의 Realtime Database Rules에 루트 `database.rules.json` 내용을 적용한다.
4. 앱에서 새 아이디로 중복 확인을 실행해 "사용 가능한 아이디입니다."가 표시되는지 확인한다.
5. 회원가입 후 Realtime Database에 `UserIds/{userId}`와 `Users/{uid}`가 생성되는지 확인한다.
6. 로그인 후 프로필 저장, 수조 추가/삭제, 커뮤니티 작성/삭제를 순서대로 실행한다.

## 보수적 결론

로컬 코드 기준 DB 경로 매핑과 공개 읽기는 정리되었지만, Firebase Authentication이 `CONFIGURATION_NOT_FOUND` 상태라 실제 인증 계정으로 `UserIds`와 `Users` 쓰기까지 확인할 수 없다. 회원가입 저장 실패는 우선 Firebase Authentication 시작 및 Email/Password 제공자 활성화가 필요하다.
