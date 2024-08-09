package microservice.employee_service.Model;

import at.backend.drugstore.microservice.common_models.DTOs.Employee.EmployeInsertDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.employee_service.Model.enums.Genre;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Genre genre;


    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "company_email")
    private String companyEmail;

    @Column(name = "company_phone")
    private String companyPhone;

    @Column(name = "hired_at")
    private LocalDateTime hiredAt;

    @Column(name = "fired_at")
    private LocalDateTime firedAt;

    private String address;

    private boolean isEmployeeActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @OneToOne(mappedBy = "employee")
    private PhoneNumber phoneNumber;

    public Employee(EmployeInsertDTO employeInsertDTO) {
        this.firstName = employeInsertDTO.getFirstName();
        this.lastName = employeInsertDTO.getLastName();
        this.birthDate = employeInsertDTO.getBirthDate();
        this.hiredAt = employeInsertDTO.getHiredAt();
        this.address = employeInsertDTO.getAddress();
        this.isEmployeeActive = true;
        this.genre = Genre.valueOf(employeInsertDTO.getGenre());
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
