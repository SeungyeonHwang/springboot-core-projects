package springboot.springbootweb.repository;

import springboot.springbootweb.domain.Member;

import java.util.List;
import java.util.Optional;

/**
 * 기능 List
 */
//Optional -> Java8 : Null일 경우 감싸서 반환
public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);
    List<Member> findAll();
}
