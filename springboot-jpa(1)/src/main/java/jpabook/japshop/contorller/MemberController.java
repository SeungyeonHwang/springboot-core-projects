package jpabook.japshop.contorller;

import jpabook.japshop.domain.Address;
import jpabook.japshop.domain.Member;
import jpabook.japshop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) { //Model : 화면의 값과 연동할 수 있는 값
        model.addAttribute("memberForm", new MemberForm()); //validation 위해서 빈껍데기
        return "members/createMemberFrom";
    }

    //MemberForm 사용 -> Form 의 NotEmpty 등의 Validation 사용 가능
    //화면 Validation, Domain Validation 다를 수 있음
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {


        //validation 다음 BindingResult안에 에러 실린다 -> Action 추가 가능
        //에러 있어도 Form의 내용은 그대로 유지됨
        if (result.hasErrors()) {
            return "members/createMemberFrom";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/"; //저장 되면 다시 재로딩되거나하면 안좋기 때문에 redirect 많이 쓴다
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers(); //뿌려줄때 실무에서는 Dto 사용해서 필요한 필드만 뽑는게 좋다, API에서는 엔티티를 절대 넘기면 안됨
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
