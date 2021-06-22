package de.thkoeln.ra.team3.risc_thk_simulator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import de.thkoeln.ra.team3.risc_thk_simulator.guiTemplates.PrimaryController;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static PrimaryController controller;
    
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 1160, 720);
        
        stage.setScene(scene);
        stage.show();
        controller.updateRegister();
        controller.genMemoryView();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        controller = new PrimaryController();
        fxmlLoader.setController(controller);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}