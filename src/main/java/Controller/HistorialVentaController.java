package Controller;

import Model.Alcancia;
import Model.Cliente;
import Model.HistorialVenta;
import Model.HistorialVentaDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class HistorialVentaController {

    @FXML private DatePicker fechaPicker;
    @FXML private TextField clienteSearchField;
    @FXML private TextField modeloSearchField;
    @FXML private ListView<Cliente> clienteListView;
    @FXML private ListView<Alcancia> modeloListView;
    @FXML private ComboBox<String> filtroEstadoEnvio;
    @FXML private TableView<HistorialVenta> historialTable;
    @FXML private TableColumn<HistorialVenta, String> colFecha;
    @FXML private TableColumn<HistorialVenta, String> colFolio;
    @FXML private TableColumn<HistorialVenta, String> colCliente;
    @FXML private TableColumn<HistorialVenta, String> colModelo;
    @FXML private TableColumn<HistorialVenta, Integer> colCantidad;
    @FXML private TableColumn<HistorialVenta, Double> colTotal;
    @FXML private TableColumn<HistorialVenta, String> colEstadoEnvio;
    @FXML private Label totalLabel;
    @FXML private Label registrosLabel;

    private HistorialVentaDAO historialDAO = new HistorialVentaDAO();
    private ObservableList<HistorialVenta> historialList = FXCollections.observableArrayList();

    private List<Cliente> todosClientes;
    private List<Alcancia> todasAlcancias;
    private Cliente clienteSeleccionado;
    private Alcancia modeloSeleccionado;
    private boolean seleccionandoCliente = false;
    private boolean seleccionandoModelo = false;

    @FXML
    public void initialize() {
        filtroEstadoEnvio.setItems(FXCollections.observableArrayList(
                "Todos", "En proceso", "Entregado", "Con incidencia", "Venta"
        ));
        filtroEstadoEnvio.setValue("Todos");

        setupColumnas();
        cargarClientes();
        cargarModelos();
        cargarTodo();
    }

    private void cargarClientes() {
        todosClientes = historialDAO.getClientesCombo();
        ObservableList<Cliente> clientesObs = FXCollections.observableArrayList(todosClientes);

        clienteListView.setItems(clientesObs);
        clienteListView.setCellFactory(lv -> new ListCell<Cliente>() {
            @Override protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNombre());
            }
        });

        clienteSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (seleccionandoCliente) return;
            ObservableList<Cliente> filtrados = FXCollections.observableArrayList();
            for (Cliente c : todosClientes) {
                if (newVal == null || newVal.isEmpty() ||
                        c.getNombre().toLowerCase().contains(newVal.toLowerCase())) {
                    filtrados.add(c);
                }
            }
            clienteListView.setItems(filtrados);
            clienteListView.setVisible(true);
            clienteListView.setManaged(true);
            clienteSeleccionado = null;
        });

        clienteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, sel) -> {
            if (sel != null) {
                seleccionandoCliente = true;
                clienteSeleccionado = sel;
                clienteSearchField.setText(sel.getNombre());
                clienteListView.setVisible(false);
                clienteListView.setManaged(false);
                javafx.application.Platform.runLater(() -> seleccionandoCliente = false);
            }
        });

        clienteSearchField.setOnMouseClicked(e -> {
            seleccionandoCliente = false;
            clienteListView.setItems(clientesObs);
            clienteListView.setVisible(true);
            clienteListView.setManaged(true);
            javafx.application.Platform.runLater(() -> clienteSearchField.selectAll());
        });

        clienteSearchField.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.BACK_SPACE ||
                    e.getCode() == javafx.scene.input.KeyCode.DELETE) {
                seleccionandoCliente = false;
                clienteListView.setVisible(true);
                clienteListView.setManaged(true);
            }
        });

        clienteListView.setVisible(false);
        clienteListView.setManaged(false);
    }

    private void cargarModelos() {
        todasAlcancias = historialDAO.getAlcanciasCombo();
        ObservableList<Alcancia> alcanciasObs = FXCollections.observableArrayList(todasAlcancias);

        modeloListView.setItems(alcanciasObs);
        modeloListView.setCellFactory(lv -> new ListCell<Alcancia>() {
            @Override protected void updateItem(Alcancia a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getNombre());
            }
        });

        modeloSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (seleccionandoModelo) return;
            ObservableList<Alcancia> filtradas = FXCollections.observableArrayList();
            for (Alcancia a : todasAlcancias) {
                if (newVal == null || newVal.isEmpty() ||
                        a.getNombre().toLowerCase().contains(newVal.toLowerCase())) {
                    filtradas.add(a);
                }
            }
            modeloListView.setItems(filtradas);
            modeloListView.setVisible(true);
            modeloListView.setManaged(true);
            modeloSeleccionado = null;
        });

        modeloListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, sel) -> {
            if (sel != null) {
                seleccionandoModelo = true;
                modeloSeleccionado = sel;
                modeloSearchField.setText(sel.getNombre());
                modeloListView.setVisible(false);
                modeloListView.setManaged(false);
                javafx.application.Platform.runLater(() -> seleccionandoModelo = false);
            }
        });

        modeloSearchField.setOnMouseClicked(e -> {
            seleccionandoModelo = false;
            modeloListView.setItems(alcanciasObs);
            modeloListView.setVisible(true);
            modeloListView.setManaged(true);
            javafx.application.Platform.runLater(() -> modeloSearchField.selectAll());
        });

        modeloSearchField.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.BACK_SPACE ||
                    e.getCode() == javafx.scene.input.KeyCode.DELETE) {
                seleccionandoModelo = false;
                modeloListView.setVisible(true);
                modeloListView.setManaged(true);
            }
        });

        modeloListView.setVisible(false);
        modeloListView.setManaged(false);
    }

    private void setupColumnas() {
        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha() != null ?
                        data.getValue().getFecha().toString() : ""));
        colFolio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFolio()));
        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCliente()));
        colModelo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getModelo()));
        colCantidad.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCantidad()).asObject());
        colTotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getTotal()).asObject());
        colTotal.setCellFactory(col -> new TableCell<HistorialVenta, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        colEstadoEnvio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstadoEnvio()));
        colEstadoEnvio.setCellFactory(col -> new TableCell<HistorialVenta, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    switch (item) {
                        case "en_proceso" -> {
                            setText("En proceso");
                            setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                        }
                        case "entregado" -> {
                            setText("Entregado");
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        }
                        case "con_incidencia" -> {
                            setText("Con incidencia");
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        }
                        case "venta" -> {
                            setText("Solo venta");
                            setStyle("-fx-text-fill: #4747D4; -fx-font-style: italic;");
                        }
                        default -> {
                            setText(item);
                            setStyle("");
                        }
                    }
                }
            }
        });

        historialTable.setItems(historialList);
    }

    private void cargarTodo() {
        historialList.clear();
        historialList.addAll(historialDAO.getTodo());
        actualizarResumen();
    }

    @FXML
    private void handleBuscar() {
        LocalDate fecha = fechaPicker.getValue();
        String idCliente = clienteSeleccionado != null ?
                String.valueOf(clienteSeleccionado.getId()) : null;
        String idModelo = modeloSeleccionado != null ?
                String.valueOf(modeloSeleccionado.getId()) : null;

        String estadoEnvio = filtroEstadoEnvio.getValue();
        String estadoQuery = null;
        if (estadoEnvio != null && !estadoEnvio.equals("Todos")) {
            switch (estadoEnvio) {
                case "En proceso" -> estadoQuery = "en_proceso";
                case "Entregado" -> estadoQuery = "entregado";
                case "Con incidencia" -> estadoQuery = "con_incidencia";
                case "Venta" -> estadoQuery = "venta";
            }
        }

        historialList.clear();
        historialList.addAll(historialDAO.getHistorial(fecha, idCliente, idModelo, estadoQuery));
        actualizarResumen();
    }

    @FXML
    private void handleLimpiar() {
        fechaPicker.setValue(null);
        clienteSearchField.clear();
        modeloSearchField.clear();
        clienteSeleccionado = null;
        modeloSeleccionado = null;
        filtroEstadoEnvio.setValue("Todos");
        clienteListView.setVisible(false);
        clienteListView.setManaged(false);
        modeloListView.setVisible(false);
        modeloListView.setManaged(false);
        cargarTodo();
    }

    private void actualizarResumen() {
        double total = historialList.stream().mapToDouble(HistorialVenta::getTotal).sum();
        totalLabel.setText(String.format("$%.2f", total));
        registrosLabel.setText(String.valueOf(historialList.size()));
    }
}