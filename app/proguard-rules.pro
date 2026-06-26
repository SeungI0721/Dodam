# 도담도담 앱 릴리스 빌드에서 사용할 ProGuard 규칙 파일이다.
# 현재 릴리스 빌드는 난독화를 사용하지 않지만, 추후 필요한 보존 규칙은 이 파일에 추가한다.

# WebView에서 JavaScript 인터페이스를 공개할 경우 필요한 클래스 멤버 보존 규칙을 이 위치에 추가한다.
# -keepclassmembers class 패키지명.클래스명 {
#     public *;
# }

# 오류 추적을 위해 라인 번호 정보를 보존해야 할 경우 아래 규칙을 사용할 수 있다.
# -keepattributes SourceFile,LineNumberTable

# 원본 소스 파일 이름을 숨겨야 할 경우 아래 규칙을 사용할 수 있다.
# -renamesourcefileattribute SourceFile
