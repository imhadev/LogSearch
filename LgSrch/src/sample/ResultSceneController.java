package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.Main.*;

public class ResultSceneController implements Initializable {

    @FXML
    public TreeView<TreeElement> fileTree;

    @FXML
    public TabPane tabPaneID;

    public TextArea fileText;

    public void SwitchToStartScene(ActionEvent event) throws IOException {
        Parent groot = FXMLLoader.load(getClass().getResource("startScene.fxml"));
        Scene startScene = new Scene(groot, 800, 600);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(startScene);
        window.show();
    }

    public void AddTab() {
        Tab tab = new Tab(selectedFile);
        final File folder = new File(selectedFile);

        BufferedReader reader;
        StringBuilder textAreaContent = new StringBuilder("");

        try {
            reader = new BufferedReader(new FileReader(
                    folder));
            String line = reader.readLine();
            while (line != null) {
                textAreaContent.append(line + "\n");
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileText = new TextArea();
        fileText.setText(textAreaContent.toString());
        fileText.setEditable(false);

        tab.setContent(fileText);
        tabPaneID.getTabs().add(tab);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //tree
        TreeItem<TreeElement> rootItem = SearchFiles();
        fileTree.setRoot(rootItem);
        setTreeCellFactory(fileTree);

        //tabs
        tabPaneID.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
    }
}
