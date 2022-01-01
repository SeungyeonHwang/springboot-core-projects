package springboot.springbootweb.service;

import springboot.springbootweb.domain.Member;
import springboot.springbootweb.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

//비즈니스 로직(Repository -> 단순 DB CRUD / Service -> 비즈니스에 가까운 이름)
//@Service //@Component 포함
public class MemberService {

    private final MemberRepository memberRepository;

    //의존성 주입(직접 new 하지 않는다 : DI)
    //DI 중 생성자 주입
//    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입
     */
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증 -> 로직 들어갈때마다 Test Case 추가
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 한명 조회
     */
    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> { //Optional
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }
}
