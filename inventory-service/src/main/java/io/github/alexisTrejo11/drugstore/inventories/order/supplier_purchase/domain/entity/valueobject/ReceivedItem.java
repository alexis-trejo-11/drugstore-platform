package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject;

public class ReceivedItem {
    protected String itemId;
    protected String batchNumber;
    protected Integer receivedQuantity;

    public ReceivedItem(String itemId, String batchNumber, Integer receivedQuantity) {
        this.itemId = itemId;
        this.batchNumber = batchNumber;
        this.receivedQuantity = receivedQuantity;
    }

    public String getItemId() {
        return itemId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public Integer getReceivedQuantity() {
        return receivedQuantity;
    }
}
