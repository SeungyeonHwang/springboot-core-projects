package springboot.springbootweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import springboot.springbootweb.service.MemberService;

//Bean 관리(Annotation : 컴포넌트 스캔 방식)
//SpringbootWebApplication 하위 클래스만 컴포넌트 스캔함(패키지 단위)
//싱글톤 등록(하나만 등록 -> 공유)
@Controller
public class MemberController {

    //DI -> 여러군대서 쓰이기 때문에 하나만 생성하는게 좋음(스프링 컨테이너에 Bean 관리 되기 때문에)
    private final MemberService memberService;

    @Autowired //컨테이너의 memberService 연결 해줌
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
}
