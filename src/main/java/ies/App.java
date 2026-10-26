package ies;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Carga el archivo FXML desde los recursos igual que en la guía oficial
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main_view.fxml"));
        Parent root = loader.load();
        
        stage.setTitle("Gestión de Incidencias - Scene Builder & FXML");
        stage.setScene(new Scene(root, 950, 680));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
