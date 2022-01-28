package jpabook.japshop.api;

import jpabook.japshop.domain.Address;
import jpabook.japshop.domain.Order;
import jpabook.japshop.domain.OrderItem;
import jpabook.japshop.domain.OrderStatus;
import jpabook.japshop.repository.OrderRepository;
import jpabook.japshop.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

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
                .collect(Collectors.toList());
        return collect;
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
                    .collect(Collectors.toList());
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
