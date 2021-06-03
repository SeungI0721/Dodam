package com.example.dodam;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PostingRequest extends StringRequest {
    //서버 URL설정 (php파일 연동)
    final static private String URL = "http://rays0507.dothome.co.kr/Notice.php";
    private Map<String, String> map;

    public PostingRequest(String noticeTitle, String noticeContent, String userName, String time, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("noticeTitle", noticeTitle);
        map.put("noticeContent", noticeContent);
        map.put("userName", userName);
        map.put("time", time);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
