package kr.co.ansandy.prompt_ev.dto;

import kr.co.ansandy.prompt_ev.entity.Prompt;
import kr.co.ansandy.prompt_ev.entity.PromptCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PromptListResponse {

    private final Long id;
    private final String content;
    private final PromptCategory category;
    private final String aiModel;
    private final Integer overallSatisfaction;
    private final String comment;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PromptListResponse(Prompt prompt) {
        this.id = prompt.getId();
        this.content = truncateContent(prompt.getContent());
        this.category = prompt.getCategory();
        this.aiModel = prompt.getAiModel();
        this.overallSatisfaction = prompt.getEvaluationScore() != null
                ? prompt.getEvaluationScore().getOverallSatisfaction()
                : null;
        this.comment = truncateComment(prompt.getComment());
        this.createdAt = prompt.getCreatedAt();
        this.updatedAt = prompt.getUpdatedAt();
    }

    /**
     * 프롬프트 내용을 100자로 제한 (리스트에서는 미리보기만)
     */
    private String truncateContent(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() <= 100) {
            return content;
        }
        return content.substring(0, 100) + "...";
    }

    /**
     * 코멘트를 50자로 제한
     */
    private String truncateComment(String comment) {
        if (comment == null) {
            return null;
        }
        if (comment.length() <= 50) {
            return comment;
        }
        return comment.substring(0, 50) + "...";
    }

    /**
     * 카테고리 설명 반환
     */
    public String getCategoryDescription() {
        return category != null ? category.getDescription() : "";
    }
}
