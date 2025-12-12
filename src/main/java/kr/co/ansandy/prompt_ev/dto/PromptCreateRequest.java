package kr.co.ansandy.prompt_ev.dto;

import kr.co.ansandy.prompt_ev.entity.PromptCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromptCreateRequest {

    private String content;
    private PromptCategory category;
    private String aiModel;
}
