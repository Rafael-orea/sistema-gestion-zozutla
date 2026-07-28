package Controller;

import Model.Venta;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class DetalleVentaController {

    @FXML private Label folioLabel;
    @FXML private Label totalLabel;
    @FXML private Label vendedorLabel;

    private Venta venta;

    public void setVenta(Venta venta) {
        this.venta = venta;
        folioLabel.setText(venta.getFolio());
        totalLabel.setText(String.format("$%.2f", venta.getTotal()));
        vendedorLabel.setText("Administrador");
    }

    @FXML
    private void handlePanelPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/PanelView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) folioLabel.getScene().getWindow();
            stage.close();
            folioLabel.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegistrarOtraVenta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/VentaView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) folioLabel.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}