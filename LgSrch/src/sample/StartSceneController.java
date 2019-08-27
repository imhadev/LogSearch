package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

    @FXML
    public TextArea systemTextArea;

    public ArrayList<TextAreaTab> textAreasArr;
    public Thread searchThread;
    public int fileOpeningThreadsCount;

    public boolean searchCompleted;

    public void GetFields() {
        if (!searchThread.isAlive()) {
            if (fileOpeningThreadsCount == 0) {
                if ((pathField.getText() != null) && (textField.getText() != null) && (typeField.getText() != null)) {
                    searchCompleted = false;

                    DisplaySystemMessage(0);

                    tabPaneID.getTabs().clear();
                    fileTree.setRoot(null);

                    folderPath = pathField.getText();
                    searchText = textField.getText();
                    fileType = typeField.getText();

                    searchThread = new Thread(new FileSeacher(this));
                    searchThread.start();
                } else {
                    DisplaySystemMessage(4);
                }
            }
            else {
                DisplaySystemMessage(8);
            }
        }
        else {
            DisplaySystemMessage(1);
        }
    }

    public void BuildTree() {
        TreeItem<TreeElement> rootItem = SearchFiles();

        Runnable updater = () -> {
            if (rootItem == null) {
                DisplaySystemMessage(9);
            }
            else {
                fileTree.setRoot(rootItem);
                setTreeCellFactory(fileTree);

                DisplaySystemMessage(2);

                searchCompleted = true;
            }
        };

        Platform.runLater(updater);
    }

    public void CreateNewTab() {
        if (!searchThread.isAlive()) {
            if (searchCompleted == true) {
                if (selectedElement == null) {
                    DisplaySystemMessage(5);
                }
                else {
                    DisplaySystemMessage(6);
                    fileOpeningThreadsCount++;
                    Thread fileOpeningThread = new Thread(new FIleOpener(this));
                    fileOpeningThread.start();
                }
            }
            else {
                DisplaySystemMessage(3);
            }
        }
        else {
            DisplaySystemMessage(1);
        }
    }

    public void AddTab() {
        String selectedFile = GetFullPath(selectedElement);
        ArrayList<Integer> curElementsArray = selectedElement.getValue().getpatternMatches();

        Tab tab = new Tab(selectedFile);
        final File folder = new File(selectedFile);

        InlineCssTextArea bigTextArea = new InlineCssTextArea();
        StringBuilder sb = GetFileContent(folder);
        bigTextArea.appendText(sb.toString());
        bigTextArea.setParagraphGraphicFactory(LineNumberFactory.get(bigTextArea));

        for (int i = 0; i < curElementsArray.size(); i++) {
            bigTextArea.setStyle(curElementsArray.get(i), curElementsArray.get(i) + searchText.length(), "-fx-font-weight: bold;");
        }

        bigTextArea.setEditable(false);

        textAreasArr.add(new TextAreaTab(bigTextArea, curElementsArray, -1));

        VirtualizedScrollPane vPane = new VirtualizedScrollPane(bigTextArea);
        tab.setContent(vPane);

        tab.setOnClosed(c -> {
            int selectedTabNum = tabPaneID.getSelectionModel().getSelectedIndex();
            textAreasArr.remove(selectedTabNum);
        });

        Runnable updater = () -> {
            tabPaneID.getTabs().add(tab);
            fileOpeningThreadsCount--;

            DisplaySystemMessage(7);
        };
        Platform.runLater(updater);

        selectedElement = null;
    }

    public void MoveToPrev() {
        int selectedTabNum = tabPaneID.getSelectionModel().getSelectedIndex();
        if (selectedTabNum != -1) {
            TextAreaTab curTextAreaTab = textAreasArr.get(selectedTabNum);
            if (curTextAreaTab.curPosIndex > 0) {
                curTextAreaTab.curPosIndex--;
                System.out.println(curTextAreaTab.curPosIndex);
                System.out.println(curTextAreaTab.curElementsArray.get(curTextAreaTab.curPosIndex));
                MoveToPosition(curTextAreaTab.tabTextArea, curTextAreaTab.curElementsArray.get(curTextAreaTab.curPosIndex));
            }
        }
    }

    public void MoveToNext() {
        int selectedTabNum = tabPaneID.getSelectionModel().getSelectedIndex();
        if (selectedTabNum != -1) {
            TextAreaTab curTextAreaTab = textAreasArr.get(selectedTabNum);
            if (curTextAreaTab.curPosIndex < curTextAreaTab.curElementsArray.size() - 1) {
                curTextAreaTab.curPosIndex++;
                System.out.println(curTextAreaTab.curPosIndex);
                System.out.println(curTextAreaTab.curElementsArray.get(curTextAreaTab.curPosIndex));
                MoveToPosition(curTextAreaTab.tabTextArea, curTextAreaTab.curElementsArray.get(curTextAreaTab.curPosIndex));
            }
        }
    }

    public void MoveToPosition(InlineCssTextArea curTextArea, int curPos) {
        curTextArea.moveTo(curPos);
        curTextArea.requestFollowCaret();
    }

    public void DisplaySystemMessage(int messageNum) {
        systemTextArea.appendText("\n");
        switch(messageNum) {
            case 0:
                systemTextArea.appendText("Search started");
                break;
            case 1:
                systemTextArea.appendText("Search in process");
                break;
            case 2:
                systemTextArea.appendText("Search completed");
                break;
            case 3:
                systemTextArea.appendText("Search first");
                break;
            case 4:
                systemTextArea.appendText("Fill all fields");
                break;
            case 5:
                systemTextArea.appendText("Select file");
                break;
            case 6:
                systemTextArea.appendText("Opening file");
                break;
            case 7:
                systemTextArea.appendText("File opened");
                break;
            case 8:
                systemTextArea.appendText("File opening in process");
                break;
            case 9:
                systemTextArea.appendText("Invalid file path");
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchThread = new Thread();
        fileOpeningThreadsCount = 0;
        searchCompleted = false;
        pathField.setText(null);
        textField.setText(null);
        typeField.setText(".log");
        tabPaneID.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        systemTextArea.setEditable(false);
        textAreasArr = new ArrayList<>();
    }
}
