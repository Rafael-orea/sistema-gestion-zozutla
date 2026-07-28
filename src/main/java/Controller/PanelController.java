package Controller;

import Model.Panel;
import Model.PanelDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class PanelController {

    @FXML private Label lblMoldes;
    @FXML private Label lblInventario;
    @FXML private Label lblVentasDia;
    @FXML private Label lblVentasMes;
    @FXML private Label lblGanancia;

    private PanelDAO panelDAO = new PanelDAO();

    @FXML
    public void initialize() {
        cargarDatos();
    }

    private void cargarDatos() {
        Panel panel = panelDAO.getDatosPanel();
        lblMoldes.setText(String.valueOf(panel.getTotalMoldes()));
        lblInventario.setText(String.valueOf(panel.getTotalInventario()));
        lblVentasDia.setText("$" + panel.getVentasDia().toString());
        lblVentasMes.setText("$" + panel.getVentasMes().toString());
        lblGanancia.setText("$" + panel.getGananciaEstimada().toString());
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/InicioView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblMoldes.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Gestion Artesanias Zozutla");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cerrar sesion: " + e.getMessage());
        }
    }
}