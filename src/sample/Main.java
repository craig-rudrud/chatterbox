package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    /*
    Constructs the top-level JavaFX container holding all the UI elements to start the program.

    @author Craig Rudrud
    @param  primaryStage the top-level container holding all JavaFX elements
    @throws IOException on input/output error
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        primaryStage.setTitle("chatterbox");
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);
        primaryStage.setOnHidden(event -> {
            controller.disconnect();
            Platform.exit();
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
