module GestionZozutla {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    exports App;
    exports Controller;
    exports Model;

    opens Controller to javafx.fxml;
    opens View to javafx.fxml;
    opens App to javafx.fxml;

}