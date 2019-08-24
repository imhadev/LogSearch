package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.Main.*;

public class ResultSceneController implements Initializable {

    @FXML
    public TreeView<TreeElement> fileTree;

    public void SwitchToStartScene(ActionEvent event) throws IOException {
        Parent groot = FXMLLoader.load(getClass().getResource("startScene.fxml"));
        Scene startScene = new Scene(groot, 800, 600);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(startScene);
        window.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TreeItem<TreeElement> rootItem = SearchFiles();
        fileTree.setRoot(rootItem);

        setTreeCellFactory(fileTree);
    }
}
