package microservice.user_service.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "recovery_email")
    private String recoveryEmail;

    @Column(name = "last_old_password")
    private String lastOldPassword;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "client_id")
    private Long clientId;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
}
