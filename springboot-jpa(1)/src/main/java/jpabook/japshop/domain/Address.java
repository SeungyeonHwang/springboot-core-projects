package jpabook.japshop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable //JPA 내장 타입
@Getter //값 타입은 기본적으로 Emutable하게 설계 되어야 한다, 변경 금지(Setter X)
public class Address {

    private String city;
    private String street;
    private String zipcode;

    //기본 생성자는 스펙상 필요(public or protected)
    protected Address() {
    }

    public Address(String city) {
        this.city = city;
    }

    //생성할때만 값 변경
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
