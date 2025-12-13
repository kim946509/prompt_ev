#!/usr/bin/env python3
"""
Claude Code Hook: UserPromptSubmit
사용자가 프롬프트를 입력할 때 자동으로 DB에 저장합니다.
"""

import json
import sys
import urllib.request
import urllib.error

# Spring Boot API URL
API_URL = "http://localhost:8090/api/prompts"
PROJECT_NAME = "prompt_ev"
def classify_category(prompt_text):
    """
       카테고리:
       - FEATURE: 새로운 기능 구현
       - REFACTORING: 리팩토링
       - BUG_FIX: 오류 수정
       - TEST: 테스트 코드 작성
       - DOCUMENTATION: 문서 작성
       - ETC: 기타
       """

    text_lower = prompt_text.lower()

    # 키워드 매칭
    bug_keywords = [
        '버그', '오류', '에러', '예외', '비정상', '작동안함', '깨짐', '중단', '죽음',
        '고치', '수정', 'fix', 'bug', 'error', 'crash', 'exception', 'rollback',
        '핫픽스', '패치', 'hotfix', 'timeout', 'not working', 'undefined', 'broken',
        'leak', 'segfault', 'npe', 'traceback'
    ]

    test_keywords = [
        '테스트', '단위 테스트', '통합 테스트', 'e2e', '인수 테스트', '검증', '시나리오', '케이스',
        '회귀 테스트', 'mock', 'stub', 'fixture', 'assert', 'assertion', 'coverage',
        'test', 'unit test', 'integration test', 'snapshot', 'spec'
    ]

    refactor_keywords = [
        '리팩토링', '리펙터링', '코드 정리', '구조 개선', '리네임', '모듈화', '의존성 정리',
        '중복 제거', '성능 개선', '최적화', '가독성', '단순화', 'clean', 'refactor',
        'restructure', 'rename', 'optimize', 'simplify', 'cleanup', 'tune', 'improve'
    ]

    feature_keywords = [
        '기능', '추가', '만들', '구현', '생성', '확장', '연동', '지원', '요청', '요구사항',
        'add', 'create', 'implement', 'introduce', 'enable', 'feature', 'integration',
        'api', 'endpoint', 'option', 'story', 'spec'
    ]

    document_keywords = [
        '문서', '설명', '주석', '가이드', 'readme', 'wiki', 'docs', 'documentation', 'docstring', '정리문서'
    ]

    # 우선순위: BUG_FIX > TEST > REFACTORING > FEATURE > DOCUMENTATION > ETC
    for keyword in bug_keywords:
        if keyword in text_lower:
            return 'BUG_FIX'

    for keyword in test_keywords:
        if keyword in text_lower:
            return 'TEST'

    for keyword in refactor_keywords:
        if keyword in text_lower:
            return 'REFACTORING'

    for keyword in feature_keywords:
        if keyword in text_lower:
            return 'FEATURE'

    for keyword in document_keywords:
        if keyword in text_lower:
            return 'DOCUMENTATION'

    return 'ETC'

def main():
    try:
        # stdin으로부터 JSON 입력 받기
        input_data = json.load(sys.stdin)
    except json.JSONDecodeError as e:
        print(f"Error: Invalid JSON input: {e}", file=sys.stderr)
        sys.exit(1)

    # 프롬프트 내용 추출
    prompt_text = input_data.get("prompt", "")

    # 프롬프트가 비어있으면 저장하지 않음
    if not prompt_text or prompt_text.strip() == "":
        sys.exit(0)

    # 카테고리 자동 분류
    category = classify_category(prompt_text)

    # API 요청 페이로드
    payload = {
        "content": prompt_text,
        "category": category,
        "project": PROJECT_NAME,
        "aiModel": "claude-sonnet-4-5"
    }

    try:
        # JSON 데이터를 bytes로 변환
        data = json.dumps(payload).encode('utf-8')

        # HTTP 요청 생성
        req = urllib.request.Request(
            API_URL,
            data=data,
            headers={'Content-Type': 'application/json'},
            method='POST'
        )

        # API 호출 (2초 타임아웃)
        with urllib.request.urlopen(req, timeout=2) as response:
            if response.status == 200:
                # 성공 (디버깅용 로그, 필요시 주석 해제)
                # result = json.loads(response.read().decode('utf-8'))
                # print(f"✓ Prompt saved (ID: {result.get('id')})", file=sys.stderr)
                pass

    except (urllib.error.URLError, urllib.error.HTTPError, TimeoutError) as e:
        # 네트워크 오류는 무시하고 계속 진행
        # print(f"Warning: Could not save prompt: {e}", file=sys.stderr)
        pass

    # 정상 종료 (프롬프트 처리 계속)
    sys.exit(0)

if __name__ == "__main__":
    main()
