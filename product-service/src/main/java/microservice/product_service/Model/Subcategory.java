package microservice.product_service.Model;

import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "subcategory")
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship
    @ManyToOne
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "subcategory", cascade = CascadeType.ALL)
    private List<Product> products;



    public Subcategory(SubcategoryDTO subcategoryDTO) {
        this.name = subcategoryDTO.getName();

        if (subcategoryDTO.getCategoryId() != null) {
            Category category = new Category();
            category.setId(subcategoryDTO.getCategoryId());
            this.category = category;
        }
    }

    public void updateFromDTO(SubcategoryDTO subcategoryDTO) {
        this.name = subcategoryDTO.getName();
        this.updatedAt = LocalDateTime.now(); // Update the updated at time

        if (subcategoryDTO.getCategoryId() != null) {
            Category category = new Category();
            category.setId(subcategoryDTO.getCategoryId());
            this.category = category;
        }
    }


}
