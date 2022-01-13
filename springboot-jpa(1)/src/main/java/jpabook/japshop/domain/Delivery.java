package jpabook.japshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) //숫자로 들어감(ORDINAL-기본):다른 타입들어가면 망함
    private DeliveryStatus deliveryStatus; //배송 상태 [READY, COMP]
}
