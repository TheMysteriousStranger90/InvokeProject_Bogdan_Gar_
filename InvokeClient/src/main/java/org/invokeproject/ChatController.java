package org.invokeproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.invokeproject.data.User;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    private Button exitButton;
    @FXML
    private Button userButton;
    @FXML
    private Button resetButton;
    @FXML
    private Label errorMessageField;
    @FXML
    private TextField enterIPAddressField;
    @FXML
    private TextField enterNumberPortField;
    @FXML
    private TextField enterLoginField;
    @FXML
    private PasswordField enterPasswordField;

    public static String userName, userPassword, address;
    public static int port;
    public static ArrayList<User> loggedInUser = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<User>();
    private boolean connected = false;
    private double xOffset;
    private double yOffset;
    private static Scene scene;

    public void enterUserButtonOnAction(ActionEvent event) throws IOException {
        if (enterIPAddressField.getText().isEmpty() || enterNumberPortField.getText().isEmpty() || enterLoginField.getText().isEmpty() || enterPasswordField.getText().isEmpty()) {
            errorMessageField.setText("Введите IP-адрес сервера, порт, логин и пароль!");
        } else {
            userName = enterLoginField.getText();
            userPassword = enterPasswordField.getText();
            address = enterIPAddressField.getText();
            port = Integer.parseInt(enterNumberPortField.getText());
            if (checkUserName(userName)) {
                User tmpUser = new User(userName, userPassword);
                users.add(tmpUser);
                loggedInUser.add(tmpUser);
                connected = true;
                if (connected) {
                    changeWindow();
                } else {
                    errorMessageField.setText("Ошибка!");
                }
            }
        }
    }

    public void changeWindow() throws IOException {
        Stage primaryStage = new Stage();
        scene = new Scene(loadFXML("chatroomcontroller"));
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = primaryStage.getX() - event.getScreenX();
                yOffset = primaryStage.getY() - event.getScreenY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() + xOffset);
                primaryStage.setY(event.getScreenY() + yOffset);
            }
        });
        primaryStage.setTitle("InvokeChat");
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public boolean checkUserName(String userName) {
        for(User user : users) {
            if(user.userName.equalsIgnoreCase(userName)) {
                return false;
            }
        }
        return true;
    }

    public void exitButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    public void resetButtonOnAction(ActionEvent event) {
        enterIPAddressField.clear();
        enterNumberPortField.clear();
        enterLoginField.clear();
        enterPasswordField.clear();
        errorMessageField.setText("");
    }

    public void portFieldKeyTyped(KeyEvent event) {
            enterNumberPortField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    enterNumberPortField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
            errorMessageField.setText("Вводите только цифры!");
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainChat.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}




