package jpabook.japshop.repository;

import jpabook.japshop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) { //JPA 저장하기 전까지  ID 존재하지 않기 때문(=새로 생성하는 객체)
            em.persist(item); //영속화(신규 등록)
        } else {
            //DB에 등록한 된 객체를 update 하는 느낌(수정 목적/준영속 상태의 엔티티듸 변경)
            //아이템을 찾고 param의 값으로 찾아온 값을 바꿔치기 한다 -> commit 될때 반영 된다
            //merge됬다고 해서 param이 영속성 상태로 변하지는 않는다, 쓸려면 반환 Item 사용
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
