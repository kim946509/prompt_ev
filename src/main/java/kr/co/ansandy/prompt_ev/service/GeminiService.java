package kr.co.ansandy.prompt_ev.service;

import kr.co.ansandy.prompt_ev.config.GeminiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Gemini API 호출 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final RestClient geminiRestClient;
    private final GeminiConfig geminiConfig;

    /**
     * 프롬프트 평가를 위한 시스템 프롬프트
     */
    private static final String SYSTEM_PROMPT = """
            당신은 AI 프롬프트 품질 평가 전문가입니다. 주어진 프롬프트를 다음 기준으로 분석해주세요:
            
            1. 명확성 (1-10점): 프롬프트가 명확하고 이해하기 쉬운가?
            2. 구체성 (1-10점): 구체적인 요구사항이 포함되어 있는가?
            3. 컨텍스트 (1-10점): 충분한 배경 정보를 제공하는가?
            4. 구조 (1-10점): 논리적으로 구조화되어 있는가?
            5. 예상 결과 명시 (1-10점): 기대하는 결과가 명시되어 있는가?
            
            각 항목에 대한 점수와 간단한 피드백, 그리고 전체 개선 제안을 한국어로 작성해주세요.
            마크다운 형식으로 깔끔하게 정리해주세요.
            """;

    /**
     * 프롬프트를 분석하고 결과를 반환합니다.
     *
     * @param promptContent 분석할 프롬프트 내용
     * @return 분석 결과 텍스트
     */
    public String analyzePrompt(String promptContent) {
        if (geminiConfig.getApiKey() == null || geminiConfig.getApiKey().isBlank()) {
            log.warn("Gemini API key is not configured. Skipping analysis.");
            return null;
        }

        try {
            String userPrompt = "다음 프롬프트를 분석해주세요:\n\n" + promptContent;

            // Gemini API 요청 본문 구성
            Map<String, Object> requestBody = Map.of(
                    "system_instruction", Map.of(
                            "parts", List.of(Map.of("text", SYSTEM_PROMPT))
                    ),
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", userPrompt)))
                    )
            );

            // API 호출
            @SuppressWarnings("unchecked")
            Map<String, Object> response = geminiRestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(geminiConfig.getGenerateContentUrl())
                            .queryParam("key", geminiConfig.getApiKey())
                            .build())
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            // 응답 파싱
            return extractTextFromResponse(response);

        } catch (Exception e) {
            log.error("Failed to analyze prompt with Gemini API", e);
            return null;
        }
    }

    /**
     * Gemini API 응답에서 텍스트를 추출합니다.
     */
    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map<String, Object> response) {
        if (response == null) {
            return null;
        }

        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return null;
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if (content == null) {
                return null;
            }

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                return null;
            }

            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            log.error("Failed to parse Gemini response", e);
            return null;
        }
    }
}
