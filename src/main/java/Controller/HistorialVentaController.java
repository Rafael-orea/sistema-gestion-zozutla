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
    @FXML private ComboBox<Cliente> clienteCombo;
    @FXML private ComboBox<Alcancia> modeloCombo;
    @FXML private TableView<HistorialVenta> historialTable;
    @FXML private TableColumn<HistorialVenta, String> colFecha;
    @FXML private TableColumn<HistorialVenta, String> colFolio;
    @FXML private TableColumn<HistorialVenta, String> colCliente;
    @FXML private TableColumn<HistorialVenta, String> colModelo;
    @FXML private TableColumn<HistorialVenta, Integer> colCantidad;
    @FXML private TableColumn<HistorialVenta, Double> colTotal;
    @FXML private Label totalLabel;
    @FXML private Label registrosLabel;

    private HistorialVentaDAO historialDAO = new HistorialVentaDAO();
    private ObservableList<HistorialVenta> historialList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumnas();
        cargarClientes();
        cargarModelos();
        cargarTodo();
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
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        historialTable.setItems(historialList);
    }

    private void cargarClientes() {
        List<Cliente> clientes = historialDAO.getClientesCombo();
        Cliente todos = new Cliente();
        todos.setId(0);
        todos.setNombre("Todos");
        clientes.add(0, todos);

        clienteCombo.setItems(FXCollections.observableArrayList(clientes));
        clienteCombo.setValue(todos);
        clienteCombo.setConverter(new javafx.util.StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) { return c == null ? "" : c.getNombre(); }
            @Override
            public Cliente fromString(String s) { return null; }
        });
    }

    private void cargarModelos() {
        List<Alcancia> alcancias = historialDAO.getAlcanciasCombo();
        Alcancia todos = new Alcancia();
        todos.setId(0);
        todos.setNombre("Todos");
        alcancias.add(0, todos);

        modeloCombo.setItems(FXCollections.observableArrayList(alcancias));
        modeloCombo.setValue(todos);
        modeloCombo.setConverter(new javafx.util.StringConverter<Alcancia>() {
            @Override
            public String toString(Alcancia a) { return a == null ? "" : a.getNombre(); }
            @Override
            public Alcancia fromString(String s) { return null; }
        });
    }

    private void cargarTodo() {
        historialList.clear();
        historialList.addAll(historialDAO.getTodo());
        actualizarResumen();
    }

    @FXML
    private void handleBuscar() {
        LocalDate fecha = fechaPicker.getValue();

        Cliente clienteSeleccionado = clienteCombo.getValue();
        String idCliente = (clienteSeleccionado == null || clienteSeleccionado.getId() == 0)
                ? null : String.valueOf(clienteSeleccionado.getId());

        Alcancia modeloSeleccionado = modeloCombo.getValue();
        String idModelo = (modeloSeleccionado == null || modeloSeleccionado.getId() == 0)
                ? null : String.valueOf(modeloSeleccionado.getId());

        historialList.clear();
        historialList.addAll(historialDAO.getHistorial(fecha, idCliente, idModelo));
        actualizarResumen();
    }

    private void actualizarResumen() {
        double total = historialList.stream().mapToDouble(HistorialVenta::getTotal).sum();
        totalLabel.setText(String.format("$%.2f", total));
        registrosLabel.setText(String.valueOf(historialList.size()));
    }
}