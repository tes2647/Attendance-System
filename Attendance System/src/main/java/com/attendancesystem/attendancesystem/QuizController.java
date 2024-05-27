package com.attendancesystem.attendancesystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.*;
import java.time.LocalTime;

public class QuizController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/Attendance_Project";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Akard2023$";

    @FXML
    private Label dataLabel;

    @FXML
    private TextField inputTextField;

    @FXML
    private TextField inputTextField2;

    @FXML
    private TextField inputTextField3;

    @FXML
    private Label successLabel;

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

    @FXML
    private void handleSubmitButton() {
        String professorLastName = inputTextField2.getText();
        String questionID = inputTextField.getText();
        String quizAvailabilityStr = inputTextField3.getText();

        try {
            // Parse the QuizAvailability string to an integer representing minutes
            int quizAvailabilityMinutes = Integer.parseInt(quizAvailabilityStr);

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            int passwordID = -1; // Initialize with a default value
            String getPasswordIDQuery = "SELECT PasswordID FROM Temporary_Passwords ORDER BY PasswordID DESC LIMIT 1";
            // Adjust the query as per your table structure
            Statement getPasswordIDStmt = conn.createStatement();
            ResultSet getPasswordIDRs = getPasswordIDStmt.executeQuery(getPasswordIDQuery);
            if (getPasswordIDRs.next()) {
                passwordID = getPasswordIDRs.getInt("PasswordID");
            }
            getPasswordIDRs.close();
            getPasswordIDStmt.close();
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Daily_Quiz (CourseID, PasswordID, QuizAvailability, QuizDate, ClassStartTime, ClassEndTime, QuestionID, ChoiceID) " +
                            "VALUES (?, ?, ?, CURDATE(), ?, ?, ?, ?)"
            );

            // Assuming CourseID and PasswordID are retrieved from somewhere else in your application
            int courseID = 1; // Sample value, replace it with the actual value

            // Get current time
            LocalTime currentTime = LocalTime.now();

            // Calculate ClassEndTime by adding QuizAvailability minutes to ClassStartTime
            LocalTime classEndTime = currentTime.plusMinutes(quizAvailabilityMinutes);

            // Set values for the prepared statement
            stmt.setInt(1, courseID);
            stmt.setInt(2, passwordID);
            stmt.setInt(3, quizAvailabilityMinutes); // Set quiz availability minutes based on user input
            stmt.setTime(4, Time.valueOf(currentTime)); // Set current time as ClassStartTime
            stmt.setTime(5, Time.valueOf(classEndTime)); // Set calculated ClassEndTime
            stmt.setString(6, questionID);
            stmt.setString(7, questionID); // Set ChoiceID as the same value as QuestionID

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                successLabel.setText("Quiz Created!");
            } else {
                successLabel.setText("Try Again");
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
