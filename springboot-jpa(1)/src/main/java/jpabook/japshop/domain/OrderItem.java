package jpabook.japshop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.japshop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) //주인
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 가격
    private int count; //주문 수량

    //protected OrderItem() {} //생성 로직이외의 객체 생성 방지용(protected) == @NoArgsConstructor(access = AccessLevel.PROTECTED)

    //==생성 메서드==// : 생성하는게 단순하지 않음(쿠폰, 할인 등등 일정하지 않을 경우가 있음)
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); //재고 감소
        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() {
        getItem().addStock(count); //재고 수량 원복
    }

    //==조회 로직==// : 주문 가격 * 주문 수량

    /**
     * 주문 상품 전체 가격조회
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
