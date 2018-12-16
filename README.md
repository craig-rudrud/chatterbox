# chatterbox
A lightweight chat program that runs over TCP.

The executables to run are located in the out/artifacts/chatterbox/ directory and are called chatterbox_client.jar and chatterbox_server.jar. The files do the same thing but I created duplicates so that separate instances of the same program can be run (for testing).

How to use:
1. As a server, type in host IP and desired port number, then click the Host button. Then, chatterbox will use this port to allow one client to connect to the chat. During this time, the UI will freeze while it waits for a connection. NOTE: If testing out this program on your computer (with two instances of chatterbox open), use 'localhost' as the server IP and '5001' as the port number (without quotes).
3. As a client, type in the server IP and port number, then click the Join button. Then, chatterbox will attempt to make a connection to that server's port. If a connection is successful, the chat program will begin.
4. Use the message text field to type a message, then click the Send button or hit the enter/return key to send a message to the other user. The text area below will update once a message is sent/received.
5. To disconnect, either the client or server has to simply close the program.
