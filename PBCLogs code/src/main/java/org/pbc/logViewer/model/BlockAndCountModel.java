package org.pbc.logViewer.model;

import java.util.List;

public class BlockAndCountModel {

    private long totalBlocks;
    private long savedCount;
    private long deletedCount;
    private List<BlockStatusModel> blocks;

    public long getTotalBlocks() {
        return totalBlocks;
    }

    public void setTotalBlocks(long totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    public long getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(long savedCount) {
        this.savedCount = savedCount;
    }

    public long getDeletedCount() {
        return deletedCount;
    }

    public void setDeletedCount(long deletedCount) {
        this.deletedCount = deletedCount;
    }

    public List<BlockStatusModel> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockStatusModel> blocks) {
        this.blocks = blocks;
    }
}
