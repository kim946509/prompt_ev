package kr.co.ansandy.prompt_ev.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prompts")
@Getter
@Setter
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

    /** 목표 달성도 (1-5점) */
    @Column(name = "goal_achievement")
    private Integer goalAchievement;

    /** 실행 성공 여부 */
    @Column(name = "execution_success")
    private Boolean executionSuccess;

    /** 응답 품질 (1-5점) */
    @Column(name = "response_quality")
    private Integer responseQuality;

    /** 효율성 (1-5점) */
    @Column(name = "efficiency")
    private Integer efficiency;

    /** 재사용 가능성 (1-5점) */
    @Column(name = "reusability")
    private Integer reusability;

    /** 자유 코멘트 */
    @Column(columnDefinition = "TEXT")
    private String comment;

}
