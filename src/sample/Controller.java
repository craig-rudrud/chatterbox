package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
        mode = -1;

        hostButton.setOnAction(event -> {
            if (mode != 0) {
                hostIP = receiverIPField.getText();
                hostPort = receiverPortField.getText();
                try {
                    serverSocket = new ServerSocket(Integer.valueOf(hostPort), 0, InetAddress.getByName(hostIP));
                    serverSocket.setSoTimeout(10000);

                    chatLogArea.appendText("Waiting for client on port " + serverSocket.getLocalPort() + "...\n");
                    server = serverSocket.accept();

                    chatLogArea.appendText("Just connected to " + server.getRemoteSocketAddress() + "\n");
                    mode = 0;

                    out = new DataOutputStream(server.getOutputStream());
                    in = new BufferedReader(new InputStreamReader(server.getInputStream()));

                    Server s = new Server();
                    s.start();
//                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        joinButton.setOnAction(event -> {
            if (mode != 1) {
                hostIP = receiverIPField.getText();
                hostPort = receiverPortField.getText();
                try {
                    chatLogArea.appendText("Connecting to " + hostIP + " on port " + hostPort + "...\n");
                    client = new Socket(InetAddress.getByName(hostIP), Integer.parseInt(hostPort));

                    chatLogArea.appendText("Just connected to " + client.getRemoteSocketAddress() + "\n");
                    mode = 1;

                    out = new DataOutputStream(client.getOutputStream());
                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    Client c = new Client();
                    c.start();
//                    client.close();
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
        String message = messageField.getText();
        if(!message.isEmpty()) {
            try {
                out.writeUTF("Other: " + message + "\n");
                chatLogArea.appendText("You: " + message + "\n");
                messageField.setText("");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Server extends Thread {
        public void run() {
            String input;
            try {
                while ((input = in.readLine()) != null) {
                    chatLogArea.appendText(input);
                }
            } catch (IOException e) {
                chatLogArea.appendText("Unable to get messages!");
            }
        }
    }

    class Client extends Thread {
        public void run() {
            String input;
            try {
                while ((input = in.readLine()) != null) {
                    chatLogArea.appendText(input);
                }
            } catch (IOException e) {
                chatLogArea.appendText("Unable to get messages!");
            }
        }
    }

    private BufferedReader in;
    private DataOutputStream out;
    private int mode;
    private ServerSocket serverSocket;
    private Socket client;
    private Socket server;
    private String clientIP;
    private String clientPort;
    private String hostIP;
    private String hostPort;
}