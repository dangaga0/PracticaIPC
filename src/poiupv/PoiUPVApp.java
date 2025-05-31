/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poiupv;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.User;

/**
 *
 * @author jose
 */
public class PoiUPVApp extends Application {
    
    static private User user;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("vistas/MainScreen.fxml"));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        Scene scene = new Scene(root);
        stage.setTitle("Carta Nautica");
        stage.setScene(scene);
        stage.show();
                
        stage.setOnCloseRequest(event -> {
            if(user!=null) user.addSession(ProblemasController.getHits(), ProblemasController.getFails());
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    static public void setUser(User u){
        user = u;
    }
    
    static public User getUser(){
        return user;
    }
}
