# Skala Pulse

**SKALA 교육 전용 피드백 & 원인 진단 서비스**

구글 폼 5점 척도 설문의 한계(관대화 경향, 응답 피로도, 점수 하락 시 원인 추적 불가)를 드래그 기반 응답 UX와 블록별 원인 진단 대시보드로 해결하는 B2B SaaS입니다.

> 1인 풀스택 미니 프로젝트 · Vue 3 + Spring Boot · 2026.09

<br>

## 문제와 해결

| 문제 | 해결 |
| :-- | :-- |
| 반복되는 라디오 버튼 → 영혼 없는 5점 연타 | **카드 순위 드래그**로 항목 간 상대 평가 유도 |
| 긴 서술형 작성 부담 → 피드백 포기 | **원클릭 퀵태그** + 한 줄 요약 (선택 입력) |
| 수백 개의 서술형 응답을 일일이 읽어야 함 | 태그 **동시 선택 키워드 그래프**로 핵심 이슈 한눈에 파악 |
| 총점이 떨어져도 어떤 항목이 원인인지 모름 | 종합점수·항목 순위·키워드를 **블록별 카드로 나란히** 보여주는 원인 진단 대시보드 |

<br>

## 주요 기능

### 교육생
- 수업(과목) 단위로 발급된 설문에 **종합평가(드래그 슬라이더) → 세부 항목 카드 순위 → 퀵태그·한 줄 요약** 순서로 응답
- 여러 과목을 병행 수강하면 미제출 설문을 이어서 안내

### 교수자
- 담당 수업의 블록별 분석 카드 (점수 트렌드, 항목 순위표)와 **전체 익명 평균 벤치마크** 비교
- 키워드 그래프, 피드백 갤러리, 수업 개선 액션 메모

### 관리자
- **노드 기반 설문 빌더** — 과목 단위로 블록 구성과 분기 로직 편집 ([Vue Flow](https://vueflow.dev/))
- 기수·과정·반 개설, 교수자 배정, 반별 제출 현황(카운트만)
- 회원가입 승인 및 역할(관리자/교수자/교육생) 관리
- 피드백에서 도출된 개선 과제 관리

<br>

## 설계 원칙

**1. 시스템은 인과관계를 단정하지 않는다**
블록마다 분석 카드를 나란히 보여줄 뿐, "이 항목 때문에 점수가 떨어졌다"는 자동 판단은 하지 않습니다. 해석은 강사·관리자의 몫입니다.

**2. 개인 순위는 노출하지 않는다**
강제 순위 특성상 개인 단위로는 항상 '꼴찌' 항목이 생기므로, **반 평균 순위**(1~5위)로만 집계해 개인의 선택 노이즈를 상쇄합니다. 분기 로직 때문에 블록에 도달하지 못한 응답자는 해당 블록 집계에서 제외합니다.

**3. 익명성은 역할이 아니라 콘텐츠의 속성이다**
응답 내용은 관리자에게도 익명 집계로만 제공되고, 제출 현황은 "N명 중 M명 제출" 카운트만 보여줍니다. 교수자는 다른 교수자의 반 데이터에 접근할 수 없습니다.

<br>

## 설문 빌더 분기 규칙

| 블록 타입 | 분기 조건 | 원인 진단 | 키워드 분석 |
| :-- | :-: | :-: | :-: |
| 점수드래그 | 비교 연산자 + 임계값 (예: 종합점수 ≤ 2) | ✅ | — |
| 카드순위 | 특정 항목의 순위 (예: '실습' 4~5위) | ✅ | — |
| 퀵태그 | 특정 태그 포함 여부 | — | ✅ |
| 단답형 / 서술형 | — (항상 다음 노드로) | — | ✅ |

- 위에서부터 **첫 매칭 조건**만 적용하고, 모든 노드는 기본(else) 엣지를 가집니다.
- 조건은 단일 조건만 허용하며, 루프를 막아 항상 **DAG** 구조를 유지합니다.
- 엣지는 다음 블록 또는 "제출" 노드로 연결할 수 있습니다 (예: 고득점 응답은 세부 블록 생략).

<br>

## 기술 스택

| 영역 | 기술 |
| :-- | :-- |
| Frontend | Vue 3 (`<script setup>`), Vite, Vue Router, Pinia |
| UI | Tailwind CSS 4, shadcn-vue, Lucide |
| Interaction | vuedraggable (카드 순위·응답 UI), Vue Flow (설문 빌더·키워드 그래프), vue-chartjs |
| Backend | Java 21, Spring Boot 3.4, Spring Security (JWT), Spring Data JPA |
| Database | PostgreSQL 16, Flyway |

<br>

## 프로젝트 구조

```
skala-pulse/
├── frontend/                 # Vue 3 SPA
│   └── src/
│       ├── views/            # survey / instructor / admin 역할별 화면
│       ├── components/       # dashboard 카드, survey 블록, shadcn-vue UI
│       ├── stores/           # Pinia (auth, courses, survey, members ...)
│       ├── router/           # 역할 기반 Navigation Guard
│       └── lib/api.js        # JWT 자동 첨부 fetch 래퍼
├── backend/                  # Spring Boot API
│   └── src/main/
│       ├── java/io/skala/feedback/
│       │   ├── auth/         # 로그인, JWT 발급·검증
│       │   ├── member/       # 회원가입 승인, 역할 관리
│       │   ├── course/       # 과정·반 관리
│       │   ├── survey/       # 설문 템플릿(그래프), 응답 제출
│       │   ├── analysis/     # 반 평균 순위, 키워드 그래프 집계
│       │   └── improvement/  # 개선 과제
│       └── resources/db/migration/   # Flyway 스키마 + 시드 데이터
└── docker-compose.yml        # PostgreSQL
```

<br>

## 로컬 실행

**요구 사항:** Java 21, Node.js 20+, Docker

```bash
# 1. DB
docker compose up -d

# 2. Backend (http://localhost:8080) — 첫 실행 시 Flyway가 스키마와 시드 데이터를 생성
cd backend
./gradlew bootRun

# 3. Frontend (http://localhost:5173)
cd frontend
npm install
npm run dev
```

백엔드 주소를 바꾸려면 `frontend/.env.local`에 `VITE_API_BASE_URL`을 지정합니다.

**데모 계정** — 시드 계정은 모두 기본 비밀번호 `skala1234!`를 사용합니다.

| 역할 | 이메일 |
| :-- | :-- |
| 관리자 | `admin@skala.io` |
| 교수자·교육생 | `V2__seed.sql`의 시드 계정 |

<br>

## 문서

- [최종 기획안 v2](./최종%20기획안%20v2.pdf) — 설문 빌더 & 원인 진단 로직 정교화
- [초기 기획안](./초기%20기획안.pdf)

## 향후 계획

- Slack OAuth 로그인 (현재는 이메일 + 비밀번호)
- 서술형 응답 텍스트 마이닝(Word2Vec) 기반 키워드 분석 — 현재 키워드 그래프는 퀵태그 동시 선택 빈도로 구성
