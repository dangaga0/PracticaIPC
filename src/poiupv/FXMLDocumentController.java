/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poiupv;

import java.io.IOException;
import java.net.Socket;
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
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.plaf.RootPaneUI;
import model.Session;
import model.User;

/**
 *
 * @author .
 */
public class FXMLDocumentController implements Initializable {

    // la variable zoomGroup se utiliza para dar soporte al zoom
    // el escalado se realiza sobre este nodo, al escalar el Group no mueve sus nodos
    private Group zoomGroup;
    
    // Point atributes
    private Color pointColor;
    private double pointSize;
    private Circle previewCircle;
    
    //line atributes
    private Color lineColor;
    private double lineSize;
    private Line previewLine;
    
    //text atributes
    private Color textColor;
    private double textSize;
    private TextField previewText;
    
    //arc atrubites
    private Color arcColor;
    private double arcSize;
    private DoubleProperty arcRad;
    
    //Trasportador atributos
    private Color transColor;
    private double transSize;
    private double TransX, TransY;
    
    //ruler
    private double TransXR, TransYR;
    double rotationAngle;
    Point2D localBase;
    
    User user;
    static BooleanProperty isUserNull;
    
    @FXML
    private Circle previewArcCircle;
    @FXML
    private Circle previewArcRCircle;
    @FXML
    private Circle previewArcLCircle;
    @FXML
    private Arc previewArc;

    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    
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
    @FXML
    private ToggleButton eraserButton;
    @FXML
    private ToggleButton lineButton;
    @FXML
    private Pane previwePane;
    @FXML
    private StackPane stackPane;
    @FXML
    private Rectangle hitBox;
    @FXML
    private ToggleButton textButton;
    @FXML
    private Slider tamañoTrazoR;
    @FXML
    private ColorPicker colorTrazoR;
    @FXML
    private Label tamTex;
    @FXML
    private Label colText;
    @FXML
    private Label tamTexR;
    @FXML
    private Label colTextR;
    @FXML
    private ToggleButton arcButton;
    @FXML
    private ToggleButton rulerButton;
    @FXML
    private Button trashButton;
    @FXML
    private Button regla;
    @FXML
    private Button testButton;
    @FXML
    private Button profileButton;
    @FXML
    private ImageView profileImg;
    
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
        zoom_slider.setValue(.4);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));

        //=========================================================================
        //Envuelva el contenido de scrollpane en un grupo para que 
        //ScrollPane vuelva a calcular las barras de desplazamiento tras el escalado
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);
        
        //initialize the slider and color picker
        colorTrazo.disableProperty().bind(
            paintingOptions.selectedToggleProperty().isNull()
            .or(
                paintingOptions.selectedToggleProperty().isEqualTo(eraserButton)
            )
        );
        tamañoTrazo.disableProperty().bind(
            paintingOptions.selectedToggleProperty().isNull()
            .or(
                paintingOptions.selectedToggleProperty().isEqualTo(eraserButton)
            )
        );
        tamTex.styleProperty().bind(
            Bindings.when(paintingOptions.selectedToggleProperty().isNull()
            .or(
                paintingOptions.selectedToggleProperty().isEqualTo(eraserButton)
            ))
            .then("-fx-text-fill: #cccccc;")
            .otherwise("-fx-text-fill: black;")
        );
        colText.styleProperty().bind(
            Bindings.when(paintingOptions.selectedToggleProperty().isNull()
            .or(
                paintingOptions.selectedToggleProperty().isEqualTo(eraserButton)
            ))
            .then("-fx-text-fill: #cccccc;")
            .otherwise("-fx-text-fill: black;")
        );
        
        colorTrazoR.disableProperty().bind(Bindings.and(transportadorButton.selectedProperty().not(), rulerButton.selectedProperty().not()));
        tamañoTrazoR.disableProperty().bind(Bindings.and(transportadorButton.selectedProperty().not(), rulerButton.selectedProperty().not()));
        tamTexR.styleProperty().bind(
            Bindings.when(Bindings.and(transportadorButton.selectedProperty().not(), rulerButton.selectedProperty().not()))
            .then("-fx-text-fill: #cccccc;")
            .otherwise("-fx-text-fill: black;")
        );
        colTextR.styleProperty().bind(
            Bindings.when(Bindings.and(transportadorButton.selectedProperty().not(), rulerButton.selectedProperty().not()))
            .then("-fx-text-fill: #cccccc;")
            .otherwise("-fx-text-fill: black;")
        );
        
        //initialize the preview circle
        previewCircle = new Circle();
        previewCircle.visibleProperty().bind(pointButton.selectedProperty());
        previwePane.getChildren().add(previewCircle);
        //default values
        pointColor = Color.RED;
        pointSize = 10;

        //trans
        //Deepseek para la base del codigo del Binding
        transportador.styleProperty().bind(Bindings.createStringBinding(() -> {
            Color color = colorTrazoR.getValue();
            // Formato RGBA (red, green, blue, alpha)
            String rgbaColor = String.format("rgba(%d, %d, %d, %f)",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255),
                color.getOpacity());

            return "-fx-background-color: " + rgbaColor + ";"+
                   "-fx-pref-width: " + (int)tamañoTrazoR.getValue()*100 + ";" +
                   "-fx-pref-height: " + (int)tamañoTrazoR.getValue()*100 + ";" +
                   "-fx-content-display: graphic-only;";
        }, colorTrazoR.valueProperty(),tamañoTrazoR.valueProperty()));
        //default values
        transColor = Color.RED; 
        transSize = 3;
        transportador.setMouseTransparent(true);
        transportador.setFocusTraversable(false);
        hitBox.widthProperty().bind(transportador.widthProperty());
        hitBox.heightProperty().bind(transportador.heightProperty());
        hitBox.setFill(Color.TRANSPARENT);
        hitBox.setStroke(Color.TRANSPARENT);
        
        //ruler
        rotationAngle = 0;
        //regla.set
        regla.styleProperty().bind(Bindings.createStringBinding(() -> {
            Color color = colorTrazoR.getValue();
            // Formato RGBA (red, green, blue, alpha)
            String rgbaColor = String.format("rgba(%d, %d, %d, %f)",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255),
                color.getOpacity());

            return "-fx-background-color: " + rgbaColor + ";"+
                   "-fx-pref-width: " + (int)tamañoTrazoR.getValue()*285 + ";" +
                   "-fx-pref-height: " + (int)tamañoTrazoR.getValue()*14 + ";" +
                   "-fx-content-display: graphic-only;";
        }, colorTrazoR.valueProperty(),tamañoTrazoR.valueProperty()));
        
        //Line
        previewLine = new Line();
        previewLine.setVisible(false);
        previewLine.setStrokeLineCap(StrokeLineCap.ROUND);
        previwePane.getChildren().add(previewLine);
        //default values
        lineColor = Color.RED;
        lineSize = 2;
        
        //text
        previewText = new TextField();
        previewText.setVisible(false);
        previewText.setAlignment(Pos.CENTER);
        // Binding para el color del texto
       previewText.styleProperty().bind(Bindings.createStringBinding(() -> {
            Color color = colorTrazo.getValue();
            // Formato RGBA (red, green, blue, alpha)
            String rgbaColor = String.format("rgba(%d, %d, %d, %f)",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255),
                color.getOpacity());

            return "-fx-text-fill: " + rgbaColor + ";"+
                   "-fx-font-size: " + (int)tamañoTrazo.getValue() + ";";
        }, colorTrazo.valueProperty(),tamañoTrazo.valueProperty()));
       
        previwePane.getChildren().add(previewText);
        
        textColor = Color.RED;
        textSize = 30;
        
        //Arc
        
        previewArc.strokeProperty().bind(colorTrazo.valueProperty());
        previewArc.strokeWidthProperty().bind(tamañoTrazo.valueProperty());
        
        DoubleBinding endAngle = Bindings.createDoubleBinding(() -> {
            double dx = previewArcLCircle.getCenterX() - previewArc.getCenterX();
            double dy = previewArcLCircle.getCenterY() - previewArc.getCenterY();
            return Math.toDegrees(Math.atan2(dy, dx))*-1;
        }, previewArcLCircle.centerXProperty(), previewArcLCircle.centerYProperty());

        DoubleBinding startAngle = Bindings.createDoubleBinding(() -> {
            double dx = previewArcRCircle.getCenterX() - previewArc.getCenterX();
            double dy = previewArcRCircle.getCenterY() - previewArc.getCenterY();
            return Math.toDegrees(Math.atan2(dy, dx)*-1);
        }, previewArcRCircle.centerXProperty(), previewArcRCircle.centerYProperty());

        // Vincular las propiedades del arco
        previewArc.startAngleProperty().bind(startAngle);
        previewArc.lengthProperty().bind(Bindings.createDoubleBinding(() -> {
            double angleDiff = endAngle.get() - startAngle.get();
            // Ajustar para que el ángulo sea positivo (0-360)
            return angleDiff < 0 ? angleDiff + 360 : angleDiff;
        }, startAngle, endAngle));
        previewArc.setFill(Color.TRANSPARENT);
        
        arcRad = new SimpleDoubleProperty(50);
        previewArcCircle.radiusProperty().bind(arcRad);
        previewArc.radiusXProperty().bind(arcRad);
        previewArc.radiusYProperty().bind(arcRad);
        
        previewArcCircle.strokeProperty().bind(colorTrazo.valueProperty());
        previewArcRCircle.fillProperty().bind(colorTrazo.valueProperty());
        previewArcLCircle.fillProperty().bind(colorTrazo.valueProperty());
        previewArcRCircle.strokeProperty().bind(colorTrazo.valueProperty());
        previewArcLCircle.strokeProperty().bind(colorTrazo.valueProperty());
        
        previewArcCircle.strokeWidthProperty().bind(Bindings.divide(tamañoTrazo.valueProperty(), 2));
        previewArcRCircle.radiusProperty().bind(tamañoTrazo.valueProperty());
        previewArcLCircle.radiusProperty().bind(tamañoTrazo.valueProperty());
        
        
        previewArcCircle.setOpacity(.4);
        previewArcCircle.setFill(Color.TRANSPARENT);
        previewArcCircle.getStrokeDashArray().addAll(2d, 13d);
        
        
        arcColor = Color.RED;
        arcSize = 6;
        
        //Test button
        
        isUserNull = new SimpleBooleanProperty(PoiUPVApp.getUser() == null);
        testButton.disableProperty().bind(isUserNull);  
        
        //Profile button
       profileImg.imageProperty().bind(
        Bindings.createObjectBinding(
        () -> {
            if (isUserNull.get()) {
                return new Image("/resources/profile.png");
            } else {
                return PoiUPVApp.getUser().getAvatar();
            }
        },
        isUserNull // Dependencia: se actualiza cuando isUserNull cambia
        )
    );
    }
    

    @FXML
    private void showPosition(MouseEvent e) {
        if(pointButton.isSelected()){
                previewCircle.setCenterX(e.getX());
                previewCircle.setCenterY(e.getY());
                previewCircle.setRadius(tamañoTrazo.getValue());
                previewCircle.setFill(colorTrazo.getValue());
                
        }
        if(lineButton.isSelected()){
            if(previewLine.isVisible()){
                previewLine.setEndX(e.getX());
                previewLine.setEndY(e.getY());
                previewLine.setStrokeWidth(tamañoTrazo.getValue());
                previewLine.setStroke(colorTrazo.getValue());
            }
        }else previewLine.setVisible(false);
        if(!textButton.isSelected()){
            previewText.setVisible(false);
        }
        if(!arcButton.isSelected()){
            previewArcCircle.setVisible(false);
            previewArc.setVisible(false);
            previewArcRCircle.setVisible(false);
            previewArcLCircle.setVisible(false);
        }
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
                pointColor = colorTrazo.getValue();
                pointSize = tamañoTrazo.getValue();
                Circle circle = new Circle();
                circle.setUserData("drawing");
                circle.setOnMouseClicked(e2 -> {
                    if (e2.getButton() == MouseButton.PRIMARY && eraserButton.isSelected()) {
                        ((Pane)circle.getParent()).getChildren().remove(circle);
                    }
                });
                circle.setCenterX(e.getX());
                circle.setCenterY(e.getY());
                circle.setRadius(pointSize);
                circle.setFill(pointColor);

                card.getChildren().add(circle);
            }
            if(lineButton.isSelected()){
                if(!previewLine.isVisible()){
                    previewLine.setStartX(e.getX());
                    previewLine.setStartY(e.getY());
                    previewLine.setEndX(e.getX());
                    previewLine.setEndY(e.getY());
                    previewLine.setVisible(true);
                    
                }else{
                    lineColor  = colorTrazo.getValue();
                    lineSize = tamañoTrazo.getValue();
                    Line line = new Line(previewLine.getStartX(),previewLine.getStartY(),e.getX(),e.getY());
                    line.setUserData("drawing");
                    line.setStrokeLineCap(StrokeLineCap.ROUND);
                    line.setOnMouseClicked(e2 -> {
                    if (e2.getButton() == MouseButton.PRIMARY && eraserButton.isSelected()) {
                        ((Pane)line.getParent()).getChildren().remove(line);
                    }
                });
                    line.setStrokeWidth(lineSize);
                    line.setStroke(lineColor);
                    
                    card.getChildren().add(line);
                    previewLine.setVisible(false);
                }
            }
            if(textButton.isSelected()){
                if(previewText.isVisible()){
                    textColor  = colorTrazo.getValue();
                    textSize = tamañoTrazo.getValue();
                    Text text = new Text(previewText.getText());
                    text.setFill(textColor);
                    text.setFont(Font.font("Sans", textSize));
                    text.setUserData("drawing");
                    text.setOnMouseClicked(e2 -> {
                        if (e2.getButton() == MouseButton.PRIMARY && eraserButton.isSelected()) {
                            ((Pane)text.getParent()).getChildren().remove(text);
                        }
                    });
                    text.setLayoutX(previewText.getLayoutX() + (previewText.getWidth()/2));
                    text.setLayoutY(previewText.getLayoutY() + (previewText.getHeight()/2));
                    card.getChildren().add(text);
                    previewText.setVisible(false);
                }else{
                    previewText.setVisible(true);
                    previewText.setText("");
                    previewText.setLayoutX(e.getX() - (previewText.getWidth()/2));
                    previewText.setLayoutY(e.getY() - (previewText.getHeight()/2));
                }
            }
            if(arcButton.isSelected()){
                if(previewArc.isVisible() && isOutsideWithMargin(e.getX(),e.getY(),
                    previewArc.getCenterX(),previewArc.getCenterY(), arcRad.get(), 40)
                ){
                    arcColor  = colorTrazo.getValue();
                    arcSize = tamañoTrazo.getValue();
                    
                    Arc arc = new Arc();
                    arc.setCenterX(previewArc.getCenterX());
                    arc.setCenterY(previewArc.getCenterY());
                    arc.setRadiusX(arcRad.get());
                    arc.setRadiusY(arcRad.get());
                    arc.setStroke(arcColor);
                    arc.setStrokeWidth(arcSize);
                    arc.setStartAngle(previewArc.getStartAngle());
                    arc.setLength(previewArc.getLength());
                    arc.setFill(null);
                    arc.setUserData("drawing");
                    arc.setOnMouseClicked(e2 -> {
                    if (e2.getButton() == MouseButton.PRIMARY && eraserButton.isSelected()) {
                            ((Pane)arc.getParent()).getChildren().remove(arc);
                        }
                     });
                    
                    card.getChildren().add(arc);
                    
                    previewArcCircle.setVisible(false);
                    previewArc.setVisible(false);
                    previewArcRCircle.setVisible(false);
                    previewArcLCircle.setVisible(false);
                    
                }else if(!previewArc.isVisible()){
                    previewArcCircle.setCenterX(e.getX());
                    previewArcCircle.setCenterY(e.getY());
                    previewArc.setCenterX(e.getX());
                    previewArc.setCenterY(e.getY());
                    
                    double r = previewArcCircle.getRadius();

                    previewArcRCircle.setCenterX(previewArcCircle.getCenterX() + r);
                    previewArcRCircle.setCenterY(previewArcCircle.getCenterY());

                    previewArcLCircle.setCenterX(previewArcCircle.getCenterX());
                    previewArcLCircle.setCenterY(previewArcCircle.getCenterY() - r);

                    previewArcCircle.setVisible(true);
                    previewArc.setVisible(true);
                    previewArcRCircle.setVisible(true);
                    previewArcLCircle.setVisible(true);
                }
            }
        }
    }   

    @FXML
    private void pointSelected(ActionEvent event) {
        colorTrazo.setValue(pointColor);
        tamañoTrazo.setValue(pointSize);
    }

    @FXML
    private void transportadorSelected(ActionEvent event) {
        if(transportador.isVisible()){
            transportador.setVisible(false);
            hitBox.setVisible(false);
        }
        else{
            transportador.setVisible(true);
            hitBox.setVisible(true);
            colorTrazoR.setValue(transColor);
            tamañoTrazoR.setValue(transSize);
        }
    }


    @FXML
    private void transportadorDragged(MouseEvent e) {
        if(e.getButton()==MouseButton.PRIMARY){
            hitBox.setX(e.getX()-(hitBox.getWidth()/2));
            hitBox.setY(e.getY()-(hitBox.getHeight()/2));
            transportador.setTranslateX(e.getX() - TransX -(hitBox.getWidth()/2));
            transportador.setTranslateY(e.getY() - TransY -(hitBox.getWidth()/2));
            e.consume();
        }
    }

    @FXML
    private void transportadorClicked(MouseEvent e) {
        if(e.getButton()==MouseButton.PRIMARY){
            transportador.setTranslateX(0);
            transportador.setTranslateY(0);
            TransX = transportador.getTranslateX();
            TransY = transportador.getTranslateY();
            transportador.setTranslateX(e.getX() - TransX -(hitBox.getWidth()/2));
            transportador.setTranslateY(e.getY() - TransY -(hitBox.getWidth()/2));
            transColor = colorTrazoR.getValue();
            transSize = tamañoTrazoR.getValue();
            transportador.toFront();
        }
        e.consume();
    }

    @FXML
    private void lineSelected(ActionEvent event) {
        colorTrazo.setValue(lineColor);
        tamañoTrazo.setValue(lineSize);
    }

    @FXML
    private void removeMovement(MouseEvent e) {
        if(e.getButton()==MouseButton.PRIMARY){
            e.consume();
        }
    }

    @FXML
    private void textSelected(ActionEvent event) {
        colorTrazo.setValue(textColor);
        tamañoTrazo.setValue(textSize);
    }


    @FXML
    private void arcSelected(ActionEvent event) {
        colorTrazo.setValue(arcColor);
        tamañoTrazo.setValue(arcSize);
        previewText.setVisible(false);
    }

    @FXML
    private void previewArcCircleDrag(MouseEvent e) {
        arcRad.set(Math.max(Math.abs(previewArcCircle.getCenterX() - e.getX()),10));
        
        double r = previewArcCircle.getRadius();
        
        double dxr = previewArcRCircle.getCenterX() - previewArcCircle.getCenterX();
        double dyr = previewArcRCircle.getCenterY() - previewArcCircle.getCenterY();
        double dr = Math.hypot(dxr, dyr);
        
        double dxl = previewArcLCircle.getCenterX() - previewArcCircle.getCenterX();
        double dyl = previewArcLCircle.getCenterY() - previewArcCircle.getCenterY();
        double dl = Math.hypot(dxl, dyl);
        

        previewArcRCircle.setCenterX(previewArcCircle.getCenterX() + dxr * r/dr);
        previewArcRCircle.setCenterY(previewArcCircle.getCenterY() + dyr * r/dr);
        
        previewArcLCircle.setCenterX(previewArcCircle.getCenterX() + dxl * r/dl);
        previewArcLCircle.setCenterY(previewArcCircle.getCenterY() + dyl * r/dl);
        e.consume();
    }

    @FXML
    private void previewArcRCircleDrag(MouseEvent e) {
        
        double dx = e.getX() - previewArcCircle.getCenterX();
        double dy = e.getY() - previewArcCircle.getCenterY();
        double d = Math.hypot(dx, dy);
        double r = previewArcCircle.getRadius();

        previewArcRCircle.setCenterX(previewArcCircle.getCenterX() + dx * r/d);
        previewArcRCircle.setCenterY(previewArcCircle.getCenterY() + dy * r/d);
        e.consume();
        }


    @FXML
    private void previewArcLCircleDrag(MouseEvent e) {
        double dx = e.getX() - previewArcCircle.getCenterX();
        double dy = e.getY() - previewArcCircle.getCenterY();
        double d = Math.hypot(dx, dy);
        double r = previewArcCircle.getRadius();

        previewArcLCircle.setCenterX(previewArcCircle.getCenterX() + dx * r/d);
        previewArcLCircle.setCenterY(previewArcCircle.getCenterY() + dy * r/d);
        e.consume();
    }

    
    public boolean isOutsideWithMargin(double mouseX, double mouseY, 
                                 double centerX, double centerY, 
                                 double radius, double margin) {
    double dx = mouseX - centerX;
    double dy = mouseY - centerY;
    return (dx * dx + dy * dy) > (radius + margin) * (radius + margin);
}

    @FXML
    private void rulerSelected(ActionEvent event) {
        if(regla.isVisible()){
            regla.setVisible(false);
        }
        else{
            regla.setVisible(true);
            colorTrazoR.setValue(transColor);
            tamañoTrazoR.setValue(transSize);
        }
    }

    @FXML
    private void remove(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Carta nautica");
        alert.setHeaderText(null);
        alert.setContentText("¿Quieres borrar todo el contenido\n dibujado en la carta?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            Platform.runLater(() -> {
                for (int i = card.getChildren().size() - 1; i >= 0; i--) {
                    Node node = card.getChildren().get(i);
                    if ("drawing".equals(node.getUserData())) {
                    card.getChildren().remove(i);
                    }
                }
            }); 
        }
    }

    @FXML
    private void rulerPre(MouseEvent e) {
        if(e.getButton() == MouseButton.PRIMARY) {
            localBase = zoomGroup.sceneToLocal(e.getSceneX(),e.getSceneY());
            TransXR = regla.getTranslateX();
            TransYR = regla.getTranslateY();
            transColor = colorTrazoR.getValue();
            transSize = tamañoTrazoR.getValue();
            regla.toFront();
        }
    }

    @FXML
    private void rulerDra(MouseEvent e) {
        if(e.getButton() == MouseButton.PRIMARY) {
            Point2D localPos = zoomGroup.sceneToLocal(e.getSceneX(),e.getSceneY());
            regla.setTranslateX(TransXR + localPos.getX()- localBase.getX());
            regla.setTranslateY(TransYR + localPos.getY()- localBase.getY());
        }
    }

    @FXML
    private void rulerScr(ScrollEvent e) {
    double deltaY = e.getDeltaY();
    
    if (deltaY > 0) {
        rotationAngle += 2;
    } else {
        rotationAngle -= 2;
    }
    
    rotationAngle = rotationAngle % 360;
   
    regla.setRotate(rotationAngle);
    
    e.consume();
    }


    @FXML
    private void openProfile(ActionEvent event) throws IOException {
        if(isUserNull.getValue()){
            FXMLLoader loader= new FXMLLoader(getClass().getResource("vistas/IniciarSe.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setTitle("Iniciar Sesión");
            stage.showAndWait();
        }else{
            
            FXMLLoader loader= new FXMLLoader(getClass().getResource("vistas/Perfil.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setTitle("Perfil");
            stage.showAndWait();
        }
        
    }

    @FXML
    private void openTest(ActionEvent event) throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("vistas/Problemas.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        Stage stage = new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
        stage.setTitle("Problemas");
        stage.showAndWait();
        ProblemasController proCont = loader.getController();
    }
}
