package jpabook.japshop.domain;

import jpabook.japshop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany //예제일 뿐, 실제에서는 사용하면 안됨, 1-* , *-1 로 풀어야 됨
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"), //중간테이블의 category_id
            inverseJoinColumns = @JoinColumn(name = "item_id") //아이템 FK
    ) //다-다는 JointTable 이 필요하다 -> 중간테이블 매핑, 객체는 가능하지만 RDB에서는 안된다(다대다)
    private List<Item> items = new ArrayList<>();

    //같은 엔티티내에서 다른 엔티티처럼 매핑
    //계층 구조(부모)/하나
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    //계층 구조(자식)/여러개
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관관계 편의 메소드==//
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
