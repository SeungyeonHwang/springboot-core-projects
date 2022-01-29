package jpabook.japshop.repository.order.query;

import jpabook.japshop.domain.Address;
import jpabook.japshop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderFlatDto {

    //Order
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문 시간
    private Address address;
    private OrderStatus orderStatus;

    //Item
    private String itemName; //상품명

    //orderItem
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderFlatDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
