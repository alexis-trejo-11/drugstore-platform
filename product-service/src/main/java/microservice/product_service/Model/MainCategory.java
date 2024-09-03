package microservice.product_service.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "main_categories")
public class MainCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "mainCategory")
    private List<Category> categories;

    @OneToMany(mappedBy = "mainCategory")
    private List<Subcategory> subcategories;

    @OneToMany(mappedBy = "mainCategory")
    private List<Product> products;

    public MainCategory(String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public MainCategory(Long id) {
        this.id = id;
    }
}
