package jpabook.japshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter //Setter 는 실무에서 열지 않는다(수정의 가능성이 있기 때문)
public class Member {

    @Id @GeneratedValue //Seq 자동 생성
    @Column(name = "member_id") //table column name
    private long id;

    private String name;

    @Embedded //내장 타입 포함(Value Type)
    private Address address;

    //Best Practice(필드에서 바로 초기화)
    // 1. null safe 2.Hibernate가 내장 컬렉션으로 바꾸는 (추적가능한) -> 외부 주입의 경우 매커니즘 위반 가능성 있다
    //@JsonIgnore //양방향 연관관계 있으면 한쪽은 ignore
    @OneToMany(mappedBy = "member") //거울, Order table의 member 필드에의해 매핑 되었다라는 의미(읽기 전용)
    private List<Order> orders = new ArrayList<>(); //이 컬렉션은 가급적 변경 하면 안됨, 있는 것을 그대로 쓰기
}
