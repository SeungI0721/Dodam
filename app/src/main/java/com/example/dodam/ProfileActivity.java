// Firebase 사용자 프로필의 닉네임과 이미지 정보를 수정하는 Activity 파일이다.
package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private EditText nickname;
    private EditText photoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 프로필 화면 XML과 Activity를 연결한다.
        setContentView(R.layout.activity_profile);

        nickname = findViewById(R.id.et_profile_nickname);
        photoName = findViewById(R.id.et_profile_photo);
        Button save = findViewById(R.id.btn_save_profile);
        Button back = findViewById(R.id.btn_back);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            nickname.setText(user.getDisplayName());
        }
        photoName.setText("p_rofile1");

        save.setOnClickListener(v -> saveProfile());
        back.setOnClickListener(v -> finish());
    }

    private void saveProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        String displayName = nickname.getText().toString().trim();
        String photo = photoName.getText().toString().trim();
        if (displayName.isEmpty()) {
            Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
        user.updateProfile(request).addOnSuccessListener(unused -> {
            Map<String, Object> profile = new HashMap<>();
            profile.put("uid", user.getUid());
            profile.put("email", user.getEmail());
            profile.put("nickname", displayName);
            profile.put("photoName", photo.isEmpty() ? "p_rofile1" : photo);
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("profile")
                    .setValue(profile);
            Toast.makeText(this, "프로필이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(error ->
                Toast.makeText(this, "저장 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
