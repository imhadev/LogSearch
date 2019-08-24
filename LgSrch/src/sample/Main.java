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

public class Main extends Application {

    public static String folderPath, searchText, fileType;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("startScene.fxml"));
        primaryStage.setTitle("LogSearch");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static TreeItem<TreeElement> SearchFiles() {
        TreeItem<TreeElement> rootItem = new TreeItem<>(new TreeElement(folderPath, false));
        final File folder = new File(folderPath);

        FileWalk(folder, rootItem);
        return rootItem;
    }

    public static boolean FileWalk(final File folder, TreeItem<TreeElement> root) {
        boolean g = false;
        for (final File fileEntry : folder.listFiles()) {
            boolean f = false;
            TreeItem<TreeElement> cur;
            if (fileEntry.isDirectory()) {
                cur = new TreeItem<>(new TreeElement(fileEntry.getName(), false));
                root.getChildren().add(cur);
                f = FileWalk(fileEntry, cur);
                if (f) {
                    g = true;
                }
            } else {
                if (fileEntry.getName().endsWith(fileType)) {
                    f = FileCheck(fileEntry);
                }
                cur = new TreeItem<>(new TreeElement(fileEntry.getName(), f));
                root.getChildren().add(cur);
            }
            if (f) {
                g = true;
            }
        }

        if (g) {
            root.setExpanded(true);
        }

        return g;
    }

    public static boolean FileCheck(final File fileEntry) {
        boolean f = false;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    fileEntry));
            String line = reader.readLine();
            while (line != null) {
                if (line.contains(searchText)) {
                    f = true;
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    public static void setTreeCellFactory(TreeView<TreeElement> tree) {
        tree.setCellFactory(param -> new TreeCell<>() {
            @Override
            public void updateItem(TreeElement item, boolean empty) {
                super.updateItem(item, empty);
                //setDisclosureNode(null);

                if (empty) {
                    setText("");
                    setStyle("-fx-background-color: white;");
                } else {
                    if (item.getCheck()) {
                        setText(item.getName());
                        setStyle("-fx-background-color: lightskyblue;");
                    }
                    else {
                        setText(item.getName());
                    }
                }
            }
        });

        /*tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println(newValue.getValue());
            }
        });*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}
