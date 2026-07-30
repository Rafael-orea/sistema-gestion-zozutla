package Controller;

import Model.*;
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

public class VentaController {

    @FXML private RadioButton rbNormal;
    @FXML private RadioButton rbMayoreo;
    @FXML private TextField clienteSearchField;
    @FXML private ListView<Cliente> clienteListView;
    @FXML private Label clienteSeleccionadoLabel;
    @FXML private Label descuentoLabel;
    @FXML private DatePicker fechaPicker;
    @FXML private TableView<DetalleVenta> detalleTable;
    @FXML private TableColumn<DetalleVenta, String> colProducto;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleVenta, Double> colPrecio;
    @FXML private TableColumn<DetalleVenta, Double> colDescuento;
    @FXML private TableColumn<DetalleVenta, Double> colSubtotal;
    @FXML private TableColumn<DetalleVenta, Void> colEliminar;
    @FXML private Label totalLabel;

    private AlcanciaDAO alcanciaDAO = new AlcanciaDAO();
    private VentaDAO ventaDAO = new VentaDAO();
    private ObservableList<DetalleVenta> detalleList = FXCollections.observableArrayList();
    private List<Alcancia> todasAlcancias = new ArrayList<>();
    private List<Cliente> todosClientes = new ArrayList<>();
    private Cliente clienteActual = null;
    private boolean esCompradorNormal = false;
    private boolean seleccionandoCliente = false;

    private static final double DESCUENTO_CLIENTE = 0.15;

    @FXML
    public void initialize() {
        fechaPicker.setValue(LocalDate.now());

        ToggleGroup grupo = new ToggleGroup();
        rbNormal.setToggleGroup(grupo);
        rbMayoreo.setToggleGroup(grupo);
        rbNormal.setSelected(true);

        todasAlcancias = alcanciaDAO.getAllAlcancias();
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
            esCompradorNormal = false;
        });

        clienteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, seleccionado) -> {
            if (seleccionado != null) {
                seleccionandoCliente = true;
                clienteActual = seleccionado;
                esCompradorNormal = false;
                clienteSeleccionadoLabel.setText("Cliente: " + seleccionado.getNombre());
                descuentoLabel.setText("15% de descuento aplicado");
                clienteSearchField.setText(seleccionado.getNombre());
                clienteListView.setVisible(false);
                clienteListView.setManaged(false);
                recalcularDetalles();
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

    @FXML
    private void handleCompradorNormal() {
        clienteActual = null;
        esCompradorNormal = true;
        clienteSearchField.clear();
        clienteListView.setVisible(false);
        clienteListView.setManaged(false);
        clienteSeleccionadoLabel.setText("Comprador normal (sin registro)");
        descuentoLabel.setText("");
        recalcularDetalles();
    }

    private void setupColumnas() {
        colProducto.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreAlcancia()));

        colCantidad.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCantidad()).asObject());

        colPrecio.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrecioUnitario()).asObject());
        colPrecio.setCellFactory(col -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        colDescuento.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getDescuento()).asObject());
        colDescuento.setCellFactory(col -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else if (item > 0) {
                    setText(String.format("-$%.2f", item));
                    setStyle("-fx-text-fill: #27ae60;");
                } else {
                    setText("--");
                    setStyle("");
                }
            }
        });

        colSubtotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());
        colSubtotal.setCellFactory(col -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        colEliminar.setCellFactory(col -> new TableCell<DetalleVenta, Void>() {
            private final Button btnEliminar = new Button("X");
            {
                btnEliminar.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");
                btnEliminar.setOnAction(e -> {
                    detalleList.remove(getTableView().getItems().get(getIndex()));
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

    private double getPrecioSegunTipo(Alcancia a) {
        return rbMayoreo.isSelected() ? a.getPrecioMayoreo() : a.getPrecio();
    }

    private void recalcularDetalles() {
        for (DetalleVenta d : detalleList) {
            double descuento = clienteActual != null ? d.getPrecioUnitario() * DESCUENTO_CLIENTE * d.getCantidad() : 0;
            d.setDescuento(descuento);
            d.setSubtotal((d.getPrecioUnitario() * d.getCantidad()) - descuento);
        }
        detalleTable.refresh();
        actualizarTotal();
    }

    @FXML
    private void handleAgregarProducto() {
        if (clienteActual == null && !esCompradorNormal) {
            showAlert("Error", "Selecciona un cliente o elige Comprador Normal primero.");
            return;
        }

        ObservableList<Alcancia> alcanciasObs = FXCollections.observableArrayList(
                todasAlcancias.stream()
                        .filter(a -> a.getExistencia() > 0)
                        .collect(java.util.stream.Collectors.toList())
        );

        Dialog<DetalleVenta> dialog = new Dialog<>();
        dialog.setTitle("Agregar Producto");
        dialog.setHeaderText("Selecciona una alcancia");

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
                if (empty || a == null) setText(null);
                else {
                    double precio = getPrecioSegunTipo(a);
                    setText(a.getNombre() + " | Stock: " + a.getExistencia() +
                            " | $" + String.format("%.2f", precio));
                }
            }
        });

        Label precioLabel = new Label("--");
        precioLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label descLabel = new Label("");
        descLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

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
                for (Alcancia a : todasAlcancias) {
                    if (a.getExistencia() > 0 &&
                            a.getNombre().toLowerCase().contains(newVal.toLowerCase())) {
                        filtradas.add(a);
                    }
                }
                listaView.setItems(filtradas);
            }
        });

        listaView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, seleccionada) -> {
            if (seleccionada != null) {
                double precio = getPrecioSegunTipo(seleccionada);
                precioLabel.setText(String.format("$%.2f", precio));
                stockLabel.setText("Disponibles: " + seleccionada.getExistencia());
                if (clienteActual != null) {
                    double desc = precio * DESCUENTO_CLIENTE;
                    descLabel.setText(String.format("Con descuento: $%.2f", precio - desc));
                } else {
                    descLabel.setText("");
                }
            }
        });

        grid.add(new Label("Buscar:"), 0, 0);
        grid.add(buscarField, 1, 0);
        grid.add(new Label("Alcancia:"), 0, 1);
        grid.add(listaView, 1, 1);
        grid.add(new Label("Precio:"), 0, 2);
        grid.add(precioLabel, 1, 2);
        grid.add(new Label("Precio con desc:"), 0, 3);
        grid.add(descLabel, 1, 3);
        grid.add(new Label("Stock:"), 0, 4);
        grid.add(stockLabel, 1, 4);
        grid.add(new Label("Cantidad:"), 0, 5);
        grid.add(cantidadSpinner, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == agregarBtn) {
                Alcancia seleccionada = listaView.getSelectionModel().getSelectedItem();
                if (seleccionada == null) {
                    showAlert("Error", "Selecciona una alcancia.");
                    return null;
                }
                int cantidad = cantidadSpinner.getValue();
                if (cantidad > seleccionada.getExistencia()) {
                    showAlert("Error", "Stock insuficiente. Disponibles: " + seleccionada.getExistencia());
                    return null;
                }

                double precio = getPrecioSegunTipo(seleccionada);
                double descuento = clienteActual != null ? precio * DESCUENTO_CLIENTE * cantidad : 0;
                double subtotal = (precio * cantidad) - descuento;

                DetalleVenta d = new DetalleVenta();
                d.setIdAlcancia(seleccionada.getId());
                d.setNombreAlcancia(seleccionada.getNombre());
                d.setCantidad(cantidad);
                d.setPrecioUnitario(precio);
                d.setDescuento(descuento);
                d.setSubtotal(subtotal);
                return d;
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
        if (clienteActual == null && !esCompradorNormal) {
            showAlert("Error", "Selecciona un cliente o elige Comprador Normal.");
            return;
        }
        if (detalleList.isEmpty()) {
            showAlert("Error", "Agrega al menos un producto.");
            return;
        }

        Venta venta = new Venta();
        venta.setIdCliente(clienteActual != null ? clienteActual.getId() : 0);
        venta.setNombreCliente(clienteActual != null ? clienteActual.getNombre() : "Comprador Normal");
        venta.setFecha(fechaPicker.getValue());
        venta.setTotal(detalleList.stream().mapToDouble(DetalleVenta::getSubtotal).sum());
        venta.setTipoPrecio(rbMayoreo.isSelected() ? "mayoreo" : "normal");
        venta.setCompradorNormal(esCompradorNormal);
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

            limpiarFormulario();

        } catch (IOException e) {
            showAlert("Error", "No se pudo abrir el detalle: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        clienteSearchField.clear();
        clienteActual = null;
        esCompradorNormal = false;
        clienteSeleccionadoLabel.setText("Sin cliente seleccionado");
        descuentoLabel.setText("");
        fechaPicker.setValue(LocalDate.now());
        detalleList.clear();
        totalLabel.setText("0.00");
        rbNormal.setSelected(true);
        todasAlcancias = alcanciaDAO.getAllAlcancias();
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