package jpabook.japshop.service;

import jpabook.japshop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        Book book = em.find(Book.class, 1L);

        //TX
        book.setName("abcdefg");

        //변경 감지 == dirty checking JPA에서는 변경분을 자동으로 Update하는 쿼리를 날린다 e.g)order.cancel
        //TX commit
    }
}
