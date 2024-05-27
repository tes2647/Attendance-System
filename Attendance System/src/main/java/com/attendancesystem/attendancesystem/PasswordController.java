package com.attendancesystem.attendancesystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class PasswordController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/Attendance_Project";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Akard2023$";

    @FXML
    private Label dataLabel;

    @FXML
    private TextField inputTextField;

    @FXML
    private Label successLabel;

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
        String password = inputTextField.getText();

        // Get expiry time from ClassEndTime in the daily_quiz table
        LocalDateTime expiryTime = getExpiryTimeFromDatabase();

        // Insert the password into the database
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "INSERT INTO Temporary_Passwords (PasswordID, Password, ExpiryTime, CourseID) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, getNextPasswordId(conn)); // Get the next available PasswordID
            statement.setString(2, password);
            statement.setObject(3, expiryTime);
            statement.setInt(4, getDefaultCourseID()); // Provide a default value for CourseID

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                successLabel.setText("Password Created!");
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private LocalDateTime getExpiryTimeFromDatabase() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT QuizDate, ClassEndTime FROM daily_quiz ORDER BY QuizID DESC LIMIT 1"; // Assuming QuizID is unique and in descending order
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                LocalDateTime quizDate = resultSet.getDate("QuizDate").toLocalDate().atStartOfDay(); // Get the QuizDate at the start of the day
                LocalDateTime classEndTime = resultSet.getTime("ClassEndTime").toLocalTime().atDate(quizDate.toLocalDate()); // Combine QuizDate with ClassEndTime
                return classEndTime.plusHours(24); // Add 24 hours to class end time
            }
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no expiry time found
    }


    private int getDefaultCourseID() {
        // Provide a default value for CourseID

        return 1;
    }

    private int getNextPasswordId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(PasswordID) FROM Temporary_Passwords";
        PreparedStatement statement = conn.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        int lastId = 0;
        if (resultSet.next()) {
            lastId = resultSet.getInt(1);
        }
        resultSet.close();
        statement.close();
        return lastId + 1;
    }
}
