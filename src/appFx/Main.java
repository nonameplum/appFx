package appFx;

import appFx.controllers.Controller;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_MODENA);

        URL location = getClass().getResource("views/main.fxml");

        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = (Parent) fxmlLoader.load(location.openStream());
        primaryStage.setTitle("Application Title");
        primaryStage.setScene(new Scene(root, 500, 500));

        primaryStage.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                ((Controller) fxmlLoader.getController()).setConfiguration(primaryStage);
            }
        });

        primaryStage.show();
        //ScenicView.show(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
