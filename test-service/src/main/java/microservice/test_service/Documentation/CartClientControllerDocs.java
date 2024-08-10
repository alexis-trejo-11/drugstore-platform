package microservice.test_service.Documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public class CartClientControllerDocs {

    @Operation(summary = "Get cart by client ID", description = "Fetches the cart for the specified client ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart successfully fetched."),
            @ApiResponse(responseCode = "409", description = "No cart found for client ID.")
    })
    public void getCartByClientId() {
    }

    @Operation(summary = "Add product to cart", description = "Adds a product to the cart for the specified client ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products successfully added to the cart."),
            @ApiResponse(responseCode = "404", description = "Failed to add product to cart.")
    })
    public void addProductToCart() {
    }

    @Operation(summary = "Delete product from cart", description = "Deletes a product from the cart for the specified client ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully deleted from the cart."),
            @ApiResponse(responseCode = "400", description = "Failed to delete product from cart.")
    })
    public void deleteProductFromCart() {
    }

    @Operation(summary = "Purchase products from cart", description = "Processes the purchase of products in the cart for the specified client ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order successfully created. Payment will be validated soon."),
            @ApiResponse(responseCode = "404", description = "Failed to prepare client data for purchase.")
    })
    public void purchaseProductsFromCart() {
    }

    @Operation(summary = "Get afterward products by client ID", description = "Fetches products marked for 'afterwards' for the specified client ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Afterward items successfully fetched.")
    })
    public void getAfterwardProductsByClientId() {
    }

    @Operation(summary = "Move product to afterwards", description = "Moves a product to 'afterwards' for the specified client ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully moved to afterwards."),
            @ApiResponse(responseCode = "400", description = "Failed to move product to afterwards.")
    })
    public void moveProductToAfterwards() {
    }

    @Operation(summary = "Return product to cart", description = "Returns a product from 'afterwards' to the cart for the specified client ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully returned to cart."),
            @ApiResponse(responseCode = "400", description = "Failed to return product to cart.")
    })
    public void returnProductToCart() {
    }
}
