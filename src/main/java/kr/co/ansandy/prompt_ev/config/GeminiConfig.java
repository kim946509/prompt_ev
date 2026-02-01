package kr.co.ansandy.prompt_ev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Gemini API 설정 클래스
 * API 키와 모델명을 설정 파일에서 관리합니다.
 */
@Configuration
public class GeminiConfig {

    /**
     * Gemini API 키 (env.properties에서 주입)
     */
    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    /**
     * 사용할 Gemini 모델명 (기본값: gemini-2.0-flash)
     * env.properties에서 GEMINI_MODEL로 변경 가능
     */
    @Value("${GEMINI_MODEL:gemini-2.0-flash}")
    private String model;

    /**
     * Gemini API 기본 URL
     */
    private static final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta";

    @Bean
    public RestClient geminiRestClient() {
        return RestClient.builder()
                .baseUrl(GEMINI_API_BASE_URL)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    public String getGenerateContentUrl() {
        return "/models/" + model + ":generateContent";
    }
}
