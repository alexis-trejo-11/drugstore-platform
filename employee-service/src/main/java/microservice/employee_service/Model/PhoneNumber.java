package microservice.employee_service.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "phone_numbers")
@Data
@NoArgsConstructor
public class PhoneNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    @Column(name = "phone_company")
    private String phoneCompany;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
