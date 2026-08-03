package Controller;

import Model.ReporteDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class ReporteController {

    // Filtros
    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;
    @FXML private TabPane tabPane;

    // Resumen ganancias
    @FXML private Label lblTotalVentas;
    @FXML private Label lblNumVentas;
    @FXML private Label lblDescuentos;
    @FXML private Label lblGananciaNet;

    // Tabla top productos
    @FXML private TableView<Object[]> topProductosTable;
    @FXML private TableColumn<Object[], String> colTopNombre;
    @FXML private TableColumn<Object[], Integer> colTopPiezas;
    @FXML private TableColumn<Object[], Double> colTopTotal;

    // Tabla ventas por cliente
    @FXML private TableView<Object[]> clientesTable;
    @FXML private TableColumn<Object[], String> colClienteNombre;
    @FXML private TableColumn<Object[], Integer> colClienteVentas;
    @FXML private TableColumn<Object[], Double> colClienteTotal;

    // Resumen perdidas
    @FXML private Label lblPerdidasMerma;
    @FXML private Label lblPerdidasEnvio;
    @FXML private Label lblPerdidasTotal;

    // Tabla mermas
    @FXML private TableView<Object[]> mermasTable;
    @FXML private TableColumn<Object[], String> colMermaNombre;
    @FXML private TableColumn<Object[], Integer> colMermaCantidad;
    @FXML private TableColumn<Object[], String> colMermaMotivo;
    @FXML private TableColumn<Object[], String> colMermaFecha;
    @FXML private TableColumn<Object[], Double> colMermaValor;

    // Tabla incidencias
    @FXML private TableView<Object[]> incidenciasTable;
    @FXML private TableColumn<Object[], String> colIncFolio;
    @FXML private TableColumn<Object[], String> colIncProducto;
    @FXML private TableColumn<Object[], Integer> colIncCantidad;
    @FXML private TableColumn<Object[], String> colIncResponsabilidad;
    @FXML private TableColumn<Object[], Double> colIncPctCliente;
    @FXML private TableColumn<Object[], Double> colIncPctZozutla;
    @FXML private TableColumn<Object[], Double> colIncPerdidaZozutla;
    @FXML private TableColumn<Object[], String> colIncFecha;

    private ReporteDAO reporteDAO = new ReporteDAO();

    @FXML
    public void initialize() {
        // Fechas por defecto: mes actual
        fechaInicioPicker.setValue(LocalDate.now().withDayOfMonth(1));
        fechaFinPicker.setValue(LocalDate.now());

        setupTablas();
        cargarReporte();
    }

    private void setupTablas() {
        // Top productos
        colTopNombre.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[0]));
        colTopPiezas.setCellValueFactory(data ->
                new SimpleIntegerProperty((Integer) data.getValue()[1]).asObject());
        colTopTotal.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[2]).asObject());
        colTopTotal.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        // Clientes
        colClienteNombre.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[0]));
        colClienteVentas.setCellValueFactory(data ->
                new SimpleIntegerProperty((Integer) data.getValue()[1]).asObject());
        colClienteTotal.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[2]).asObject());
        colClienteTotal.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        // Mermas
        colMermaNombre.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[0]));
        colMermaCantidad.setCellValueFactory(data ->
                new SimpleIntegerProperty((Integer) data.getValue()[1]).asObject());
        colMermaMotivo.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[2]));
        colMermaFecha.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[3]));
        colMermaValor.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[4]).asObject());
        colMermaValor.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        // Incidencias
        colIncFolio.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[0]));
        colIncProducto.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[1]));
        colIncCantidad.setCellValueFactory(data ->
                new SimpleIntegerProperty((Integer) data.getValue()[2]).asObject());
        colIncResponsabilidad.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[3].equals("cliente") ? "Cliente 100%" : "Acuerdo"));
        colIncPctCliente.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[4]).asObject());
        colIncPctCliente.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.0f pct", item));
            }
        });
        colIncPctZozutla.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[5]).asObject());
        colIncPctZozutla.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.0f pct", item));
            }
        });
        colIncPerdidaZozutla.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[9]).asObject());
        colIncPerdidaZozutla.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    setText(String.format("$%.2f", item));
                    setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });
        colIncFecha.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[7]));
    }

    @FXML
    private void handleGenerar() {
        if (fechaInicioPicker.getValue() == null || fechaFinPicker.getValue() == null) {
            showAlert("Error", "Selecciona el periodo.");
            return;
        }
        if (fechaInicioPicker.getValue().isAfter(fechaFinPicker.getValue())) {
            showAlert("Error", "La fecha de inicio no puede ser mayor a la fecha final.");
            return;
        }
        cargarReporte();
    }

    private void cargarReporte() {
        LocalDate inicio = fechaInicioPicker.getValue();
        LocalDate fin = fechaFinPicker.getValue();

        // Ganancias
        double totalVentas = reporteDAO.getTotalVentasPeriodo(inicio, fin);
        int numVentas = reporteDAO.getTotalVentasCount(inicio, fin);
        double descuentos = reporteDAO.getTotalDescuentos(inicio, fin);
        double perdidasMerma = reporteDAO.getTotalPerdidasMerma(inicio, fin);
        double perdidasEnvio = reporteDAO.getTotalPerdidasEnvio(inicio, fin);
        double totalPerdidas = perdidasMerma + perdidasEnvio;
        double gananciaNet = totalVentas - totalPerdidas;

        lblTotalVentas.setText(String.format("$%.2f", totalVentas));
        lblNumVentas.setText(String.valueOf(numVentas));
        lblDescuentos.setText(String.format("$%.2f", descuentos));
        lblGananciaNet.setText(String.format("$%.2f", gananciaNet));
        lblGananciaNet.setStyle(gananciaNet >= 0
                ? "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #27ae60;"
                : "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        // Top productos
        List<Object[]> topProductos = reporteDAO.getTopProductosVendidos(inicio, fin);
        topProductosTable.setItems(FXCollections.observableArrayList(topProductos));

        // Ventas por cliente
        List<Object[]> clientes = reporteDAO.getVentasPorCliente(inicio, fin);
        clientesTable.setItems(FXCollections.observableArrayList(clientes));

        // Perdidas
        lblPerdidasMerma.setText(String.format("$%.2f", perdidasMerma));
        lblPerdidasEnvio.setText(String.format("$%.2f", perdidasEnvio));
        lblPerdidasTotal.setText(String.format("$%.2f", totalPerdidas));
        lblPerdidasTotal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        // Mermas
        List<Object[]> mermas = reporteDAO.getDetalleMermas(inicio, fin);
        mermasTable.setItems(FXCollections.observableArrayList(mermas));

        // Incidencias
        List<Object[]> incidencias = reporteDAO.getDetalleIncidencias(inicio, fin);
        incidenciasTable.setItems(FXCollections.observableArrayList(incidencias));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}