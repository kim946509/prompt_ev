package kr.co.ansandy.prompt_ev.event;

import kr.co.ansandy.prompt_ev.service.PromptAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 프롬프트 이벤트 리스너
 * 트랜잭션 커밋 후에 비동기 분석을 실행합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PromptEventListener {

    private final PromptAnalysisService promptAnalysisService;

    /**
     * 프롬프트 저장 트랜잭션이 커밋된 후에 비동기 분석을 실행합니다.
     * 
     * @TransactionalEventListener(phase = AFTER_COMMIT):
     * - 트랜잭션이 성공적으로 커밋된 후에만 이벤트 처리
     * - DB에 데이터가 확실히 저장된 후 비동기 작업 실행
     * 
     * @Async:
     * - 이벤트 처리를 별도 스레드에서 비동기로 실행
     * - 호출자에게 즉시 반환
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePromptSaved(PromptSavedEvent event) {
        log.info("Received PromptSavedEvent for prompt ID: {} (after commit)", event.promptId());
        promptAnalysisService.analyzePrompt(event.promptId(), event.content());
    }
}
