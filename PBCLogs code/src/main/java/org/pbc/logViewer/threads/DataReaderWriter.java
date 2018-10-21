package org.pbc.logViewer.threads;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.pbc.logViewer.controller.StatisticsController;
import org.pbc.logViewer.model.LogModel;
import org.pbc.logViewer.model.StatisticsModel;
import org.pbc.logViewer.network.Client;
import org.pbc.logViewer.utils.StringConstants;

import java.io.IOException;
import java.io.RandomAccessFile;

import static org.pbc.logViewer.launcher.Main.FILE_PATH;
import static org.pbc.logViewer.launcher.Main.REFRESH_INTERVAL;

public class DataReaderWriter implements Runnable {

    private boolean keepRunning = true;
    private long filePointer = 0;
    private final Label label;
    private String URL = StringConstants.EMPTY_STRING;
    private boolean isFirst = true;

    public DataReaderWriter(final Label label, final String url) {
        this.label = label;
        this.URL = url;
    }

    @Override
    public void run() {
        try {
            while (keepRunning) {
                try {
                    if (URL.contains("pointerLocation")) {
                        final LogModel data = Client.getInstance().setUrl(URL).setPointerLocation(filePointer).getLogData();
                        if (null != data) {
                            filePointer = data.getCurrentPointer();
                            final String line = data.getLogData();
                            if (isFirst && null != label) {
                                isFirst = false;
                            }
                            if (null != line && !line.isEmpty()) {
                                appendLine("\n\n" + line.trim());
                            }
                        }
                        Thread.sleep(REFRESH_INTERVAL);
                    } else {
                        final StatisticsModel statisticsModel = Client.getInstance().setUrl(URL).getStatisticsData();
                        if (null != statisticsModel) {
                            StatisticsController.getInstance().update(statisticsModel);
                            keepRunning = false;
                        }
                        Thread.sleep(2000);
                    }
                } catch (final InterruptedException ie) {
                    //No Action
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(final String message) {
        Platform.runLater(() -> label.setText(message));
    }

    private void appendLine(final String line) {
        Platform.runLater(() -> {
            if (URL.contains("pointerLocation")) {
                writeLog(line);
            } else {
                label.setText(line);
            }
        });

    }

    private void writeLog(final String line) {
        try {
            final RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "rw");
            raf.seek(FILE_PATH.length());
            raf.write(line.getBytes());
            raf.close();
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
}