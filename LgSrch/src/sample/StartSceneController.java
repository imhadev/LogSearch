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
    private TextField pathField, textField, typeField;

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

    public enum SysMsg {
        SEARCH_STARTED,
        SEARCH_IN_PROCESS,
        SEARCH_COMPLETED,
        SEARCH_FIRST,
        FILL_ALL_FIELDS,
        SELECT_FILE,
        OPENING_FILE,
        FILE_OPENED,
        FILE_OPENING_IN_PROCESS,
        INVALID_FILE_PATH
    }

    public void GetFields() {
        if (!searchThread.isAlive()) {
            if (fileOpeningThreadsCount == 0) {
                if ((pathField.getText() != null) && (textField.getText() != null) && (typeField.getText() != null)) {
                    searchCompleted = false;

                    DisplaySystemMessage(SysMsg.SEARCH_STARTED);

                    tabPaneID.getTabs().clear();
                    fileTree.setRoot(null);

                    folderPath = pathField.getText();
                    searchText = textField.getText();
                    fileType = typeField.getText();

                    searchThread = new Thread(new FileSeacher(this));
                    searchThread.start();
                } else {
                    DisplaySystemMessage(SysMsg.FILL_ALL_FIELDS);
                }
            }
            else {
                DisplaySystemMessage(SysMsg.FILE_OPENING_IN_PROCESS);
            }
        }
        else {
            DisplaySystemMessage(SysMsg.SEARCH_IN_PROCESS);
        }
    }

    public void BuildTree() {
        TreeItem<TreeElement> rootItem = SearchFiles();

        Runnable updater = () -> {
            if (rootItem == null) {
                DisplaySystemMessage(SysMsg.INVALID_FILE_PATH);
            }
            else {
                fileTree.setRoot(rootItem);
                setTreeCellFactory(fileTree);

                DisplaySystemMessage(SysMsg.SEARCH_COMPLETED);

                searchCompleted = true;
            }
        };

        Platform.runLater(updater);
    }

    public void CreateNewTab() {
        if (!searchThread.isAlive()) {
            if (searchCompleted == true) {
                if (selectedElement == null) {
                    DisplaySystemMessage(SysMsg.SELECT_FILE);
                }
                else {
                    DisplaySystemMessage(SysMsg.OPENING_FILE);
                    fileOpeningThreadsCount++;
                    Thread fileOpeningThread = new Thread(new FIleOpener(this));
                    fileOpeningThread.start();
                }
            }
            else {
                DisplaySystemMessage(SysMsg.SEARCH_FIRST);
            }
        }
        else {
            DisplaySystemMessage(SysMsg.SEARCH_IN_PROCESS);
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

        selectedElement = null;

        Runnable updater = () -> {

            tabPaneID.getTabs().add(tab);
            fileOpeningThreadsCount--;

            DisplaySystemMessage(SysMsg.FILE_OPENED);
        };
        Platform.runLater(updater);
    }

    public void MoveToPrev() {
        int selectedTabNum = tabPaneID.getSelectionModel().getSelectedIndex();
        if (selectedTabNum != -1) {
            TextAreaTab curTextAreaTab = textAreasArr.get(selectedTabNum);
            if (curTextAreaTab.curPosIndex > 0) {
                curTextAreaTab.curPosIndex--;
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
                MoveToPosition(curTextAreaTab.tabTextArea, curTextAreaTab.curElementsArray.get(curTextAreaTab.curPosIndex));
            }
        }
    }

    public void MoveToPosition(InlineCssTextArea curTextArea, int curPos) {
        curTextArea.moveTo(curPos + searchText.length());
        curTextArea.requestFollowCaret();
    }

    public void DisplaySystemMessage(SysMsg message) {
        systemTextArea.appendText("\n");
        switch(message) {
            case SEARCH_STARTED:
                systemTextArea.appendText("Search started");
                break;
            case SEARCH_IN_PROCESS:
                systemTextArea.appendText("Search in process");
                break;
            case SEARCH_COMPLETED:
                systemTextArea.appendText("Search completed");
                break;
            case SEARCH_FIRST:
                systemTextArea.appendText("Search first");
                break;
            case FILL_ALL_FIELDS:
                systemTextArea.appendText("Fill all fields");
                break;
            case SELECT_FILE:
                systemTextArea.appendText("Select file");
                break;
            case OPENING_FILE:
                systemTextArea.appendText("Opening file");
                break;
            case FILE_OPENED:
                systemTextArea.appendText("File opened");
                break;
            case FILE_OPENING_IN_PROCESS:
                systemTextArea.appendText("File opening in process");
                break;
            case INVALID_FILE_PATH:
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
