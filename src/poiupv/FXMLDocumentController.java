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
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.ColorPicker;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    
    // Point atributes
    private Color pointColor;
    private double pointSize;
    private Circle previewCircle;
    
    //Trasportador atributos
    private Color transColor;
    private double transSize;

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
    @FXML
    private Slider tamañoTrazo;
    @FXML
    private ColorPicker colorTrazo;
    @FXML
    private ToggleButton transportadorButton;
    
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
        
        //initialize the preview circle
        previewCircle = new Circle();
        card.getChildren().add(previewCircle);
        //default values
        pointColor = Color.RED;
        pointSize = 10;

        //trans
        transportador.styleProperty().bind(Bindings.createStringBinding(() -> 
        "-fx-pref-width: " + (int)tamañoTrazo.getValue()*100 + ";" +
        "-fx-pref-height: " + (int)tamañoTrazo.getValue()*100 + ";" +
        "-fx-content-display: graphic-only;",
        tamañoTrazo.valueProperty()
        ));
        pointColor = Color.RED;
        pointSize = 10;
        //default values
        
    }

    @FXML
    private void showPosition(MouseEvent e) {
        mousePosition.setText("sceneX: " + (int) e.getSceneX() + ", sceneY: " + (int) e.getSceneY() + "\n"
                + "         X: " + (int) e.getX() + ",          Y: " + (int) e.getY());
        if(pointButton.isSelected()){
                previewCircle.setCenterX(e.getX());
                previewCircle.setCenterY(e.getY());
                previewCircle.setRadius(tamañoTrazo.getValue());
                previewCircle.setFill(colorTrazo.getValue());
        }
        
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
    

    @FXML
    private void mapClicked(MouseEvent e) {
        if(e.getButton()==MouseButton.PRIMARY){
            if(pointButton.isSelected()){
                Color color = colorTrazo.getValue();
                pointColor = color;
                double size = tamañoTrazo.getValue();
                pointSize = size;
                Circle circle = new Circle();
                circle.setCenterX(e.getX());
                circle.setCenterY(e.getY());
                circle.setRadius(size);
                circle.setFill(color);

                card.getChildren().add(circle);
            }
            e.consume();
        }
    }

    @FXML
    private void iniciarSe(ActionEvent e) throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("vistas/IniciarSe.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Iniciar Sesión");
        stage.showAndWait();
        IniciarSeController iniCont = loader.getController();
    }

    @FXML
    private void registrarse(ActionEvent e) throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("vistas/Registro.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Registrarse");
        stage.showAndWait();
        RegistroController regCont = loader.getController();
    }

    @FXML
    private void pointSelected(ActionEvent event) {
        colorTrazo.setValue(pointColor);
        tamañoTrazo.setValue(pointSize);
        transportador.setVisible(false);
    }

    @FXML
    private void transportadorSelected(ActionEvent event) {
        transportador.setVisible(true);
        colorTrazo.setValue(transColor);
        tamañoTrazo.setValue(transSize);
    }

    @FXML
    private void mouseMovedMap(MouseEvent e) {
    }

    //Does not work dont know why
    @FXML
    private void transportadorDragged(MouseEvent e) {
        if(e.getButton()==MouseButton.PRIMARY){
            transportador.setTranslateX(e.getX()+ transportador.getLayoutX() -TransX);
            transportador.setTranslateY(e.getY()+ transportador.getLayoutX() -TransY);
        }
    }

    @FXML
    private void transportadorClicked(MouseEvent e) {
        if(e.getButton()==MouseButton.PRIMARY){
            TransX = e.getX();
            TransY = e.getY();
        }
        transColor = colorTrazo.getValue();
        transSize = tamañoTrazo.getValue();
    }

}
