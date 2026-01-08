package petcarereminder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Dashboard dashboard = new Dashboard(stage); // ✅ Pass the stage here
        Scene scene = new Scene(dashboard.getView(), 900, 600);

        stage.setTitle("Pet Care Reminder");
        stage.setScene(scene);


        stage.setOnCloseRequest(e -> System.exit(0));

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
