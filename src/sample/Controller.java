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

    /*
    Initializes the controller so that JavaFX elements can be manipulated.

    @author Craig Rudrud
    @param  location a URL location
    @param  resources a ResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
        Sets the mode which dictates who is the server and client.
        When no one is the client or server, the value of mode is -1.
        When the server is running, the value of mode is 0.
        When the client is connected to the server, the value of mode is 1.
         */
        mode = -1;

        // Creates a server upon being clicked.
        // Throws IOException if there's a problem trying to establish a connection.
        hostButton.setOnAction(event -> {
            if (mode != 0) {
                hostIP = serverIPField.getText();
                hostPort = serverPortField.getText();
                try {
                    // Generates a server socket which the client uses to connect to the server
                    serverSocket = new ServerSocket(Integer.valueOf(hostPort), 0, InetAddress.getByName(hostIP));
                    serverSocket.setSoTimeout(10000);

                    // The server waits for a client to connect. The UI freezes here because accept() runs in a loop.
                    chatLogArea.appendText("Connection opened on port " + serverSocket.getLocalPort() + ". Waiting for client.\n");
                    server = serverSocket.accept();

                    // The mode is set to 0 when the server successfully makes a connection to the client.
                    chatLogArea.appendText("Successfully connected to " + server.getRemoteSocketAddress() + ". Say hello!\n");
                    mode = 0;

                    // Starts a thread which continuously updates the chat log area.
                    startChat(server);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Joins a server upon being clicked.
        // Throws IOException if there's a problem trying to join a server
        joinButton.setOnAction(event -> {
            if (mode != 1) {
                hostIP = serverIPField.getText();
                hostPort = serverPortField.getText();
                try {
                    // Creates a new socket which is connected to a server's IP and port number
                    chatLogArea.appendText("Attempting to connect to " + hostIP + " on port " + hostPort + "...\n");
                    client = new Socket(InetAddress.getByName(hostIP), Integer.parseInt(hostPort));

                    // Sets the mode to 1 when the client successfully connects to the server.
                    chatLogArea.appendText("Successfully connected to " + client.getRemoteSocketAddress() + ". Say hello!\n");
                    mode = 1;

                    // Starts a thread which continuously updates the chat log area.
                    startChat(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Sends a message to the other user when the send button is clicked.
        sendButton.setOnAction(event -> sendMessage());

        // Sends a message to the other user when the enter/return key is pressed.
        messageField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
    }

    /*
    Disconnects the host from the other user.

    @author Craig Rudrud
    @throws IOException if there's an IO error, such as the server/client socket not existing or a data stream not
                        existing.
     */
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

    /*
    Starts the chat process by creating IO data streams to send/receive messages and creates a chat log updater.

    @author Craig Rudrud
    @param  socket the socket to read from/write to the data streams
    @throws IOException if the client/server socket isn't properly initialized
     */
    private void startChat(Socket socket) throws IOException {
        out = new DataOutputStream(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ChatLogUpdater chat = new ChatLogUpdater();
        chat.start();
    }

    /*
    Sends a message to the other user. Checks the mode to make sure the client/server connection has been initialized.

    @author Craig Rudrud
    @throws IOException if the data streams haven't been initialized.
     */
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

    /*
    Creates a thread which continuously updates the chat log area with new messages by reading from the input data
    stream.

    @author Craig Rudrud
    @throws IOException if there is an input error
     */
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