package Controller;

import Model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        loadView("/View/PanelView.fxml");
    }

    @FXML
    private void handleInicio() {
        loadView("/View/PanelView.fxml");
    }

    @FXML
    private void handleMoldes() {
        loadView("/View/MoldeView.fxml");
    }

    @FXML
    private void handleInventario() {
        loadView("/View/InventarioView.fxml");
    }

    @FXML
    private void handleVentas() {
        loadView("/View/VentaView.fxml");
    }

    @FXML
    private void handleHistorialVentas() {
        loadView("/View/HistorialVentaView.fxml");
    }

    @FXML
    private void handleClientes() {
        loadView("/View/ClienteView.fxml");
    }

    @FXML
    private void handleInsumos() {
        loadView("/View/InsumoView.fxml");
    }

    @FXML
    private void handleInsumoAlcancia() {
        loadView("/View/InsumoAlcanciaView.fxml");
    }

    @FXML
    private void handleEnvios() {
        loadView("/View/EnvioView.fxml");
    }

    @FXML
    private void handleReportes() {
        loadView("/View/ReporteView.fxml");
    }

    @FXML
    private void handleEmpleados() {
        showAlert("Info", "Modulo en construccion.");
    }



    @FXML
    private void handleProveedores() {
        showAlert("Info", "Modulo en construccion.");
    }
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/InicioView.fxml"));
            Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) contentArea.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Sistema de Gestion Artesanias Zozutla");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "No se pudo cerrar sesion: " + e.getMessage());
        }
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar la vista: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}