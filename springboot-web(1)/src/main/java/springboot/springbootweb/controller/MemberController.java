package springboot.springbootweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import springboot.springbootweb.domain.Member;
import springboot.springbootweb.service.MemberService;

import java.util.List;

//Bean 관리(Annotation : 컴포넌트 스캔 방식)
//SpringbootWebApplication 하위 클래스만 컴포넌트 스캔함(패키지 단위)
//싱글톤 등록(하나만 등록 -> 공유)
@Controller
public class MemberController {
    //DI -> 여러군대서 쓰이기 때문에 하나만 생성하는게 좋음(스프링 컨테이너에 Bean 관리 되기 때문에)
    private final MemberService memberService;

    /**
     * (3)setter주입(단점 -> MemberService가 public으로 노출되어 있기 때문에 문제의 소지가 있다, 세팅이 되면 바꿀일이 없다)
     */
//    @Autowired
//    public void setMemberService(MemberService memberService) {
//        this.memberService = memberService;
//    }

    /**
     * (1)필드 주입(비추 -> 바꿀 수 있는 방법이 없다)
     */
//    @Autowired private MemberService memberService;

    /**
     * (2)**생성자 주입
     */
    @Autowired //컨테이너의 memberService 연결 해줌, bean에 등록되어있어야 작동
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
//        System.out.println("memberService = " + memberService.getClass()); -> Proxy 서버 올라감(가짜), DI의 장점
    }

    /**
     * 회원 가입(화면 전이)
     */
    @GetMapping("/members/new") //GET mapping 조회
    public String createdForm() {
        return "members/createMemberForm";
    }

    /**
     * 회원 가입(내부 처리)
     */
    @PostMapping("/members/new") //POST mapping 데이터 전달
    public String create(MemberForm form) {
        Member member = new Member();
        member.setName(form.getName());

        memberService.join(member);

        return "redirect:/";
    }

    /**
     * 회원 조회(All)
     */
    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
