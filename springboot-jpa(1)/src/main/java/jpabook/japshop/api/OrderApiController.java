package jpabook.japshop.api;

import jpabook.japshop.domain.Address;
import jpabook.japshop.domain.Order;
import jpabook.japshop.domain.OrderItem;
import jpabook.japshop.domain.OrderStatus;
import jpabook.japshop.repository.OrderRepository;
import jpabook.japshop.repository.OrderSearch;
import jpabook.japshop.repository.order.query.OrderFlatDto;
import jpabook.japshop.repository.order.query.OrderItemQueryDto;
import jpabook.japshop.repository.order.query.OrderQueryDto;
import jpabook.japshop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * 주문 리스트 조회(+ 주문 아이템) : 엔티티 노출
     * 엔티티를 직접 노출하기 떄문에 NG
     */
    @GetMapping("api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());//검색 조건 없이 가져오면 다가져옴
        for (Order order : all) {
            //Lazy 강제 초기화(Hibernate 기본이 Lazy면 Proxy이기 때문에 안뿌린다 -> 강제 초기화 시켜줘야 한다)
            order.getMember().getName();
            order.getDelivery().getAddress();

            //Lazy 강제 초기화(nested)
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /**
     * 주문 리스트 조회(+ 주문 아이템) : Dto Wrapping
     * 연관관계안의 연관관계 까지 전부 Dto로 래핑, Dto에 엔티티 들어있으면 안된다
     * Collection(OrderItem) 쿼리 많이 나간다(성능 나쁨) -> 최적화 필요
     */
    @GetMapping("api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    /**
     * Collection 쿼리 1방으로 쿼리(fetch join + distinct)
     * 장점 : 쿼리가 한방
     * 단점 : **페이징 불가능 (1대 다 fetch join 시) , 1:다 이외는 상관 없음, 데이터 전송량 자체가 많아짐(중복)
     * ex) firstResult, setMaxResult ..페이징 조건 사용 불가 -> 메모리에서 페이징(대용량에서는 Out of Memory/매우 위험, 사용 불가)
     * *참고
     * 컬렉션 fetch join(1:다), OrderItems -> 1개만 사용가능 컬렉션 둘 이상에 페치조인 사용할 경우 데이터 부정합 가능성 있음
     * ===>
     * 1. 페이징 불가능
     * 2. 1:다 fetch join 1개만 사용 가능
     */
    @GetMapping("api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    /**
     * 컬렉션 조회 (페이징이 가능한 버전) -> 추천
     * ToOne관계는 fetch join 관계를 잡는게 좋다. / 안에있는 1:다 는 batch_fetch_size로 해결하는게 좋음
     *
     * @BatchSize(size = 100) -> 글로벌 하게 말고 특정 항목만 적용 하고 싶을 때 사용(Collection)
     * @BatchSize(size = 100) -> Collection 아닐 경우는 최상단 기재
     * 100 ~ 1000 개정도로 제한(오류 발생 가능성 있음) 부하량에 따라 다름(리소스), 메모리는 동일(최종)
     * application.yml -> default_batch_fetch_size: 100 : in query로 한방에 가져온다(컬렉션 관련 항목)
     * default_batch_fetch_size -> inquery 갯수 지정, 한번에 땡겨오는 in query size 지정
     * DB 입장에서 최적화 잘되는 쿼리다 쿼리 총3 -> 1:N:M -> 1:1:1
     * 이정도 최적화에서 원하는 성능 나온다. 이이상은 redis .. 등 Native Query
     * 장점 : 최적화된 DB만 추출 가능 하다(데이터 전송량 자체가 적음 / 중복X) / 단점 : 쿼리가 더 나가기는 함
     * 데이터가 만이 없으면 V3, 데이터가 많으면 V3.1이 나을 수 있음
     */
    @GetMapping("api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); //ToOne 만 fetch join -> 페이지에 영향X

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    /**
     * Dto로 직접 반환 하는 방법 -> ToOne(N:1, 1:1)관계를 먼저 조회하고 ToMany(1:N) 관계는 각각 별도로 처리
     * 각각 조회하기 때문에 별도 메소드 만들어준다
     */
    @GetMapping("api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    /**
     * 쿼리를 한방에 보내고 메모리 상에서 맵으로 돌려서 맞춰준다
     * ToOne관계를 먼저 조회하고 식별자 OrderId로 ToMany관계인 OrderItem을 한꺼번에 조회
     * 직접 쓰는만큼 select하는 양이 줄어드는 장점이 있다 -> 김영한이 가장 자주 사용하는 방법
     */
    @GetMapping("api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    /**
     * 중복을 포함해서 JSON 리턴 된다 상황에 따라 V5보다 느릴 수도 있다
     * 한방 쿼리로 해결 됨, 페이징 불가(성형된 데이터가 넘어오기 때문에 기준을 잡을 수가 없음)
     * Api 반환 스펙 바뀔경우 수동으로 중복제거 하는 로직 넣어줘야 한다(분해하고 조립하는 과정) -> 애플리케이션 추가 작업이 크다
     */
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        //flat을 가지고 OrderQueryDto로 바꾸는 과정(Memory)
        return flats.stream()
                //group by 묶을때 객체기 때문에 Equal, hashcode로 알려줘야한다
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems; //Dto 안에 Entity 있으면 안된다, 엔티티 노출된다. 완전하게 의존관계 끊지 못했다 -> Dto

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream() //엔티티 이기 때문에 또 돌린다
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {

        //필요한 데이터만 작성
        private String itemName; //상품 명
        private int orderPrice; //주문 가격
        private int count; //주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
