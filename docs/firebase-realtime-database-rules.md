# Firebase Realtime Database Rules

회원가입의 아이디 중복 확인은 로그인 전 상태에서 `UserIds/{userId}`를 읽어야 한다. 그래서 `UserIds`는 읽기만 공개하고, 쓰기는 로그인된 사용자가 자기 UID로만 새 아이디를 등록하도록 제한한다.

Firebase Console의 Realtime Database Rules에 다음 형태로 적용한다.

```json
{
  "rules": {
    "UserIds": {
      "$userId": {
        ".read": true,
        ".write": "auth != null && newData.val() == auth.uid && (!data.exists() || data.val() == auth.uid)"
      }
    },
    "Users": {
      "$uid": {
        ".read": "auth != null && auth.uid == $uid",
        ".write": "auth != null && auth.uid == $uid"
      }
    },
    "Post": {
      ".read": true,
      "$postId": {
        ".write": "auth != null && ((!data.exists() && newData.child('authorUid').val() == auth.uid) || (data.exists() && data.child('authorUid').val() == auth.uid))"
      }
    }
  }
}
```

주의:

- 위 규칙은 포트폴리오 검증용 최소 예시다.
- 실제 서비스에서는 닉네임, 게시글, 수조 데이터별 검증 규칙을 더 세분화해야 한다.
- `UserIds`는 아이디 존재 여부 확인을 위해 읽기만 공개한다. 쓰기는 로그인된 사용자가 자기 UID 값을 저장할 때만 허용하며, 이미 같은 UID로 저장된 값은 재저장할 수 있다. 이메일, 닉네임 같은 개인정보는 이 경로에 저장하지 않는다.
- `Post` 쓰기는 로그인 사용자에게만 허용하되, 생성 시 `authorUid`가 로그인 UID와 같아야 하며 기존 게시글 변경/삭제는 기존 `authorUid`와 로그인 UID가 같을 때만 허용한다.
