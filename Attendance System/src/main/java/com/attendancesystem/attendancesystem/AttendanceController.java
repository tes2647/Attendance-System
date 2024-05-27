package com.attendancesystem.attendancesystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class AttendanceController {

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
        String attendanceType = inputTextField.getText();
        String lastName = inputTextField2.getText();

        LocalDateTime timestamp = LocalDateTime.now().plusHours(24);

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String selectSql = "SELECT StudentID FROM Students WHERE LastName = ?";
            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
            selectStatement.setString(1, lastName);
            ResultSet resultSet = selectStatement.executeQuery();

            int studentID = -1; // Default value
            if (resultSet.next()) {
                studentID = resultSet.getInt("StudentID");
            }

            resultSet.close();
            selectStatement.close();

            if (studentID != -1) { // If student found
                String insertSql = "INSERT INTO Attendance_Records (AttendanceID, CourseID, StudentID, AttendanceType, Timestamp) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertStatement = conn.prepareStatement(insertSql);
                insertStatement.setInt(1, getNextAttendanceID(conn));
                insertStatement.setInt(2, getDefaultCourseID());
                insertStatement.setInt(3, studentID);
                insertStatement.setString(4, attendanceType);
                insertStatement.setObject(5, timestamp);

                int rowsInserted = insertStatement.executeUpdate();
                if (rowsInserted > 0) {
                    successLabel.setText("Attendance recorded successfully!");
                }

                insertStatement.close();
            } else {
                successLabel.setText("Student not found!");
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getDefaultStudentID() {
        // Provide a default value for CourseID

        return 1;
    }

    private int getDefaultCourseID() {
        // Provide a default value for CourseID

        return 1;
    }

    private int getNextAttendanceID(Connection conn) throws SQLException {
        String sql = "SELECT MAX(AttendanceID) FROM Attendance_Records";
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
