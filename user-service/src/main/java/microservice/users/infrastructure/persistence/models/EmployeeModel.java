package microservice.users.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeModel extends UserModel {
    @Column(name = "department")
    private String department;

    @Column(name = "name")
    private String name;
}
