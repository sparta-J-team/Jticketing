
# JTicketing README

## 프로젝트 소개

- **진행 기간**: `2024-11-22` ~ `2024-11-29`
- JTicketing은 인터파크 티켓팅을 벤치마킹하여 개발된
  **티켓팅 백엔드 서비스** 프로젝트입니다.
- 사용자는 콘서트에 대한 **검색 및 예매**를 할 수 있으며,
  관리자는 **콘서트 및 장소를 생성**할 수 있습니다.
- 주요 기능으로는 동시성 제어입니다.

<br>

---


## 팀원 구성

<div align="center">

<img width="646" alt="image" src="https://github.com/user-attachments/assets/7321ec27-4a74-4290-bf9a-7f7b38515e66">

</div>

<br>

---



<br>

---

## 2. 채택한 개발 기술과 브랜치 전략

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"><img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"><img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"><img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"><img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"><img src="https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white"><img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white">




### 개발 환경

- **협업 툴**: Slack, Notion
- **Back-end**: Java, Spring Boot, JPA
- **Database**: MySQL
- **Cache** : Redis
- **버전 및 이슈관리**: GitHub, GitHub Issues

### Spring Boot & JPA

- **Spring Boot**를 사용하여 RESTful API 설계와 서버 구축을 진행하였습니다.
- **JPA**로 데이터베이스와의 매핑을 통해 효율적인 데이터 관리 및 트랜잭션을 유지했습니다.

### JWT 인증

- 사용자 인증에 **JWT(Json Web Token)**를 적용하여 회원 로그인과 인증을 처리했습니다.

### 브랜치 전략

- Git-flow 전략을 활용하여 **main**, **develop**, **issue** 브랜치를 관리했습니다.
    - **main**: 배포 버전을 관리하는 브랜치
    - **develop**: 개발 단계에서 통합되는 브랜치
    - **issue**: 기능별로 작업 후 develop 브랜치에 병합

<br>

---

## 프로젝트 설계

### 와이어프레임

<img src="https://teamsparta.notion.site/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Ff989b27a-0933-4dc0-b632-935a8b1c5e47%2FGroup_7.png?table=block&id=17b2dc3e-f514-8109-9185-de5db963c031&spaceId=83c75a39-3aba-4ba4-a792-7aefe4b07895&width=2000&userId=&cache=v2" width="1200">

### ERD (Entity Relationship Diagram)

서비스의 데이터베이스 구조는 아래 ERD에 기반합니다. 각 엔터티와 관계를 통해 효율적인 데이터 관리를 고려했습니다.

<img src="https://teamsparta.notion.site/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fcc3ef161-423e-4dc1-b983-96b10feff309%2Fimage.png?table=block&id=17b2dc3e-f514-8142-8b77-d414eeaf7c9c&spaceId=83c75a39-3aba-4ba4-a792-7aefe4b07895&width=2000&userId=&cache=v2" width="1200">



<br>

---

## 프로젝트 구조

```
└─src
    ├─main
    │  ├─java
    │  │  └─com
    │  │      └─project
    │  │          └─jticketing
    │  │              ├─aop
    │  │              ├─config
    │  │              │  ├─redis
    │  │              │  └─security
    │  │              └─domain
    │  │                  ├─auth
    │  │                  │  ├─controller
    │  │                  │  ├─dto
    │  │                  │  │  ├─request
    │  │                  │  │  └─response
    │  │                  │  └─service
    │  │                  ├─common
    │  │                  │  └─entity
    │  │                  ├─concert
    │  │                  │  ├─controller
    │  │                  │  ├─dto
    │  │                  │  │  ├─request
    │  │                  │  │  └─response
    │  │                  │  ├─entity
    │  │                  │  ├─repository
    │  │                  │  └─service
    │  │                  ├─event
    │  │                  │  ├─entity
    │  │                  │  └─repository
    │  │                  ├─place
    │  │                  │  ├─controller
    │  │                  │  ├─dto
    │  │                  │  │  ├─request
    │  │                  │  │  └─response
    │  │                  │  ├─entity
    │  │                  │  ├─repository
    │  │                  │  └─service
    │  │                  ├─reservation
    │  │                  │  ├─controller
    │  │                  │  ├─dto
    │  │                  │  │  ├─request
    │  │                  │  │  └─response
    │  │                  │  ├─entity
    │  │                  │  ├─repository
    │  │                  │  └─service
    │  │                  └─user
    │  │                      ├─controller
    │  │                      ├─dto
    │  │                      │  ├─request
    │  │                      │  └─response
    │  │                      ├─entity
    │  │                      ├─enums
    │  │                      ├─repository
    │  │                      └─service
    │  └─resources
    └─test
        └─java
            └─com
                └─project
                    └─jticketing
                        └─domain
                            └─reservation
                                └─service
```

<br>

---

## 구현 목표

### 1️⃣  **필수 구현 기능**

- [ ]  **순간적으로 많은 요청이 쏟아질 수 있는 어플리케이션 기획 및 개발**
    - 순간적으로 요청이 쏟아지더라도 데이터 정합성이 완벽하게 지켜져야하는 비즈니스 필요
    - **위 비즈니스는 필수적으로 포함되어야하며, 이외의 요구사항은 자유롭게 기획에 포함**
    - **예시)**
        1. 콘서트 티켓팅 어플리케이션
        2. 선착순 할인쿠폰 이벤트 어플리케이션
- [ ]  **동시성 이슈를 검증할 수 있는 테스트 코드 작성**
    - 여러 `Thread` 가 동시에 동시성 이슈가 발생하는 메소드를 호출하는 시나리오를 토대로 테스트 코드를 작성
        - `ExecutorService` , `CyclicBarrier`  활용가능
        - `Coroutine`  활용 가능
    - **이 시점에 작성된 테스트는 실패해야 정상이다.**
- [ ]  **Redis 를 이용해 Lock 을 구현함으로써 동시성 이슈 제어**
    - `Lettuce` 를 이용해 Redis Lock 구현 **(`Redisson` 사용 금지!)**
    
    <aside>
    💡 **Redis Lock 을 구현할 때 고려해야할 것!**
      
    1) Lock 획득에 실패했을 때 어떻게 할 것인가?
    2) Redis 를 이용해 Lock 을 구현한 이유는 무엇일까?
    3) Redis 에서 Lock 을 걸때 Key 로 어떤 값을 사용했고, 왜 해당 Key 를 이용해 Lock 을 만들었을까?
    
    </aside>
    
    - **기본구조 Hint**
        - Redis Lock 을 처리하는 별도 `LockRedisRepository` , `LockService` 객체를 생성
        - 다른 비즈니스 로직에서는 `LockService`  만 의존하는 구조로 개발
- [ ]  **앞서 작성했던 테스트 코드를 통해 동시성 이슈에 대한 검증**

### 2️⃣  선택 구현 기능

- [ ]  **Lock 을 AOP 방식으로 적용할 수 있도록 코드 리팩토링**
    - `Spring AOP` 혹은 `Kotlin Trailing Lambda` 를 이용한 AOP 구조 개발 (Kotlin 적용 시)

### 3️⃣  심화 구현 기능

- [ ]  **Redis 대신 MySQL 을 이용해 Lock 구현**
    - MySQL 로 Lock 을 구현할 경우 장단점이 무엇인지 꼭! 확인하기

<aside>
💡 **Hint.**
 - JPA 비관적 Lock
 - MySQL Exclusive Lock

</aside>

- [ ]  **`Redisson` 을 이용한 Redis Lock 개발**
    - `Lettuce` 가 아니라 `Redisson` 을 사용한 이유를 설명할 수 있어야한다.
