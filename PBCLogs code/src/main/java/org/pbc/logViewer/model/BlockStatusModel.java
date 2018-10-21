package org.pbc.logViewer.model;

import java.io.Serializable;

public class BlockStatusModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private final StringBuilder blockInfo = new StringBuilder();

    private String transactionId;
    private String tag;
    private String receiverAddress;
    private String status;
    private String createdAt;
    private String updatedAt;

    public BlockStatusModel() {
        // Default
    }

    public BlockStatusModel(final String tag, final String transactionId, final String status) {
        this.transactionId = transactionId;
        this.status = status;
        this.tag = tag;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BlockStatusModel setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public BlockStatusModel setTag(final String tag) {
        this.tag = tag;
        return this;
    }

    private String getReceiverAddress() {
        return receiverAddress;
    }

    public BlockStatusModel setReceiverAddress(final String receiverAddress) {
        this.receiverAddress = receiverAddress;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public BlockStatusModel setStatus(final String status) {
        this.status = status;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public BlockStatusModel setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    private String getUpdatedAt() {
        return updatedAt;
    }

    public BlockStatusModel setUpdatedAt(final String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public String toString() {
        blockInfo.setLength(0);
        blockInfo.append("Hash of TxnId: ")
                .append(this.getTransactionId())
                .append("\n\nReceiver Address: ")
                .append(this.getReceiverAddress())
                .append("\n\nStatus: ")
                .append(this.getStatus())
                .append("\n\nUpdated At: ")
                .append(this.getUpdatedAt())
                .append("\n\n=========================================\n\n");
        return blockInfo.toString();
    }
}
