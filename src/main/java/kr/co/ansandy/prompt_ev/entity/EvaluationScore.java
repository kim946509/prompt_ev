package kr.co.ansandy.prompt_ev.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프롬프트 평가 점수를 담는 Embeddable 클래스
 * 모든 점수는 0-10점 척도
 */
@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationScore {

    /** 목표 달성도 (0-10점) */
    @Column(name = "goal_achievement")
    private Integer goalAchievement;

    /** 실행 성공 여부 */
    @Column(name = "execution_success")
    private Boolean executionSuccess;

    /** 응답 품질 (0-10점) */
    @Column(name = "response_quality")
    private Integer responseQuality;

    /** 효율성 (0-10점) */
    @Column(name = "efficiency")
    private Integer efficiency;

    /** 재사용 가능성 (0-10점) */
    @Column(name = "reusability")
    private Integer reusability;

    /** 종합 만족도 점수 (0-10점) */
    @Column(name = "overall_satisfaction")
    private Integer overallSatisfaction;
}
