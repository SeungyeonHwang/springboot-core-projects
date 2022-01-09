package jpabook.japshop;

import jpabook.japshop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext //Spring Boot 는 컨테이너 위에서 전부 움직임, Em 자동 주입 해준다
    private EntityManager em; //JPA 사용하기 때문에, Entity Manger 필요

    public Long save(Member member) { //Id 반환(커맨드성이기 때문에(SideEffect 가능성 존재))
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
