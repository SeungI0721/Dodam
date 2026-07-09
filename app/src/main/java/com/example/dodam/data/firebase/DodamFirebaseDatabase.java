// 도담 Realtime Database 인스턴스를 한 곳에서 생성하는 도우미 파일이다.
package com.example.dodam.data.firebase;

import com.google.firebase.database.FirebaseDatabase;

public final class DodamFirebaseDatabase {
    private static final String DATABASE_URL = "https://dodam-bb28c-default-rtdb.firebaseio.com";

    private DodamFirebaseDatabase() {
    }

    public static FirebaseDatabase getInstance() {
        return FirebaseDatabase.getInstance(DATABASE_URL);
    }
}
