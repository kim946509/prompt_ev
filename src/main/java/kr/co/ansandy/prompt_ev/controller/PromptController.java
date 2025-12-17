package kr.co.ansandy.prompt_ev.controller;

import kr.co.ansandy.prompt_ev.dto.PromptCreateRequest;
import kr.co.ansandy.prompt_ev.dto.PromptResponse;
import kr.co.ansandy.prompt_ev.dto.PromptUpdateRequest;
import kr.co.ansandy.prompt_ev.entity.Prompt;
import kr.co.ansandy.prompt_ev.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/prompts")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    @PostMapping
    public ResponseEntity<PromptResponse> createPrompt(@RequestBody PromptCreateRequest request) {
        return ResponseEntity.ok(promptService.savePrompt(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromptResponse> updatePrompt(
            @PathVariable Long id,
            @RequestBody PromptUpdateRequest request) {
        return ResponseEntity.ok(promptService.updatePrompt(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrompt(@PathVariable Long id) {
        promptService.deletePrompt(id);
        return ResponseEntity.noContent().build();
    }
}
