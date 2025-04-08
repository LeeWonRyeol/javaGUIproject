module org.example.testfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.testfx to javafx.fxml;
    exports org.example.testfx;
}