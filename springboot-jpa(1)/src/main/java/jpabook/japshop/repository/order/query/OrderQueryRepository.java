package jpabook.japshop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    //repository는 순수하게 엔티티 취득 할때 사용(핵심 비즈니스 로직),
    //쿼리는 그외 화면이나 API에 의존할 경우 분리 시켜준다(유지보수 위해서) 라이프 사이클 분리 할 수 있다

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); //2건
        //OrderItems 못넣었기 때문에 loop 시켜서 넣어준다
        result.forEach(o -> {
            //각각 1번 총 3번(결과적으로는1+2 -> N+1)
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); //각각 채워준다(쿼리 날려서)
            o.setOrderItems(orderItems);
        });
        return result;
    }


    public List<OrderQueryDto> findAllByDto_optimization() {
        //root 조회(Order(2))
        List<OrderQueryDto> result = findOrders();

        //Order 결과 에서 OrderItems Map추출
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        //Collection 데이터 채워 준다
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.japshop.repository.order.query.OrderItemQueryDto(" +
                                "oi.order.id," +
                                "i.name," +
                                "oi.orderPrice," +
                                "oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" + //OrderItem 입장에서 toOne 이기 때문에 Join
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class) //하나씩이 아니라 in Query로 들고 온다
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        //1:다 부분 쿼리 따로 짜준다
        return em.createQuery(
                        "select new jpabook.japshop.repository.order.query.OrderItemQueryDto(" +
                                "oi.order.id," +
                                "i.name," +
                                "oi.orderPrice," +
                                "oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" + //OrderItem 입장에서 toOne 이기 때문에 Join
                                " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.japshop.repository.order.query.OrderQueryDto(" +
                                "o.id," +
                                "m.name," +
                                "o.orderDate," +
                                "o.status," +
                                "d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                        "select new" +
                                " jpabook.japshop.repository.order.query.OrderFlatDto(" +
                                "o.id," +
                                "m.name," +
                                "o.orderDate," +
                                "o.status," +
                                "d.address," +
                                "i.name," +
                                "oi.orderPrice," +
                                "oi.count)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d" +
                                " join o.orderItems oi" +
                                " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
