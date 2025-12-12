package kr.co.ansandy.prompt_ev.entity;

import jakarta.persistence.*;
import kr.co.ansandy.prompt_ev.dto.PromptCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prompts")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Prompt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 프롬프트 원문 */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 프롬프트 카테고리 */
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PromptCategory category;

    /** 사용한 AI 모델 (예: GPT-4, Claude-3.5) */
    @Column(name = "ai_model", length = 100)
    private String aiModel;

    /** AI 응답 결과 */
    @Column(name = "response_text", columnDefinition = "TEXT")
    private String responseText;

    /** 평가 점수 */
    @Embedded
    private EvaluationScore evaluationScore;

    /** 자유 코멘트 */
    @Column(columnDefinition = "TEXT")
    private String comment;

    public static Prompt from(PromptCreateRequest request){
        return Prompt.builder()
                .content(request.getContent())
                .category(request.getCategory())
                .aiModel(request.getAiModel())
                .build();
    }

}
