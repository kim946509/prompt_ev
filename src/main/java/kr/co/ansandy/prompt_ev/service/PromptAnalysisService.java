package kr.co.ansandy.prompt_ev.service;

import kr.co.ansandy.prompt_ev.entity.Prompt;
import kr.co.ansandy.prompt_ev.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프롬프트 분석 서비스
 * 이벤트 리스너에서 호출되어 실제 분석을 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromptAnalysisService {

    private final GeminiService geminiService;
    private final PromptRepository promptRepository;

    /**
     * 프롬프트를 분석하고 결과를 저장합니다.
     * 이 메서드는 PromptEventListener에서 트랜잭션 커밋 후 비동기로 호출됩니다.
     *
     * @param promptId 분석할 프롬프트 ID
     * @param content  분석할 프롬프트 내용
     */
    @Transactional
    public void analyzePrompt(Long promptId, String content) {
        log.info("Starting analysis for prompt ID: {}", promptId);

        try {
            // Gemini API로 분석 수행
            String analysisResult = geminiService.analyzePrompt(content);

            if (analysisResult != null) {
                // 분석 결과 저장
                Prompt prompt = promptRepository.findById(promptId).orElse(null);
                if (prompt != null) {
                    prompt.updateAiAnalysis(analysisResult);
                    promptRepository.save(prompt);
                    log.info("Analysis completed and saved for prompt ID: {}", promptId);
                } else {
                    log.warn("Prompt not found for ID: {}. This should not happen after commit.", promptId);
                }
            } else {
                log.warn("Analysis returned null for prompt ID: {}", promptId);
            }

        } catch (Exception e) {
            log.error("Failed to analyze prompt ID: {}", promptId, e);
        }
    }
}
