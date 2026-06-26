// 도담이 로컬 FAQ 문서를 검색하는 Repository 파일이다.
package com.example.dodam.data.assistant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// 제목, 키워드, 질문 변형을 점수화해 가장 관련 있는 FAQ를 찾는다.
public class FaqRepository {
    private final List<FaqDocument> documents;

    // DataSource에서 FAQ 문서를 한 번 읽어 메모리에 보관한다.
    public FaqRepository(LocalFaqDataSource dataSource) {
        this.documents = dataSource.loadDocuments();
    }

    // 사용자 질문과 가장 잘 맞는 FAQ 문서를 반환한다.
    public FaqDocument search(String query) {
        String normalized = normalize(query);
        FaqDocument best = null;
        int bestScore = 0;
        for (FaqDocument document : documents) {
            int score = score(document, normalized);
            if (score > bestScore) {
                best = document;
                bestScore = score;
            }
        }
        return bestScore >= 2 ? best : null;
    }

    public List<FaqDocument> getDocuments() {
        return new ArrayList<>(documents);
    }

    // 제목, 키워드, 질문 변형 일치 정도를 단순 점수로 계산한다.
    private int score(FaqDocument document, String query) {
        int score = 0;
        if (query.contains(normalize(document.getTitle()))) {
            score += 4;
        }
        for (String keyword : document.getKeywords()) {
            if (query.contains(normalize(keyword))) {
                score += 3;
            }
        }
        for (String variant : document.getQuestionVariants()) {
            String normalizedVariant = normalize(variant);
            if (query.contains(normalizedVariant) || normalizedVariant.contains(query)) {
                score += 2;
            }
        }
        if (query.contains(normalize(document.getCategory()))) {
            score += 1;
        }
        return score;
    }

    // 검색 비교를 위해 문자열을 소문자와 공백 정리 형태로 맞춘다.
    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.KOREA);
    }
}
