package springboot.springbootweb.repository;

import springboot.springbootweb.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository {

    //EntityManager로 모든걸 동작한다(connection 및 여러 정보 포함 되어 있음) -> Injection
    private final EntityManager em;

    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        em.persist(member); //영속화(DB 저장까지 다됨)
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id); //id,PK의 경우
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        //JPQL(객체 지향 쿼리) Member as m, 객체 자체를 select
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
