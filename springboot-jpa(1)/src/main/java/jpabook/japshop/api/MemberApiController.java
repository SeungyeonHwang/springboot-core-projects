package jpabook.japshop.api;

import jpabook.japshop.domain.Member;
import jpabook.japshop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController //@Controller + @ResponseBody : 데이터 자체를 JSON, XML로 보내기위한 Annotation
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 회원 목록 조회(V1) (NG)
     * 엔티티의 정보가 전부 외부에 노출됨 @JsonIgnore넣어서 무시시킬 수 있지만 엔티티 회손 됨(클라이언트 별 다른 API 만들 때 영향)
     * 엔티티에 화면을 위한 로직이 추가됨 -> 의존관계형성되어 파탄 가능성
     * 엔티티 스펙 바뀌면 전부 바꾸지 않으면 안됨(API 스펙 변경)
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() { //RestController -> JSON으로 자동 변환
        return memberService.findMembers();
    }

    /**
     * 회원 목록 조회(V2) (OK)
     * 필요한 것만 노출하는 유연성 가능
     * 유지보수 쉬워진다
     */
    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect); //유연성을 위해 Result로 한번 감싸준다
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data; //data 필드
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }


    /**
     * 회원 등록(V1) - Entity를 RequestBody 사용하는 경우 (NG)
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { //@RequestBody :Json으로 온 Mapping을 객체에 매핑 해준다
        //Member와 같은 엔티티를 validation 목적으로 직접 변경되게 해선 안된다(Api의 스펙이 변경 되어서는 안된다) -> Dto 사용
        //엔티티를 외부에 노출해서도 안된다
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 회원 등록(V2) - Dto를 RequestBody 사용하는 경우 (OK)
     * 장점
     * 1. Api 스펙 유지
     * 2. 필요한 값을 매핑 시킬 수 있다.(전체를 보지 않아도 된다(Spec))
     * 3. Validation 필요한 만큼 fit 하게 넣을 수 있다
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 회원 업데이트
     * PUT -> 같은 값을 업데이트 해도 결과 동일(멱등)
     * 등록이랑 수정은 스펙이 대부분 다르기 때문에 Req, Res 따로 가져간다
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        //변경감지 패턴
        memberService.update(id, request.getName()); //업데이트 에서 끝낸다 or id 정도(유지보수성 증가)
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    //안에서만 사용할거면 inner로 작성
    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor //Dto에는 롬복 막써도 무관(실용적인 관점에서), 모든 필드에 대한 생성자(생략)
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty //Validation 스펙 맞게 꽂을 수 있다
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
