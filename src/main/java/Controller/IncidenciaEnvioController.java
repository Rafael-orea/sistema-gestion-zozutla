package Controller;

import Model.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class IncidenciaEnvioController {

    @FXML private Label folioLabel;
    @FXML private TableView<DetalleVenta> productosTable;
    @FXML private TableColumn<DetalleVenta, String> colProducto;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private Spinner<Integer> faltantesSpinner;
    @FXML private Spinner<Integer> rotasSpinner;
    @FXML private Label productoSeleccionadoLabel;
    @FXML private TextArea descripcionArea;
    @FXML private RadioButton rbCliente100;
    @FXML private RadioButton rbAcuerdo;
    @FXML private javafx.scene.layout.VBox acuerdoBox;
    @FXML private Spinner<Double> porcentajeClienteSpinner;
    @FXML private Spinner<Double> porcentajeZozutlaSpinner;

    private IncidenciaEnvioDAO incidenciaDAO = new IncidenciaEnvioDAO();
    private EnvioDAO envioDAO = new EnvioDAO();
    private Envio envio;
    private ObservableList<DetalleVenta> productosList = FXCollections.observableArrayList();
    private DetalleVenta productoSeleccionado;

    @FXML
    public void initialize() {
        faltantesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
        rotasSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
        porcentajeClienteSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100, 50, 5));
        porcentajeZozutlaSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100, 50, 5));
        porcentajeClienteSpinner.setEditable(true);
        porcentajeZozutlaSpinner.setEditable(true);

        ToggleGroup grupo = new ToggleGroup();
        rbCliente100.setToggleGroup(grupo);
        rbAcuerdo.setToggleGroup(grupo);
        rbCliente100.setSelected(true);

        acuerdoBox.setVisible(false);
        acuerdoBox.setManaged(false);

        grupo.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean esAcuerdo = rbAcuerdo.isSelected();
            acuerdoBox.setVisible(esAcuerdo);
            acuerdoBox.setManaged(esAcuerdo);
        });

        porcentajeClienteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                porcentajeZozutlaSpinner.getValueFactory().setValue(100.0 - newVal);
        });

        porcentajeZozutlaSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                porcentajeClienteSpinner.getValueFactory().setValue(100.0 - newVal);
        });

        setupTablaProductos();
    }

    private void setupTablaProductos() {
        colProducto.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreAlcancia()));
        colCantidad.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCantidad()).asObject());

        productosTable.setItems(productosList);

        productosTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, seleccionado) -> {
            if (seleccionado != null) {
                productoSeleccionado = seleccionado;
                productoSeleccionadoLabel.setText("Producto: " + seleccionado.getNombreAlcancia() +
                        " (cant. comprada: " + seleccionado.getCantidad() + ")");
                faltantesSpinner.setValueFactory(
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, seleccionado.getCantidad(), 0));
                rotasSpinner.setValueFactory(
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, seleccionado.getCantidad(), 0));
            }
        });
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
        folioLabel.setText(envio.getFolio());

        List<DetalleVenta> productos = envioDAO.getProductosDeVenta(envio.getIdVenta());
        productosList.addAll(productos);
    }

    @FXML
    private void handleGuardarIncidencia() {
        if (productoSeleccionado == null) {
            showAlert("Error", "Selecciona el producto afectado de la lista.");
            return;
        }

        int faltantes = faltantesSpinner.getValue();
        int rotas = rotasSpinner.getValue();
        int totalAfectadas = faltantes + rotas;

        if (totalAfectadas == 0) {
            showAlert("Error", "Registra al menos una pieza faltante o rota.");
            return;
        }
        if (totalAfectadas > productoSeleccionado.getCantidad()) {
            showAlert("Error", "El total de piezas afectadas no puede ser mayor a la cantidad comprada.");
            return;
        }
        if (descripcionArea.getText().trim().isEmpty()) {
            showAlert("Error", "La descripcion es obligatoria.");
            return;
        }

        IncidenciaEnvio inc = new IncidenciaEnvio();
        inc.setIdEnvio(envio.getId());
        inc.setIdAlcancia(productoSeleccionado.getIdAlcancia());
        inc.setNombreAlcancia(productoSeleccionado.getNombreAlcancia());
        inc.setFaltantes(faltantes);
        inc.setRotas(rotas);
        inc.setCantidadAfectada(totalAfectadas);
        inc.setDescripcion(descripcionArea.getText().trim());

        if (rbCliente100.isSelected()) {
            inc.setResponsabilidad("cliente");
            inc.setPorcentajeCliente(100.0);
            inc.setPorcentajeZozutla(0.0);
        } else {
            inc.setResponsabilidad("acuerdo");
            inc.setPorcentajeCliente(porcentajeClienteSpinner.getValue());
            inc.setPorcentajeZozutla(porcentajeZozutlaSpinner.getValue());
        }

        if (incidenciaDAO.registrarIncidencia(inc)) {
            envioDAO.actualizarEstado(envio.getId(), "con_incidencia");
            showAlert("Exito", totalAfectadas + " piezas de " +
                    productoSeleccionado.getNombreAlcancia() + " registradas como incidencia.");
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