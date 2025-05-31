/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package poiupv;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Answer;
import model.NavDAOException;
import model.Navigation;
import model.Problem;
import model.Session;

/**
 * FXML Controller class
 *
 * @author damia
 */
public class ProblemasController implements Initializable {

    @FXML
    private ListView<Problem> listView;
    List<Problem> problemas;
    List<String> problemasText;
    ObservableList<Problem> listaObservable;
    
    List<Session> s;
    ObservableList<Session> listaSes;
    
    List<Answer> ans;
            
    Navigation nav;
    
    static int hits;
    static int fails;
    
    @FXML
    private Label enunciado;
    @FXML
    private GridPane answers;
    @FXML
    private ToggleGroup test;
    @FXML
    private Button selecCheckButton;
    @FXML
    private RadioButton ans1;
    @FXML
    private RadioButton ans2;
    @FXML
    private RadioButton ans3;
    @FXML
    private RadioButton ans4;
    @FXML
    private TableView<Session> tableView;
    @FXML
    private TableColumn<Session, LocalDateTime> fechaCol;
    @FXML
    private TableColumn<Session, Integer> hitsCol;
    @FXML
    private TableColumn<Session, Integer> failsCol;
    @FXML
    private TableColumn<Session, String> porCol;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            nav = Navigation.getInstance();
        } catch (NavDAOException ex) {
            Logger.getLogger(ProblemasController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        problemas = nav.getProblems();
        problemasText = new ArrayList();
        for (Problem p : problemas) {
            problemasText.add(p.getText());
        }
        
        listaObservable = FXCollections.observableList(problemas);
        listView.setItems(listaObservable);
        
        
        selecCheckButton.disableProperty().bind(
            Bindings.isNull(listView.getSelectionModel().selectedItemProperty())
            .and(Bindings.isNull(test.selectedToggleProperty()))
        );
        
        s = PoiUPVApp.getUser().getSessions();
        listaSes= FXCollections.observableList(s);
        tableView.setItems(listaSes);
        
        fechaCol.setCellValueFactory(
        new PropertyValueFactory<>("timeStamp"));
        
        hitsCol.setCellValueFactory(
        new PropertyValueFactory<>("hits"));
        
        failsCol.setCellValueFactory(
        new PropertyValueFactory<>("faults"));
        
       porCol.setCellValueFactory(cellData ->{
        Session s = cellData.getValue();
        int hits = s.getHits();
        int faults = s.getFaults();
        
        double por = (hits + faults == 0) ? 0.0 : (double) hits/(hits + faults) * 100;
        
        String porF = String.format("%.2f%%",por);
        return new SimpleObjectProperty(porF);
    });

        hits = 0;
        fails = 0;
    }    


    @FXML
    private void cerrar(ActionEvent event) {
        Stage stagec = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stagec.close();
    }

    @FXML
    private void selec_Check(ActionEvent event) {
        if(selecCheckButton.getText().equals("Seleccionar")){
            
            setProb(listView.getSelectionModel().getSelectedItem()); 
            
        }else{
            RadioButton selectedRadio = (RadioButton) test.getSelectedToggle();
            Answer selectedAnswer = (Answer) selectedRadio.getUserData();   
            if (selectedAnswer.getValidity()) {
                hits++;
                selectedRadio.setStyle("-fx-background-color: rgba(128, 232, 149, .7)");
                
            }else{
                selectedRadio.setStyle("-fx-background-color: rgba(227, 144, 138, .7)");
                fails++;
            }
            
            ans1.setDisable(true);
            ans2.setDisable(true);
            ans3.setDisable(true);
            ans4.setDisable(true);
            
            test.selectToggle(null);
        }
    }

    @FXML
    private void choseProblem(ActionEvent event) {
        listView.setVisible(true);
        listView.toFront();
        selecCheckButton.setText("Seleccionar");
        selecCheckButton.setVisible(true);
        enunciado.setVisible(false);
        answers.setVisible(false);
        tableView.setVisible(false);
        test.selectToggle(null);
    }

    @FXML
    private void randomProblem(ActionEvent event) {
        
        setProb(problemas.get(ThreadLocalRandom.current().nextInt(problemas.size()))); 
            
    }
    
    private void setProb(Problem p){
        
            enunciado.setText(p.getText());
            
            ans = p.getAnswers();
            
            //Collections.shuffle(ans);
           
            ans1.setText(ans.get(0).getText());
            ans1.setUserData(ans.get(0));
            
            ans2.setText(ans.get(1).getText());
            ans2.setUserData(ans.get(1));
            
            ans3.setText(ans.get(2).getText());
            ans3.setUserData(ans.get(2));
            
            ans4.setText(ans.get(3).getText());
            ans4.setUserData(ans.get(3));
            
            
            listView.setVisible(false);
            enunciado.setVisible(true);
            answers.setVisible(true);
            tableView.setVisible(false);
            selecCheckButton.setText("Check");
            selecCheckButton.setVisible(true);
            
            ans1.setDisable(false);
            ans2.setDisable(false);
            ans3.setDisable(false);
            ans4.setDisable(false);
            
            ans1.setStyle("");
            ans2.setStyle("");
            ans3.setStyle("");
            ans4.setStyle("");
            
            listView.getSelectionModel().clearSelection();
    }
    
    static public int getHits(){
        return hits;
    }
    
    static public int getFails(){
        return fails;
    }

    @FXML
    private void stats(ActionEvent event) {
        tableView.setVisible(true);
        listView.setVisible(false);
        tableView.toFront();
        selecCheckButton.setVisible(false);
        enunciado.setVisible(false);
        answers.setVisible(false);
        test.selectToggle(null);
        listView.getSelectionModel().clearSelection();
    }
    
}
