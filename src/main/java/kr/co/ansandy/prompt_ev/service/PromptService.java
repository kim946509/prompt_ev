package kr.co.ansandy.prompt_ev.service;

import jakarta.persistence.criteria.Predicate;
import kr.co.ansandy.prompt_ev.dto.PromptCreateRequest;
import kr.co.ansandy.prompt_ev.dto.PromptFilterRequest;
import kr.co.ansandy.prompt_ev.dto.PromptListResponse;
import kr.co.ansandy.prompt_ev.dto.PromptResponse;
import kr.co.ansandy.prompt_ev.dto.PromptUpdateRequest;
import kr.co.ansandy.prompt_ev.entity.Prompt;
import kr.co.ansandy.prompt_ev.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptService {

    private final PromptRepository promptRepository;

    @Transactional
    public PromptResponse savePrompt(PromptCreateRequest request) {
        Prompt prompt = Prompt.from(request);
        promptRepository.save(prompt);
        return new PromptResponse(prompt);
    }

    /**
     * 프롬프트 업데이트 (AI 응답, 평가 점수, 코멘트)
     *
     * @param id 프롬프트 ID
     * @param request 업데이트할 데이터
     * @return 업데이트된 프롬프트 DTO
     */
    @Transactional
    public PromptResponse updatePrompt(Long id, PromptUpdateRequest request) {
        Prompt prompt = promptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프롬프트를 찾을 수 없습니다. ID: " + id));

        prompt.update(
                request.getResponseText(),
                request.getComment(),
                request.toEvaluationScore()
        );

        return new PromptResponse(prompt);
    }

    /**
     * 프롬프트 목록 조회 (페이지네이션 + 필터링)
     *
     * @param filterRequest 필터 조건 및 페이지 정보
     * @return 페이지네이션된 프롬프트 DTO 목록
     */
    public Page<PromptListResponse> getPrompts(PromptFilterRequest filterRequest) {
        // Pageable 생성 (최신순 정렬)
        Pageable pageable = PageRequest.of(
                filterRequest.getValidatedPage(),
                filterRequest.getValidatedSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Specification을 사용한 동적 쿼리 생성
        Specification<Prompt> spec = createSpecification(filterRequest);

        // Entity 조회 후 DTO로 변환
        Page<Prompt> promptPage = promptRepository.findAll(spec, pageable);
        return promptPage.map(PromptListResponse::new);
    }

    /**
     * 프롬프트 삭제
     *
     * @param id 삭제할 프롬프트 ID
     */
    @Transactional
    public void deletePrompt(Long id) {
        Prompt prompt = promptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프롬프트를 찾을 수 없습니다. ID: " + id));

        promptRepository.delete(prompt);
    }

    /**
     * 필터 조건에 따른 Specification 생성
     */
    private Specification<Prompt> createSpecification(PromptFilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 카테고리 필터
            if (filterRequest.getCategory() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), filterRequest.getCategory()));
            }

            // 종합 만족도 최소 점수
            if (filterRequest.getMinSatisfaction() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("evaluationScore").get("overallSatisfaction"),
                        filterRequest.getMinSatisfaction()
                ));
            }

            // 종합 만족도 최대 점수
            if (filterRequest.getMaxSatisfaction() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("evaluationScore").get("overallSatisfaction"),
                        filterRequest.getMaxSatisfaction()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
