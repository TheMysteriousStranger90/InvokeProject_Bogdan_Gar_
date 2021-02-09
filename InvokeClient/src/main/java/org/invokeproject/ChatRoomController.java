package org.invokeproject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.invokeproject.data.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static org.invokeproject.ChatController.users;

public class ChatRoomController extends Thread implements Initializable {

    @FXML
    public ListView<String> listView = new ListView<>();
    @FXML
    private Button sendMessageButton;
    @FXML
    private Label labelNameUser;
    @FXML
    private TextArea messageField;
    @FXML
    private TextArea textArea;

    private BufferedReader inReader;
    private PrintWriter outWriter;
    private Socket socket;
    private Thread thread;
    ObservableList<String> chatOnline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelNameUser.setText("Пользователь: " + ChatController.userName);
        connectToServer();
    }

    public void connectToServer() {
        try {
            socket = new Socket(ChatController.address, ChatController.port);
            inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outWriter = new PrintWriter(socket.getOutputStream(), true);
            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnectFromServer() {
        outWriter.close();
        try {
            inReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageButtonOnAction(ActionEvent event) {
        sendMessage();
        for(User user : users) {
            System.out.println(user.userName);
        }
    }

    public void sendMessage() {
        String msg = messageField.getText();
        outWriter.println(ChatController.userName + ": " + msg + " " + "[" + initTimeMessage() + "]");
        textArea.appendText("[" + initTimeMessage() + "]" + "  " + ChatController.userName + ": " + msg + "\n");
        messageField.setText("");
        if(msg.equalsIgnoreCase("Выйти")) {
            disconnectFromServer();
            System.exit(0);
        }
    }

    public void exitButtonOnAction2(ActionEvent event) {
        //disconnectFromServer();
        Platform.exit();
        System.exit(0);
    }

    public String initTimeMessage() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ROOT);
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Platform.runLater(() -> {
                    for (User u : users){
                        chatOnline = FXCollections.observableArrayList(u.userName);
                        listView.setItems(chatOnline);
                    }
                });
                textArea.setEditable(false);
                String msg = inReader.readLine();
                String[] tokens = msg.split(" ");
                String cmd = tokens[0];
                StringBuilder fulmsg = new StringBuilder();
                for(int i = 1; i < tokens.length; i++) {
                    fulmsg.append(tokens[i]);
                }
                if (cmd.equalsIgnoreCase(ChatController.userName + ":")) {
                    continue;
                } else if(fulmsg.toString().equalsIgnoreCase("Пока")) {
                    break;
                }
                    textArea.appendText(msg + "\n");
                }
                inReader.close();
                outWriter.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
        }
    }
}



