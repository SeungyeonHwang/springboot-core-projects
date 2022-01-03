package springboot.springbootweb.domain;

import javax.persistence.*;

@Entity //JPA 맵핑(관리)
public class Member {

    //Id 식별자(data 구분)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //DB가 id 자동생성 해주는 -> Identity 전략
    private Long id;

//    @Column(name = "username") //DB 컬럼 명
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
