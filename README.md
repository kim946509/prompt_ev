# Prompt Evaluation System

AI 프롬프트를 체계적으로 평가하고 개선하기 위한 자기주도 학습 플랫폼입니다. AI 코딩 어시스턴트(Claude Code, Cursor 등)와 대화하며 자동으로 프롬프트를 저장하고, 실행 결과를 평가하여 지속적으로 프롬프트 품질을 향상시킬 수 있습니다.

## 주요 기능

### 현재 제공 기능 (v0.0.1-SNAPSHOT)

- **자동 프롬프트 수집**: Claude Code와 대화 시 프롬프트를 자동으로 DB에 저장
- **지능형 카테고리 분류**: 키워드 기반 자동 분류 (FEATURE, BUG_FIX, REFACTORING, TEST, DOCUMENTATION, ETC)
- **프로젝트별 관리**: 프로젝트 이름으로 프롬프트 구분 및 필터링
- **웹 UI**: 프롬프트 목록 조회 및 카테고리/프로젝트별 필터링
- **REST API**: 프롬프트 생성 및 조회 API 제공
- **SQLite 기반**: 로컬 파일 DB로 데이터 프라이버시 보장

### 지원 플랫폼

| 플랫폼 | 상태 | 비고 |
|--------|------|------|
| **Claude Code** | ✅ 지원 | Webhook을 통한 자동 프롬프트 저장 |
| **Cursor** | 🔜 예정 | 향후 업데이트 예정 |

## 시작하기

### 필수 요구사항

다음 소프트웨어가 설치되어 있어야 합니다:

#### 1. Docker 사용 시 (권장)
- **Docker**: 20.10 이상
- **Docker Compose**: v2.0 이상

#### 2. 로컬 실행 시
- **Java**: JDK 21 이상
- **Python**: 3.7 이상 (Claude Code Hook 사용 시)

### 설치 방법

#### Option 1: Docker로 실행 (권장)

```bash
# 1. 프로젝트 클론
git clone https://github.com/kim946509/prompt_ev.git
cd prompt_ev

# 2. Docker Compose로 실행
docker-compose up -d

# 3. 로그 확인
docker-compose logs -f
```

접속: `http://localhost:18080/prompts`

데이터는 `./data/prompt_ev.db`에 저장되며 컨테이너 삭제 후에도 유지됩니다.

```bash
# 중지
docker-compose down

# 완전 삭제 (데이터 포함)
docker-compose down -v
rm -rf ./data
```

#### Option 2: 로컬에서 직접 실행

```bash
# 1. 프로젝트 클론
git clone https://github.com/yourusername/prompt_ev.git
cd prompt_ev

# 2. 빌드 및 실행
./gradlew bootRun
```

접속: `http://localhost:18080/prompts`

## Claude Code 연동 설정

Claude Code와 연동하여 프롬프트를 자동으로 저장하려면 다음 단계를 따르세요.

### 1. Hook 파일 복사

프로젝트의 `.claude/hooks/save-prompt.py` 파일을 **Claude Code를 사용할 프로젝트**의 `.claude/hooks/` 디렉토리에 복사합니다.

```bash
# 예시: 다른 프로젝트에 Hook 설정
cp .claude/hooks/save-prompt.py /path/to/your-project/.claude/hooks/
```

### 2. 프로젝트 이름 설정

`save-prompt.py` 파일을 열어 **14번째 줄**의 `PROJECT_NAME`을 수정합니다:

```python
# save-prompt.py (14번째 줄)
PROJECT_NAME = "your-project-name"  # 여기를 변경하세요!
```

프로젝트 이름을 설정하면 여러 프로젝트의 프롬프트를 구분하여 관리할 수 있습니다.

### 3. Hook 활성화

해당 프로젝트의 `.claude/settings.local.json` 파일에 다음 설정을 추가합니다:

```json
{
  "hooks": {
    "UserPromptSubmit": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "python .claude/hooks/save-prompt.py"
          }
        ]
      }
    ]
  }
}
```

### 4. 동작 확인

1. Prompt Evaluation System이 실행 중인지 확인 (`http://localhost:18080`)
2. Claude Code에서 프롬프트를 입력
3. 웹 UI(`http://localhost:18080/prompts`)에서 프롬프트가 자동 저장되었는지 확인

### Hook 동작 방식

- **이벤트**: 사용자가 Claude Code에 프롬프트를 입력할 때마다 자동 실행
- **자동 분류**: 프롬프트 내용의 키워드를 분석하여 카테고리 자동 할당
- **비동기 실행**: 2초 타임아웃으로 백그라운드에서 실행 (Claude 응답 지연 없음)
- **장애 허용**: 네트워크 오류 발생 시 무시하고 계속 진행

#### 카테고리 분류 규칙

| 카테고리 | 키워드 예시 |
|----------|------------|
| **BUG_FIX** | 버그, bug, fix, 오류, error, crash |
| **TEST** | 테스트, test, unit test, mock, assert |
| **REFACTORING** | 리팩토링, refactor, 개선, optimize, cleanup |
| **FEATURE** | 기능, feature, add, create, implement |
| **DOCUMENTATION** | 문서, docs, readme, 주석, docstring |
| **ETC** | 위 키워드에 해당하지 않는 경우 |

우선순위: BUG_FIX > TEST > REFACTORING > FEATURE > DOCUMENTATION > ETC

## 사용 방법

### 웹 UI

프롬프트 목록 조회 및 필터링:
- URL: `http://localhost:18080/prompts`
- 카테고리별 필터링
- 프로젝트별 필터링
- 페이지네이션 지원 (페이지당 20개)

### REST API

#### 프롬프트 생성

```bash
POST /api/prompts
Content-Type: application/json

{
  "content": "사용자 인증 기능을 추가해주세요",
  "category": "FEATURE",
  "project": "my-app",
  "aiModel": "claude-sonnet-4-5"
}
```

#### 프롬프트 목록 조회

```bash
GET /api/prompts?category=FEATURE&project=my-app&page=0&size=20
```

## 프로젝트 구조

```
prompt_ev/
├── src/main/java/kr/co/ansandy/prompt_ev/
│   ├── config/          # JPA Auditing 등 설정
│   ├── controller/      # REST & View 컨트롤러
│   ├── dto/             # 요청/응답 DTO
│   ├── entity/          # JPA 엔티티 (Prompt, BaseEntity)
│   ├── repository/      # Spring Data JPA 리포지토리
│   └── service/         # 비즈니스 로직
├── src/main/resources/
│   ├── application.yaml # Spring Boot 설정
│   ├── properties/      # 환경 변수
│   └── templates/       # Thymeleaf 템플릿
├── .claude/
│   ├── hooks/           # Claude Code Hook 스크립트
│   └── settings.local.json
├── Dockerfile           # Docker 이미지 빌드 설정
├── docker-compose.yml   # Docker Compose 설정
└── data/                # SQLite DB 저장 위치 (Docker 볼륨)
```

## 기술 스택

- **Backend**: Spring Boot 4.0.0, Java 21
- **Database**: SQLite (Hibernate Community Dialects)
- **View**: Thymeleaf
- **Build**: Gradle 9.2.1
- **Containerization**: Docker, Docker Compose
- **Integration**: Python 3.x (Claude Code Hook)

## 개발 가이드

### 로컬 개발 환경

```bash
# 빌드
./gradlew build

# 테스트
./gradlew test

# 실행
./gradlew bootRun

# Clean 빌드
./gradlew clean build
```

### Docker 개발 환경

```bash
# 이미지 빌드
docker build -t prompt-ev:latest .

# 컨테이너 실행
docker run -d -p 18080:8080 -v ./data:/app/data --name prompt-evaluation prompt-ev:latest

# 로그 확인
docker logs -f prompt-evaluation
```

### 데이터베이스

- **위치**: 프로젝트 루트의 `prompt_ev.db` (로컬 실행 시) 또는 `./data/prompt_ev.db` (Docker 실행 시)
- **스키마**: Hibernate DDL auto-update (자동 생성/업데이트)
- **접근**: SQLite 클라이언트 도구로 직접 조회 가능

## 문제 해결

### Hook이 동작하지 않을 때

1. Prompt Evaluation System이 실행 중인지 확인
   ```bash
   curl http://localhost:18080/
   ```

2. Python이 설치되어 있는지 확인
   ```bash
   python --version
   ```

3. Hook 스크립트 경로가 올바른지 확인
   ```bash
   ls -la .claude/hooks/save-prompt.py
   ```

4. 프로젝트 이름이 설정되어 있는지 확인
   ```python
   # save-prompt.py의 14번째 줄
   PROJECT_NAME = "your-project-name"  # 비어있으면 안됨
   ```

### Docker 컨테이너가 시작되지 않을 때

```bash
# 로그 확인
docker-compose logs

# 컨테이너 상태 확인
docker ps -a

# 포트 충돌 확인
netstat -ano | findstr :18080  # Windows
lsof -i :18080                 # Mac/Linux
```

### 빌드 실패 시

```bash
# Gradle 캐시 삭제
./gradlew clean

# Docker 빌드 캐시 삭제
docker-compose build --no-cache
```

## 향후 계획 (Roadmap)

### Phase 2: AI 분석 통합
- OpenAI/Anthropic API 연동
- AI 기반 프롬프트 품질 분석
- 개선 제안 기능

### Phase 3: 통계 대시보드
- 카테고리별 프롬프트 사용 통계
- 시간대별 분석
- 프로젝트별 인사이트

### Phase 4: 고급 기능
- 프롬프트 템플릿
- 버전 관리
- 고급 검색 및 필터링
- Cursor 플랫폼 지원

## 라이선스

이 프로젝트는 자유롭게 사용 가능합니다.

## 기여하기

이슈 제보 및 개선 제안을 환영합니다!

## 문의

- GitHub Issues: [프로젝트 이슈 페이지]
- 개발 가이드: [CLAUDE.md](./CLAUDE.md)
- 프로젝트 계획: [PROJECT_PLAN.md](./PROJECT_PLAN.md)
