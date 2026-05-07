# 🏡 Air-Trip 프로젝트

<div align="center">
  <img width="450" src="https://github.com/user-attachments/assets/20c3ccc1-28fa-4bd0-8dd1-5b0916578ab5" />


[![hits](https://myhits.vercel.app/api/hit/https%3A%2F%2Fgithub.com%2Fgeun-00%2FAirL-J?color=blue&label=hits&size=small)](https://myhits.vercel.app)

<div>
</div>

<img src="https://img.shields.io/badge/프로젝트 기간-2025.07~2025.12-green?style=flat&logo=&logoColor=white" />
<img src="https://img.shields.io/badge/마지막 리드미 수정-2026.05.07-gold?style=flat&logo=&logoColor=white" />

<div>
</div>

[![배포 주소](https://img.shields.io/badge/서비스_보러_가기-yellow?style=flat&logo=vercel&logoColor=white)](https://www.jgy914.shop)
[![배포 주소](https://img.shields.io/badge/API_문서_보러_가기-6DB33F?style=flat&logo=swagger&logoColor=white)](https://airbackend.jgy914.shop/api-docs)

</div>

## 🚀 프로젝트 소개

- **Air-Trip**은 Airbnb를 모티브로 개발한 **숙소 예약 플랫폼**입니다.
- OpenAPI 연동으로 확보한 **실제 숙소 데이터**를 기반으로 지역, 편의시설, 가격대별 상세 검색이 가능합니다.
- **Toss Payments 간편 결제**로 안전하고 빠른 예약이 가능하며, **위시리스트 기능**으로 마음에 드는 숙소를 저장하고 비교할 수 있습니다.
- **WebSocket + Redis Pub/Sub** 아키텍처로 실시간 채팅을 구현하였으며, **OpenAI API**를 활용한 AI 챗봇이 숙소를 추천해 줍니다.

## ⚙ 기술 스택

### 🧱 Core

<div>
  <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=Spring-Boot&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=springsecurity&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Data_Jpa-6DB33F?style=flat&logo=%20Data%20JPA&logoColor=white">
  <img src="https://img.shields.io/badge/JWT-black?style=flat&logo=JSON-Web-Tokens&logoColor=white">
  <img src="https://img.shields.io/badge/Querydsl-blue?style=flat&logoColor=white">
  <img src="https://img.shields.io/badge/WebSocket-orange?style=flat&logoColor=white">
</div>

### 🛢️ Database

<div>
  <img src="https://img.shields.io/badge/Redis-FF4438?style=flat&logo=redis&logoColor=white" />
  <img src="https://img.shields.io/badge/Mariadb-003545?style=flat&logo=mariadb&logoColor=white" />
</div>

### 📚 Test & Docs

<div> 
  <img src="https://img.shields.io/badge/Junit5-25A162?style=flat&logo=junit5&logoColor=white" />
  <img src="https://img.shields.io/badge/RestDocs-8CA1AF?style=flat&logo=readthedocs&logoColor=white" />
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=white" />
</div>

### 🌐 Infra

<div>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/Cloudflare-F38020?style=flat&logo=cloudflare&logoColor=white" />
  <img src="https://img.shields.io/badge/Terraform-844FBA?style=flat&logo=terraform&logoColor=white">
  <img src="https://img.shields.io/badge/Nginx Proxy Manager-F15833?style=flat&logo=nginxproxymanager&logoColor=white">
  <img src="https://img.shields.io/badge/AWS EC2-F38020?style=flat&logoColor=white" />
  <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=flat&logo=githubactions&logoColor=white">
  <img src="https://img.shields.io/badge/Grafana-F46800?style=flat&logo=grafana&logoColor=white">
  <img src="https://img.shields.io/badge/Prometheus-E6522C?style=flat&logo=prometheus&logoColor=white">
  <img src="https://img.shields.io/badge/DNSZi-FDC300?style=flat&logoColor=white">
  <img src="https://img.shields.io/badge/Gabia-2B2B2B?style=flat&logoColor=white">
</div>

### 🧩 ETC

<div>
  <img src="https://img.shields.io/badge/Toss Payments-0854C1?style=flat&logoColor=white" />
  <img src="https://img.shields.io/badge/OpenAI-6BA539?style=flat&logoColor=white" />
  <img src="https://img.shields.io/badge/Google-4285F4?style=flat&logo=google&logoColor=white" />
  <img src="https://img.shields.io/badge/Kakao-FFCD00?style=flat&logo=kakao&logoColor=white" />
  <img src="https://img.shields.io/badge/Naver-03C75A?style=flat&logo=naver&logoColor=white" />
  <img src="https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white" />
  <img src="https://img.shields.io/badge/Google Maps-4285F4?style=flat&logo=googlemaps&logoColor=white" />
</div>

## 🛠️ 프로젝트 아키텍처

### 🗺️ ERD

<img width="2207" height="1283" alt="Air-Trip ERD" src="https://github.com/user-attachments/assets/3655d34c-585a-4247-843b-232f2960a11f" />

<br>

| 애그리거트 루트 | 하위 애그리거트                                  |
|----------|-------------------------------------------|
| 회원       | 회원 상세                                     |
| 숙소       | 숙소 상세, 숙소 가격, 숙소 이미지, 숙소 편의시설(연관 엔티티) |
| 채팅방      | 채팅 참여자                                    |
| 위시리스트    | 위시리스트 숙소(연관 엔티티)                      |
| 채팅 메시지   | -                                         |
| 후기       | -                                         |
| 예약       | -                                         |
| 결제       | -                                         |
| 지역 코드    | -                                         |
| 편의시설     | -                                         |

> JPA 엔티티 설계 시 같은 애그리거트에 속하는 엔티티는 객체 직접 참조로 연관 관계를 맺고, 다른 애그리거트에 속하는 엔티티는 PK(ID) 간접 참조로만 연결하도록 설계했습니다.

### 🌐 인프라

<img width="2304" height="1070" alt="Web App Reference Architecture" src="https://github.com/user-attachments/assets/165e33c0-02a4-4c54-90fc-3c31d4a5f920" />

### 🔄 CI/CD

<img width="1853" height="773" alt="CICD Architecture" src="https://github.com/user-attachments/assets/c8b78e89-eec1-40f4-bf71-b6db840bc24c" />

### 💼 주요 비즈니스 로직

<details>
<summary><b>🔐 인증 플로우</b></summary>
<img width="930" height="2183" alt="image" src="https://github.com/user-attachments/assets/ad5f3442-1d35-4c33-8700-cb3ebf2af977" />
</details>

<details>
<summary><b>📅 예약 플로우</b></summary>
<img width="879" height="1449" alt="image" src="https://github.com/user-attachments/assets/6503d777-c301-431c-8456-4e1e412fed1c" />
</details>

<details>
<summary><b>💳 결제 플로우</b></summary>
<img width="985" height="1890" alt="image" src="https://github.com/user-attachments/assets/9688b31d-ed75-42b2-8e3d-76dd9c4f6af3" />
</details>

<details>
<summary><b>💬 채팅 플로우</b></summary>
  
#### 채팅 요청 및 응답
<img width="1120" height="2570" alt="image" src="https://github.com/user-attachments/assets/c9080f62-0e88-4008-8f1e-f49ec4fe2adf" />

#### 실시간 채팅
<img width="922" height="1840" alt="image" src="https://github.com/user-attachments/assets/2a9887e6-813f-43cb-8276-35ea93b31876" />

</details>
