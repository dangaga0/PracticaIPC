/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poiupv;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author .
 */
public class FXMLDocumentController implements Initializable {

    // la variable zoomGroup se utiliza para dar soporte al zoom
    // el escalado se realiza sobre este nodo, al escalar el Group no mueve sus nodos
    private Group zoomGroup;
    private double TransX, TransY;

    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    
    @FXML
    private SplitPane splitPane;
    @FXML
    private Label mousePosition;
    @FXML
    private ToggleGroup paintingOptions;
    @FXML
    private Button transportador;
    @FXML
    private ToggleButton pointButton;
    @FXML
    private Pane card;

    void zoomIn(ActionEvent event) {
        //================================================
        // el incremento del zoom dependerá de los parametros del 
        // slider y del resultado esperado
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal += 0.1);
    }

    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + -0.1);
    }
    
    // esta funcion es invocada al cambiar el value del slider zoom_slider
    private void zoom(double scaleValue) {
        //===================================================
        //guardamos los valores del scroll antes del escalado
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        //===================================================
        // escalamos el zoomGroup en X e Y con el valor de entrada
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        //===================================================
        // recuperamos el valor del scroll antes del escalado
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Code from ChatGPT to avoid getting the focuss on any button when open
        Platform.runLater(() -> {
           map_scrollpane.requestFocus();
        });
        //==========================================================
        // inicializamos el slider y enlazamos con el zoom
        zoom_slider.setMin(0.2);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(1.0);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));

        //=========================================================================
        //Envuelva el contenido de scrollpane en un grupo para que 
        //ScrollPane vuelva a calcular las barras de desplazamiento tras el escalado
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);
        
        //initialize the A-lists

    }

    @FXML
    private void showPosition(MouseEvent event) {
        mousePosition.setText("sceneX: " + (int) event.getSceneX() + ", sceneY: " + (int) event.getSceneY() + "\n"
                + "         X: " + (int) event.getX() + ",          Y: " + (int) event.getY());
    }

    private void closeApp(ActionEvent event) {
        ((Stage) zoom_slider.getScene().getWindow()).close();
    }

    @FXML
    private void zoomScroll(ScrollEvent e) {
        
        //apply the scroll to the zoom
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + (e.getDeltaY() * 0.001));
        
        //avoid moving the map
         e.consume();
    }
    
    //Movement of trnsportador, it DOESNT work. The sensibility is insainly high 
    //if you move the mouse 1px it goes to x = 1000000

    @FXML
    private void moveTrasportador(MouseEvent e) {
        //transportador.setTranslateX(e.getX()+ transportador.getLayoutX() -TransX);
        //transportador.setTranslateY(e.getY()+ transportador.getLayoutX() -TransY);
    }

    private void TrasportadorClck(MouseEvent e) {
        TransX = e.getX();
        TransY = e.getY();
    }

    @FXML
    private void mapClicked(MouseEvent e) {
        if(e.getButton()==MouseButton.PRIMARY){
            if(pointButton.isSelected()){
                Button b = new Button();
                b.setLayoutX(e.getX());
                b.setLayoutY(e.getY());
                b.setStyle("-fx-background-color: #000000;");
                card.getChildren().add(b);
            }
            e.consume();
        }
    }

    @FXML
    private void iniciarSe(ActionEvent e) throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("/tarea2/vista/VistaPersona.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Iniciar Sesión");
        stage.showAndWait();
        //VistaPersonaController controlador2 = loader.getController();
    }

    @FXML
    private void registrarse(ActionEvent e) throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("/tarea2/vista/VistaPersona.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Registrarse");
        stage.showAndWait();
        //VistaPersonaController controlador2 = loader.getController();
    }

}
