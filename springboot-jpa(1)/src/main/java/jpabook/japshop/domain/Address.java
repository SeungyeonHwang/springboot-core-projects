package jpabook.japshop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable //JPA 내장 타입
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;
}
