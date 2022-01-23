package jpabook.japshop.service;

import jpabook.japshop.domain.Delivery;
import jpabook.japshop.domain.Member;
import jpabook.japshop.domain.Order;
import jpabook.japshop.domain.OrderItem;
import jpabook.japshop.domain.item.Item;
import jpabook.japshop.repository.ItemRepository;
import jpabook.japshop.repository.MemberRepository;
import jpabook.japshop.repository.OrderRepository;
import jpabook.japshop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티 조회 : 값을 꺼내기위해 Repository 필요
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); //회원 주소로 배송

        //주문상품 생성(static)
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count); //유지보수 위해서 여기 이외의 생성은 막아야 된다

        //주문 생성(static)
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        //이거 하나로 저장(cascade = CascadeType.ALL), 일괄 persist
        //order가 Delivery, orderItem 관리(라이프사이클 동일하게 관리 할때 의미가 있다[private 주인]), 다른곳에서(중요 엔티티) 참조하고 쓰면 이렇게 막쓰면 안됨 -> 별도 repository
        orderRepository.save(order);
        return order.getId(); //식별자 반환
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel(); //엔티티 안에서 비즈니스로직으로 엔티티 값 바꿔주면 JPA에서 더티체킹(변경 감지) 업데이트 해준다, 원래는 전부 쿼리 날려야 된다(데이터 변경 사항)
    }

    //검색
    public List<Order> findOrder(OrderSearch orderSearch) {
        return orderRepository.findAllByCriteria(orderSearch);
    }
}