package jpabook.japshop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")
@Getter
@Setter
public class Book extends Item { //추상 클래스 상속(공통 속성)

    private String author;
    private String isbn;
}
