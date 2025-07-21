package microservice.ecommerce_cart_service.app.infrastructure.port.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_cart_service.app.application.command.afterwards.CreateAfterwardsCommand;
import microservice.ecommerce_cart_service.app.application.command.afterwards.RemoveAfterwardsCommand;
import microservice.ecommerce_cart_service.app.application.dto.AfterwardsSummary;
import microservice.ecommerce_cart_service.app.application.queries.GetCartAfterwardsQuery;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;
import microservice.ecommerce_cart_service.app.domain.port.in.usecase.AfterwardUseCase;
import microservice.ecommerce_cart_service.app.infrastructure.port.in.web.dto.CreateAfterwardsRequest;
import microservice.ecommerce_cart_service.app.infrastructure.port.in.web.dto.DeleteAfterwardsRequest;
import microservice.ecommerce_cart_service.app.shared.QueryPageData;
import microservice.ecommerce_cart_service.app.shared.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v2/api/carts/afterwards")
@RequiredArgsConstructor
public class AfterwardsController {
    private final AfterwardUseCase afterwardUseCase;

    @GetMapping("/{clientId}")
    public ResponseWrapper<AfterwardsSummary> getAfterwardProductsByClientId(
            HttpServletRequest request,
            @PathVariable String clientId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        GetCartAfterwardsQuery query = new GetCartAfterwardsQuery(
                CustomerId.from(clientId),
                new QueryPageData(offset, size, sort, sortDirection)
        );
        AfterwardsSummary afterwardsSummary = afterwardUseCase.getCartAfterwards(query);
        return ResponseWrapper.found(afterwardsSummary, "Afterwards Items");
    }

    @Operation(summary = "Move product to afterwards", description = "Move a specific product emptyCart cart to afterwards list for a client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully moved to afterwards"),
            @ApiResponse(responseCode = "400", description = "Failed to move product to afterwards")
    })
    @PostMapping("/move")
    public ResponseEntity<ResponseWrapper<Void>> moveProductToAfterwards(@RequestBody CreateAfterwardsRequest afterwardsRequest) {
        var command = CreateAfterwardsCommand.fromRequest(afterwardsRequest);
        afterwardUseCase.moveItemToAfterwards(command);
        return ResponseEntity.ok(ResponseWrapper.ok("Products", "Moved to Afterwards"));
    }

    @Operation(summary = "Return product to cart", description = "Return a specific product emptyCart afterwards list to the cart for a client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully returned to cart"),
            @ApiResponse(responseCode = "400", description = "Failed to return product to cart")
    })
    @PostMapping("/return")
    public ResponseEntity<ResponseWrapper<Void>> returnProductToCart(@Valid @RequestBody DeleteAfterwardsRequest afterwardsRequest) {
        var command = RemoveAfterwardsCommand.from(afterwardsRequest);
        afterwardUseCase.removeItemFromAfterwards(command);
        return ResponseEntity.ok(ResponseWrapper.ok("Products", "Restore"));
    }
}
