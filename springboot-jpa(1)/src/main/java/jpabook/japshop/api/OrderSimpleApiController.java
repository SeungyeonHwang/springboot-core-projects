package jpabook.japshop.api;

import jpabook.japshop.domain.Address;
import jpabook.japshop.domain.Order;
import jpabook.japshop.domain.OrderStatus;
import jpabook.japshop.repository.OrderRepository;
import jpabook.japshop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne[ManyToOne/OneToOne] 성능 최적화
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    /**
     * 엔티티 직접 노출에 의해 유지보수 헬이 된다. 엔티티에 의존 관계 생성 된다
     * Hibernate5Module써서 어느정도 해결했지만 그럼에도 불구하고 여러가지 문제가 발생한다
     * 뭔가 성능이 이상하고 느낌 안좋으면 양방향 연관관계 한쪽 JsonIgnore 했는지 확인하기
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch()); //주문 목록 전부(상호 참조에의한 무한루프 빠짐)
        for (Order order : all) {
            order.getMember().getName(); //proxy + 실제 Name -> Lazy 강제 초기화(끌고 온다), 그렇다고 Eager(직접 로딩) 이걸로 바꾸면 안됨
            order.getDelivery().getAddress();
        }
        return all;
    }

    /**
     * 값을 반환할때 절대적으로 Dto를 사용하여 감싸준 형태로 반환한다 -> 도메인의 노출을 막고 사양 변동에의 유연성을 가지기 위해
     * 불필요한 쿼리가 날아가는 성능 문제 있음
     *  Order -> SQL 1번 -> 주문2개(result row)
     *  N+1 문제 -> 1+N
     *  orders
     *  1(orders) + N(2) 쿼리가 추가 실행
     *  1 + 회원 N + 배송 N = 5번의 쿼리
     *  EAGER로 해도 소용 없음, 쓰면 안됨(쿼리 예측 불가)
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() { //Result로 한번 감싸야 하지만 그냥 진행
        //order 2개
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());

        //loop
        //2바퀴 + lazy 로딩 X 2 쿼리가(한개씩만 들고 오기 때문에)
        return orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    //Api 사양을 명확히 결정
    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) { //Dto에서 엔티티를 파라미터로 받는건 크게 문제되지 않는다(중요하지 않은데서 중요한거를 의존하는 것은)
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화 -> 영속성 컨텍스트가 MemberId를 가지고 찾는다 -> 없으면 쿼리 날린다
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }

}
