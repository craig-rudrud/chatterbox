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
    private TextField serverIPField;
    @FXML
    private TextField serverPortField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mode = -1; // 0 = you are the server, 1 = you're joining a server

        // Establishing the server
        hostButton.setOnAction(event -> {
            if (mode != 0) {
                hostIP = serverIPField.getText();
                hostPort = serverPortField.getText();
                try {
                    serverSocket = new ServerSocket(Integer.valueOf(hostPort), 0, InetAddress.getByName(hostIP));
                    serverSocket.setSoTimeout(10000);

                    chatLogArea.appendText("Connection opened on port " + serverSocket.getLocalPort() + ". Waiting for client.\n");
                    server = serverSocket.accept(); // UI freezes here because accept() runs in a loop.

                    chatLogArea.appendText("Successfully connected to " + server.getRemoteSocketAddress() + ". Say hello!\n");
                    mode = 0;

                    startChat(server);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Joining a server
        joinButton.setOnAction(event -> {
            if (mode != 1) {
                hostIP = serverIPField.getText();
                hostPort = serverPortField.getText();
                try {
                    chatLogArea.appendText("Attempting to connect to " + hostIP + " on port " + hostPort + "...\n");
                    client = new Socket(InetAddress.getByName(hostIP), Integer.parseInt(hostPort));

                    chatLogArea.appendText("Successfully connected to " + client.getRemoteSocketAddress() + ". Say hello!\n");
                    mode = 1;

                    startChat(client);
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

    void disconnect() {
        try {
            out.writeUTF("Other has disconnected.");
            chatLogArea.appendText("You have disconnected.\n");
            messageField.setText("");
            out.flush();
            if (mode == 0) {
                server.close();
            }
            else if (mode == 1) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startChat(Socket socket) throws IOException {
        out = new DataOutputStream(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ChatLogUpdater chat = new ChatLogUpdater();
        chat.start();
    }

    private void sendMessage() {
        if (mode > -1) {
            String message = messageField.getText();
            if (!message.isEmpty()) {
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
    }

    class ChatLogUpdater extends Thread {
        public void run() {
            String input;
            try {
                while ((input = in.readLine()) != null) {
                    chatLogArea.appendText(input + "\n");
                }
            } catch (IOException e) {
                chatLogArea.appendText("Unable to get messages!");
            }
        }
    }

    class getConnections extends Thread {

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