package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class community_f extends AppCompatActivity {

    private Button btu_cback, btu_topo, btu_go, btu_q;
    private RecyclerView re;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Post> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        //이동 버튼
        btu_cback = findViewById(R.id.btu_cback);
        btu_topo = findViewById(R.id.btu_topo);
        btu_go = findViewById(R.id.btu_go);
        btu_q = findViewById(R.id.btu_q);

        re = findViewById(R.id.re);
        re.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        re.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        adapter = new CustomAdapter(arrayList, this);
        re.setAdapter(adapter);

        //돌아가기
        btu_cback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_f.this, MainScreen.class);
                startActivity(intent);
                finish();
            }
        });
        //공지사항
        btu_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_f.this, Community.class);
                startActivity(intent);
                finish();
            }
        });

        //Q&A
        btu_q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_f.this, community_q.class);
                startActivity(intent);
                finish();
            }
        });

        //글쓰기
        btu_topo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_f.this, Commu_po.class);
                startActivity(intent);
                finish();
            }
        });


           /*String TAG_JSON="webnautes";
            String TAG_ID = "id";
            String TAG_NAME = "name";
            String TAG_COUNTRY ="country";


            try {
                JSONObject mJsonString;
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for(int i=0;i<jsonArray.length();i++){

                    JSONObject item = jsonArray.getJSONObject(i);

                    String id = item.getString(TAG_ID);
                    String name = item.getString(TAG_NAME);
                    String address = item.getString(TAG_COUNTRY);

                    PersonalData personalData = new PersonalData();

                    personalData.setMember_id(id);
                    personalData.setMember_name(name);
                    personalData.setMember_address(address);

                    mArrayList.add(personalData);
                    mAdapter.notifyDataSetChanged();
                }



            } catch (JSONException e) {

            }*/
    }
}