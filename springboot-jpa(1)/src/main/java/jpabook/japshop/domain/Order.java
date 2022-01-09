package jpabook.japshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //order 는 혼란 유발 하기 때문에 관례상 orders
@Getter @Setter //Setter 는 안여는 게좋음 실무는
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    //FK(업데이트하는 건 하나만 선택해야됨 양방향관계에서) -> 주인 정하기
    //연관관계 주인은 FK가 가까운 곳 (Order), 반대면 관리하기가 매우 힘듬, 성능 이슈 발생 가능성
    //주문한 회원에 대한 정보 맵핑, 그대로 둠
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    //OneToOne : FK 어디에나 둬도 된다, But Access를 많이 하는 곳에 두는 걸 추천
    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문 시간 (LocalDateTime 자동 지원)

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문 상태 [ORDER, CANCEL]
}
