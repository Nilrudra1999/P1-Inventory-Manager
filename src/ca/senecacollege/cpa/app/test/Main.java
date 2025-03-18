/****************************************************************************************************
 * P1-Inventory-Manager - Application Launch
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.test;

import ca.senecacollege.cpa.app.controllers.InventoryAppController;
import ca.senecacollege.cpa.app.controllers.LoginController;
import ca.senecacollege.cpa.app.controllers.PartController;
import ca.senecacollege.cpa.app.controllers.ProductController;
import ca.senecacollege.cpa.app.utils.SceneName;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/****************************************************************************************************
 * Main Class: Starting point of the app, uses launch() and start() to load/start the app
 * Uses a SceneMap and LoaderMap class variables to hold references to all scenes and fxml loaders.
 * The Start() method is used to per-load all scenes before the "first" scene is displayed.
 ****************************************************************************************************/
public class Main extends Application {
    private static Map<SceneName, Scene> SceneMap = new HashMap<>(); // scene map
    private static Map<SceneName, FXMLLoader> LoaderMap = new HashMap<>(); // loader map


    // loads the FXML loader and scene objects using Dependency Injection into separate parallel HashMaps
    @Override public void start(Stage stage) throws IOException {
        String scene1 = "/ca/senecacollege/cpa/app/views/login-view.fxml";
        String scene2 = "/ca/senecacollege/cpa/app/views/inventoryApp-view.fxml";
        String scene3 = "/ca/senecacollege/cpa/app/views/addModPart-view.fxml";
        String scene4 = "/ca/senecacollege/cpa/app/views/addModProduct-view.fxml";
        LoaderMap.put(SceneName.LOGIN, new FXMLLoader(LoginController.class.getResource(scene1)));
        LoaderMap.put(SceneName.MAIN, new FXMLLoader(InventoryAppController.class.getResource(scene2)));
        LoaderMap.put(SceneName.PART, new FXMLLoader(PartController.class.getResource(scene3)));
        LoaderMap.put(SceneName.PRODUCT, new FXMLLoader(ProductController.class.getResource(scene4)));
        SceneMap.put(SceneName.LOGIN, new Scene(LoaderMap.get(SceneName.LOGIN).load()));
        SceneMap.put(SceneName.MAIN, new Scene(LoaderMap.get(SceneName.MAIN).load()));
        SceneMap.put(SceneName.PART, new Scene(LoaderMap.get(SceneName.PART).load()));
        SceneMap.put(SceneName.PRODUCT, new Scene(LoaderMap.get(SceneName.PRODUCT).load()));
        stage.setScene(SceneMap.get(SceneName.LOGIN)); // loading first scene
        stage.setTitle("Login");
        stage.show();
    }


    // Provides access to the App wide SceneMap and LoaderMap
    public static Map<SceneName, Scene> getScene() { return SceneMap; }
    public static Map<SceneName, FXMLLoader> getLoader() { return LoaderMap; }

    public static void main(String[] args) { launch(); }
}
