package microservice.inventory_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Inventory.*;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import microservice.inventory_service.Model.Inventory;
import microservice.inventory_service.Model.InventoryTransaction;

import java.util.ArrayList;
import java.util.List;

public class ModelTransform {

    public static InventoryDTO inventoryToReturnDTO(Inventory inventory, String productName) {
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(inventory.getId());
        inventoryDTO.setQuantity(inventory.getQuantity());
        inventoryDTO.setBatchNumber(inventory.getBatchNumber());
        inventoryDTO.setDateReceived(inventory.getExpirationDate());
        inventoryDTO.setLocation(inventory.getLocation());
        inventoryDTO.setExpirationDate(inventory.getExpirationDate());
        inventoryDTO.setProductName(productName);

        // Make Inventory Transaction Into DTOs
        List<InventoryTransactionDTO> inventoryTransactionDTOS = new ArrayList<>();
        for (var transaction : inventory.getTransactions()) {
            var inventoryTransactionReturnDTO = inventoryTransactionToReturnDTO(transaction);
            inventoryTransactionDTOS.add(inventoryTransactionReturnDTO);
        }
        inventoryDTO.setInventoryTransactionDTOS(inventoryTransactionDTOS);

        return inventoryDTO;
    }


    public static Inventory insertDtoTOInventory(InventoryInsertDTO inventoryInsertDTO) {
        Inventory inventory = new Inventory();
        inventory.setBatchNumber(inventoryInsertDTO.getBatchNumber());
        inventory.setDateReceived(inventoryInsertDTO.getExpirationDate());
        inventory.setLocation(inventoryInsertDTO.getLocation());

        return inventory;
    }

    public static InventoryTransactionDTO inventoryTransactionToReturnDTO(InventoryTransaction inventoryTransaction) {
        InventoryTransactionDTO inventoryTransactionDTO = new InventoryTransactionDTO();
        inventoryTransactionDTO.setId(inventoryTransaction.getId());
        inventoryTransactionDTO.setTransactionType(inventoryTransaction.getTransactionType());
        inventoryTransactionDTO.setEmployeeId(inventoryTransactionDTO.getEmployeeId());
        inventoryTransactionDTO.setDate(inventoryTransaction.getDate());
        inventoryTransactionDTO.setQuantity(inventoryTransaction.getQuantity());
        inventoryTransactionDTO.setEmployeeId(inventoryTransaction.getEmployeeId());


        return inventoryTransactionDTO;

    }

    public static ProductStockDTO inventoryToProductStockDTO(List<Inventory> inventories, ProductDTO productDTO) {
        ProductStockDTO productStockDTO = new ProductStockDTO();
        productStockDTO.setProductId(productDTO.getId());
        productStockDTO.setProductName(productDTO.getName());
        int totalStock = 0;

        List<InventoryStockDTO> inventoryStockDTOS = new ArrayList<>();
        for (var inventory : inventories) {
            InventoryStockDTO inventoryStockDTO = new InventoryStockDTO();
            inventoryStockDTO.setId(inventory.getId());
            inventoryStockDTO.setBatchNumber(inventory.getBatchNumber());
            inventoryStockDTO.setExpirationDate(inventory.getExpirationDate());
            inventoryStockDTO.setQuantity(inventory.getQuantity());
            totalStock += inventory.getQuantity();

            inventoryStockDTOS.add(inventoryStockDTO);
        }

        productStockDTO.setInventoryStockDTOS(inventoryStockDTOS);
        productStockDTO.setTotalStock(totalStock);

        return productStockDTO;
    }


}
