package org.pbc.logViewer.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import org.pbc.logViewer.model.BlockAndCountModel;
import org.pbc.logViewer.model.BlockStatusModel;
import org.pbc.logViewer.model.StatisticsModel;
import org.pbc.logViewer.network.Url;
import org.pbc.logViewer.threads.DataReaderWriter;
import org.pbc.logViewer.utils.MessageConstant;
import org.pbc.logViewer.utils.StringConstants;
import org.pbc.logViewer.utils.Utility;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.pbc.logViewer.launcher.Main.executor;

public class StatisticsController implements Initializable, EventHandler<ActionEvent> {

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public Label labelTotalBlocks, labelDeliveredBlocks, labelAvailableBlocks, labelBlocksInfo;

    @FXML
    public Hyperlink hyRefresh;

    @FXML
    public TextField txtSearch;

    @FXML
    public Button btnSearch;

    @FXML
    private RadioButton rbTxnId;

    @FXML
    private ToggleGroup radioToggle;

    private static StatisticsController statisticsController;

    private List<BlockStatusModel> blockStatusModelList;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        scrollPane.setMinWidth(screenSize.getWidth() - 50);
        scrollPane.setMinHeight(screenSize.getHeight() - 100);

        statisticsController = this;

        hyRefresh.setOnAction(this);
        btnSearch.setOnAction(this);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() <= 0) {
                btnSearch.setDisable(true);
            } else {
                btnSearch.setDisable(false);
            }
        });

        radioToggle.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if (null != radioToggle.getSelectedToggle()) {
                txtSearch.setPromptText(MessageConstant.TXT_ENTER + StringConstants.SPACE + radioToggle.getSelectedToggle().getUserData().toString());
            }
        });

    }

    public static StatisticsController getInstance() {
        return statisticsController;
    }

    public void loadStatistics() {
        executor.execute(new DataReaderWriter(labelBlocksInfo, Url.getStatisticsUrl(1)));
    }

    public void update(final StatisticsModel statisticsModel) {
        if (statisticsModel.getStatus().equals(StringConstants.ERR)) {
            updateError(statisticsModel.getMessage());
            return;
        }
        final BlockAndCountModel blockAndCountModel = statisticsModel.getResultSet();
        if (blockAndCountModel == null) {
            updateError(MessageConstant.PROBLEM_FACING_STATICS);
            return;
        }
        blockStatusModelList = blockAndCountModel.getBlocks();
        if (null == blockStatusModelList || blockStatusModelList.isEmpty()) {
            updateError(MessageConstant.NO_BLOCK);
            return;
        }

        Platform.runLater(() -> labelAvailableBlocks.setText(String.valueOf(blockAndCountModel.getSavedCount())));
        Platform.runLater(() -> labelDeliveredBlocks.setText(String.valueOf(blockAndCountModel.getDeletedCount())));
        Platform.runLater(() -> labelTotalBlocks.setText(String.valueOf(blockAndCountModel.getTotalBlocks())));

        showBlocksInfo(blockStatusModelList);
    }

    private void showBlocksInfo(final List<BlockStatusModel> blockStatusModelList) {
        if (null == blockStatusModelList || blockStatusModelList.isEmpty()) {
            Platform.runLater(() -> labelBlocksInfo.setText(MessageConstant.NO_SEARCH_RESULT));
            return;
        }
        Platform.runLater(() -> labelBlocksInfo.setText(StringConstants.EMPTY_STRING));
        for (int i = 0; i < blockStatusModelList.size(); i++) {
            final int finalI = i;
            Platform.runLater(() -> labelBlocksInfo.setText(
                    labelBlocksInfo.getText() + blockStatusModelList.get(finalI).toString()));
        }
    }

    private void updateError(final String errorMessage) {
        Platform.runLater(() -> labelBlocksInfo.setText(errorMessage));
    }

    @Override
    public void handle(final ActionEvent event) {
        if (event.getSource().equals(hyRefresh)) {
            labelBlocksInfo.setText(MessageConstant.TXT_LOADING_STATICS);
            loadStatistics();
        } else if (event.getSource().equals(btnSearch)) {
            final String id;
            if (radioToggle.getSelectedToggle().equals(rbTxnId)) {
                id = Utility.calculateHash(txtSearch.getText().getBytes(), "SHA-256");
            } else {
                id = txtSearch.getText();
            }
            if (null != blockStatusModelList && !blockStatusModelList.isEmpty()) {
                final List<BlockStatusModel> collectedStatusList = blockStatusModelList.stream().filter(blockStatusModel ->
                        blockStatusModel.getTransactionId().contains(id)).collect(Collectors.toList());
                showBlocksInfo(collectedStatusList);
            }
        }
    }
}
