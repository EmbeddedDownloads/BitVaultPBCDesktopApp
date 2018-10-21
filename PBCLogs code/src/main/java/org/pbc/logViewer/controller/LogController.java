package org.pbc.logViewer.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import org.pbc.logViewer.network.Url;
import org.pbc.logViewer.threads.DataReaderWriter;
import org.pbc.logViewer.threads.DataViewer;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

import static org.pbc.logViewer.launcher.Main.executor;

public class LogController implements Initializable {

    @FXML
    public Label labelLog;

    @FXML
    public ScrollPane scrollPane;

    private static LogController logController;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        scrollPane.setMinWidth(screenSize.getWidth());
        scrollPane.setMinHeight(screenSize.getHeight());
        scrollPane.setVvalue(1.0);
        logController = this;
    }

    public static LogController getInstance() {
        return logController;
    }

    public void loadLog() {
        executor.execute(new DataReaderWriter(labelLog, Url.getLogUrl()));
        executor.execute(new DataViewer(labelLog));
    }
}
