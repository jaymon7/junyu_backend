# 와이어바알리 백엔드 과제.

## 지원자.

- 이름: 유준
- 이메일: zlsl7343@gmail.com
- 포지션: 백엔드
- 경력: 만 5년

## 과제 목표.

주어진 기능 요구사항에 따라 API를 개발.

- 계좌 등록/삭제 API 개발
- 계좌 입금/출금/이체 API 개발
  - 출금 시 일일 한도 100만원
  - 이체 시 일일 한도 300만원
  - 이체 시 수수료 1% 부과
- 계좌 송금/수신 내역 조회 API 개발
  - 최신순 정렬

## 설계 의도

복잡한 은행 도메인 시스템에서 비즈니스 로직을 중심에 두고,
외부 기술 의존성과의 결합을 최소화하여 도메인 로직이 애플리케이션이나 인프라 계층에 종속되지 않도록 하기 위해
헥사고날 아키텍처와 DDD를 기반으로 설계했습니다.

### 어댑터 레이어

- input은 통신(HTTP)별 패키지 구성 및 예외 처리 구성.
- output은 영속화 패키지 구성 및 DB 영속화를 위한 JPA 사용.

### 도메인 레이어
- 도메인 모델 정의 및 비즈니스 로직 구현.
  - 도메인 모델
    - Account(계좌)
    - AccountTransaction(계좌 거래 기록)
- 도메인에서 발생할 수 있는 예외 구성.
- ValueObject 정의를 통한 불변성 보장 및 도메인의 의미있는 값 명시 및 로직 응집. 
- 도메인 레이어에서는 롬복을 제외한 외부 의존성을 사용하지 않고, 도메인 모델을 정의.(개발 편의성을 위한 롬복 사용.)

### 어플리케이션 레이어

- 도메인 모델에 대해 데이터 영속화를 위한 Repository 인터페이스 정의.
- UseCase 정의 및 구현을 통해 비즈니스 로직을 수행.


### 동시성 보장.

은행 시스템은 데이터 정합성 보장이 필수적이므로, 높은 동시성을 보장할 수 있지만 실패할 경우 재시도 해야하는 낙관적 락이 아닌 
동시성이 낮아질 수 있지만 비관적 락을 사용하여 계좌의 돈의 정합성을 보장했습니다.

배타락 사용 로직: [AccountJpaRepository](./src/main/java/com/example/bankaccount/adapter/output/persistence/AccountJpaRepository.java)
```java
public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    /*
     * @Lock(LockModeType.PESSIMISTIC_WRITE): 쓰기 잠금.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
```

## 앱 실행 방법

[Docker Compose](./docker-compose.yml) 파일을 통해 도커를 실행합니다. (H2 인메모리 디비 사용으로 별도 DB 설정 필요 없음)
(Docker Client 설치 필요)
```bash
  docker-compose up -d
```

## 의존성 설명

[build.gradle](./build.gradle) 파일을 통해 의존성을 관리합니다.

```groovy
dependencies {
    /*
     * Spring Boot
     */
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    /*
     * Inmemory DB: 데이터베이스
     */
    implementation("com.h2database:h2:$h2Version")
    /*
     * Open API (Swagger): API 문서화 용도.
     */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiVersion")
    /*
     * Lombok: 개발 편의성을 위해 사용.
     */
    implementation("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testImplementation("com.h2database:h2:$h2Version")
    /*
     * Spring Boot Test: 테스트 용도.
     */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

## API 명세

OpenAPI 문서화 도구를 사용하여 API 문서 생성.

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- API 문서: [Open API Spec](./open-api-spec.json)


## 테스트 코드

레이어 별로 단위 및 통합 테스트를 작성했으며, 동시성 테스트를 진행했습니다.

- Adapter Layer: Controller에 대한 단위 테스트.
  - input
    - 요청 파라미터 검증 및 예외 처리 검증.
    - 기대하는 요청 응답값 검증.
  - output
    - JPA Entity 설계에 따른 영속화 검증.
- Application Layer: UseCase에 대한 통합 테스트. 동시성 테스트도 진행.
  - UseCase에 대한 단위 테스트.
  - 입금/출금/이체/계좌 등록/삭제에 대한 통합 테스트. (동시성 테스트도 포함)
- Domain Layer: 도메인 모델에 대한 단위 테스트.





