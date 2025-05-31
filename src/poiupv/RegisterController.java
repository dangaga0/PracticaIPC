package poiupv;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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

/**
 *
 * @author jsoler
 */
public class RegisterController implements Initializable {
    private Label labelMessage;
    @FXML
    private ImageView avatar;
    @FXML
    
    private DatePicker datePickerNacimiento;
    
    private BooleanProperty validEmail;
    private BooleanProperty validPassword;
    private BooleanProperty validNickname;

    private ChangeListener<String> listenerEmail;
    private ChangeListener<String> listenerPassword;
    private ChangeListener<String> listenerNickname;
    
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField nicknameField;
    @FXML
    private Button confirmButton;
    

    
    //=========================================================
    // you must initialize here all related with the object 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //emailField.setStyle("-fx-background-color: red");
        validEmail = new SimpleBooleanProperty();
        validEmail.setValue(Boolean.FALSE);
        
        validPassword = new SimpleBooleanProperty();
        validPassword.setValue(Boolean.FALSE);
        
        validNickname = new SimpleBooleanProperty();
        validNickname.setValue(Boolean.FALSE);
        
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkEmail();
                if (!validEmail.get()) {
                    //If it is not correct, a listener is added to the text or value 
                    //so that the field is validated while it is being edited.
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
                    //If it is not correct, a listener is added to the text or value 
                    //so that the field is validated while it is being edited.
                    if (listenerPassword == null) {
                        listenerPassword = (a, b, c) -> checkPassword();
                        passwordField.textProperty().addListener(listenerPassword);
                    }
                }
            }
        });
        
        nicknameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkNickname();
                if (!validNickname.get()) {
                    //If it is not correct, a listener is added to the text or value 
                    //so that the field is validated while it is being edited.
                    if (listenerNickname == null) {
                        listenerNickname = (a, b, c) -> checkNickname();
                        nicknameField.textProperty().addListener(listenerNickname);
                    }
                }
            }
        });
        
        BooleanBinding validFields = Bindings.and(validEmail, validPassword)
            .and(validPassword);
        
        confirmButton.disableProperty().bind(
            Bindings.not(validFields)
        );
    }    
    
    private void showError(boolean isValid, Node field){
        field.setStyle(((isValid) ? "" : "-fx-background-color: rgba(227, 144, 138, .5)"));
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
    
    //Código sacado de ChatGPT
    private boolean esMayorDeEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return false;
        LocalDate hoy = LocalDate.now();
        Period edad = Period.between(fechaNacimiento, hoy);
        return edad.getYears() >= 18;
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
            datePickerNacimiento.setValue(LocalDate.parse("2005-01-01"));
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
    
    private void checkNickname() {
        String nickname = nicknameField.getText();
        boolean match = User.checkNickName(nickname);
        validNickname.set(match);
        showError(match, nicknameField);
    }

    @FXML
    private void registroUsuario(ActionEvent event) throws NavDAOException {
        if(validEmail.get() && validNickname.get() && validPassword.get() && !Navigation.getInstance().exitsNickName(nicknameField.getText())){
            String nickName = nicknameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            LocalDate birthdate = datePickerNacimiento.getValue();
            Navigation navigation = Navigation.getInstance();
            if(birthdate == null){
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Error");
                alerta.setHeaderText(null);
                alerta.setContentText("Fecha de nacimiento incorrecta");
                alerta.showAndWait();
                }else{
                User result = navigation.registerUser(nickName, email, password, avatar.getImage() , birthdate);
                Stage stagec = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stagec.close();
            }
        }
    }

    @FXML
    private void cerrar(ActionEvent event) {
        Stage stagec = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stagec.close();
    }
}