package springboot.springbootweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springboot.springbootweb.repository.MemberRepository;
import springboot.springbootweb.service.MemberService;

//@Service, @Repository @Autowired 등의 컴포넌트 스캔 방식이외에 직접 등록하는 방법
@Configuration
public class SpringConfig {

    //Spring Data JPA(가장 진화환 형태) -> 그냥 인젝션 받으면 알아서 구현체 등록됨
    private final MemberRepository memberRepository;

    @Autowired
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    //JPA
//    EntityManager em;

//    @Autowired
//    public SpringConfig(EntityManager em) {
//        this.em = em;
//    }

    //JDBC 구현체(Legacy)위한 DataSource
    //Spring에서 만들어줌(DB 접속 소스 보고)
//    private DataSource dataSource;
//
//    @Autowired
//    public SpringConfig(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository);
    }

    //다형성 활용의 예시, 스프링의 메리트 : 바꿔끼움으로써 코드 변화 최소화(일관성 유지)
    //개방-폐쇄 원칙(OCP, Open-Closed Principle)
    //스프링의 DI를 활용하면 기존 코드를 전혀 손대지 않고 설정만으로 구현 클래스를 변경할 수 있다
//    @Bean
//    public MemberRepository memberRepository() {
////        return new MemoryMemberRepository(); //Memory 구현체(임시)
////        return new JdbcMemberRepository(dataSource); //JDBC 구현체(Legacy)
////        return new JdbcTemplateMemberRepository(dataSource); //JDBC Template
////        return new JpaMemberRepository(em); //가장 효율적인 형태(JPA는 인터페이스 -> Hibernate 구현체(오픈 소스))
//    }

    //AOP
/*    @Bean
    public TimeTraceAop timeTraceAop() {
        return new TimeTraceAop();
    }*/
}
