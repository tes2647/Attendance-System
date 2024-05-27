package com.attendancesystem.attendancesystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class StudentsController {
    @FXML
    private Label dataLabel;

    public void setData(String data) {
        dataLabel.setText(data);
    }

    @FXML
    private void goBackToMainScene() {
        Stage stage = (Stage) dataLabel.getScene().getWindow();
        stage.close(); // Close the attendance scene


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage mainStage = new Stage();
            mainStage.setScene(scene);
            mainStage.setTitle("Attendance System");
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
