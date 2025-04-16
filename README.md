![header](https://capsule-render.vercel.app/api?type=waving&height=300&color=87CEEB&text=bingo-jango&section=header&animation=fadeIn)

## 📑 개요

- **개발 기간** : 2025.03.20 ~ 2025.04.08

- **개발 인원** : 1명 (개인 프로젝트)

- **프로젝트 이름** : bingo-jango-v2

- **프로젝트 정보**

  2024년 2월 26일부터 4월 5일까지 진행된 내일배움캠프 스파르타코딩클럽 Kotlin/Spring 1기 최종 팀 프로젝트로 진행된 '빙고장고' 프로젝트를 **계승**하여,
코드를 리팩토링하고, 추가 기능을 개발하여 완성도를 높이는 것을 목표로 합니다.

- **프로젝트 설명**
  
  집, 셰어하우스 등 물리적으로 같은 공간에 거주하며 '같은 냉장고'를 사용하는 구성원들이 **냉장고 내 식품을 공동으로 관리**할 수 있도록 지원하는 웹 애플리케이션입니다. 구성원들은 식품의 잔여 수량을 관리하고, 유통 기한 정보를 기입하여 식품 낭비를 줄이고, 채팅/투표 기능을 통해 식품 관리 및 구매 결정을 공동으로 처리할 수 있습니다. 또한, 외형상 상품성이 떨어지지만 맛과 영양에 문제가 없는 '못난이 농산물' 등의 **유기농 식품을 농가와 직거래하여 추천 및 판매**하는 역할도 수행합니다.

## ⚒️ 기술 스택

- **프레임워크 :**
  - **Kotlin** - 간결하고 생산성 높은 JVM 기반 프로그래밍 언어
  - **Spring Boot** - 빠르고 쉽게 독립 실행형 Spring 기반 애플리케이션 개발 지원
  - **Gradle** - JVM 기반 프로젝트의 빌드, 테스트, 배포 자동화 도구
  - **WebFlux** - Spring의 비동기, 논블로킹 웹 프레임워크

- **데이터베이스 관련 :** 
  - **MySQL** - 관계형 데이터베이스
  - **Redis** - 인메모리 데이터 구조 저장소 (캐싱)
  - **JPA** - Java ORM 표준, 데이터베이스와 객체 간 매핑
  - **QueryDsl** - 복잡한 SQL 쿼리 작성

- **인증 및 인가 :**
  - **JJWT** - JWT (JSON Web Tokens) 생성 및 검증 라이브러리
  - **AOP** - 관심사를 모듈화하는 프레임워크

- **테스트 :**
  - **JUnit5** - 테스트 프레임워크
  - **TestContainers** - Docker 컨테이너를 활용한 통합 테스트 환경 구축
  - **Mockito** - Mock 객체를 생성하여 단위 테스트 격리

- **배포 및 인프라 :**
  - **Docker** - 컨테이너화 플랫폼
  - **AWS S3** - 서버 배포
  - **AWS ECS** - Docker 컨테이너 관리
  - **GitHub Actions** - GitHub 워크플로우 자동화 플랫폼

- **기타 도구 :**
  - **Git** - 분산 버전 관리 시스템
  - **Notion** - 문서화 도구

## ⚙️ 주요 기능

**1. 냉장고 식품 관리 동시성 제어 강화**  

다수의 사용자가 동시에 냉장고 식품에 접근할 때 데이터 정합성을 보장해야 합니다. 따라서, 동시성 제어를 위한 아래와 같은 4가지 후보군을 선정하였습니다.

- **Synchronized**
 
  특정 코드 블록을 임계 영역(=공유 자원에 접근할 수 있는 영역)으로 만들어, 다른 스레드의 접근을 차단하는 방법이지만, 단일 JVM 환경에서만 동기화를 보장하므로 서버 확장 시 동시성 문제를 해결할 수 없어서 후보군에서 우선적으로 제외하였습니다.

-  **Optimistic Lock**

    Lock 기법을 사용하지 않고, update 시 버전 정보를 활용하여 충돌 여부를 판단하는 방법으로, 버전이 일치할때까지 재시도를 수행하므로 충돌 빈도가 높아지면 성능이 저하되는 문제점이 있습니다. 본 프로젝트에서는 냉장고에 최대 100명의 멤버만 수용할 수 있기 때문에 대용량 트래픽이 예상되지는 않지만, 어느정도의 **음식 수량(quantity) 필드에 대한 빈번한 update**가 예상됩니다. 따라서, 버전 불일치가 발생하는 경우 **똑같은 로직을 재시도해야하므로 불필요한 DB 비용 및 부하가 발생**할 수 있다고 판단하여, Lock 기법을 통해 충돌 자체를 방지하는 Pessimistic Lock이 안정성 측면에서 더 나은 선택이라고 판단하여 후보군에서 제외하였습니다.

- **Redisson (Redis 분산 Lock)**

  Redisson 라이브러리를 활용해 Redis 내부에서 자체적으로 Pub/Sub 기능을 활용하여 Lock의 획득 및 해제 과정을 관리하는 방법입니다. 본 프로젝트는 기존의 모놀리식 아키텍처(v1)를 **MSA**(v2)로 전환하는 것을 전제로 하는데, 만약, 서비스 별로 분리된 DB를 사용하는 경우, 서비스 A가 자체 DB에 Lock을 걸어도, 서비스 B의 DB에는 Lock이 전파되지 않기 때문에, Multi-DB 환경에서는 Redis를 활용한 분산 락을 구현하여 동기화를 처리해야 합니다.

  하지만 본 프로젝트에서는 Master DB 1개와 Slave DB 3개로 구성된 **DB Replication 전략**을 사용합니다. 모든 서비스는 하나의 DB만을 바라보고 있고, 즉, 쓰기 작업은 모두 1개의 Master DB에서 처리하기 때문에 DB Lock 전략을 사용해도 큰 문제가 없고, 무엇보다도 Redis 분산 락을 사용하게 되면 불필요한 Redis 네트워크 호출이 발생하고, Redisson 라이브러리를 추가해야 하므로 오버엔지니어링이 될 수 있다고 판단하였습니다.

위와 같은 이유로 본 프로젝트에서는 **Pessimistic Lock**으로도 충분히 동시성을 제어할 수 있다고 판단하였습니다. 하지만, 이후 프로젝트 정책의 변화로 인해 '음식 소비' API에 대한 대용량 트래픽이 예상되는 경우에는 과도학 Lock 경합으로 인한 Connection Pool 고갈 가능성이 존재하므로 Redis 분산 락으로의 전환을 고려해볼 수 있습니다.

**2. 사용자 맞춤형 소비 패턴 분석 및 상품 추천**

빙고장고의 주 목적인 '농가 연계를 통한 저렴한 유기농 식품 구매 기회 제공'을 위해, 사용자의 **음식 패턴을 시간대별로 분석**하고, 소비가 많은 시간대에 해당 카테고리 상품을 추천하는 기능을 구현하였습니다.

사용자의 소비 기록을 저장하기 위해, '음식 소비' API 호출이 들어오면, 사용자별 음식(카테고리) 소비 횟수를 **Redis ZSet에 score로 저장**합니다. Redis ZSet은 score를 기준으로 자동 정렬되기 때문에, **가장 높은 score, 즉 해당 시간대에 사용자가 가장 많이 소비한 카테고리를 추출**할 수 있습니다. 해당 카테고리에 포함된 상품(Product) 중 사용자의 구매 이력이 없는 상품을 랜덤하게 추천하는데, 사용자의 음식 소비 이력이 없는 경우에는, 다른 사용자의 구매 빈도가 높은 상위 10개의 상품 중 랜덤하게 추천합니다.

**3. 대용량 트래픽 대비 결제 대기열 시스템 구축**

본 프로젝트의 웹 애플리케이션에서는 농가 직거래를 통해 저렴하고 희소성 있는 농작물을 제공하므로, 인기 특가 상품의 경우 사용자의 구매 요청이 집중될 수 있기 때문에, **대규모 트래픽**에 효율적으로 대응하고, **상품 재고 범위 내에서만 결제 권한을 부여**할 수 있는 대기열 시스템을 구축하였습니다.

사용자가 '구매하기' 버튼을 클릭하면 대기열 페이지로 리다이렉트하고, 해당 사용자를 대기열에 등록합니다. **대기열은 Redis ZSet으로 관리**되며, 사용자의 id를 value, **현재 시간을 score로 저장**하기 때문에, 사용자의 대기 순위를 쉽게 조회할 수 있습니다. 프론트엔드 서버는 3초 간격으로 사용자의 현재 순위를 조회하여 화면에 업데이트하고, 백엔드 서버는 스케줄러를 활용해 대기열 내 사용자 중 일부를 5초 간격으로 '결제 진행 큐'로 이동시켜 결제 권한을 부여합니다. **결제 권한을 얻은 사용자의 브라우저 쿠키로 JWT 토큰을 발급**하여, 사용자 실수 혹은 네트워크 문제로 인해 결제 페이지를 이탈하더라도 10분 이내에 해당 페이지로 재접근 시 결제를 다시 진행할 수 있습니다.

※ 세부적인 기술 구현 내용은 다음 [포토폴리오 링크](https://drive.google.com/file/d/1lToKeLiujyB7tMOpk1Z578X8LWyX4zmQ/view?usp=sharing)에서 확인하실 수 있습니다.
