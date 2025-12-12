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
API_URL = "http://localhost:8080/api/prompts"

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

    # API 요청 페이로드
    payload = {
        "content": prompt_text,
        "category": None,
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
