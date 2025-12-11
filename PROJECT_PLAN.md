# Prompt Evaluation System (프롬프트 평가 시스템)

## 프로젝트 개요

### 프로젝트 비전
AI 프롬프트 품질을 체계적으로 평가하고 개선하여, 사용자가 더 효과적인 프롬프트를 작성할 수 있도록 지원하는 자기주도형 학습 플랫폼

### 핵심 가치 제안
- **데이터 기반 학습**: 자신의 프롬프트 히스토리를 분석하여 패턴 파악
- **AI 기반 인사이트**: AI가 프롬프트를 분석하고 구체적인 개선 방향 제시
- **체계적 평가**: 명확한 평가 지표를 통한 정량적/정성적 분석
- **지속적 개선**: 프롬프트 작성 능력의 점진적 향상 추적

---

## 핵심 기능 요구사항

### 1. 프롬프트 관리
#### 1.1 프롬프트 저장
- 사용자가 작성한 프롬프트를 로컬 DB에 저장
- 프롬프트 메타데이터 관리
  - 작성 일시
  - 프롬프트 카테고리/태그
  - 사용 목적
  - 대상 AI 모델 정보

#### 1.2 프롬프트 실행 결과 저장
- AI 응답 결과 원문 저장
- 실행 컨텍스트 정보
  - 실행 환경
  - 사용한 파라미터 (temperature, max_tokens 등)
  - 실행 시간 및 토큰 사용량

### 2. 평가 시스템
#### 2.1 사용자 직접 평가
사용자가 다음 평가 지표를 직접 기입:

| 평가 항목 | 설명 | 평가 방식 |
|---------|------|---------|
| **목표 달성도** | 원하는 결과가 나왔는지 | 5점 척도 |
| **실행 성공 여부** | 오류 없이 잘 실행되었는지 | Yes/No |
| **응답 품질** | 답변의 정확성과 완성도 | 5점 척도 |
| **효율성** | 적절한 길이와 명확성 | 5점 척도 |
| **재사용 가능성** | 향후 재사용 의향 | 5점 척도 |
| **자유 코멘트** | 추가 메모 및 소감 | 텍스트 |

#### 2.2 자동 평가 지표
시스템이 자동으로 수집하는 메트릭:
- 프롬프트 길이 (글자 수, 단어 수)
- 응답 생성 시간
- 토큰 사용량
- 실행 성공/실패 여부
- 재시도 횟수

### 3. AI 분석 기능
#### 3.1 프롬프트 분석
AI가 프롬프트를 분석하여 다음을 제공:
- **명확성 평가**: 프롬프트가 얼마나 명확한가
- **구조 분석**: 프롬프트의 구성 요소 파악
  - Context 제공 여부
  - 명확한 지시사항 포함 여부
  - 예시 제공 여부
  - 제약사항 명시 여부
- **개선 제안**: 구체적인 수정 방향
  - Before/After 예시
  - 개선 포인트 설명

#### 3.2 분석 결과 저장
- AI 분석 결과를 프롬프트와 함께 저장
- 분석 버전 관리 (추후 재분석 가능)
- 개선 이력 추적

### 4. 대시보드 및 통계
#### 4.1 개인 통계
- 총 프롬프트 수
- 평균 평가 점수
- 카테고리별 성과
- 시간에 따른 개선 추세

#### 4.2 인사이트 제공
- 잘 작동한 프롬프트 패턴 식별
- 개선이 필요한 영역 하이라이트
- 베스트 프랙티스 추천

---

## 데이터 모델 설계

### ERD 개요

```
┌─────────────────┐
│    Prompt       │
├─────────────────┤
│ id (PK)         │
│ content         │
│ category        │
│ purpose         │
│ created_at      │
│ updated_at      │
└────────┬────────┘
         │
         │ 1:N
         │
┌────────▼─────────────┐
│  PromptExecution     │
├──────────────────────┤
│ id (PK)              │
│ prompt_id (FK)       │
│ ai_model             │
│ parameters           │
│ response_text        │
│ execution_time       │
│ token_usage          │
│ success              │
│ executed_at          │
└────────┬─────────────┘
         │
         │ 1:1
         │
┌────────▼──────────────┐      ┌──────────────────┐
│  UserEvaluation       │      │   AIAnalysis     │
├───────────────────────┤      ├──────────────────┤
│ id (PK)               │      │ id (PK)          │
│ execution_id (FK)     │      │ prompt_id (FK)   │
│ goal_achievement      │◄─────┤ clarity_score    │
│ execution_success     │  1:1 │ structure_score  │
│ response_quality      │      │ suggestions      │
│ efficiency            │      │ improvements     │
│ reusability           │      │ analyzed_at      │
│ comment               │      │ version          │
│ evaluated_at          │      └──────────────────┘
└───────────────────────┘
```

### 엔티티 상세 정의

#### 1. Prompt (프롬프트)
```java
@Entity
public class Prompt {
    @Id @GeneratedValue
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;          // 프롬프트 원문

    private String category;         // 카테고리 (예: 코드생성, 문서작성, 분석)
    private String purpose;          // 사용 목적

    @OneToMany(mappedBy = "prompt")
    private List<PromptExecution> executions;

    @OneToMany(mappedBy = "prompt")
    private List<AIAnalysis> analyses;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### 2. PromptExecution (실행 기록)
```java
@Entity
public class PromptExecution {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Prompt prompt;

    private String aiModel;          // 사용한 AI 모델 (예: GPT-4, Claude-3.5)

    @Column(columnDefinition = "JSON")
    private String parameters;       // 실행 파라미터 (temperature, max_tokens 등)

    @Column(columnDefinition = "TEXT")
    private String responseText;     // AI 응답 결과

    private Integer executionTime;   // 실행 시간 (ms)
    private Integer tokenUsage;      // 사용 토큰 수
    private Boolean success;         // 실행 성공 여부

    @OneToOne(mappedBy = "execution")
    private UserEvaluation evaluation;

    private LocalDateTime executedAt;
}
```

#### 3. UserEvaluation (사용자 평가)
```java
@Entity
public class UserEvaluation {
    @Id @GeneratedValue
    private Long id;

    @OneToOne
    private PromptExecution execution;

    private Integer goalAchievement;   // 1-5점
    private Boolean executionSuccess;  // true/false
    private Integer responseQuality;   // 1-5점
    private Integer efficiency;        // 1-5점
    private Integer reusability;       // 1-5점

    @Column(columnDefinition = "TEXT")
    private String comment;            // 자유 코멘트

    private LocalDateTime evaluatedAt;
}
```

#### 4. AIAnalysis (AI 분석)
```java
@Entity
public class AIAnalysis {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Prompt prompt;

    private Double clarityScore;       // 명확성 점수 (0.0-1.0)
    private Double structureScore;     // 구조 점수 (0.0-1.0)

    @Column(columnDefinition = "JSON")
    private String structureElements;  // 구조 요소 분석 결과

    @Column(columnDefinition = "TEXT")
    private String suggestions;        // 개선 제안

    @Column(columnDefinition = "TEXT")
    private String improvements;       // 개선된 프롬프트 예시

    private String version;            // 분석 버전
    private LocalDateTime analyzedAt;
}
```

---

## 시스템 아키텍처

### 기술 스택
- **Backend**: Spring Boot 4.0.0 + Java 21
- **Database**: H2 (개발), PostgreSQL/MySQL (프로덕션)
- **ORM**: Spring Data JPA
- **Template Engine**: Thymeleaf
- **AI Integration**:
  - OpenAI API (GPT-4)
  - Anthropic API (Claude)
  - 또는 로컬 LLM 옵션
- **Build Tool**: Gradle

### 아키텍처 계층

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│      (Thymeleaf + REST API)         │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Service Layer               │
│  ┌──────────┐  ┌─────────────────┐ │
│  │ Prompt   │  │  AI Analysis    │ │
│  │ Service  │  │  Service        │ │
│  └──────────┘  └─────────────────┘ │
│  ┌──────────┐  ┌─────────────────┐ │
│  │Evaluation│  │  Statistics     │ │
│  │ Service  │  │  Service        │ │
│  └──────────┘  └─────────────────┘ │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Repository Layer               │
│         (Spring Data JPA)           │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Database Layer              │
│            (H2/PostgreSQL)          │
└─────────────────────────────────────┘
```

### 외부 시스템 연동

```
┌─────────────────┐
│  Prompt Ev App  │
└────────┬────────┘
         │
         ├─────► OpenAI API (프롬프트 분석)
         │
         ├─────► Anthropic API (대안 분석)
         │
         └─────► Local LLM (선택적)
```

---

## 사용자 워크플로우

### 1. 프롬프트 등록 및 실행
```
1. 사용자가 프롬프트 입력
   ↓
2. 시스템이 프롬프트 저장
   ↓
3. AI에게 프롬프트 전송 및 실행
   ↓
4. 응답 결과 저장
   ↓
5. 사용자에게 결과 표시
```

### 2. 평가 프로세스
```
1. 실행 결과 확인
   ↓
2. 평가 폼 작성
   - 5개 평가 지표 입력
   - 자유 코멘트 작성
   ↓
3. 평가 결과 저장
   ↓
4. AI 자동 분석 트리거
   ↓
5. 분석 결과 확인
```

### 3. 개선 사이클
```
1. 대시보드에서 통계 확인
   ↓
2. 낮은 점수 프롬프트 식별
   ↓
3. AI 분석 결과 검토
   ↓
4. 개선된 프롬프트 작성
   ↓
5. 재실행 및 비교
   ↓
6. 학습 및 패턴 내재화
```

---

## 화면 구성

### 1. 메인 대시보드
- **상단**: 주요 통계 카드
  - 총 프롬프트 수
  - 평균 평가 점수
  - 이번 주 활동
- **중단**: 최근 프롬프트 목록
- **하단**: 성과 추세 그래프

### 2. 프롬프트 작성/실행 페이지
- 프롬프트 입력 폼
  - 카테고리 선택
  - 목적 입력
  - AI 모델 선택
  - 파라미터 설정
- 실행 버튼
- 결과 표시 영역

### 3. 평가 페이지
- 실행 결과 표시
- 평가 폼
  - 5개 평가 항목 (슬라이더 또는 별점)
  - 자유 코멘트 텍스트 영역
- 제출 버튼

### 4. 분석 결과 페이지
- 원본 프롬프트
- AI 분석 결과
  - 명확성/구조 점수
  - 구조 요소 분석
  - 개선 제안
  - 개선 예시 (Before/After)
- 재실행 버튼

### 5. 히스토리 페이지
- 프롬프트 목록
  - 필터링 (카테고리, 날짜, 점수)
  - 정렬 옵션
- 상세 보기 모달
  - 전체 실행 기록
  - 모든 평가 결과
  - AI 분석 히스토리

---

## 개발 로드맵

### Phase 1: MVP (최소 기능 제품)
**목표**: 핵심 기능 구현 및 검증

- [ ] 데이터베이스 스키마 구현
  - [ ] Entity 클래스 작성
  - [ ] Repository 인터페이스 정의
  - [ ] 초기 데이터 마이그레이션
- [ ] 프롬프트 CRUD 기능
  - [ ] 프롬프트 등록
  - [ ] 프롬프트 조회
  - [ ] 프롬프트 수정/삭제
- [ ] 실행 기록 관리
  - [ ] 실행 결과 저장
  - [ ] 실행 히스토리 조회
- [ ] 사용자 평가 기능
  - [ ] 평가 폼 UI
  - [ ] 평가 결과 저장
  - [ ] 평가 통계 조회
- [ ] 기본 UI 구현
  - [ ] 메인 대시보드
  - [ ] 프롬프트 작성 페이지
  - [ ] 평가 페이지

### Phase 2: AI 분석 통합
**목표**: AI 기반 분석 기능 추가

- [ ] AI API 연동
  - [ ] OpenAI API 클라이언트 구현
  - [ ] Anthropic API 클라이언트 구현
  - [ ] API 키 관리 및 보안
- [ ] 프롬프트 분석 로직
  - [ ] 프롬프트 분석 프롬프트 작성
  - [ ] 분석 결과 파싱
  - [ ] 분석 결과 저장
- [ ] 분석 결과 UI
  - [ ] 분석 결과 페이지
  - [ ] Before/After 비교 뷰
  - [ ] 개선 제안 표시
- [ ] 자동 분석 트리거
  - [ ] 평가 완료 시 자동 분석
  - [ ] 배치 분석 기능

### Phase 3: 통계 및 인사이트
**목표**: 데이터 기반 학습 지원

- [ ] 통계 대시보드
  - [ ] 전체 통계 집계
  - [ ] 카테고리별 분석
  - [ ] 시간별 추세 분석
- [ ] 시각화
  - [ ] 점수 추세 그래프
  - [ ] 카테고리별 비교 차트
  - [ ] 개선도 시각화
- [ ] 패턴 분석
  - [ ] 높은 점수 프롬프트 패턴 추출
  - [ ] 공통 실패 패턴 식별
  - [ ] 베스트 프랙티스 추천

### Phase 4: 고급 기능
**목표**: 사용자 경험 향상

- [ ] 프롬프트 템플릿
  - [ ] 카테고리별 템플릿 제공
  - [ ] 커스텀 템플릿 저장
  - [ ] 템플릿 공유 (선택적)
- [ ] 버전 관리
  - [ ] 프롬프트 수정 이력
  - [ ] 버전 간 비교
  - [ ] 롤백 기능
- [ ] 고급 검색
  - [ ] 전문 검색
  - [ ] 태그 기반 필터링
  - [ ] 복합 조건 검색
- [ ] 내보내기/가져오기
  - [ ] JSON/CSV 내보내기
  - [ ] 데이터 백업
  - [ ] 다른 도구와 연동

---

## 평가 지표 상세

### 정량적 지표
1. **목표 달성도** (1-5점)
   - 1점: 전혀 원하는 결과가 아님
   - 2점: 부분적으로만 달성
   - 3점: 보통 수준으로 달성
   - 4점: 대부분 만족스러움
   - 5점: 완벽하게 목표 달성

2. **응답 품질** (1-5점)
   - 정확성
   - 완성도
   - 관련성

3. **효율성** (1-5점)
   - 응답 길이의 적절성
   - 불필요한 내용 최소화
   - 명확하고 간결한 표현

4. **재사용 가능성** (1-5점)
   - 유사 상황에서 재사용 의향
   - 프롬프트의 범용성

### 정성적 지표
1. **실행 성공 여부** (Boolean)
   - 오류 없이 정상 실행
   - 예상대로 동작

2. **자유 코멘트** (Text)
   - 특별한 관찰 사항
   - 개선 아이디어
   - 컨텍스트 정보

---

## 보안 및 개인정보

### 데이터 보안
- 로컬 데이터베이스 사용으로 데이터 외부 유출 방지
- API 키는 환경변수 또는 암호화된 설정 파일로 관리
- 민감한 프롬프트 내용은 암호화 옵션 제공

### 개인정보 보호
- 사용자의 프롬프트는 로컬에만 저장
- AI 분석 시 최소한의 데이터만 외부 API로 전송
- 선택적 익명화 옵션 제공

---

## 성공 메트릭

### 사용성 지표
- 프롬프트 등록 수
- 평가 완료율
- 재방문율

### 품질 지표
- 평균 평가 점수 추세
- 프롬프트 개선 횟수
- 재사용률

### 학습 지표
- 사용자의 프롬프트 평균 점수 상승률
- 동일 카테고리 내 개선도
- AI 제안 수용률

---

## 향후 확장 가능성

### 단기 확장
- 다양한 AI 모델 지원 (GPT-4, Claude, Gemini 등)
- 프롬프트 카테고리 커스터마이징
- 모바일 앱 개발

### 중기 확장
- 팀 협업 기능
- 프롬프트 공유 커뮤니티
- A/B 테스트 기능

### 장기 확장
- AI 기반 프롬프트 자동 최적화
- 산업별 프롬프트 베스트 프랙티스 DB
- 프롬프트 마켓플레이스

---

## 참고 자료

### 프롬프트 엔지니어링 가이드
- [OpenAI Prompt Engineering Guide](https://platform.openai.com/docs/guides/prompt-engineering)
- [Anthropic Prompt Engineering](https://docs.anthropic.com/claude/docs/prompt-engineering)
- [Prompt Engineering Guide by DAIR.AI](https://www.promptingguide.ai/)

### 유사 도구
- PromptPerfect
- PromptBase
- LangSmith (LangChain)

---

## 프로젝트 정보

- **프로젝트명**: Prompt Evaluation System
- **버전**: 0.0.1-SNAPSHOT
- **그룹**: kr.co.ansandy
- **설명**: 프롬프트 품질 평가 및 개선 지원 시스템
- **라이선스**: TBD
