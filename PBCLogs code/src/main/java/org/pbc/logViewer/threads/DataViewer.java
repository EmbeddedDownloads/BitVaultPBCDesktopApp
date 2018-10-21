package org.pbc.logViewer.threads;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.pbc.logViewer.utils.MessageConstant;

import java.io.IOException;
import java.io.RandomAccessFile;

import static org.pbc.logViewer.launcher.AppConstants.MAX_LINES;
import static org.pbc.logViewer.launcher.Main.FILE_PATH;

public class DataViewer implements Runnable {

    private volatile boolean keepAlive = true;
    private final Label label;

    public DataViewer(final Label label) {
        this.label = label;
    }

    public void destroy() {
        this.keepAlive = false;
    }

    @Override
    public void run() {
        while (keepAlive) {
            try {
                Platform.runLater(() -> label.setText(tailFile()));
                if (label.getText().isEmpty()) {
                    Platform.runLater(() -> label.setText(MessageConstant.TXT_LOADING_LOG));
                }
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                //Do Nothing
            } catch (final Exception e) {
                Platform.runLater(() -> label.setText(MessageConstant.TXT_LOADING_LOG));
            }
        }
    }

    private String tailFile() {
        if (FILE_PATH.exists()) {
            try (RandomAccessFile fileHandler = new RandomAccessFile(FILE_PATH, "r")) {
                final long fileLength = fileHandler.length() - 1;
                final StringBuilder sb = new StringBuilder();
                int line = 0;

                for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                    fileHandler.seek(filePointer);
                    final int readByte = fileHandler.readByte();

                    if (readByte == 0xA) {
                        if (filePointer < fileLength) {
                            line = line + 1;
                        }
                    } else if (readByte == 0xD) {
                        if (filePointer < fileLength - 1) {
                            line = line + 1;
                        }
                    }
                    if (line >= MAX_LINES * 2) {
                        break;
                    }
                    sb.append((char) readByte);
                }
                return sb.reverse().toString();
            } catch (final IOException e) {
                e.printStackTrace();
                return MessageConstant.TXT_LOADING_LOG;
            }
        } else {
            return MessageConstant.TXT_LOADING_LOG;
        }
    }
}
