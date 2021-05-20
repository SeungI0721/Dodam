package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.Socket;

public class MainScreen extends AppCompatActivity {

   /* private Handler mHandler;
    Socket socket;
    private String ip = "100.100.106.154"; // 서버의 IP 주소
    private int port = 3000; // PORT번호를 꼭 맞추어 주어야한다. */

    private  Button btu_community, btu_setting, btu_chatbot, btu_heter, btu_light;
    private TextView tv_name, tv_point;
    private ImageView member_photo, Light_bt, tem_im, heter_bt, water_im;

    boolean lig = true;
    boolean het = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //버튼 값 지정--------------------------------------------------------------
        btu_community = findViewById(R.id.btu_community);
        btu_chatbot = findViewById(R.id.btu_chatbot);
        btu_setting = findViewById(R.id.btu_setting);
        btu_heter = findViewById(R.id.btu_heter);
        btu_light = findViewById(R.id.btu_light);

        tv_name = findViewById(R.id.tv_name);
        tv_point = findViewById(R.id.tv_point);

        member_photo = findViewById(R.id.member_photo);
        Light_bt = findViewById(R.id.Light_bt);
        tem_im = findViewById(R.id.tem_im);
        heter_bt = findViewById(R.id.heter_bt);
        water_im = findViewById(R.id.water_im);

        //Main으로 부터 정보 받아오기
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String point = intent.getStringExtra("point");
        String userPhoto = intent.getStringExtra("userPhoto");

        tv_name.setText(userName);
        tv_point.setText(point);

        //사진을 불러오는 명령어
        //사진칸 id.setImageResource(R.drawable.사진);
        member_photo.setImageResource(R.drawable.p_rofile1);

        //조명 on&off
        btu_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lig == true){
                    Light_bt.setImageResource(R.drawable.m_light_off);
                    lig = false;
                } else {
                    Light_bt.setImageResource(R.drawable.m_light_on);
                    lig = true;
                }
            }
        });

        //히터 on&off
        btu_heter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (het == true) {
                    heter_bt.setImageResource(R.drawable.m_heater_off);
                    het = false;
                } else {
                    heter_bt.setImageResource(R.drawable.m_heater_on);
                    het = true;
                }
            }
        });

        /*class ConnectThread extends Thread{//소켓통신을 위한 스레드
            public void run(){
                try{
                    while(true) {  // 계속 반복
                        //소켓 생성
                        InetAddress serverAddr = InetAddress.getByName(ip);
                        socket = new Socket(serverAddr, port);

                        //소켓에서 넘어오는 stream 형태의 문자를 얻은 후 읽어 들여서  bufferstream 형태로 in 에 저장.
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        //in에 저장된 데이터를 String 형태로 변환 후 읽어들어서 String에 저장
                        String read = in.readLine();
                        int idx = read.indexOf("*");  // *를 기준으로 인덱스 찾음
                        String Temperature = read.substring(0, idx);  // 0번째부터 *까지의 문자열 추출
                        String Water = read.substring(idx+1);  // * 다음부터 끝까지 추출
                        System.out.println("Data get");
                        //client에 다시 전송
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(LED);
                        //화면 출력
                        mHandler.post(new msgTempUpdate(Temperature));
                        mHandler.post(new msgWaterUpdate(Water));
                        Log.d("=============", read);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }   */



        //페이지 이동-------------------------------------------------------------------
        //커뮤니티 버튼
        btu_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, Community.class);
                startActivity(intent);
            }
        });

        //챗봇 버튼
        btu_chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, Chatbot.class);
                startActivity(intent);
            }
        });

        //셋팅 버튼
        btu_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, Setting.class);
                startActivity(intent);
            }
        });
    }
}