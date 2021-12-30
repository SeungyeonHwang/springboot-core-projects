package springboot.springbootweb.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import springboot.springbootweb.domain.Member;
import springboot.springbootweb.repository.MemoryMemberRepository;

import static org.assertj.core.api.Assertions.assertThat;

//테스트는 한글로 하는게 직관적
class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;

    //동작하기 전 실행
    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository); //MemberService의 MemoryMemberRepository 일치(일관성)
    }

    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
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

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}