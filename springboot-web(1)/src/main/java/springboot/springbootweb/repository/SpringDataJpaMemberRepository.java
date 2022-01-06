package springboot.springbootweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.springbootweb.domain.Member;

import java.util.Optional;

//SpringData JPA(가장 진화된 형태)
//<T, id(PK_Type)>, 인터페이스는 다중 상속 가능
public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {

    //select m from Member m where m.name = ? (JPQL로 변환)
    //인터페이스를 통한 기본적인 CRUD 가능
    //페이징 기능 제공, 복잡한 동적 퀘리는 Querydsl 사용
    @Override
    Optional<Member> findByName(String name);
}
