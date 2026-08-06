module GestionZozutla {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires org.apache.poi.ooxml;

    exports App;
    exports Controller;
    exports Model;

    opens Controller to javafx.fxml;
    opens View to javafx.fxml;
    opens App to javafx.fxml;

}