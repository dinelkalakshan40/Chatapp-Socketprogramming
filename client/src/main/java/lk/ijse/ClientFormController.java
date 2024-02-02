package lk.ijse;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ClientFormController implements Initializable {


    public ScrollPane scrollPane;

    public TextField textField;
    public JFXButton btnEmoji;
    public JFXButton btnCamera;
    public JFXButton btnSend;
    public Text userNameText;
    public JFXButton btnClose;
    public JFXButton btnMinimize;
    public AnchorPane root;
    public VBox vBoxChat;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private HBox hBox;
    private String message;
    private String user;

    private ArrayList<String> wordList;

    Label label;

    {
        message = "";
    }

    Stage stage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setName();
        message();
        emoji();
    }

    private void setName(){
        userNameText.setText(LoginFormController.currentUserName);
    }


    private void message() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 3000);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                while (socket.isConnected()) {
                    hBox = new HBox(12);
                    message = dataInputStream.readUTF();
                    if (message.endsWith(".jpg") || message.endsWith(".jpeg") || message.endsWith(".png") || message.endsWith(".gif")) {
                        Platform.runLater(() -> {
                            String path = splitImg(message);
                            File file = new File(path);
                            Image image = new Image(file.toURI().toString());
                            ImageView img = new ImageView(image);
                            img.setFitWidth(150);
                            img.setFitHeight(150);
                            if (user.equals(userNameText.getText())) {
                                HBox hBox1 = new HBox();
                                hBox1.setPadding(new Insets(5, 5, 5, 10));
                                hBox1.getChildren().add(img);
                                hBox1.setAlignment(Pos.CENTER_RIGHT);

                                vBoxChat.getChildren().add(hBox1);
                            } else {
                                HBox hBox1 = new HBox();
                                hBox1.setAlignment(Pos.CENTER_LEFT);
                                Text text = new Text(user);
                                hBox1.getChildren().add(text);
                                label = new Label(user + " :\n\n");
                                label.setGraphic(img);

                                HBox hBox2 = new HBox();
                                hBox2.setAlignment(Pos.CENTER_LEFT);
                                hBox2.setPadding(new Insets(5, 5, 5, 10));
                                hBox2.getChildren().add(img);

                                Platform.runLater(() -> {
                                    vBoxChat.getChildren().add(hBox1);
                                    vBoxChat.getChildren().add(hBox2);
                                });
                            }
                        });
                    } else {
                        splitMsg(message);
                        String preparedMsg = makeMsg();
                        if (user.equals(userNameText.getText())) {

                            HBox hBox = new HBox();
                            hBox.setAlignment(Pos.CENTER_RIGHT);
                            hBox.setPadding(new Insets(5, 5, 5, 10));

                            Text text = new Text(preparedMsg);
                            TextFlow textFlow = new TextFlow(text);
                            textFlow.setStyle("-fx-background-color: #8122e2; -fx-font-weight: bold; -fx-background-radius: 20px");
                            textFlow.setPadding(new Insets(5, 10, 5, 10));
                            text.setFill(Color.color(0, 0, 0));

                            hBox.getChildren().add(textFlow);

                            Platform.runLater(() -> {
                                vBoxChat.getChildren().add(hBox);
                            });

                        } else {
                            HBox hBox = new HBox();
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.setPadding(new Insets(5, 5, 0, 10));

                            Text text = new Text(preparedMsg);
                            TextFlow textFlow = new TextFlow(text);
                            textFlow.setStyle("-fx-background-color: #06e3d1; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
                            textFlow.setPadding(new Insets(5, 10, 5, 10));
                            text.setFill(Color.color(1, 1, 1));

                            hBox.getChildren().add(textFlow);

                            HBox hBoxName = new HBox();
                            hBoxName.setAlignment(Pos.CENTER_LEFT);
                            Text textName = new Text(user);
                            TextFlow textFlowName = new TextFlow(textName);

                            hBoxName.getChildren().add(textFlowName);

                            Platform.runLater(() -> {
                                vBoxChat.getChildren().add(hBoxName);
                                vBoxChat.getChildren().add(hBox);
                            });
                        }
                    }
                    scrollPane.vvalueProperty().bind(vBoxChat.heightProperty());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private String splitImg(String message) {
        String[] words = message.split("!!!!split!!!!");
        this.user = words[0];
        return words[1];
    }

    private String makeMsg() {
        wordList.remove(0);
        return String.join(" ", wordList);
    }

    private void splitMsg(String message) {
        String[] words = message.split(" ");
        this.user = words[0];
        wordList = new ArrayList<>(Arrays.asList(words));
    }

    public void msgOnAction(ActionEvent actionEvent) throws IOException {
        dataOutputStream.writeUTF(userNameText.getText() + " " + textField.getText());
        dataOutputStream.flush();
        textField.setText("");

    }


    private void emoji() {
        EmojiController emojiPicker = new EmojiController();

        VBox vBox = new VBox(emojiPicker);
        vBox.setPrefSize(90, 110);
        //vBox.setSpacing(10);
        vBox.setFillWidth(true);
        vBox.setLayoutX(313);
        vBox.setLayoutY(400);
        vBox.setStyle("-fx-font-size: 20");

        root.getChildren().add(vBox);

        emojiPicker.setVisible(false);
        emojiPicker.setLayoutX(0);
        emojiPicker.setLayoutY(0);

        btnEmoji.setOnAction(event -> {
            emojiPicker.setVisible(!emojiPicker.isVisible());
        });



        emojiPicker.getEmojiListView().setOnMouseClicked(event -> {
            String selectedEmoji = emojiPicker.getEmojiListView().getSelectionModel().getSelectedItem();
            if (selectedEmoji != null) {
                textField.setText(textField.getText() + selectedEmoji + "  ");
            }
            emojiPicker.setVisible(false);
        });
    }

    public void btnEmojiOnAction(ActionEvent actionEvent) {


    }

    public void btnCameraOnAction(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("image file","*.jpg", "*.jpeg", "*.png", "*.gif");

        fileChooser.getExtensionFilters().add(imageFilter);

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String imagePath = selectedFile.getAbsolutePath();
            dataOutputStream.writeUTF(userNameText.getText()+"!!!!split!!!!"+imagePath);
            dataOutputStream.flush();
            dataOutputStream.flush();
        }

    }

    public void btnSendOnAction(ActionEvent actionEvent) throws IOException {
        dataOutputStream.writeUTF(userNameText.getText() + " " + textField.getText());
        dataOutputStream.flush();
        textField.setText("");
    }


    public void closeOnAction(ActionEvent actionEvent) throws IOException {

       // System.out.println("logout 1");
        dataOutputStream.writeUTF("/usrLogOut//!-> "+userNameText.getText());
        dataOutputStream.flush();
        stage = (Stage) root.getScene().getWindow();
        stage.close();


      //  System.out.println("logout");


    }
}
