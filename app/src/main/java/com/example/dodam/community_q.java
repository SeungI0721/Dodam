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

        //이동 버튼
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

        databaseReference = database.getReference("Post"); // DB 테이블 연결
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터 베이스 받음
                arrayList.clear(); //기존 배열 리스트 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    Post post = snapshot.getValue(Post.class); //만든 Post.java에 맞춰 데이터 담기
                    arrayList.add(post); //배열리스트에 넣고 리사이클러뷰로 쏘기
                }
                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //DB 에러 발생시
                Log.e("community_q", String.valueOf(databaseError.toException())); //에러문 출력
            }
        });

        adapter = new CustomAdapter(arrayList, this);
        recycler.setAdapter(adapter);

        //돌아가기
        btu_cback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, MainScreen.class);
                startActivity(intent);
                finish();
            }
        });
        //공지사항
        btu_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, Community.class);
                startActivity(intent);
                finish();
            }
        });

        //자유게시판
        btu_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, community_f.class);
                startActivity(intent);
                finish();
            }
        });

        //글쓰기
        btu_topo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, Commu_po.class);
                startActivity(intent);
                finish();
            }
        });

    }
}