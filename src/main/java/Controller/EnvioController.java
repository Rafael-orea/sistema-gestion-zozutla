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
    @FXML private ComboBox<Cliente> clienteCombo;
    @FXML private TableView<Envio> envioTable;
    @FXML private TableColumn<Envio, String> colId;
    @FXML private TableColumn<Envio, String> colFolio;
    @FXML private TableColumn<Envio, String> colCliente;
    @FXML private TableColumn<Envio, String> colDestino;
    @FXML private TableColumn<Envio, String> colEstado;
    @FXML private TableColumn<Envio, Void> colAcciones;
    @FXML private Label totalLabel;

    private EnvioDAO envioDAO = new EnvioDAO();
    private ObservableList<Envio> envioList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumnas();
        cargarClientes();
        cargarEnvios();
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

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstado()));
        colEstado.setCellFactory(col -> new TableCell<Envio, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
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
                        default -> setText(item);
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

    private void cargarClientes() {
        List<Cliente> clientes = envioDAO.getClientesDisponibles();
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

    private void cargarEnvios() {
        envioList.clear();
        envioList.addAll(envioDAO.getAllEnvios());
        actualizarTotal();
    }

    @FXML
    private void handleBuscar() {
        LocalDate fecha = fechaPicker.getValue();
        Cliente clienteSeleccionado = clienteCombo.getValue();
        String idCliente = (clienteSeleccionado == null || clienteSeleccionado.getId() == 0)
                ? null : String.valueOf(clienteSeleccionado.getId());

        envioList.clear();
        envioList.addAll(envioDAO.searchEnvios(fecha, idCliente));
        actualizarTotal();
    }

    @FXML
    private void handleAgregarEnvio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ReportarEnvioView.fxml"));
            Parent root = loader.load();
            // Buscar el contentArea del MainController
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
            stage.setTitle("Incidencia de Envio");
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