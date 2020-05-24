import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

public class AuthWindow extends Application {
    private FXMLMainController mainController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainFxml.fxml"));
        mainController=new FXMLMainController();
        fxmlLoader.setController(mainController);
        try{
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException ex){
            System.out.println("Ошибка загрузки GUI");
            ex.printStackTrace();
        }
        primaryStage.setTitle("LZ78 Decoder");
        primaryStage.show();
        mainController.start();
    }
}
