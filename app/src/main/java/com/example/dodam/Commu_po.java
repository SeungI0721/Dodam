// Firebase Realtime Database에 커뮤니티 게시글을 작성하는 Activity 파일이다.
package com.example.dodam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dodam.data.firebase.DodamFirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Commu_po extends AppCompatActivity {

    private FirebaseDatabase database = DodamFirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private Button btu_back, btu_pos;
    private EditText tv_main, tv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu_po);

        btu_back = findViewById(R.id.btu_back);
        btu_pos = findViewById(R.id.btu_pos);

        tv_main = findViewById(R.id.tv_main);
        tv_text = findViewById(R.id.tv_text);

        // 뒤로가기 버튼을 누르면 메인 대시보드로 돌아간다.
        btu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Commu_po.this, MainScreen.class);
                startActivity(intent);
                finish();
            }
        });

        // 작성 버튼을 누르면 로그인 상태와 입력값을 검증한 뒤 게시글을 저장한다.
        btu_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "로그인 후 게시글을 작성할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userName = user != null && user.getDisplayName() != null ? user.getDisplayName() : "도담 사용자";
                String authorUid = user.getUid();
                String selectedCategory = getIntent().getStringExtra("category");
                if (selectedCategory == null || selectedCategory.trim().isEmpty()) {
                    selectedCategory = "FREE";
                }
                final String category = selectedCategory;
                String title = tv_main.getText().toString().trim();
                String content = tv_text.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Commu_po.this);
                    Dialog dialog = builder.setMessage("제목과 내용을 모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                String key = databaseReference.child("Post").push().getKey();
                if (key == null) {
                    Toast.makeText(getApplicationContext(), "게시글 키 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Post post = new Post(key, authorUid, userName, category, title, content, System.currentTimeMillis());
                databaseReference.child("Post").child(key).setValue(post)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getApplicationContext(), "게시글이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Commu_po.this, getCommunityListClass(category)));
                            finish();
                        })
                        .addOnFailureListener(error ->
                                Toast.makeText(getApplicationContext(), "저장 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show());

            }
        });
    }

    private Class<?> getCommunityListClass(String category) {
        if ("NOTICE".equals(category)) {
            return Community.class;
        }
        if ("QNA".equals(category)) {
            return community_q.class;
        }
        return community_f.class;
    }

}
