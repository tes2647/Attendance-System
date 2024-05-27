module com.attendancesystem.attendancesystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.attendancesystem.attendancesystem to javafx.fxml;
    exports com.attendancesystem.attendancesystem;
}