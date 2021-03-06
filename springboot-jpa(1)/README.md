# Tips

---

- ./gradlew clean build : 지우고 새로 빌드
- build -> libs -> snapshot : 배포할때 쓰이는 jar 파일
  - 실행 : java -jar japshop-0.0.1-SNAPSHOT.jar
- 쿼리 파라미터 로그 남기기
  - org.hibernate.type: trace
- **엔티티 설계**
  - 엔티티 설계 단계에서는 양방향 관계보다 단방향 관계를 써라(필요없는 경우가 많음, 단순 Filter로 취득 가능하기 때문에 )
  - ManyToMany 는 쓰면 안된다 * - *, Mapping 데이터 둬서, 일대다 다대일로 풀어야 된다
  - db에는 소문자 + 언더스코어 사용
  - 연관관계 주인 : 일대다에서 다에 외래키 존재하는쪽이 다(연관관계 주인)
    - 주인쪽에 값을 변경해야 값이 변경됨
    - 반대편은 mappedBy 단순한 거울이다(조회용)
  - 단방향 표시(->)
  - 일대일 관계는 외래키 어디다 놔도 상관 없다
  - 상황에 따라서, 정합성의 중요도에따라서 외래키 걸어주는게 좋을때가 있다, 아니면 인덱스만 잘잡아줘도 된다
- Getter는 기본적으로 열어주는 것이 편하다, Setter의 경우 데이터의 변형 가능성이 있다 -> 어떻게 엔티티가 수정이 되는지 파악이 되지 않는 문제가 있다, 변경이 필요할떄 어디서 변경되는지 다 찾아야된다, 변경지점이 명확하게 , 변경용 비즈니스 메서드를 제공하는 게 좋다(유지보수를 위해), Setter를 다 닫는게 좋다(여기는 예제일 뿐)
- table_id -> 단순히id이면 찾기가 쉽지 않다, Join 불편하다(명확하지 않음)
- ⭐엔티티 설계 주의
  - Setter 열지 말기(변경 메소드 만들기)
  - 모든 연관관계는 지연로딩으로 설정!!(외워야함)
    - 즉시로딩 : 필요한 시점에 다른애들 다 로딩해버림(연관관계 있는)
      - 추적하기가 매우 까다롭다
      - JPQL 실행시 N+1 자주 발생
      - @XToOne 디폴트가 즉시로딩이므로 직접 지연로딩으로 설정해야 됨 -> Lazy로 다바꿔야됨
    - fetch join, 엔티티 그래프를 사용한다(최적화)
- 개발 패턴
  - 도메인에 비즈니스 로직 넣어서 Service에는 단순위임 : 도메인 모델 패턴
  - Service에 비즈니스 몰아넣는 스타일 : 트랜잭션 스크립트 패턴
  - 양립하는 경우도 있다
- 화면과 API는 컨트롤러를 분리하는게 좋다(공통요소가 많이 다르기 때문), 공통 에러 대응 등
- ⭐상항 지연로딩을 기본으로 하고, 성능 최적화가 꼭 필요한 경우에는 패치 조인(fetch join)을 사용!!
- 의존 관계는 한방향으로 흐르는게 좋다
  - Controller -> Service -> Repository..
- ⭐쿼리 방식 선택 권장 순서
  - 우선 엔티티를 DTO로 변환하는 방법을 선택
  - 필요하면 fetch join으로 성능을 최적화 -> 대부분의 성능이슈 해결
  - 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다
  - 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다
- ⭐엔티티 조회 권장 순서
  1. 엔티티조회방식으로우선접근
     1. 페치조인으로 쿼리 수를 최적화 
     2. 컬렉션 최적화
        1. 페이징 필요 hibernate.default_batch_fetch_size , @BatchSize 로 최적화
        2. 페이징 필요X 페치 조인 사용
  2. 엔티티 조회방식 으로해결이 안되면 DTO조회방식사용
  3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate
- ⭐OSIV(기본값 on)
  - JPA DB connection -> Transaction 시작할때 획득
  - 고객 서비스의 실시간 API -> 끄기, ADMIN 처럼 커넥션 많이 사용하지 않는 곳에서는 켠다(절대적이진 않음)
  - **on의 경우**
    - view에 return 할때까지 Connection 반환 안함
      - 영속성 컨텍스트를 길게 살려둔다(커넥션 물고 있다)(고객의 요청이 완전히 끝나 화면다 그려질떄까지)
      - 데이터 리스폰스 나가고 사라진다
      - 지연로딩을 가능하게 하는 옵션(커넥션 유지 필요하기 때문에)(장점)
    - 치명적 단점 : 커넥션을 너무 오래 물고 있기 때문에 실시간 트래픽 높으면 커넥션이 말라버린다 
      - ex) 외부 API에서 지연 생기면 그대로 리소스 먹혀 버리기때문에 장애로 이어진다
  - **off의 경우**
    - 장점 : 영속성 닫고, 커넥션을 짧은기간동안 유지(트랜잭션 범위)
      - 트래픽이 많은경우 커넥션을 유연하게 사용할 수 있다
    - 단점 : 끄면 지연로딩을 트랜잭션 안에서 처리 해야 한다(모든 지연로딩 코드(Controller))
    - 대응
      - 지연 로딩 처리를 Transaction 처리안에 작성 or fetch join 해서 다 끌고 오는 쿼리 발행
      - service안에다 쿼리용 서비스를 만든다 -> @Transaction read only true로 잡고 변환 쿼리 다 옮긴다(핵심 비즈니스로직/쿼리용 서비스 분리)
      - 커맨드와 쿼리 분리
        - 실무에서 OSIV를 끈 상태로 복잡성을 관리하는 추천 방법
        - 보통 복잡한 화면을 출력할때는 화면에 맞추어 성능을 최적화 하기 때문에
          - OrderService
            - OrderService : 핵심 비즈니스 로직(LifeCycle 길다)
            - OrderQueryService : 화면이나 API에 맞춘 서비스(주로 읽기 전용 트랜잭션 사용)(LifeCycle 짧다)
- 스프링 데이터 JPA는 기본적인 CRUD 기능이 모두 제공된다(일반적으로 상상할 수 있는)
