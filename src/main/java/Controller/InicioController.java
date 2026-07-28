package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.io.InputStream;

public class InicioController {

    @FXML
    private ImageView logoImage;

    @FXML
    public void initialize() {
        try {
            InputStream is = getClass().getResourceAsStream("/images/logo.jpeg");
            if (is != null) {
                Image image = new Image(is);
                logoImage.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }
    }

    @FXML
    private void handleIngresar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio de Sesion - Artesanias Zozutla");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar la pantalla de login: " + e.getMessage());
            alert.showAndWait();
        }
    }
}