package com.example.dodam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.BreakIterator;

public class MainScreen extends AppCompatActivity {

    private Handler mHandler;
    Socket socket;
    private String ip = "192.168.137.22"; // 서버의 IP 주소
    private int port = 3000; // PORT번호를 꼭 맞추어 주어야한다.

    private Button btu_community, btu_setting, btu_chatbot, btu_heter, btu_light;
    private TextView tv_name, tv_point;
    private ImageView member_photo, Light_bt, tem_im, heter_bt, water_im;

    NotificationCompat.Builder builder;
    private static String CHANNEL_ID = "channel1";

    boolean lig = true;
    boolean het = true;
    int Heter = 0;
    int LED = 0;

    private int Temperature;
    private int Water;
    private int Depth;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mHandler = new Handler();

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
                if (lig == true) {
                    Light_bt.setImageResource(R.drawable.bt_light_off);
                    lig = false;
                    LED = 1;
                } else {
                    Light_bt.setImageResource(R.drawable.bt_light_on);
                    lig = true;
                    LED = 0;
                }
            }
        });

        //히터 on&off
        btu_heter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (het == true) {
                    heter_bt.setImageResource(R.drawable.bt_heater_off);
                    het = false;
                    Heter = 1;
                } else {
                    heter_bt.setImageResource(R.drawable.bt_heater_on);
                    het = true;
                    Heter = 0;
                }
            }
        });

        //페이지 이동-------------------------------------------------------------------
        //커뮤니티 버튼
        btu_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, community_f.class);
                startActivity(intent);
            }
        });

        //챗봇 버튼
        btu_chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, Dodam_Cat.class);
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

        //셋팅으로 부터 정보 받아오기
        Intent setIntent = getIntent();
        boolean wl_m = setIntent.getExtras().getBoolean("wl_m");
        boolean wl_t = setIntent.getExtras().getBoolean("wl_t");
        boolean wq_m = setIntent.getExtras().getBoolean("wq_m");
        boolean wq_t = setIntent.getExtras().getBoolean("wq_t");
        boolean he = setIntent.getExtras().getBoolean("he");

        //임시 변수

        try {

            //수온 조절
            if (Temperature < 20) {
                tem_im.setImageResource(R.drawable.m_tem1);
            }
            if (Temperature > 20 || Temperature < 30) {
                tem_im.setImageResource(R.drawable.m_tem2);
            } else {
                tem_im.setImageResource(R.drawable.m_tem3);
            }


            //수질
            if (Water < 20) {
                water_im.setImageResource(R.drawable.m_water1);
            }
            if (Water > 20 || Water < 30) {
                water_im.setImageResource(R.drawable.m_water2);
            } else {
                water_im.setImageResource(R.drawable.m_water3);
            }

            //수위
            if (Depth < 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
                Dialog dialog = builder.setMessage("수위가 낮습니다!").setNegativeButton("물을 보충해 주세요!", null).create();
                dialog.show();
                return;
            }

            if (!wl_m) {
                if (Temperature <= 20) {
                    builder = null;
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.p_rofile1)
                            .setContentTitle("온도가 낮습니다!")
                            .setContentText("히터를 작동해 온도를 올려주세요!")
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                    notificationManager.notify(1, builder.build());
                }
            }

            if (!wl_t) {
                if (Temperature <= 20) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
                    Dialog dialog = builder.setMessage("온도가 낮습니다!").setNegativeButton("히터를 켜주세요!", null).create();
                    dialog.show();
                    return;
                }
            }

            if (!wq_m) {
                if (Water <= 20) {
                    builder = null;
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.p_rofile1)
                            .setContentTitle("수질이 낮습니다!")
                            .setContentText("환수해주세요!")
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                    notificationManager.notify(1, builder.build());
                }
            }

            if (!wq_t) {
                if (Water <= 20) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
                    Dialog dialog = builder.setMessage("수질이 낮습니다!").setNegativeButton("환수해주세요!", null).create();
                    dialog.show();
                    return;
                }
            }

            if (!he) {
                if (Temperature <= 20) {
                    heter_bt.setImageResource(R.drawable.bt_heater_on);
                    het = true;
                    Heter = 1;
                }
                if (Temperature >= 30) {
                    heter_bt.setImageResource(R.drawable.bt_heater_off);
                    het = false;
                    Heter = 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        ConnectThread th = new ConnectThread();  // 접속하면 바로 연결
        th.start();
    }

    @Override
    protected void onStop() {  // 소켓 서버 종료 관련
        super.onStop();
        try {
            socket.close();//종료시 소켓도 닫아주어야한다.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ConnectThread extends Thread {  //소켓통신을 위한 스레드
        public void run() {
            try {
                while (true) {  // 계속 반복
                    //소켓 생성
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    //소켓에서 넘어오는 stream 형태의 문자를 얻은 후 읽어 들여서  bufferstream 형태로 in 에 저장.
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //in에 저장된 데이터를 String 형태로 변환 후 읽어들어서 String에 저장
                    String read = in.readLine();
                    int idx = 0;
                    int idxx = 0;
                    idx = read.indexOf("*");  // *를 기준으로 인덱스 찾음
                    String Temperature = read.substring(0, idx);  // 0번째부터 *까지의 문자열 추출
                    String tem = read.substring(idx + 1);  // * 다음부터 끝까지 추출
                    idxx = tem.indexOf("=");  // 또 찾기
                    String Water = tem.substring(0, idxx);
                    String Depth = tem.substring(idxx + 1);
                    System.out.println("Data get - " + Temperature + " " + Water + " " + Depth);
                    //버튼이 눌리면 out 값을 내보내기 (기본 0)
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    out.println(Heter);  // out 값을 아두이노 스케치에 출력하기
                    System.out.println(Heter);
                    out.println(Heter);
                    Log.d("=============", read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}