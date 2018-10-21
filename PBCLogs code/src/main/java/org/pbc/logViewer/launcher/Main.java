package org.pbc.logViewer.launcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.pbc.logViewer.controller.LogController;
import org.pbc.logViewer.controller.StatisticsController;
import org.pbc.logViewer.utils.MessageConstant;
import org.pbc.logViewer.utils.StringConstants;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.pbc.logViewer.launcher.AppConstants.*;
import static org.pbc.logViewer.network.Url.TARGET_NODE;

public class Main extends Application {

    public static File FILE_PATH;
    public static long REFRESH_INTERVAL = DEFAULT_REFRESH_INTERVAL;
    public static final ExecutorService executor = Executors.newFixedThreadPool(3);
    private Stage mainStage;

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) {
        try {
            this.mainStage = stage;
            final URL location = getClass().getResource("/main.fxml");
            final FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            final Parent root = fxmlLoader.load(location.openStream());

            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Scene scene = new Scene(new Group(), screenSize.getWidth(), screenSize.getHeight());
            scene.setRoot(root);
            mainStage.setScene(scene);
            mainStage.setTitle(MessageConstant.DEFAULT_TITLE);
            mainStage.show();
            showDialog(scene);

            mainStage.setOnCloseRequest(event -> {
                executor.shutdownNow();
                Platform.exit();
                System.exit(0);
            });
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog(final Scene scene) {
        // Create the custom dialog.
        final Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(MessageConstant.DIALOG_TITLE);
        dialog.setHeaderText(MessageConstant.DIALOG_HEADER_TEXT);
        dialog.initOwner(scene.getWindow());
        dialog.setResizable(false);
        // Set the button types.
        final ButtonType btnOk = new ButtonType(StringConstants.BUTTON_GO, ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(btnOk);

        final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 20));
        grid.setMinWidth(500);

        final ToggleGroup group = new ToggleGroup();
        final RadioButton rb1 = new RadioButton(MessageConstant.NODE1);
        rb1.setToggleGroup(group);
        rb1.setSelected(true);
        rb1.setUserData(NODE_1);

        final RadioButton rb2 = new RadioButton(MessageConstant.NODE2);
        rb2.setToggleGroup(group);
        rb2.setUserData(NODE_2);

        final RadioButton rb3 = new RadioButton(MessageConstant.NODE3);
        rb3.setToggleGroup(group);
        rb3.setUserData(NODE_3);

        final HBox hBox = new HBox();
        final Label label = new Label(MessageConstant.TXT_REFRESH_INTERVAL);

        final TextField textField = new TextField();
        textField.setPromptText(MessageConstant.TXT_INTERVAL_SECONDS);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() <= 3) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", StringConstants.EMPTY_STRING));
                }
            } else {
                textField.setText(oldValue);
            }
            if (textField.getText().equals(StringConstants.EMPTY_STRING)) {
                dialog.getDialogPane().lookupButton(btnOk).setDisable(true);
            } else {
                dialog.getDialogPane().lookupButton(btnOk).setDisable(false);
            }
        });

        hBox.getChildren().addAll(label, textField);
        hBox.setAlignment(Pos.CENTER);

        grid.add(rb1, 0, 0);
        grid.add(rb2, 0, 1);
        grid.add(rb3, 0, 2);
        grid.add(hBox, 0, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().lookupButton(btnOk).setDisable(true);

        group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (group.getSelectedToggle() != null) {
                TARGET_NODE = group.getSelectedToggle().getUserData().toString();
                switch (TARGET_NODE) {
                    case NODE_1:
                        setTitle(MessageConstant.TXT_NODE1_VIEWER);
                        break;
                    case NODE_2:
                        setTitle(MessageConstant.TXT_NODE2_VIEWER);
                        break;
                    case NODE_3:
                        setTitle(MessageConstant.TXT_NODE3_VIEWER);
                        break;
                }
            }
        });

        final Optional<Pair<String, String>> result = dialog.showAndWait();
        if (result.isPresent()) {
            //OK Pressed
            createLogFile();
            final String interval = textField.getText();
            REFRESH_INTERVAL = Long.parseLong(interval) * 1000;
            LogController.getInstance().loadLog();
            StatisticsController.getInstance().loadStatistics();
        } else {
            //Cancel
            System.exit(0);
        }
    }

    private void setTitle(final String title) {
        Platform.runLater(() -> mainStage.setTitle(title));
    }

    private void createLogFile() {
        final String fileName;
        switch (TARGET_NODE) {
            case NODE_1:
                fileName = "node1_reports.txt";
                break;
            case NODE_2:
                fileName = "node2_reports.txt";
                break;
            case NODE_3:
                fileName = "node3_reports.txt";
                break;
            default:
                fileName = "node1_reports.txt";
                break;
        }

        FILE_PATH = new File(System.getProperty("user.home"), fileName);
        System.out.println(FILE_PATH.getPath());
        try {
            if (FILE_PATH.exists()) {
                FILE_PATH.delete();
            }
            FILE_PATH.createNewFile();
            FILE_PATH.setWritable(true);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
