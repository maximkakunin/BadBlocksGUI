package com.kmprog.badblocksgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.InputStream;

public class BadBlocksGUIController {
    @FXML
    public GridPane grid;

    @FXML
    private TextField commandEdit;

    @FXML
    private TextArea resultText;

    public class CheckBlocksThread extends Thread {
        @Override
        public void run() {
            int[] err;
            int[] prevErr = {0, 0, 0};
            String inputstr;
            int row = 0;
            int col = 0;

            // Запускаем фоновый поток
            try {
                // Начинаем долгую задачу
                /* Create a new process */
                //ProcessBuilder pb = new ProcessBuilder("sh", "-c", "sudo badblocks -nsv /dev/sdd1");
                String cmd = commandEdit.getText();

                String password = System.getProperty("sudo_password");
                password = password == null ? "" : password;
                cmd = String.format(cmd, password);
                ProcessBuilder pb = new ProcessBuilder("sh", "-c", cmd);

                //pb.redirectErrorStream(true);
                /* Start the process */
                Process proc = pb.start();
                System.out.println("Process started!");

                InputStream out = proc.getErrorStream();

                byte[] buffer = new byte[255];
                while (proc.isAlive()) {
                    int no = out.available();
                    if (no > 0) {
                        int n = out.read(buffer, 0, Math.min(no, buffer.length));
                        // шаблон строки: %6.2f%% done, %s elapsed. (%d/%d/%d errors)
                        // пример:  0.18% done, 0:03 elapsed. (0/0/0 errors)
                        inputstr = new String(buffer, 0, n).replaceAll("\b","");
                        err = ParseElapsedStringRegex.parseElapsedString(inputstr);

                        if (err != null) {
                            //System.out.print("Errors:" + err[0] + "=" + err[1] + "=" + err[2]);
                            if ((err[0] != prevErr[0]) | (err[1] != prevErr[1]) | (err[2] != prevErr[2])) {
                                prevErr = err;
                                showBar(row, col, false);
                            }
                            else {
                                showBar(row, col, true);
                            }
                            // считаем сколько символов показали, бъем на строки по 100 символов
                            if (col == 100) {
                                row++;
                                col = 0;
                            }
                            else {
                                col++;
                            }
                        } else {
                            System.out.println(inputstr);
                        }
                        resultText.appendText(new String(inputstr));
                        Platform.runLater(() -> { // обновить в UI потоке
                            resultText.appendText("\n");
                        });
                    }

                    try {
                        Thread.sleep(1);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Process ended!");

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    @FXML
    protected void onCheckButtonClick() {

        System.out.println("onHelloButtonClick in");

        CheckBlocksThread thread = new CheckBlocksThread();
        thread.start();

        System.out.println("onHelloButtonClick out");
    }

    protected void showBar(int row, int col, boolean good) {
        // вывод в грид
        BackgroundFill fill = (good) ?
                new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY) :
                new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
        Platform.runLater(() -> { // обновить в UI потоке
            grid.add(new StackPane(){{setBackground(new Background(fill)); setMinSize(5,5);}}, col, row);
        });

        // вывод в консоль
        if (col == 0) { // новая строка
            System.out.println();
        }
        if (good) {
            System.out.print(".");
        }
        else {
            System.out.print("X");
        }
    }


}
