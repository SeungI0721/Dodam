// raw 리소스에 저장된 도담이 로컬 FAQ JSON을 읽는 DataSource 파일이다.
package com.example.dodam.data.assistant;

import android.content.Context;

import com.example.dodam.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Android 리소스의 assistant_faq.json을 파싱해 FaqDocument 목록으로 변환한다.
public class LocalFaqDataSource {
    private final Context context;

    // 리소스 접근을 위해 Application Context를 보관한다.
    public LocalFaqDataSource(Context context) {
        this.context = context.getApplicationContext();
    }

    // FAQ JSON 전체를 읽고 각 항목을 FaqDocument로 변환한다.
    public List<FaqDocument> loadDocuments() {
        List<FaqDocument> documents = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(readRawFaq());
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                documents.add(new FaqDocument(
                        item.optString("id"),
                        item.optString("category"),
                        item.optString("title"),
                        toStringList(item.optJSONArray("keywords")),
                        toStringList(item.optJSONArray("questionVariants")),
                        item.optString("answer"),
                        item.optString("updatedAt"),
                        item.optBoolean("verified", false)
                ));
            }
        } catch (Exception ignored) {
            documents.clear();
        }
        return documents;
    }

    // raw/assistant_faq.json 파일 내용을 UTF-8 문자열로 읽는다.
    private String readRawFaq() throws Exception {
        InputStream inputStream = context.getResources().openRawResource(R.raw.assistant_faq);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    // JSON 배열을 Java 문자열 목록으로 변환한다.
    private List<String> toStringList(JSONArray array) {
        List<String> result = new ArrayList<>();
        if (array == null) {
            return result;
        }
        for (int i = 0; i < array.length(); i++) {
            result.add(array.optString(i));
        }
        return result;
    }
}
