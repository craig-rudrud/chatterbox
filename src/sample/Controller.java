package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.*;
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
        mode = -1;

        hostButton.setOnAction(event -> {
            if(mode != 0) {
                hostIP = receiverIPField.getText();
                hostPort = receiverPortField.getText();
                try {
                    serverSocket = new ServerSocket(Integer.valueOf(hostPort), 0, InetAddress.getByName(hostIP));
                    serverSocket.setSoTimeout(10000);

                    chatLogArea.appendText("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                    Socket server = serverSocket.accept();

                    chatLogArea.appendText("Just connected to " + server.getRemoteSocketAddress());
                    DataInputStream in = new DataInputStream(server.getInputStream());
                    mode = 0;

                    chatLogArea.appendText(in.readUTF());
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!");
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        joinButton.setOnAction(event -> {
            if(mode != 1) {
                hostIP = receiverIPField.getText();
                hostPort = receiverPortField.getText();
                try {
                    chatLogArea.appendText("Connecting to " + hostIP + " on port " + hostPort + "...\n");
                    Socket client = new Socket(InetAddress.getByName(hostIP), Integer.parseInt(hostPort));

                    chatLogArea.appendText("Just connected to " + client.getRemoteSocketAddress());
                    OutputStream outToServer = client.getOutputStream();
                    DataOutputStream out = new DataOutputStream(outToServer);
                    mode = 1;

                    out.writeUTF("Hello from " + client.getLocalSocketAddress());
                    InputStream inFromServer = client.getInputStream();
                    DataInputStream in = new DataInputStream(inFromServer);

                    chatLogArea.appendText("Server says " + in.readUTF());
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        sendButton.setOnAction(event -> sendMessage());

        messageField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
    }



    private void sendMessage() {
        if(!messageField.getText().isEmpty()) {
            chatLogArea.appendText(messageField.getText() + "\n");
            messageField.setText("");
        }
    }

    private int mode;
    private ServerSocket serverSocket;
    private String clientIP;
    private String clientPort;
    private String hostIP;
    private String hostPort;
}
