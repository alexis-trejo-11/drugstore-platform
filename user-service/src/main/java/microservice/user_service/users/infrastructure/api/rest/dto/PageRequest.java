package microservice.user_service.users.infrastructure.api.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.user_service.utils.page.PageInput;
import microservice.user_service.utils.page.SortInput;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageRequest {
    @Min(value = 1, message = "Page number must be greater than or equal to 1")
    private int page;

    @Min(value = 1, message = "Size must be greater than or equal to 1")
    @Max(value = 100, message = "Size must be less than or equal to 100")
    private int size;

    private String sortBy = "createdAt";
    private String sortOrder = "asc";


    public PageInput toPageInput() {
        SortInput sortInput = new SortInput(
                sortBy,
                sortOrder.equalsIgnoreCase("asc")?
                        SortInput.SortDirection.ASC : SortInput.SortDirection.DESC
        );
        return new PageInput(page, size, sortInput);
    }
}
