package scheduler;

import javafx.application.Application;
import javafx.stage.Stage;
import scheduler.models.Controller;

public class App extends Application {

    @Override
    public void start(Stage mainWindow) {
        Controller.getInstance().runProgram(mainWindow);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
