package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.Main.*;

public class StartSceneController implements Initializable {

    @FXML
    public TextField pathField, textField, typeField;

    public void SwitchToResultScene(ActionEvent event) throws IOException {
        GetFields();

        Parent groot = FXMLLoader.load(getClass().getResource("resultScene.fxml"));
        Scene resultScene = new Scene(groot, 800, 600);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(resultScene);
        window.show();
    }

    public void GetFields() {
        folderPath = pathField.getText();
        searchText = textField.getText();
        fileType = typeField.getText();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
