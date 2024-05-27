package com.attendancesystem.attendancesystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class HelloController {

    @FXML
    private Label welcomeText;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Attendance_Project";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Akard2023$";

    public HelloController() {
    }

    @FXML
    private void handleCoursesButton() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT c.CourseName, c.Credits, c.StartDate, c.EndDate, p.FirstName, p.LastName FROM Courses c JOIN Professors p ON c.ProfessorID = p.ProfessorID");
            StringBuilder courses = new StringBuilder();

            while (rs.next()) {
                String courseName = rs.getString("CourseName");
                int credits = rs.getInt("Credits");
                Date startDate = rs.getDate("StartDate");
                Date endDate = rs.getDate("EndDate");
                String professorFirstName = rs.getString("FirstName");
                String professorLastName = rs.getString("LastName");

                courses.append("Course Name: ").append(courseName).append("\n");
                courses.append("Credits: ").append(credits).append("\n");
                courses.append("Start Date: ").append(startDate).append("\n");
                courses.append("End Date: ").append(endDate).append("\n");
                courses.append("Professor: ").append(professorFirstName).append(" ").append(professorLastName).append("\n\n");
            }

            openCourseScene(courses.toString());
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAttendanceButton() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT AR.StudentID, S.FirstName, S.LastName, S.NetID, C.CourseName, AR.AttendanceType, AR.Timestamp " +
                            "FROM Attendance_Records AR " +
                            "JOIN Students S ON AR.StudentID = S.StudentID " +
                            "JOIN Courses C ON AR.CourseID = C.CourseID"
            );
            ResultSet rs = stmt.executeQuery();

            // Create StringBuilder to hold the data
            StringBuilder attendanceData = new StringBuilder();

            // Append header
            attendanceData.append("StudentID |      Name |          NetID |              Course |                   Timestamp |                       Attendance\n");

            // Populate data into StringBuilder
            while (rs.next()) {
                attendanceData.append(rs.getString("StudentID")).append(" | ")
                        .append(rs.getString("FirstName")).append(" ").append(rs.getString("LastName")).append(" | ")
                        .append(rs.getString("NetID")).append(" | ")
                        .append(rs.getString("CourseName")).append(" | ")
                        .append(rs.getTimestamp("Timestamp")).append(" | ")
                        .append(rs.getString("AttendanceType")).append("\n");
            }

            // Pass the attendance data to a method to open the attendance scene
            openAttendanceScene(attendanceData.toString());

            // Close resources
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handlePasswordButton() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Temporary_Passwords");
            StringBuilder temporaryPasswords = new StringBuilder();

            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); ++i) {
                    String columnValue = rs.getString(i);
                    temporaryPasswords.append(columnValue).append("\t");
                }
                temporaryPasswords.append("\n");
            }

            openPasswordScene(temporaryPasswords.toString());
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleQuizButton() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Quiz_Questions");
            StringBuilder quizQuestions = new StringBuilder();

            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); ++i) {
                    String columnValue = rs.getString(i);
                    quizQuestions.append(columnValue).append("\t");
                }
                quizQuestions.append("\n");
            }

            openQuizScene(quizQuestions.toString());
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStudentsButton() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT AR.StudentID, S.FirstName, S.LastName, S.NetID, C.CourseName, AR.AttendanceType, AR.Timestamp " +
                            "FROM Attendance_Records AR " +
                            "JOIN Students S ON AR.StudentID = S.StudentID " +
                            "JOIN Courses C ON AR.CourseID = C.CourseID"
            );
            ResultSet rs = stmt.executeQuery();

            // Create StringBuilder to hold the data
            StringBuilder studentsData = new StringBuilder();

            // Append header
            studentsData.append("StudentID | First Name | Last Name | NetID | Course Name | Attendance Type | Timestamp\n");

            // Populate data into StringBuilder
            while (rs.next()) {
                studentsData.append(rs.getString("StudentID")).append(" | ")
                        .append(rs.getString("FirstName")).append(" | ")
                        .append(rs.getString("LastName")).append(" | ")
                        .append(rs.getString("NetID")).append(" | ")
                        .append(rs.getString("CourseName")).append(" | ")
                        .append(rs.getString("AttendanceType")).append(" | ")
                        .append(rs.getTimestamp("Timestamp")).append("\n");
            }

            // Pass the students data to a method to open the students scene
            openStudentsScene(studentsData.toString());

            // Close resources
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openAttendanceScene(String data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Attendance.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the controller from the loader
            AttendanceController controller = loader.getController();
            controller.setData(data);

            // Replace the content of the existing stage with the new scene
            Stage stage = (Stage) welcomeText.getScene().getWindow(); // Get the existing stage
            stage.setScene(scene); // Set the new scene

            stage.setTitle("Attendance Data");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openPasswordScene(String data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Password.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the controller from the loader
            PasswordController controller = loader.getController();
            //controller.setData(data);

            // Replace the content of the existing stage with the new scene
            Stage stage = (Stage) welcomeText.getScene().getWindow(); // Get the existing stage
            stage.setScene(scene); // Set the new scene

            stage.setTitle("Manage Password");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openCourseScene(String data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Course.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the controller from the loader
            CourseController controller = loader.getController();
            controller.setData(data);

            // Replace the content of the existing stage with the new scene
            Stage stage = (Stage) welcomeText.getScene().getWindow(); // Get the existing stage
            stage.setScene(scene); // Set the new scene

            stage.setTitle("Course");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openQuizScene(String data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Quiz.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the controller from the loader
            QuizController controller = loader.getController();
            controller.setData(data);

            // Replace the content of the existing stage with the new scene
            Stage stage = (Stage) welcomeText.getScene().getWindow(); // Get the existing stage
            stage.setScene(scene); // Set the new scene

            stage.setTitle("Quiz");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openStudentsScene(String data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Students.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the controller from the loader
            StudentsController controller = loader.getController();
            controller.setData(data);

            // Replace the content of the existing stage with the new scene
            Stage stage = (Stage) welcomeText.getScene().getWindow(); // Get the existing stage
            stage.setScene(scene); // Set the new scene

            stage.setTitle("Students");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
