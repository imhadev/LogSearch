package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    public static String folderPath, searchText, fileType;
    public static TreeItem<TreeElement> rootItemMain;
    public static TreeItem<TreeElement> selectedElement;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("startScene.fxml"));
        Scene startScene = new Scene(root, 1000, 800);
        primaryStage.setTitle("LogSearch");
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    public static TreeItem<TreeElement> SearchFiles() {
        rootItemMain = new TreeItem<>(new TreeElement(folderPath, false));

        final File folder = new File(folderPath);
        FileWalk(folder, rootItemMain);

        return rootItemMain;
    }

    public static boolean FileWalk(final File folder, TreeItem<TreeElement> root) {
        boolean folderContainsSearched = false;
        for (final File fileEntry : folder.listFiles()) {
            boolean isSearched = false;
            ArrayList<Integer> patternMatches = new ArrayList<>();
            TreeItem<TreeElement> cur;
            if (fileEntry.isDirectory()) {
                cur = new TreeItem<>(new TreeElement(fileEntry.getName(), false));
                root.getChildren().add(cur);
                isSearched = FileWalk(fileEntry, cur);
                if (isSearched) {
                    folderContainsSearched = true;
                }
            } else {
                if (fileEntry.getName().endsWith(fileType)) {
                    patternMatches = findMatches(fileEntry);
                    if (patternMatches.size() != 0) {
                        isSearched = true;
                    }
                }
                cur = new TreeItem<>(new TreeElement(fileEntry.getName(), isSearched, patternMatches));
                root.getChildren().add(cur);
            }
            if (isSearched) {
                folderContainsSearched = true;
            }
        }

        if (folderContainsSearched) {
            root.setExpanded(true);
        }

        return folderContainsSearched;
    }

    public static ArrayList<Integer> findMatches(final File fileEntry) {

        StringBuilder sb = GetFileContent(fileEntry);
        String fileContent = sb.toString();

        String escapedNeedle = Pattern.quote(searchText);
        Pattern pattern = Pattern.compile(escapedNeedle);
        Matcher matcher = pattern.matcher(fileContent);

        ArrayList<Integer> matches = new ArrayList<>();

        while (matcher.find()) {
            matches.add(matcher.start());
        }

        return matches;
    }


    public static StringBuilder GetFileContent(final File fileEntry) {
        StringBuilder sb = new StringBuilder();
        try (
                FileReader fileReader = new FileReader(fileEntry);
                BufferedReader reader1 = new BufferedReader(fileReader)
        ) {
            String line;
            while ((line = reader1.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {

        }
        return sb;
    }

    public static void setTreeCellFactory(TreeView<TreeElement> tree) {
        tree.setCellFactory(param -> new TreeCell<>() {
            @Override
            public void updateItem(TreeElement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                    setStyle("-fx-background-color: white;");
                } else {
                    if (item.getCheck()) {
                        setText(item.getName());
                        setStyle("-fx-background-color: lightskyblue;");
                    } else {
                        setText(item.getName());
                        setStyle("-fx-color: black;");
                    }
                }
            }
        });

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.getValue().getCheck()) {
                    selectedElement = newValue;
                }
            }
        });
    }

    public static String GetFullPath(TreeItem<TreeElement> newValue) {
        String fullPath = newValue.getValue().getName();
        while (newValue.getParent() != null) {
            newValue = newValue.getParent();
            fullPath = newValue.getValue().getName() + "\\" + fullPath;
        }
        return fullPath;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
