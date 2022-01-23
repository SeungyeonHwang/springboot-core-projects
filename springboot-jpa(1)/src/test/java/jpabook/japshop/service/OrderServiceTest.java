package jpabook.japshop.service;

import jpabook.japshop.domain.Address;
import jpabook.japshop.domain.Member;
import jpabook.japshop.domain.Order;
import jpabook.japshop.domain.OrderStatus;
import jpabook.japshop.domain.item.Book;
import jpabook.japshop.domain.item.Item;
import jpabook.japshop.exception.NotEnoughStockException;
import jpabook.japshop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//좋은 테스트 : 아무런 의존 관계없이 순수하게 메소드만 테스트 하는 게 좋은 테스트
//예제를 위해 Integration Test(JPA)
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional //테스트에서 Transactional -> 각 테스트 종료후 자동 Rollback
public class OrderServiceTest {

    @Autowired
    EntityManager em; //테스트기 때문에 단순 persist
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember("회원1", new Address("서울", "강가", "123-123"));
        Item book = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 Order", OrderStatus.ORDER, getOrder.getStatus()); //주문의 상태
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size()); //주문 수
        assertEquals("주문한 가격은 [가격 * 수량] 이다.", 10000 * orderCount, getOrder.getTotalPrice()); //가격 계산 로직
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity()); //주문 수량 만큼의 재고 감소
    }

    @Test(expected = NotEnoughStockException.class) //상품 재고 없을때의 커스텀 Exception 사용
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember("회원1", new Address("서울", "강가", "123-123"));
        Item book = createBook("시골 JPA", 10000, 10);

        int orderCount = 11; //재고 + 1
        //when
        orderService.order(member.getId(), book.getId(), orderCount);

        //then
        fail("재고 수량 부족 예외가 발생해야 한다."); //여기까지 도달하면 안됨
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember("회원1", new Address("서울", "강가", "123-123"));
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when : 테스트 목적(대상)
        orderService.cancelOrder(orderId);

        //then : 재고 복구 검증
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL 이다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야한다.", 10, item.getStockQuantity());
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name, Address address) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        em.persist(member);
        return member;
    }
}