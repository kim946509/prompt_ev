package kr.co.ansandy.prompt_ev.dto;

import kr.co.ansandy.prompt_ev.entity.Prompt;
import kr.co.ansandy.prompt_ev.entity.PromptCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PromptResponse {

    private final Long id;
    private final String content;
    private final PromptCategory category;
    private final String aiModel;
    private final LocalDateTime createdAt;

    public PromptResponse(Prompt prompt) {
        this.id = prompt.getId();
        this.content = prompt.getContent();
        this.category = prompt.getCategory();
        this.aiModel = prompt.getAiModel();
        this.createdAt = prompt.getCreatedAt();
    }
}
