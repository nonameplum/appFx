package appFx;

import appFx.controllers.Controller;
import appFx.controllers.OrmLiteController;
import appFx.controllers.TestController;
import appFx.datasource.SqlLiteDBI;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.datafx.controller.ViewFactory;
import org.datafx.controller.context.ViewContext;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_MODENA);

        ViewContext<OrmLiteController> viewContext = ViewFactory.getInstance().createByController(OrmLiteController.class);
        viewContext.getApplicationContext().register("db", new SqlLiteDBI());

        primaryStage.setTitle("DataFX");
        primaryStage.setScene(new Scene((Parent) viewContext.getRootNode(), 500, 500));
        primaryStage.show();
    }
}
