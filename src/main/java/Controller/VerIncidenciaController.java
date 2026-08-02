package Controller;

import Model.Envio;
import Model.IncidenciaEnvio;
import Model.IncidenciaEnvioDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class VerIncidenciaController {

    @FXML private Label folioLabel;
    @FXML private Label clienteLabel;
    @FXML private TableView<IncidenciaEnvio> incidenciaTable;
    @FXML private TableColumn<IncidenciaEnvio, String> colProducto;
    @FXML private TableColumn<IncidenciaEnvio, Integer> colFaltantes;
    @FXML private TableColumn<IncidenciaEnvio, Integer> colRotas;
    @FXML private TableColumn<IncidenciaEnvio, Integer> colTotal;
    @FXML private TableColumn<IncidenciaEnvio, String> colResponsabilidad;
    @FXML private TableColumn<IncidenciaEnvio, Double> colPctCliente;
    @FXML private TableColumn<IncidenciaEnvio, Double> colPctZozutla;
    @FXML private TableColumn<IncidenciaEnvio, String> colDescripcion;
    @FXML private TableColumn<IncidenciaEnvio, String> colFecha;

    private IncidenciaEnvioDAO incidenciaDAO = new IncidenciaEnvioDAO();
    private ObservableList<IncidenciaEnvio> incidenciaList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colProducto.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreAlcancia()));
        colFaltantes.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getFaltantes()).asObject());
        colRotas.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getRotas()).asObject());
        colTotal.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCantidadAfectada()).asObject());
        colResponsabilidad.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getResponsabilidad().equals("cliente")
                        ? "Cliente 100%" : "Acuerdo"));
        colPctCliente.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPorcentajeCliente()).asObject());
        colPctCliente.setCellFactory(col -> new TableCell<IncidenciaEnvio, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("%.0f pct", item));
            }
        });
        colPctZozutla.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPorcentajeZozutla()).asObject());
        colPctZozutla.setCellFactory(col -> new TableCell<IncidenciaEnvio, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("%.0f pct", item));
            }
        });
        colDescripcion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescripcion()));
        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha()));

        incidenciaTable.setItems(incidenciaList);
    }

    public void setEnvio(Envio envio) {
        folioLabel.setText(envio.getFolio());
        clienteLabel.setText(envio.getCliente());
        List<IncidenciaEnvio> incidencias = incidenciaDAO.getIncidenciasPorEnvio(envio.getId());
        incidenciaList.addAll(incidencias);
    }
}