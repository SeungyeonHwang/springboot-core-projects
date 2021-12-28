package springboot.springbootweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.html.HTMLHeadElement;

@Controller
public class HelloController {

    //MVC
    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "Spring!!");
        return "hello"; //resources -> template -> hello(Thymeleaf)
    }

    @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam(value = "name") String name, Model model) {
        model.addAttribute("name", name); ///hello-mvc?name=〇〇
        return "hello-template";
    }

    //API
    @GetMapping("hello-string")
    @ResponseBody //response(http)에 값을 직접 넣음 -> 문자가 그대로 내려옴
    public String helloString(@RequestParam(value = "name") String name) {
        return "hello " + name; //"hello spring"
    }

    @GetMapping("hello-api")
    @ResponseBody //response(http)에 값을 직접 넣음 -> 문자가 그대로 내려옴(JSON이 기본임)
    public Hello helloAPi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }

    //클래스 안에서 호출 가능
    static class Hello {
        //JavaBean 규약 : getter/setter -> private 가져다 쓸수 있게 함(메서드로 접근)
        private String name;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}
