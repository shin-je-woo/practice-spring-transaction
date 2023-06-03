package study.springtx.order;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    private Long Id;

    private String userName; // 정상, 예외, 잔고부족
    private String payStatus; // 대기, 완료
}
