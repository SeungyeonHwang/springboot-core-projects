package jpabook.japshop.contorller;

import jpabook.japshop.domain.item.Book;
import jpabook.japshop.domain.item.Item;
import jpabook.japshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "/items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {

        //실무에서는 파라미터 만들어서 파라미터로 넘기는게 더 나은 설계다(Setter 없이)
        //public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) { //... : 여러개 넘길수 있는
        //Order order = new Order();
        //실무에서는 Setter 날린다

        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId); //책만 수정한다고 가정

        //update하는데 BookEntity말고 BookForm 을 보낸다
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") String itemId, @ModelAttribute("form") BookForm form) { //updateItemForm object[form] 그대로 넘어 온다 @ModelAttribute

        Book book = new Book();
        book.setId(form.getId()); //id 조작의 가능성도 있기 때문에 유저가 아이템에 관한 권한이 있는지 체크 해줄 필요성이 있음(취약성)
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }
}
