package jpabook.japshop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    //Api 스펙에 의존하고 있다 -> 화면에 의존(tradeoff)
    //실무에서는 단순하지 않기 때문에 별도로 뽑아 놓는게 유지보수성이 좋다
    //Repository에 있으면 용도가 애매해진다
    //특수 목적에 의한 쿼리라는 것을 명시하기 위해 별도 Repository 작성
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                        //Dto에 바로 반환 할 수 없다 new 오퍼레이션 필요
                        //Address는 ValueType이기 때문에 바로 참조가능
                        "select new jpabook.japshop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
