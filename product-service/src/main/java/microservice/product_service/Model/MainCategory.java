package microservice.product_service.Model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
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

    @OneToMany(mappedBy = "mainCategory", fetch = FetchType.LAZY)
    private List<Category> categories;

    @OneToMany(mappedBy = "mainCategory", fetch = FetchType.LAZY)
    private List<Subcategory> subcategories;

    @OneToMany(mappedBy = "mainCategory", fetch = FetchType.LAZY)
    private List<Product> products;

    public MainCategory(String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "MainCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", categories=" + categories +
                ", subcategories=" + subcategories +
                ", products=" + products +
                '}';
    }
}
