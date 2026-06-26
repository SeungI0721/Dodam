// 로컬 FAQ JSON의 한 항목을 표현하는 데이터 모델 파일이다.
package com.example.dodam.data.assistant;

import java.util.ArrayList;
import java.util.List;

// FAQ ID, 분류, 키워드, 질문 변형, 검증된 답변을 보관한다.
public class FaqDocument {
    private final String id;
    private final String category;
    private final String title;
    private final List<String> keywords;
    private final List<String> questionVariants;
    private final String answer;
    private final String updatedAt;
    private final boolean verified;

    // JSON에서 읽은 FAQ 항목을 앱 내부 모델로 변환할 때 사용한다.
    public FaqDocument(String id, String category, String title, List<String> keywords,
                       List<String> questionVariants, String answer, String updatedAt, boolean verified) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.keywords = keywords == null ? new ArrayList<>() : new ArrayList<>(keywords);
        this.questionVariants = questionVariants == null ? new ArrayList<>() : new ArrayList<>(questionVariants);
        this.answer = answer;
        this.updatedAt = updatedAt;
        this.verified = verified;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getKeywords() {
        return new ArrayList<>(keywords);
    }

    public List<String> getQuestionVariants() {
        return new ArrayList<>(questionVariants);
    }

    public String getAnswer() {
        return answer;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public boolean isVerified() {
        return verified;
    }
}
