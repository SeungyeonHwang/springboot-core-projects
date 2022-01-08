package springboot.springbootweb.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

//진짜 실하기전에 가짜를 거쳐서 실행(프록시)
@Aspect //AOP : 공통 관심사
@Component //Component 스캔으로 쓰기도 하지만 Config 직접 등록을 선호 함 -> 특이 케이스 니까
public class TimeTraceAop {

    //메소드 시간 측정(공통 관심사) -> 병목 찾을 수 있다.
    //메소드 호출 때마다 인서셉트 걸리는 거다, 어떤 조건이면 넘어가지 말라든지 여러가지 활용 가능하다
    @Around("execution(* springboot.springbootweb..*(..))") //타겟팅(원하는 조건), 클래스명 등등(보통 패키지 레벨로 많이 한다)
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("START : " + joinPoint.toString());
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("END : " + joinPoint.toString() + " " + timeMs + "ms");
        }
    }
}
