package jpabook.japshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //order 는 혼란 유발 하기 때문에 관례상 orders
@Getter
@Setter //Setter 는 안여는 게좋음 실무는
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    //FK(업데이트하는 건 하나만 선택해야됨 양방향관계에서) -> 주인 정하기
    //연관관계 주인은 FK가 가까운 곳 (Order), 반대면 관리하기가 매우 힘듬, 성능 이슈 발생 가능성
    //주문한 회원에 대한 정보 맵핑, 그대로 둠
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    //order가 persist 되면 orderItemA,B,C 일괄 persist / delete 마찬가지
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //OneToOne : FK 어디에나 둬도 된다, But Access를 많이 하는 곳에 두는 걸 추천
    @JoinColumn(name = "delivery_id") //order 저장할때 delivery 같이 persist 원래는 각각(모든 Entity 각자 해줘야하는게 디폴트)
    private Delivery delivery;

    private LocalDateTime orderDate; //주문 시간 (LocalDateTime 자동 지원)

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문 상태 [ORDER, CANCEL]

    //==연관 편의 관계 메서드==// : 컨트롤하는쪽이 들고있는게 좋다(양방향 연관관계)
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    // 복잡한 생성이기 떄문에 있으면 좋다(연관관계, 주문 상태..초기 상태 편하게 설정 가능)
    // 생성 메서드에서 완결 가능하도록(유지보수 쉬워짐)
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) { //... : 여러개 넘길수 있는
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER); //처음 상태
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//

    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능 합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        //루프 돌면서 재고 원복
        for (OrderItem orderItem : orderItems) { //색칠 해주니까 this 생략 가능(강조 or 동일 이름일때 사용)
            orderItem.cancel(); //각각의 취소 요청
        }
    }

    //==조회 로직(계산)==//

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
