package jpabook.japshop.service;

import jpabook.japshop.domain.Member;
import jpabook.japshop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class) //Spring 엮어서 실행
@SpringBootTest //Integration Test, 없으면 컨테이너 실패(Autowired)
@Transactional //Rollback(commit 안한다)
public class MemberServiceTest {

    @Autowired MemberService memberService; //가장 간단한 형태
    @Autowired MemberRepository memberRepository;
    //@Autowired EntityManager em;

    @Test
    @Rollback(value = false) //-> 롤백 확인, DB확인 할 때
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        //em.flush(); //영속성에 있는 것을 DB에 반영(Commit), DB 강제 쿼리 날린다, Test이기 때문에 생략
        assertEquals(member, memberRepository.findOne(savedId)); //같은 Transaction -> 영속성 같고, id 같으면 같은 Entity 식별(select 발행X)
    }

    @Test(expected = IllegalStateException.class) //상정 Exception
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2); //예외 발생(동일 이름, IllegalStateException)

        //then
        fail("예외가 발생해야 한다."); //-> 여기까지 도달하면 안된다
    }
}