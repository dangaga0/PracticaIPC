/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package poiupv;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.NavDAOException;
import model.Navigation;
import model.Session;


/**
 
FXML Controller class*,
@author erick,*/

public class IniciarSeController implements Initializable {

    @FXML
    private TextField nicknameField;
    @FXML
    private TextField passwordField;

     
    @Override
  public void initialize(URL url, ResourceBundle rb) {// TODO

}

    @FXML
    private void inicioUsuario(ActionEvent event) throws NavDAOException {
        if(Navigation.getInstance().exitsNickName(nicknameField.getText())){
            if(Navigation.getInstance().authenticate(nicknameField.getText(), passwordField.getText()) != null){
                PoiUPVApp.setUser(Navigation.getInstance().authenticate(nicknameField.getText(), passwordField.getText()));
                FXMLDocumentController.isUserNull.set(PoiUPVApp.getUser() == null);
                Stage stagec = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stagec.close();
            }
            else{
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Error");
                alerta.setHeaderText(null);
                alerta.setContentText("Contraseña incorrecta");
                alerta.showAndWait();
            }
        }
        else{
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("Nombre de usuario no registrado");
            alerta.showAndWait();
        }
    }

    @FXML
    private void cerrar(ActionEvent event) {
        Stage stagec = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stagec.close();
    }

    @FXML
    private void openReg(ActionEvent event) throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("vistas/Register.fxml"));
        
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        Stage stage = new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Registro");
        stage.showAndWait();
        RegisterController regCont = loader.getController();
    }
    
}
