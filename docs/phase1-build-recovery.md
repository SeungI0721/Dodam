<!-- 도담도담 프로젝트의 빌드 복구 결과를 정리한 문서이다. -->
# Phase 1 빌드 복구 보고서

작성일: 2026-06-26

## 목적

오래된 Android 프로젝트를 현재 개발 환경에서 다시 빌드할 수 있도록 Gradle, Android Gradle Plugin, SDK, Firebase 의존성을 정리했다.

## 변경 내용

| 항목 | 적용 값 |
| --- | --- |
| Gradle Wrapper | 8.7 |
| Android Gradle Plugin | 8.6.1 |
| Google Services Gradle Plugin | 4.5.0 |
| Firebase BoM | 34.15.0 |
| compileSdk | 35 |
| targetSdk | 35 |
| minSdk | 24 |

## Gradle 구성

루트 `build.gradle`은 plugin DSL 기반으로 정리했다.

```gradle
plugins {
    id 'com.android.application' version '8.6.1' apply false
    id 'com.google.gms.google-services' version '4.5.0' apply false
}
```

앱 모듈 `app/build.gradle`에는 Google Services 플러그인과 Firebase SDK를 추가했다.

```gradle
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'
}

dependencies {
    implementation platform('com.google.firebase:firebase-bom:34.15.0')
    implementation 'com.google.firebase:firebase-analytics'
    implementation 'com.google.firebase:firebase-auth'
    implementation 'com.google.firebase:firebase-firestore'
    implementation 'com.google.firebase:firebase-database'
}
```

## 정리한 의존성

- 미사용 Volley 의존성 제거
- Firebase 의존성은 BoM 기준으로 통일
- AndroidX, Material, Glide 의존성은 현재 빌드 가능한 범위로 유지

## 검증 명령

```powershell
.\gradlew.bat clean
.\gradlew.bat :app:assembleDebug
.\gradlew.bat test
.\gradlew.bat lint
```

## 결과

| 명령 | 결과 |
| --- | --- |
| `.\gradlew.bat clean` | PASS |
| `.\gradlew.bat :app:assembleDebug` | PASS |
| `.\gradlew.bat test` | PASS |
| `.\gradlew.bat lint` | PASS |

Firebase 최신 의존성 일부는 lint 실행 시 Kotlin metadata 호환성 경고를 출력하지만, Gradle 태스크 결과는 `BUILD SUCCESSFUL`이다.

## 남은 확인

- 실제 Android 기기 또는 에뮬레이터 설치 실행
- Firebase 콘솔의 Authentication 활성화 상태
- Firebase Realtime Database Security Rules
