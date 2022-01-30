package jpabook.japshop.repository;

import jpabook.japshop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring JPA 적용 후
 */
public interface MemberRepository extends JpaRepository<Member, Long> { //Type, PK Type

    //select m from Member m where m.name = ? 자동으로 짜준다(네이밍 룰에 의해) -> JPQL 자동 생성
    List<Member> findByName(String name);
}
