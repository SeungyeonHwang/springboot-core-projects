package springboot.springbootweb.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import springboot.springbootweb.domain.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//실무에서는 BuildTool이랑 엮어서 테스트 케이스 통과 못하면 막아버린다
//테스트 결과는 순서에 상관없이 동작하게 설계(순서 비의존성) -> 테스트 종료후 데이터 클리어(저장소 or 공용 데이터 clear)
class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {
        //given
        Member member = new Member();
        member.setName("SpringTestMember");

        //when
        repository.save(member);

        //result
        Member result = repository.findById(member.getId()).get();//Optional
//        System.out.println("result = " + (result == member));
//        Assertions.assertEquals(member, result);
        assertThat(member).isEqualTo(result); //assertJ 이쪽이 직관적임
    }

    @Test
    public void findByName() {
        //given
        Member member1 = new Member();
        member1.setName("SpringTestMember1");
        repository.save(member1);

        Member member2 = new Member(); //shift F6 -> rename
        member2.setName("SpringTestMember2");
        repository.save(member2);

        //when
        Member result = repository.findByName("SpringTestMember1").get();

        //result
        assertThat(result).isEqualTo(member1);
    }

    @Test
    public void findAll() {
        //given
        Member member1 = new Member();
        member1.setName("SpringTestMember1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("SpringTestMember2");
        repository.save(member2);

        //when
        List<Member> result = repository.findAll();

        //result
        assertThat(result.size()).isEqualTo(2);
    }

}
