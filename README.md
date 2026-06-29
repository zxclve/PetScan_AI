# PetScan_AI

반려동물의 안구·피부 이미지를 AI로 분석하고, 진단 이력과 보호자/반려동물 정보를 함께 관리할 수 있도록 구성한 멀티 서비스 프로젝트입니다.  
웹 프론트엔드, Spring Boot 백엔드, FastAPI 기반 AI 추론 서버 2종, Jenkins/Docker 인프라 구성이 하나의 저장소에 포함되어 있습니다.

## 프로젝트 개요

- 반려동물 사진 업로드 후 안구 또는 피부 질환 진단 수행
- 진단 결과, 신뢰도, 탐지 정보, 이력 데이터를 통합 관리
- 회원가입, 로그인, Google OAuth2 로그인 지원
- 보호자별 반려동물 프로필 관리
- 대시보드에서 진단 요약 및 이벤트 현황 확인
- Docker Compose와 Jenkinsfile 기반으로 서비스 배포 흐름 구성

## 아키텍처

```text
pet-frontend (React + Vite)
        |
        v
pet-backend (Spring Boot REST API)
   |         |         |
   |         |         +-- H2 / MySQL
   |         |
   |         +-- pet-ai-eye (FastAPI + YOLO 기반 안구 추론)
   |
   +------------ pet-ai-skin (FastAPI + YOLO 기반 피부 추론)

pet-infra
  - Docker Compose
  - Jenkins Pipelines
```

## 주요 기능

### 1. 사용자 인증

- 일반 회원가입 및 로그인
- JWT 기반 인증 처리
- Google OAuth2 로그인 후 프론트엔드 콜백 페이지로 토큰 전달

### 2. 반려동물 관리

- 보호자별 반려동물 등록, 조회, 수정, 삭제
- 진단 요청 시 반려동물 소유 권한 검증

### 3. AI 진단

- 이미지 업로드 후 `eye` 또는 `skin` 타입으로 진단 요청
- FastAPI AI 서버에서 객체 탐지 기반 추론 수행
- 예측 라벨, 점수, bounding box, top5 결과, 추론 시간 반환
- 백엔드에서 진단 결과와 로그를 저장 및 조회

### 4. 대시보드 및 이력

- 사용자별 진단 요약 정보 제공
- 반려동물별 또는 사용자 전체 진단 이벤트 조회
- 업로드된 진단 이미지 조회 지원

## 서비스 구성

| 서비스 | 역할 | 기본 포트 |
|---|---|---:|
| `pet-frontend` | 사용자 웹 UI | `5173` |
| `pet-backend` | 인증, 반려동물, 진단, 대시보드 API | `8080` |
| `pet-ai-eye` | 안구 질환 추론 API | `5000` |
| `pet-ai-skin` | 피부 질환 추론 API | `5001` |
| `db` | MySQL 데이터베이스 | `3308` |
| `jenkins` | CI/CD 서버 | `8090` |

## 기술 스택

### Frontend

- React 19
- TypeScript
- Vite
- React Router
- Axios
- Recharts
- Tailwind CSS 4

### Backend

- Java 17
- Spring Boot 2.7
- Spring Security
- OAuth2 Client
- JWT
- Spring Data JPA
- H2 / MySQL
- Swagger(OpenAPI)

### AI

- Python
- FastAPI
- PyTorch
- YOLOv5 / Ultralytics YOLO 호환 로더
- Pillow / NumPy

### Infra

- Docker
- Docker Compose
- Jenkins
- Nginx 설정 파일 포함

## 저장소 구조

```text
PetScan_AI/
|-- pet-frontend/   # React 웹 프론트엔드
|-- pet-backend/    # Spring Boot 백엔드 API
|-- pet-ai-eye/     # 안구 질환 추론 서버
|-- pet-ai-skin/    # 피부 질환 추론 서버
|-- pet-infra/      # Docker Compose, Jenkins, 인프라 문서
`-- README.md
```

## 폴더별 설명

### `pet-frontend`

- 라우트: `/`, `/diagnose`, `/events`, `/me`, `/login`, `/signup`
- 기능: 대시보드, 진단 업로드, 이벤트 조회, 마이페이지, 인증 처리
- 현재 구성은 React Native가 아니라 Vite 기반 웹 프론트엔드입니다

### `pet-backend`

- 주요 API
  - `/api/member/signup`
  - `/api/member/login`
  - `/api/dashboard/summary`
  - `/api/diagnosis`
  - `/api/diagnosis/events`
- 로컬 기본 설정은 H2 DB를 사용하며, 인프라 구성에서는 MySQL 사용 가능

### `pet-ai-eye`

- `/health` 상태 점검
- `/predict` 안구 이미지 추론
- 모델 파일 경로와 confidence/iou threshold를 환경변수로 제어 가능

### `pet-ai-skin`

- `/health` 상태 점검
- `/predict` 피부 이미지 추론
- eye 서비스와 동일한 방식으로 모델 로딩 및 추론 결과 반환

### `pet-infra`

- `docker-compose.yml`로 DB, 백엔드, AI, Jenkins 통합 실행
- 서비스별 Jenkinsfile 분리
- Jenkins 설정 및 테스트 시나리오 문서 포함

## 빠른 실행 방법

### 1. 프론트엔드 실행

```bash
cd pet-frontend
npm install
npm run dev
```

### 2. 백엔드 실행

```bash
cd pet-backend
./gradlew bootRun
```

Windows에서는:

```bash
gradlew.bat bootRun
```

### 3. AI 서버 실행

안구 모델 서버:

```bash
cd pet-ai-eye
pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 5000
```

피부 모델 서버:

```bash
cd pet-ai-skin
pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 5001
```

### 4. Docker Compose 실행

```bash
cd pet-infra
docker-compose up -d --build
```

## 환경 설정 포인트

### Backend

- OAuth2 로그인용 `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`
- JWT 시크릿 키
- DB 연결 정보

### AI

- `MODEL_PATH`
- `MODEL_VERSION`
- `CONF_THRESHOLD`
- `IOU_THRESHOLD`
- `MAX_DETECTIONS`
- `IMAGE_SIZE`

## 추천 문서 흐름

- 전체 구조 파악: [`README.md`](README.md)
- 백엔드 세부 내용: [pet-backend/README.md](pet-backend/README.md)
- 프론트엔드 세부 내용: [pet-frontend/README.md](pet-frontend/README.md)
- 안구 AI 세부 내용: [pet-ai-eye/README.md](pet-ai-eye/README.md)
- 피부 AI 세부 내용: [pet-ai-skin/README.md](pet-ai-skin/README.md)
- 인프라/배포 문서: [pet-infra/README.md](pet-infra/README.md)
