package jpabook.japshop.repository;

import jpabook.japshop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository //Bean 등록
@RequiredArgsConstructor //final로 생성자 자동 생성 -> 자동 주입
public class MemberRepository {

//    @PersistenceContext //JPA 표준 어노테이션
//    private EntityManager em; //em을 만들어서 주입해준다.(Spring)

//    팩토리 직접 주입하고 싶은 경우
//    @PersistenceUnit
//    private EntityManagerFactory emf;

    private final EntityManager em;

    //저장
    public void save(Member member) {
        //트랜잭션 Commit 되는 순간 insert 쿼리 날라간다, PK는 보장되어있다
        //Generate Value 전략에서는 insert문이 안나간다, commit 될때 flush 되면서 Insert날라간다
        em.persist(member);
    }

    //단건 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id); //Type, PK
    }

    //리스트 조회
    public List<Member> findAll() {
        //JPQL(기능적으로는 동일하나 약간의 차이, SQL 테이블 대상, JPQL 엔티티 대상으로 쿼리)
        return em.createQuery("select m from Member m", Member.class) //from의 대상이 Entity
                .getResultList();
    }

    //이름 조회
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class) //파라미터 바인딩
                .setParameter("name", name)
                .getResultList();
    }
}
