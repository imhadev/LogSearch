package sample;

public class FileSeacher implements Runnable {

    public StartSceneController controller;

    public FileSeacher(StartSceneController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        controller.BuildTree();
    }
}
