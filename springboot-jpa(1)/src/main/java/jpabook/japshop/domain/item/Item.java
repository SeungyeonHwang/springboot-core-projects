package jpabook.japshop.domain.item;

import jpabook.japshop.domain.OrderItem;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 상속관계 매핑 전략
 * 1. SINGLE_TABLE : 한 테이블에 다때려박는 형태
 * 2. TABLE_PER_CLASS : 각각의 테이블이 나오는 전략
 * 3. JOINED : 가장 정규화된 형태
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //상속관계 매핑 전략 선택 필요함
@DiscriminatorColumn(name = "dtype") //구분할때
@Getter @Setter
public abstract class Item {
    //추상 클래스로 만든다 Why -> 구현체 만들 것이기 때문
    //상속관계 매핑 필요, Album/Book/Movie

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    //단방향 -> 매핑 불필요
    private List<OrderItem> orderItems = new ArrayList<>();

    //고통속성
    private String name;
    private int price;
    private int stockQuantity;
}
