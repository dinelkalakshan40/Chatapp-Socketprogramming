package lk.ijse;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class LoginFormController {


    public TextField textField;
    public Button loginBtn;

    static Socket socket;

    DataOutputStream dataOutputStream;

    Stage stage;
    double x,y =0;

    public static String currentUserName;


    public void loginOnAction(ActionEvent actionEvent) throws IOException {

        socket = new Socket("localhost", 3000);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataOutputStream.writeUTF("/usrLog//!-> "+ textField.getText());
        currentUserName = textField.getText();
        dataOutputStream.flush();
        Parent anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/client_form.fxml")));
        anchorPane.setOnMousePressed(event -> { x = event.getSceneX();y = event.getSceneY(); });
        anchorPane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });

        stage = new Stage();
        stage.setScene(new Scene(anchorPane));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setHeight(600);
        stage.setTitle(currentUserName);

        stage.show();

        textField.setText("");
    }


    public static void clsStg() throws IOException {
        if (socket != null){
            socket.close();
        }
    }
}
