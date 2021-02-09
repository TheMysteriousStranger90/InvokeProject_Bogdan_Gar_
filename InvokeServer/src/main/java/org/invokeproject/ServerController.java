package org.invokeproject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.invokeproject.data.UserHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ServerController implements Runnable, Initializable {

    @FXML
    private Button exitButton;
    @FXML
    private Button startServerButton;
    @FXML
    private Button stopServerButton;
    @FXML
    private TextField enterNumberPortField;
    @FXML
    private TextField enterNumberUsersField;
    @FXML
    public TextArea textArea;
    @FXML
    private Label labelErrorMessage;

    private static ArrayList<UserHandler> users = new ArrayList<UserHandler>();
    private ServerSocket server;
    private Socket socket;
    private int serverPort;
    private static int maxUsers;
    private Thread thread;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void startServerButtonOnAction(ActionEvent event) throws InterruptedException {
        if (enterNumberPortField.getText().isEmpty() || enterNumberUsersField.getText().isEmpty()) {
            textArea.setText("Введите данные... ");
            labelErrorMessage.setText("Заполните пустые поля!");
        } else {
            serverPort = Integer.parseInt(enterNumberPortField.getText());
            maxUsers = Integer.parseInt(enterNumberUsersField.getText());
            try {
                server = new ServerSocket(serverPort);
                textArea.setText("Сервер запущен...");
                textArea.setEditable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            thread = new Thread(this);
            thread.start();
            startServerButton.setDisable(true);
            textArea.setText("Ожидание подключения клиентов...");
        }
    }

    public void stopServerButtonOnAction(ActionEvent event) {
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                users = null;
                thread = null;
            }
            startServerButton.setDisable(false);
            textArea.setText("");
            textArea.setText("Сервер остановлен...");
            labelErrorMessage.setText("");
            enterNumberPortField.clear();
            enterNumberUsersField.clear();
        }
    }

    public void writeToAllSockets(String input) {
        for (UserHandler userThread : users) {
            userThread.writeToServer(input);
        }
    }

    public void exitButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    public void numberFieldKeyTyped(KeyEvent event) {
        enterNumberPortField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                enterNumberPortField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        enterNumberUsersField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                enterNumberUsersField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        labelErrorMessage.setText("Вводите только цифры");
    }

    @Override
    public void run() {
        while (thread != null) {
            try {
                if (users.size() <= maxUsers)
                socket = server.accept();
                UserHandler userThread = new UserHandler(socket, users);
                users.add(userThread);
                userThread.start();
                Platform.runLater(() -> {
                    textArea.appendText("\nПодключен новый клиент...");
                });
                Thread.sleep(150);
            } catch (InterruptedException | IOException ex) {
                thread = null;
                try {
                    server.close();
                    textArea.appendText("\n...");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    users = null;
                }
            }
        }
        Platform.runLater(() -> {
            textArea.setText("Сервер остановлен!");
        });
    }
}
