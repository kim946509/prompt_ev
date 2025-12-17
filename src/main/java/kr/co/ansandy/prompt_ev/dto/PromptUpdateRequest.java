package kr.co.ansandy.prompt_ev.dto;

import kr.co.ansandy.prompt_ev.entity.EvaluationScore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromptUpdateRequest {

    /** AI 응답 결과 */
    private String responseText;

    /** 자유 코멘트 */
    private String comment;

    /** 목표 달성도 (1-10점) */
    private Integer goalAchievement;

    /** 실행 성공 여부 */
    private Boolean executionSuccess;

    /** 응답 품질 (1-10점) */
    private Integer responseQuality;

    /** 효율성 (1-10점) */
    private Integer efficiency;

    /** 재사용 가능성 (1-10점) */
    private Integer reusability;

    /** 종합 만족도 점수 (1-10점) */
    private Integer overallSatisfaction;

    /**
     * 평가 점수 필드들을 EvaluationScore 객체로 변환합니다.
     */
    public EvaluationScore toEvaluationScore() {
        return EvaluationScore.builder()
                .goalAchievement(goalAchievement)
                .executionSuccess(executionSuccess)
                .responseQuality(responseQuality)
                .efficiency(efficiency)
                .reusability(reusability)
                .overallSatisfaction(overallSatisfaction)
                .build();
    }
}
