package jpabook.japshop.domain.item;

import jpabook.japshop.domain.Category;
import jpabook.japshop.exception.NotEnoughStockException;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //상속관계 매핑 전략 선택 필요함, 하나의 테이블에 다 때려박는 것
@DiscriminatorColumn(name = "dtype") //구분할때
@Getter
@Setter
public abstract class Item {
    //추상 클래스로 만든다 Why -> 구현체 만들 것이기 때문
    //상속관계 매핑 필요, Album/Book/Movie

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    //공통속성
    private String name;
    private int price;
    private int stockQuantity;

    //단방향 -> 매핑 불필요
//    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToMany(mappedBy = "items") //거울
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    //도메인 주도 설계, 엔티티 자체에서 해결할 수 있는 것은 엔티티 안에 비즈니스 로직 넣는게 좋음(응집력, 객체지향)
    //엔티티 값을 변경할 일이 있으면 핵심 비즈니스 로직 가지고 변경해야 맞다(바깥에서 계산X) -> 가장 객체 지향적인 구조
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
