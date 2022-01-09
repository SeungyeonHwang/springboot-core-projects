package jpabook.japshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue //Seq 자동 생성
    @Column(name = "member_id") //table column name
    private long id;

    private String username;

    @Embedded //내장 타입 포함(Value Type)
    private Address address;

    @OneToMany(mappedBy = "member") //거울, Order table의 member 필드에의해 매핑 되었다라는 의미(읽기 전용)
    private List<Order> orders = new ArrayList<>();
}
