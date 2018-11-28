package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button hostButton;
    @FXML
    private Button joinButton;
    @FXML
    private Button sendButton;
    @FXML
    private TextArea chatLogArea;
    @FXML
    private TextField messageField;
    @FXML
    private TextField receiverIPField;
    @FXML
    private TextField receiverPortField;
    @FXML
    private TextField senderIPField;
    @FXML
    private TextField senderPortField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
