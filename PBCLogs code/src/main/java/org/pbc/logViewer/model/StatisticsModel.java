package org.pbc.logViewer.model;

public class StatisticsModel {

    private String status;
    private String message;
    private BlockAndCountModel resultSet;

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public BlockAndCountModel getResultSet() {
        return resultSet;
    }

    public void setResultSet(final BlockAndCountModel resultSet) {
        this.resultSet = resultSet;
    }
}
