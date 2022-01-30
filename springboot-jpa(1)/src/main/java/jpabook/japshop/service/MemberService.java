package jpabook.japshop.service;

import jpabook.japshop.domain.Member;
import jpabook.japshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //데이터 변경은 기본 Transaction, Spring 라이브러리를 권장, 조회하는 곳에서는 readonly 좀더 성능 최적화(리소스 이득 있음)
@RequiredArgsConstructor //final 만가지고 생성자 만든다 -> 가장 효율적
//@AllArgsConstructor //생성자 자동생성
public class MemberService {

    //(1)Field Injection
    //@Autowired  -> 단점 : 용도에 따라 이곳을 못바꾼다(테스트 등.)
    //private MemberRepository memberRepository;

    //(2)Setter Injection
    //바로 주입X -> 장점 : Mock 을 직접 주입 가능(테스트 등(가짜 repository 등) 단점 -> 누군가가 바꿀 가능성 있음(바꿀일 없다)
//    private MemberRepository memberRepository;
//
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    //(3)Constructor Injection -> Recommend
    //Spring 로딩할때 injection, 변경 가능성 X, 테스트 케이스 할때 직접 주입해줘야 한다 Double Check 가능(의존성 명확)
    private final MemberRepository memberRepository; //변경 없기 때문에 final 좋다(컴파일시 값 체크 가능)

    //@Autowired -> 생성자가 하나만 있는 경우 생략 가능
    //@AllArgsConstructor -> 아래 코드 생략 가능
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원 가입
     */
    @Transactional //따로 써주면 이게 우선권 가져서 readOnly false 먹힌다
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 방지 Validate
        memberRepository.save(member);
        return member.getId(); //PK는 persist 하기전에 생성되므로 null safe, 뭐가 저장됬는지 알기위해 id return
    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION
        //실무에서는 동시 가입의 경우도 존재 하기 때문에 name을 DB의 유니크 조건으로 거는 것을 추천 한다
        List<Member> findMembers = memberRepository.findByName(member.getName()); //인터페이스에선 구현X
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    } //JPA에서 범용적 쿼리 다 구현되어 있다

    /**
     * 단건 회원 조회
     */
    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).get();
    }

    /**
     * 회원 수정(API)
     */
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findById(id).get(); //영속성 컨텍스트에서 찾아서 올려준다, member는 영속 상태(Transaction)
        member.setName(name); //영속상태 변화 -> 자동 Update Query(Dirty Checking) / 값을 바꾸고 엔티티 바뀌고 트랜잭션 끝나고 커밋되는 시점에서 변경 감지
        //영한님 정책 : 커맨드랑 쿼리를 분리한다 -> 여기서 member return 안함
    }
}
