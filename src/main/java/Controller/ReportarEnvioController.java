package Controller;

import Model.DetalleVenta;
import Model.Envio;
import Model.EnvioDAO;
import Model.Venta;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ReportarEnvioController {

    @FXML private TextField folioSearchField;
    @FXML private ListView<Venta> folioListView;
    @FXML private Label clienteLabel;
    @FXML private TextField destinoField;
    @FXML private DatePicker fechaPicker;
    @FXML private RadioButton rbZozutla;
    @FXML private RadioButton rbCliente;
    @FXML private TableView<DetalleVenta> productosTable;
    @FXML private TableColumn<DetalleVenta, String> colProducto;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleVenta, Double> colPrecio;

    private EnvioDAO envioDAO = new EnvioDAO();
    private ObservableList<DetalleVenta> productosList = FXCollections.observableArrayList();
    private List<Venta> ventasDisponibles;
    private Venta ventaActual;
    private boolean seleccionando = false;

    @FXML
    public void initialize() {
        fechaPicker.setValue(LocalDate.now());

        ToggleGroup fleteGrupo = new ToggleGroup();
        rbZozutla.setToggleGroup(fleteGrupo);
        rbCliente.setToggleGroup(fleteGrupo);
        rbCliente.setSelected(true);

        setupColumnas();
        cargarFolios();
    }

    private void cargarFolios() {
        ventasDisponibles = envioDAO.getVentasSinEnvio();
        ObservableList<Venta> ventasObs = FXCollections.observableArrayList(ventasDisponibles);

        folioListView.setItems(ventasObs);
        folioListView.setCellFactory(lv -> new ListCell<Venta>() {
            @Override
            protected void updateItem(Venta v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null :
                        v.getFolio() + " - " + v.getNombreCliente());
            }
        });

        folioSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (seleccionando) return;
            ObservableList<Venta> filtradas = FXCollections.observableArrayList();
            for (Venta v : ventasDisponibles) {
                if (newVal == null || newVal.isEmpty() ||
                        v.getFolio().toLowerCase().contains(newVal.toLowerCase()) ||
                        v.getNombreCliente().toLowerCase().contains(newVal.toLowerCase())) {
                    filtradas.add(v);
                }
            }
            folioListView.setItems(filtradas);
            folioListView.setVisible(true);
            folioListView.setManaged(true);
        });

        folioListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, seleccionada) -> {
            if (seleccionada != null) {
                seleccionando = true;
                ventaActual = seleccionada;
                folioSearchField.setText(seleccionada.getFolio());
                clienteLabel.setText(seleccionada.getNombreCliente());
                folioListView.setVisible(false);
                folioListView.setManaged(false);

                List<DetalleVenta> productos = envioDAO.getProductosDeVenta(seleccionada.getId());
                productosList.clear();
                productosList.addAll(productos);

                javafx.application.Platform.runLater(() -> seleccionando = false);
            }
        });

        folioSearchField.setOnMouseClicked(e -> {
            seleccionando = false;
            folioListView.setItems(ventasObs);
            folioListView.setVisible(true);
            folioListView.setManaged(true);
            javafx.application.Platform.runLater(() -> folioSearchField.selectAll());
        });

        folioSearchField.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.BACK_SPACE ||
                    e.getCode() == javafx.scene.input.KeyCode.DELETE) {
                seleccionando = false;
                folioListView.setVisible(true);
                folioListView.setManaged(true);
            }
        });

        folioListView.setVisible(false);
        folioListView.setManaged(false);
    }

    private void setupColumnas() {
        colProducto.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreAlcancia()));
        colCantidad.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCantidad()).asObject());
        colPrecio.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrecioUnitario()).asObject());
        productosTable.setItems(productosList);
    }

    @FXML
    private void handleRegistrarEnvio() {
        if (ventaActual == null) {
            showAlert("Error", "Selecciona un folio de venta.");
            return;
        }
        if (destinoField.getText().trim().isEmpty()) {
            showAlert("Error", "El destino es obligatorio.");
            return;
        }

        Envio envio = new Envio();
        envio.setIdVenta(ventaActual.getId());
        envio.setDestino(destinoField.getText().trim());
        envio.setFecha(fechaPicker.getValue());
        envio.setFlete(rbZozutla.isSelected() ? "zozutla" : "cliente");

        if (envioDAO.registrarEnvio(envio)) {
            showAlert("Exito", "Envio registrado correctamente.");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/EnvioView.fxml"));
                Parent root = loader.load();
                javafx.scene.layout.StackPane contentArea =
                        (javafx.scene.layout.StackPane) destinoField.getScene().lookup("#contentArea");
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "No se pudo registrar el envio.");
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