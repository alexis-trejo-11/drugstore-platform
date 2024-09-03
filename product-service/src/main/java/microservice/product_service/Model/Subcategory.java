package microservice.product_service.Model;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubCategoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubCategoryUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubcategoryDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "subcategories")
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

    @ManyToOne
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "subcategory", fetch = FetchType.LAZY)
    private List<Product> products;



    public Subcategory(SubCategoryInsertDTO subCategoryInsertDTO) {
        this.name = subCategoryInsertDTO.getName();
    }

    public void updateFromDTO(SubCategoryUpdateDTO subCategoryUpdateDTO) {
        this.name = subCategoryUpdateDTO.getName();
        this.updatedAt = LocalDateTime.now(); // Update the updated at time

    }


}
