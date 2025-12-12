package kr.co.ansandy.prompt_ev.service;

import kr.co.ansandy.prompt_ev.entity.Prompt;
import kr.co.ansandy.prompt_ev.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptService {

    private final PromptRepository promptRepository;

    @Transactional
    public Prompt savePrompt(Prompt prompt) {
        return promptRepository.save(prompt);
    }
}
