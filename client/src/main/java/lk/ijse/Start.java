package lk.ijse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static lk.ijse.LoginFormController.socket;

public class Start extends Application {

        @Override
        public void start(Stage stage) throws Exception {

            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login_form.fxml.")));
            stage.setTitle("LogIn");
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.setOnCloseRequest(windowEvent -> {
                try {
                    LoginFormController.clsStg();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            stage.show();

        }



}