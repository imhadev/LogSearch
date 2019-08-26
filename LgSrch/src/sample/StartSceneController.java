package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static sample.Main.*;

public class StartSceneController implements Initializable {

    @FXML
    public TextField pathField, textField, typeField;

    @FXML
    public TreeView<TreeElement> fileTree;

    @FXML
    public TabPane tabPaneID;

    public int curElement;
    public int curElementPos;
    public ArrayList<Integer> curElementsArray;
    public InlineCssTextArea bigTextArea;

    public void GetFields() {
        folderPath = pathField.getText();
        searchText = textField.getText();
        fileType = typeField.getText();

        Thread thread1 = new Thread(new FileSeacher(this));
        thread1.start();
    }

    public void BuildTree() {
        TreeItem<TreeElement> rootItem = SearchFiles();

        Runnable updater = new Runnable() {
            @Override
            public void run() {
                fileTree.setRoot(rootItem);
                setTreeCellFactory(fileTree);
            }
        };

        Platform.runLater(updater);
    }

    public void tabtab() {
        Thread thread2 = new Thread(new FIleOpener(this));
        thread2.start();
    }

    public void AddTab() {
        String selectedFile = GetFullPath(selectedElement);

        curElement = -1;
        curElementPos = -1;
        curElementsArray = selectedElement.getValue().patternMatches;

        Tab tab = new Tab(selectedFile);
        final File folder = new File(selectedFile);

        bigTextArea = new InlineCssTextArea ();
        StringBuilder sb = GetFileContent(folder);
        bigTextArea.appendText(sb.toString());
        bigTextArea.setParagraphGraphicFactory(LineNumberFactory.get(bigTextArea));
        VirtualizedScrollPane vPane = new VirtualizedScrollPane(bigTextArea);

        for (int i = 0; i < selectedElement.getValue().patternMatches.size(); i++) {
            bigTextArea.setStyle(selectedElement.getValue().patternMatches.get(i), selectedElement.getValue().patternMatches.get(i) + searchText.length(), "-fx-font-weight: bold;");
        }

        Runnable updater = new Runnable() {
            @Override
            public void run() {
                tabPaneID.getTabs().add(tab);
            }
        };

        tab.setContent(vPane);
        Platform.runLater(updater);
    }

    public void MoveToPrev() {
        if (curElement > 0) {
            curElement--;
            curElementPos = curElementsArray.get(curElement);
            bigTextArea.moveTo(curElementPos);
            bigTextArea.requestFollowCaret();
        }
    }

    public void MoveToNext() {
        if (curElement < curElementsArray.size() - 1) {
            curElement++;
            curElementPos = curElementsArray.get(curElement);
            bigTextArea.moveTo(curElementPos);
            bigTextArea.requestFollowCaret();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabPaneID.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
    }
}
