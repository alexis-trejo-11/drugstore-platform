package at.backend.drugstore.microservice.common_models.DTOs.Supplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SupplierInsertDTO {
    private Long id;

    private String name;

    @JsonProperty("contact_info")
    private String contactInfo;

    private String address;

    private String phone;

    private String email;


}
