package kr.co.ansandy.prompt_ev.event;

/**
 * 프롬프트 저장 완료 이벤트
 * 트랜잭션 커밋 후 비동기 분석을 트리거하기 위한 이벤트 객체
 */
public record PromptSavedEvent(Long promptId, String content) {
}
