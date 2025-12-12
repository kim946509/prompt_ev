package kr.co.ansandy.prompt_ev.dto;

import kr.co.ansandy.prompt_ev.entity.PromptCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromptFilterRequest {

    /** 페이지 번호 (0부터 시작) */
    private int page = 0;

    /** 페이지 당 개수 (기본: 20, 최대: 50) */
    private int size = 20;

    /** 카테고리 필터 */
    private PromptCategory category;

    /** 종합 만족도 최소 점수 (0-10) */
    private Integer minSatisfaction;

    /** 종합 만족도 최대 점수 (0-10) */
    private Integer maxSatisfaction;

    /**
     * 페이지 크기 검증 및 제한
     * 최대 50개까지만 허용
     */
    public int getValidatedSize() {
        if (size < 1) {
            return 20;
        }
        return Math.min(size, 50);
    }

    /**
     * 페이지 번호 검증
     */
    public int getValidatedPage() {
        return Math.max(page, 0);
    }
}
