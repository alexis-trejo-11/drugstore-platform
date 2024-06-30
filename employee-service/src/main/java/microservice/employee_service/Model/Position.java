package microservice.employee_service.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.employee_service.Model.enums.ClassificationWorkday;


import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position_name")
    private String positionName;

    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "classification_workday")
    private ClassificationWorkday classificationWorkday;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "position")
    private List<Employee> employees;

}
