package jpabook.japshop.service;

import jpabook.japshop.domain.item.Item;
import jpabook.japshop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //저장 안되기때문에 저장하고 싶은 것은 별도 @Transactional 붙여 준다
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId,  String name, int price, int stockQuantity) { //파라미터 많은 경우 updateDto 사용
        Item findItem = itemRepository.findOne(itemId); //영속 상태 -> 값변경 -> 더티체킹
        //변경 감지 (@Transactional -> commit -> flush(JPA): 더티체킹)

        //의미있는 변경이 행해져야한다, 단순 set을 깔면 안된다(실무) -> 변경지점이 엔티티로 간다(역추적 가능) setter 없이
//        findItem.change(price, name, stockQuantity);
//        findItem.addStock(...)
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
