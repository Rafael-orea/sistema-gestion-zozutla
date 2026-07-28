package Controller;

import Model.Alcancia;
import Model.Cliente;
import Model.DetalleVenta;
import Model.Venta;
import Model.VentaDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Model.AlcanciaDAO;

public class VentaController {

    @FXML private TextField clienteSearchField;
    @FXML private ListView<Cliente> clienteListView;
    @FXML private Label clienteSeleccionadoLabel;
    @FXML private DatePicker fechaPicker;
    @FXML private TableView<DetalleVenta> detalleTable;
    @FXML private TableColumn<DetalleVenta, String> colProducto;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleVenta, Double> colPrecio;
    @FXML private TableColumn<DetalleVenta, Double> colSubtotal;
    @FXML private TableColumn<DetalleVenta, Void> colEliminar;
    @FXML private Label totalLabel;

    private VentaDAO ventaDAO = new VentaDAO();
    private AlcanciaDAO alcanciaDAO = new AlcanciaDAO();
    private ObservableList<DetalleVenta> detalleList = FXCollections.observableArrayList();
    private List<Alcancia> alcanciasDisponibles = new ArrayList<>();
    private List<Cliente> todosClientes = new ArrayList<>();
    private Cliente clienteActual;
    private boolean seleccionandoCliente = false;

    @FXML
    public void initialize() {
        fechaPicker.setValue(LocalDate.now());
        alcanciasDisponibles = alcanciaDAO.getAllAlcancias();
        todosClientes = ventaDAO.getClientesDisponibles();
        setupColumnas();
        setupBuscadorCliente();
    }

    private void setupBuscadorCliente() {
        ObservableList<Cliente> clientesObs = FXCollections.observableArrayList(todosClientes);

        clienteListView.setItems(clientesObs);
        clienteListView.setCellFactory(lv -> new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNombre() + " - " + c.getPais());
            }
        });

        // Filtro en tiempo real
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
        });

        // Al seleccionar cliente
        clienteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, seleccionado) -> {
            if (seleccionado != null) {
                seleccionandoCliente = true;
                clienteActual = seleccionado;
                clienteSeleccionadoLabel.setText("Cliente: " + seleccionado.getNombre() + " - " + seleccionado.getPais());
                clienteSearchField.setText(seleccionado.getNombre());
                clienteListView.setVisible(false);
                clienteListView.setManaged(false);
                javafx.application.Platform.runLater(() -> seleccionandoCliente = false);
            }
        });

        // Click en el campo
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

    private void setupColumnas() {
        colProducto.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreAlcancia()));

        colCantidad.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCantidad()).asObject());

        colPrecio.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrecioUnitario()).asObject());

        colSubtotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        colEliminar.setCellFactory(col -> new TableCell<DetalleVenta, Void>() {
            private final Button btnEliminar = new Button("X");
            {
                btnEliminar.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");
                btnEliminar.setOnAction(e -> {
                    DetalleVenta detalle = getTableView().getItems().get(getIndex());
                    detalleList.remove(detalle);
                    actualizarTotal();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
            }
        });

        detalleTable.setItems(detalleList);
    }

    @FXML
    private void handleAgregarProducto() {
        ObservableList<Alcancia> alcanciasObs = FXCollections.observableArrayList(alcanciasDisponibles);

        Dialog<DetalleVenta> dialog = new Dialog<>();
        dialog.setTitle("Agregar Producto");
        dialog.setHeaderText("Selecciona una alcancia y la cantidad");

        ButtonType agregarBtn = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(agregarBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 20;");

        TextField buscarField = new TextField();
        buscarField.setPromptText("Escribe para buscar...");
        buscarField.setPrefWidth(280);

        ListView<Alcancia> listaView = new ListView<>();
        listaView.setPrefHeight(140);
        listaView.setPrefWidth(280);
        listaView.setItems(alcanciasObs);
        listaView.setCellFactory(lv -> new ListCell<Alcancia>() {
            @Override
            protected void updateItem(Alcancia a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getNombre() + " (stock: " + a.getExistencia() + ")");
            }
        });

        Label precioLabel = new Label("0.00");
        precioLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label stockLabel = new Label("Disponibles: 0");
        stockLabel.setStyle("-fx-text-fill: #6B5A45;");

        Spinner<Integer> cantidadSpinner = new Spinner<>(1, 9999, 1);
        cantidadSpinner.setPrefWidth(100);
        cantidadSpinner.setEditable(true);

        buscarField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                listaView.setItems(alcanciasObs);
            } else {
                ObservableList<Alcancia> filtradas = FXCollections.observableArrayList();
                for (Alcancia a : alcanciasDisponibles) {
                    if (a.getNombre().toLowerCase().contains(newVal.toLowerCase())) {
                        filtradas.add(a);
                    }
                }
                listaView.setItems(filtradas);
            }
        });

        listaView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, seleccionada) -> {
            if (seleccionada != null) {
                precioLabel.setText(String.valueOf(seleccionada.getPrecio()));
                stockLabel.setText("Disponibles: " + seleccionada.getExistencia());
            }
        });

        grid.add(new Label("Buscar:"), 0, 0);
        grid.add(buscarField, 1, 0);
        grid.add(new Label("Alcancia:"), 0, 1);
        grid.add(listaView, 1, 1);
        grid.add(new Label("Precio:"), 0, 2);
        grid.add(precioLabel, 1, 2);
        grid.add(new Label("Disponibles:"), 0, 3);
        grid.add(stockLabel, 1, 3);
        grid.add(new Label("Cantidad:"), 0, 4);
        grid.add(cantidadSpinner, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == agregarBtn) {
                Alcancia seleccionada = listaView.getSelectionModel().getSelectedItem();
                if (seleccionada == null) {
                    showAlert("Error", "Selecciona una alcancia de la lista.");
                    return null;
                }
                int cantidad = cantidadSpinner.getValue();
                if (cantidad > seleccionada.getExistencia()) {
                    showAlert("Error", "No hay suficiente stock. Disponibles: " + seleccionada.getExistencia());
                    return null;
                }
                DetalleVenta detalle = new DetalleVenta();
                detalle.setIdAlcancia(seleccionada.getId());
                detalle.setNombreAlcancia(seleccionada.getNombre());
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(seleccionada.getPrecio());
                detalle.setSubtotal(seleccionada.getPrecio() * cantidad);
                return detalle;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(detalle -> {
            detalleList.add(detalle);
            actualizarTotal();
        });
    }

    private void actualizarTotal() {
        double total = detalleList.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        totalLabel.setText(String.format("$%.2f", total));
    }

    @FXML
    private void handleRegistrarVenta() {
        if (clienteActual == null) {
            showAlert("Error", "Selecciona un cliente.");
            return;
        }
        if (detalleList.isEmpty()) {
            showAlert("Error", "Agrega al menos un producto.");
            return;
        }

        Venta venta = new Venta();
        venta.setIdCliente(clienteActual.getId());
        venta.setNombreCliente(clienteActual.getNombre());
        venta.setFecha(fechaPicker.getValue());
        venta.setTotal(detalleList.stream().mapToDouble(DetalleVenta::getSubtotal).sum());
        venta.setDetalles(new ArrayList<>(detalleList));

        if (ventaDAO.registrarVenta(venta)) {
            abrirDetalleVenta(venta);
        } else {
            showAlert("Error", "No se pudo registrar la venta.");
        }
    }

    private void abrirDetalleVenta(Venta venta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/DetalleVentaView.fxml"));
            Parent root = loader.load();

            DetalleVentaController controller = loader.getController();
            controller.setVenta(venta);

            Stage stage = new Stage();
            stage.setTitle("Venta Registrada");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            clienteSearchField.clear();
            clienteActual = null;
            clienteSeleccionadoLabel.setText("Ningun cliente seleccionado");
            fechaPicker.setValue(LocalDate.now());
            detalleList.clear();
            totalLabel.setText("0.00");
            alcanciasDisponibles = alcanciaDAO.getAllAlcancias();

        } catch (IOException e) {
            showAlert("Error", "No se pudo abrir el detalle: " + e.getMessage());
        }
    }

    @FXML
    private void handlePanelPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/PanelView.fxml"));
            Parent root = loader.load();
            clienteSearchField.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar el panel: " + e.getMessage());
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