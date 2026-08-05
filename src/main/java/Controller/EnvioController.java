package Controller;

import Model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EnvioController {

    @FXML private DatePicker fechaPicker;
    @FXML private TextField clienteSearchField;
    @FXML private ListView<Cliente> clienteListView;
    @FXML private ComboBox<String> filtroEstado;
    @FXML private TableView<Envio> envioTable;
    @FXML private TableColumn<Envio, String> colId;
    @FXML private TableColumn<Envio, String> colFolio;
    @FXML private TableColumn<Envio, String> colCliente;
    @FXML private TableColumn<Envio, String> colDestino;
    @FXML private TableColumn<Envio, String> colFlete;
    @FXML private TableColumn<Envio, String> colEstado;
    @FXML private TableColumn<Envio, Void> colAcciones;
    @FXML private Label totalLabel;

    private EnvioDAO envioDAO = new EnvioDAO();
    private ObservableList<Envio> envioList = FXCollections.observableArrayList();
    private List<Cliente> todosClientes;
    private Cliente clienteSeleccionado;
    private boolean seleccionandoCliente = false;

    @FXML
    public void initialize() {
        filtroEstado.setItems(FXCollections.observableArrayList(
                "Todos", "En proceso", "Entregado", "Con incidencia"
        ));
        filtroEstado.setValue("Todos");

        setupColumnas();
        cargarClientes();
        cargarEnvios();
    }

    private void cargarClientes() {
        todosClientes = envioDAO.getClientesDisponibles();
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

    private void setupColumnas() {
        colId.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        colFolio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFolio()));
        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCliente()));
        colDestino.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDestino()));

        colFlete.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFlete()));
        colFlete.setCellFactory(col -> new TableCell<Envio, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    switch (item) {
                        case "zozutla" -> {
                            setText("Zozutla lo lleva");
                            setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
                        }
                        case "cliente" -> {
                            setText("Cliente recoge");
                            setStyle("-fx-text-fill: #6B5A45;");
                        }
                        default -> { setText(item); setStyle(""); }
                    }
                }
            }
        });

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstado()));
        colEstado.setCellFactory(col -> new TableCell<Envio, String>() {
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
                        default -> { setText(item); setStyle(""); }
                    }
                }
            }
        });

        colAcciones.setCellFactory(col -> new TableCell<Envio, Void>() {
            private final Button btnEntregado = new Button("Entregado");
            private final Button btnIncidencia = new Button("Incidencia");
            private final Button btnVerIncidencia = new Button("Ver incidencia");

            {
                btnEntregado.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 4 6; -fx-cursor: hand;");
                btnIncidencia.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 4 6; -fx-cursor: hand;");
                btnVerIncidencia.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 4 6; -fx-cursor: hand;");

                btnEntregado.setOnAction(e -> {
                    Envio envio = getTableView().getItems().get(getIndex());
                    marcarEntregado(envio);
                });
                btnIncidencia.setOnAction(e -> {
                    Envio envio = getTableView().getItems().get(getIndex());
                    abrirIncidencia(envio);
                });
                btnVerIncidencia.setOnAction(e -> {
                    Envio envio = getTableView().getItems().get(getIndex());
                    verIncidencias(envio);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(4, btnEntregado, btnIncidencia, btnVerIncidencia);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        envioTable.setItems(envioList);
    }

    private void cargarEnvios() {
        envioList.clear();
        envioList.addAll(envioDAO.getAllEnvios());
        actualizarTotal();
    }

    @FXML
    private void handleBuscar() {
        LocalDate fecha = fechaPicker.getValue();
        String idCliente = clienteSeleccionado != null ?
                String.valueOf(clienteSeleccionado.getId()) : null;

        String estadoSeleccionado = filtroEstado.getValue();
        String estadoQuery = null;
        if (estadoSeleccionado != null && !estadoSeleccionado.equals("Todos")) {
            switch (estadoSeleccionado) {
                case "En proceso" -> estadoQuery = "en_proceso";
                case "Entregado" -> estadoQuery = "entregado";
                case "Con incidencia" -> estadoQuery = "con_incidencia";
            }
        }

        envioList.clear();
        envioList.addAll(envioDAO.searchEnvios(fecha, idCliente, estadoQuery));
        actualizarTotal();
    }

    @FXML
    private void handleLimpiar() {
        fechaPicker.setValue(null);
        clienteSearchField.clear();
        clienteSeleccionado = null;
        filtroEstado.setValue("Todos");
        clienteListView.setVisible(false);
        clienteListView.setManaged(false);
        cargarEnvios();
    }

    @FXML
    private void handleAgregarEnvio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ReportarEnvioView.fxml"));
            Parent root = loader.load();
            javafx.scene.layout.StackPane contentArea =
                    (javafx.scene.layout.StackPane) envioTable.getScene().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            showAlert("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    private void marcarEntregado(Envio envio) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Marcar como entregado");
        confirm.setContentText("Desea marcar el envio " + envio.getFolio() + " como entregado?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (envioDAO.actualizarEstado(envio.getId(), "entregado")) {
                cargarEnvios();
            } else {
                showAlert("Error", "No se pudo actualizar el estado.");
            }
        }
    }

    private void abrirIncidencia(Envio envio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/IncidenciaEnvioView.fxml"));
            Parent root = loader.load();
            IncidenciaEnvioController controller = loader.getController();
            controller.setEnvio(envio);
            Stage stage = new Stage();
            stage.setTitle("Incidencia - " + envio.getFolio());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarEnvios();
        } catch (IOException e) {
            showAlert("Error", "No se pudo abrir la incidencia: " + e.getMessage());
        }
    }

    private void verIncidencias(Envio envio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/VerIncidenciaView.fxml"));
            Parent root = loader.load();
            VerIncidenciaController controller = loader.getController();
            controller.setEnvio(envio);
            Stage stage = new Stage();
            stage.setTitle("Incidencias - " + envio.getFolio());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "No se pudo abrir las incidencias: " + e.getMessage());
        }
    }

    private void actualizarTotal() {
        totalLabel.setText(String.valueOf(envioList.size()));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}