package sample;

public class FIleOpener implements Runnable {

    public StartSceneController controller;

    public FIleOpener(StartSceneController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        controller.AddTab();
    }
}
