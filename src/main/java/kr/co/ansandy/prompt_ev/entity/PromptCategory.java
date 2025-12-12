package kr.co.ansandy.prompt_ev.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromptCategory {

    FEATURE("새로운 기능 구현"),
    REFACTORING("리팩토링"),
    BUG_FIX("오류 수정"),
    TEST("테스트 코드 작성"),
    DOCUMENTATION("문서 작성"),
    ETC("기타");


    private final String description;
}
