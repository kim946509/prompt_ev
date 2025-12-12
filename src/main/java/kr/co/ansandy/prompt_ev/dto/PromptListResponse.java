package kr.co.ansandy.prompt_ev.dto;

import kr.co.ansandy.prompt_ev.entity.Prompt;
import kr.co.ansandy.prompt_ev.entity.PromptCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PromptListResponse {

    private final Long id;
    private final String content;
    private final String contentPreview;  // 100자 미리보기
    private final PromptCategory category;
    private final String aiModel;
    private final String responseText;

    // 평가 점수
    private final Integer goalAchievement;
    private final Boolean executionSuccess;
    private final Integer responseQuality;
    private final Integer efficiency;
    private final Integer reusability;
    private final Integer overallSatisfaction;

    private final String comment;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PromptListResponse(Prompt prompt) {
        this.id = prompt.getId();
        this.content = prompt.getContent();  // 전체 내용
        this.contentPreview = truncateContent(prompt.getContent());  // 미리보기
        this.category = prompt.getCategory();
        this.aiModel = prompt.getAiModel();
        this.responseText = prompt.getResponseText();

        // 평가 점수 (null-safe)
        if (prompt.getEvaluationScore() != null) {
            this.goalAchievement = prompt.getEvaluationScore().getGoalAchievement();
            this.executionSuccess = prompt.getEvaluationScore().getExecutionSuccess();
            this.responseQuality = prompt.getEvaluationScore().getResponseQuality();
            this.efficiency = prompt.getEvaluationScore().getEfficiency();
            this.reusability = prompt.getEvaluationScore().getReusability();
            this.overallSatisfaction = prompt.getEvaluationScore().getOverallSatisfaction();
        } else {
            this.goalAchievement = null;
            this.executionSuccess = null;
            this.responseQuality = null;
            this.efficiency = null;
            this.reusability = null;
            this.overallSatisfaction = null;
        }

        this.comment = prompt.getComment();
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
     * 카테고리 설명 반환
     */
    public String getCategoryDescription() {
        return category != null ? category.getDescription() : "";
    }
}
