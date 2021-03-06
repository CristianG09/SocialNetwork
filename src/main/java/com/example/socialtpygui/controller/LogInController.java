package com.example.socialtpygui.controller;

import com.example.socialtpygui.LogInApplication;
import com.example.socialtpygui.domain.User;
import com.example.socialtpygui.service.SuperService;
import com.example.socialtpygui.service.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LogInController {

    @FXML
    private TextField usernameTxt;
    @FXML
    private PasswordField passwordTxt;
    @FXML
    private Button logInBtn;

    private SuperService service;
    private double yCord;
    private double xCord;
    private MainWindowController mainWindowController;

    @FXML
    private void handlerLogIn() throws IOException {
        String username= usernameTxt.getText();
        String password= passwordTxt.getText();
        try {
            User user = service.logIn(username, password);

            FXMLLoader fxmlLoader = new FXMLLoader(LogInApplication.class.getResource("mainWindow.fxml"));
            Stage manWindowStage= new Stage();
            AnchorPane panel= fxmlLoader.load();

            MainWindowController mainWindowController= fxmlLoader.getController();
            mainWindowController.load(service,user);

            Scene scene = new Scene(panel, 700, 520);

            panel.setOnMousePressed(event->{
                xCord = event.getSceneX();
                yCord = event.getSceneY();
            });
            panel.setOnMouseDragged(event->{
                manWindowStage.setX(event.getScreenX() - xCord);
                manWindowStage.setY(event.getScreenY() - yCord);
            });

            scene.getStylesheets().add(LogInApplication.class.getResource("mainWindow.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);
            manWindowStage.setScene(scene);
            manWindowStage.initStyle(StageStyle.TRANSPARENT);
            manWindowStage.initModality(Modality.NONE);
            manWindowStage.show();

            Stage logInStage =(Stage) logInBtn.getScene().getWindow();
            logInStage.close();


        }catch (ValidationException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();

        }
    }

    public void setService(SuperService service) {
        this.service = service;
    }


    public void handlerExitBtn(ActionEvent actionEvent) {
        ((Stage)(((Button)actionEvent.getSource()).getScene().getWindow())).close();
    }

    public void handlerMinimizeBtn(ActionEvent actionEvent) {
        ((Stage)(((Button)actionEvent.getSource()).getScene().getWindow())).setIconified(true);
    }

    public void handlerExtindButton(ActionEvent actionEvent) {
        ((Stage)(((Button)actionEvent.getSource()).getScene().getWindow())).setFullScreen(true);
    }
}
