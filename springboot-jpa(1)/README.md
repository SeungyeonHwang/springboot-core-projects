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