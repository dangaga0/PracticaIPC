/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package poiupv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.NavDAOException;
import model.Navigation;
import model.User;
import poiupv.PoiUPVApp;

/**
 * FXML Controller class
 *
 * @author erick
 */
public class PerfilController implements Initializable {

    @FXML
    private TextField emailField;
    @FXML
    private TextField nicknameField;
    @FXML
    private TextField passwordField;
    @FXML
    private DatePicker datePickerNacimiento;
    @FXML
    private ImageView avatar;
    
        
    private BooleanProperty validEmail;
    private BooleanProperty validPassword;
    private BooleanProperty validAvatar;
    private BooleanProperty validDate;
    

    private ChangeListener<String> listenerEmail;
    private ChangeListener<String> listenerPassword;
    private ChangeListener<Image> listenerAvatar;
    private ChangeListener<Image> listenerDate;
    
    private User username;
    @FXML
    private Button guardarCambios;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){
       
        username = PoiUPVApp.getUser();

        emailField.setText(username.getEmail());
        nicknameField.setText(username.getNickName());
        passwordField.setText(username.getPassword());
        datePickerNacimiento.setValue(username.getBirthdate());
        avatar.setImage(username.getAvatar());
        
        
        validEmail = new SimpleBooleanProperty();
        validEmail.setValue(Boolean.FALSE);
        
        validPassword = new SimpleBooleanProperty();
        validPassword.setValue(Boolean.FALSE);
        
        validAvatar = new SimpleBooleanProperty();
        validAvatar.setValue(avatar.getImage() != null);
        
        validDate = new SimpleBooleanProperty();
        validDate.setValue(Boolean.FALSE);
        
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkEmail();
                if (!validEmail.get()) {
                    if (listenerEmail == null) {
                        listenerEmail = (a, b, c) -> checkEmail();
                        emailField.textProperty().addListener(listenerEmail);
                    }
                }
            }
        });
        
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkPassword();
                if (!validPassword.get()) {
                    if (listenerPassword == null) {
                        listenerPassword = (a, b, c) -> checkPassword();
                        passwordField.textProperty().addListener(listenerPassword);
                    }
                }
            }
        });
        
        datePickerNacimiento.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkDate();
                if (!validDate.get()) {
                    if (listenerDate == null) {
                        listenerDate = (a, b, c) -> checkDate();
                        datePickerNacimiento.valueProperty().addListener((InvalidationListener) listenerDate);
                    }
                }
            }
        });
        
        avatar.imageProperty().addListener((obs, oldImage, newImage) -> {
            checkAvatar();
        });
      
        BooleanBinding validFields = Bindings.and(validEmail, validPassword)
            .and(validPassword);
        
        guardarCambios.disableProperty().bind(
            Bindings.not(validFields)
        );
    }    

    @FXML
    private void validarEdad(ActionEvent event) {
         LocalDate fechaNacimiento = datePickerNacimiento.getValue();
        if (!esMayorDeEdad(fechaNacimiento)) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Edad insuficiente");
            alerta.setHeaderText(null);
            alerta.setContentText("Debes tener al menos 18 años para registrarte.");
            alerta.showAndWait();
            datePickerNacimiento.setValue(username.getBirthdate());
        }
    }
    
    private boolean esMayorDeEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return false;
        LocalDate hoy = LocalDate.now();
        Period edad = Period.between(fechaNacimiento, hoy);
        return edad.getYears() >= 18;
    }

    @FXML
    private void onOpen(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona tu avatar");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        Window stage = ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                Image image = new Image(new FileInputStream(selectedFile));
                avatar.setImage(image);     
            } catch (FileNotFoundException e) {
        }
    }
    }
    
        private void checkEmail() {
        String email = emailField.getText();
        boolean isValid = User.checkEmail(email);
        validEmail.set(isValid);
        showError(isValid, emailField);
    }
    
    private void checkPassword() {
        String password = passwordField.getText();
        boolean isValid = User.checkPassword(password);
        validPassword.set(isValid);
        showError(isValid, passwordField); 
    }
    
    private void checkAvatar(){
        Image currentImage = avatar.getImage();
        boolean isValid = (currentImage != null); // Puedes añadir más validaciones aquí
        validAvatar.set(isValid);
        showError(isValid, avatar);
    }
    
    private boolean checkDate() {
        LocalDate fechaNacimiento = datePickerNacimiento.getValue();
        if (fechaNacimiento == null) return false;
        LocalDate hoy = LocalDate.now();
        Period edad = Period.between(fechaNacimiento, hoy);
        return edad.getYears() >= 18;
    }
    
    private void showError(boolean isValid, Node field){
         field.setStyle(((isValid) ? "" : "-fx-background-color: rgba(227, 144, 138, .5)"));
    }

    @FXML
    private void cambioPerfil(ActionEvent event) throws IOException, NavDAOException {
        
        if(validEmail.get() && validPassword.get() && validDate.get()){
            username.setEmail(emailField.getText());
            username.setAvatar(avatar.getImage());
            username.setBirthdate(datePickerNacimiento.getValue());
            username.setPassword(passwordField.getText());
            PoiUPVApp.setUser(null);
            FXMLDocumentController.isUserNull.set(PoiUPVApp.getUser() == null);
            PoiUPVApp.setUser(Navigation.getInstance().authenticate(nicknameField.getText(), passwordField.getText()));
            FXMLDocumentController.isUserNull.set(PoiUPVApp.getUser() == null);
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        username.addSession(ProblemasController.getHits(), ProblemasController.getFails());
        PoiUPVApp.setUser(null);
        FXMLDocumentController.isUserNull.set(PoiUPVApp.getUser() == null);
        Stage stagec = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stagec.close();
    }

    @FXML
    private void cerrar(ActionEvent event) {
        Stage stagec = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stagec.close();
    }
    
}