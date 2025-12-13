package kr.co.ansandy.prompt_ev.controller;

import kr.co.ansandy.prompt_ev.dto.PromptFilterRequest;
import kr.co.ansandy.prompt_ev.dto.PromptListResponse;
import kr.co.ansandy.prompt_ev.entity.PromptCategory;
import kr.co.ansandy.prompt_ev.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class PromptViewController {

    private final PromptService promptService;

    /**
     * 프롬프트 목록 페이지 (홈화면)
     */
    @GetMapping({"/", "/prompts"})
    public String listPrompts(
            @ModelAttribute PromptFilterRequest filterRequest,
            Model model
    ) {
        // 프롬프트 목록 조회 (Service에서 DTO로 변환된 결과 반환)
        Page<PromptListResponse> responsePage = promptService.getPrompts(filterRequest);

        // 모델에 데이터 추가
        model.addAttribute("prompts", responsePage.getContent());
        model.addAttribute("page", responsePage);
        model.addAttribute("filter", filterRequest);
        model.addAttribute("categories", PromptCategory.values());
        model.addAttribute("pageSizes", new int[]{10, 20, 30, 50});

        return "prompts/list";
    }
}
