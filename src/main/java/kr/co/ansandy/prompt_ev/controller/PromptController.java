package kr.co.ansandy.prompt_ev.controller;

import kr.co.ansandy.prompt_ev.dto.PromptCreateRequest;
import kr.co.ansandy.prompt_ev.dto.PromptResponse;
import kr.co.ansandy.prompt_ev.entity.Prompt;
import kr.co.ansandy.prompt_ev.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prompts")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    @PostMapping
    public ResponseEntity<PromptResponse> createPrompt(@RequestBody PromptCreateRequest request) {
        Prompt prompt = new Prompt();
        prompt.setContent(request.getContent());
        prompt.setCategory(request.getCategory());
        prompt.setAiModel(request.getAiModel());

        Prompt savedPrompt = promptService.savePrompt(prompt);
        return ResponseEntity.ok(new PromptResponse(savedPrompt));
    }
}
