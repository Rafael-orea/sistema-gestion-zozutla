package Controller;

import Model.Envio;
import Model.IncidenciaEnvio;
import Model.IncidenciaEnvioDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class IncidenciaEnvioController {

    @FXML private Label folioLabel;
    @FXML private Spinner<Integer> faltantesSpinner;
    @FXML private Spinner<Integer> rotasSpinner;
    @FXML private TextArea descripcionArea;

    private IncidenciaEnvioDAO incidenciaDAO = new IncidenciaEnvioDAO();
    private Envio envio;

    @FXML
    public void initialize() {
        faltantesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
        rotasSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
        folioLabel.setText(envio.getFolio());
    }

    @FXML
    private void handleGuardarIncidencia() {
        if (descripcionArea.getText().trim().isEmpty()) {
            showAlert("Error", "La descripcion es obligatoria.");
            return;
        }

        IncidenciaEnvio inc = new IncidenciaEnvio();
        inc.setIdEnvio(envio.getId());
        inc.setFaltantes(faltantesSpinner.getValue());
        inc.setRotas(rotasSpinner.getValue());
        inc.setDescripcion(descripcionArea.getText().trim());

        if (incidenciaDAO.registrarIncidencia(inc)) {
            // Actualizar estado del envio a con_incidencia
            new Model.EnvioDAO().actualizarEstado(envio.getId(), "con_incidencia");
            showAlert("Exito", "Incidencia registrada.");
            Stage stage = (Stage) folioLabel.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Error", "No se pudo registrar la incidencia.");
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