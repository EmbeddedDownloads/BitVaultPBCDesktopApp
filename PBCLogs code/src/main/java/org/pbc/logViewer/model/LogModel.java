package org.pbc.logViewer.model;

public class LogModel {

    private long currentPointer;
    private String logData;

    public long getCurrentPointer() {
        return currentPointer;
    }

    public void setCurrentPointer(final long currentPointer) {
        this.currentPointer = currentPointer;
    }

    public String getLogData() {
        return logData;
    }

    public void setLogData(final String logData) {
        this.logData = logData;
    }
}
