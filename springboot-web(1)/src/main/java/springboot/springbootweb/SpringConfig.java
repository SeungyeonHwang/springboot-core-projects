package springboot.springbootweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springboot.springbootweb.repository.MemberRepository;
import springboot.springbootweb.repository.MemoryMemberRepository;
import springboot.springbootweb.service.MemberService;

//@Service, @Repository @Autowired 등의 컴포넌트 스캔 방식이외에 직접 등록하는 방법
@Configuration
public class SpringConfig {

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository(); //구현체
    }
}
