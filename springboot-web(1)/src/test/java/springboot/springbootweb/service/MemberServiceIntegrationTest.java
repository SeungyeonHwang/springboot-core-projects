package springboot.springbootweb.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springboot.springbootweb.domain.Member;
import springboot.springbootweb.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;

//통합 테스트
//Spring 엮어서 테스트
//테스트는 보통 테스트 서버의 데이터 사용 or 로컬에 있는 DB 사용
@SpringBootTest //Spring 실행
@Transactional //insert query 실행 -> Commit(auto Commit) -> DB반영, //Rollabck 해주기 때문에(하나하나) 테스트 DB에 값 안남는다 (반복 가능)
class MemberServiceIntegrationTest {

    //테스트 케이스는 간이로 가장 편한 방법으로 DI (필요한것 인젝션)
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository; //구현체는 SpringConfiguration 에서 올라온다

    @Test
//    @Commit // 저장됨
    void 회원가입() {
        //given
        Member member = new Member();
        member.setName("spring");

        //when
        Long saveId = memberService.join(member);

        //then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    public void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        //when
        memberService.join(member1);

        //then
        //(1)에러 발생 TEST
        IllegalStateException e = Assertions.assertThrows(
                IllegalStateException.class, () -> memberService.join(member2)
        );//() -> 로직 , 로직을 태울때

        //(1)에러 메세지 TEST
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");

//        try {
//            memberService.join(member2);
//            Assertions.fail();
//        } catch (IllegalStateException e) {
//            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
//        }
    }
}