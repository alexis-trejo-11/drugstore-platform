package microservice.product_service.Model;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryDTO;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryUpdateDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategorySpecification> specifications = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Subcategory> subCategories;



    // Constructor to transform from DTOs to Model
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String name, MainCategory mainCategory) {
        this.name = name;
        this.mainCategory = mainCategory;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
