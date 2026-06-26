// Q&A 게시판 글 목록을 Firebase Realtime Database에서 조회하는 Activity 파일이다.
package com.example.dodam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class community_q extends AppCompatActivity {

    private Button btu_cback, btu_topo, btu_go, btu_f;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Post> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_q);

        // 게시판 이동 버튼을 연결한다.
        btu_cback = findViewById(R.id.btu_cback);
        btu_f = findViewById(R.id.btu_f);
        btu_topo = findViewById(R.id.btu_topo);
        btu_go = findViewById(R.id.btu_go);

        recycler = findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("Post"); // Firebase 게시글 경로를 연결한다.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Firebase 게시글 목록을 받아온다.
                arrayList.clear(); // 기존 게시글 목록을 초기화한다.
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    Post post = snapshot.getValue(Post.class); // Firebase 데이터를 Post 모델로 변환한다.
                    if (post != null) {
                        post.setPostId(snapshot.getKey());
                        if ("QNA".equals(post.getCategory())) {
                            arrayList.add(post); // Q&A 게시글만 화면 목록에 추가한다.
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // 게시글 목록을 새로고침한다.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Firebase 조회 오류가 발생하면 로그로 남긴다.
                Log.e("community_q", String.valueOf(databaseError.toException()));
            }
        });

        adapter = new CustomAdapter(arrayList, this);
        recycler.setAdapter(adapter);

        // 뒤로가기 버튼을 누르면 메인 대시보드로 돌아간다.
        btu_cback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, MainScreen.class);
                startActivity(intent);
                finish();
            }
        });
        // 공지 게시판 버튼을 누르면 공지 화면으로 이동한다.
        btu_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, Community.class);
                startActivity(intent);
                finish();
            }
        });

        // 자유게시판 버튼을 누르면 자유게시판 화면으로 이동한다.
        btu_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, community_f.class);
                startActivity(intent);
                finish();
            }
        });

        // 글쓰기 버튼을 누르면 Q&A 카테고리로 글쓰기 화면을 연다.
        btu_topo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, Commu_po.class);
                intent.putExtra("category", "QNA");
                startActivity(intent);
                finish();
            }
        });

    }
}
