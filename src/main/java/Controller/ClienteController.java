package Controller;

import Model.Cliente;
import Model.ClienteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class ClienteController {

    @FXML private TextField searchField;
    @FXML private TableView<Cliente> clienteTable;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCiudad;
    @FXML private TableColumn<Cliente, String> colPais;
    @FXML private TableColumn<Cliente, String> colTipo;
    @FXML private TableColumn<Cliente, Void> colAcciones;
    @FXML private Label totalLabel;

    private ClienteDAO clienteDAO = new ClienteDAO();
    private ObservableList<Cliente> clienteList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumnas();
        cargarClientes();
        setupBuscador();
    }

    private void setupColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colPais.setCellValueFactory(new PropertyValueFactory<>("pais"));

        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setCellFactory(col -> new TableCell<Cliente, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    switch (item) {
                        case "nacional" -> {
                            setText("Nacional");
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        }
                        case "internacional" -> {
                            setText("Internacional");
                            setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
                        }
                        default -> {
                            setText(item);
                            setStyle("");
                        }
                    }
                }
            }
        });

        colAcciones.setCellFactory(col -> new TableCell<Cliente, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setStyle("-fx-background-color: #C8A96E; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");

                btnEditar.setOnAction(e -> {
                    Cliente c = getTableView().getItems().get(getIndex());
                    mostrarDialogo(c);
                });
                btnEliminar.setOnAction(e -> {
                    Cliente c = getTableView().getItems().get(getIndex());
                    eliminarCliente(c);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(6, btnEditar, btnEliminar);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        clienteTable.setItems(clienteList);
    }

    private void cargarClientes() {
        clienteList.clear();
        clienteList.addAll(clienteDAO.getAllClientes());
        actualizarTotal();
    }

    private void setupBuscador() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                cargarClientes();
            } else {
                clienteList.clear();
                clienteList.addAll(clienteDAO.searchClientes(newVal));
                actualizarTotal();
            }
        });
    }

    private void actualizarTotal() {
        if (totalLabel != null)
            totalLabel.setText(String.valueOf(clienteList.size()));
    }

    @FXML
    private void handleAgregarCliente() {
        mostrarDialogo(null);
    }

    private void mostrarDialogo(Cliente cliente) {
        boolean esEdicion = cliente != null;

        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Editar Cliente" : "Agregar Cliente");
        dialog.setHeaderText(esEdicion ? "Editar informacion del cliente" : "Registrar nuevo cliente");

        ButtonType guardarBtn = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 20;");

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre completo");
        nombreField.setPrefWidth(280);

        TextField telefonoField = new TextField();
        telefonoField.setPromptText("10 digitos");
        telefonoField.setPrefWidth(280);
// Solo permite 10 digitos numericos
        telefonoField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                telefonoField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (telefonoField.getText().length() > 10) {
                telefonoField.setText(telefonoField.getText().substring(0, 10));
            }
        });

        TextField calleField = new TextField();
        calleField.setPromptText("Calle y numero");
        calleField.setPrefWidth(280);

        TextField ciudadField = new TextField();
        ciudadField.setPromptText("Ciudad");
        ciudadField.setPrefWidth(280);

        TextField estadoField = new TextField();
        estadoField.setPromptText("Estado o Provincia");
        estadoField.setPrefWidth(280);

        TextField cpField = new TextField();
        cpField.setPromptText("5 digitos");
        cpField.setPrefWidth(280);
// Solo permite 5 digitos numericos
        cpField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                cpField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (cpField.getText().length() > 5) {
                cpField.setText(cpField.getText().substring(0, 5));
            }
        });

        TextField paisField = new TextField();
        paisField.setPromptText("Pais");
        paisField.setPrefWidth(280);

        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.setItems(FXCollections.observableArrayList("nacional", "internacional"));
        tipoCombo.setValue("nacional");
        tipoCombo.setPrefWidth(280);

        if (esEdicion) {
            nombreField.setText(cliente.getNombre());
            telefonoField.setText(cliente.getTelefono() != null ? cliente.getTelefono() : "");
            calleField.setText(cliente.getCalle() != null ? cliente.getCalle() : "");
            ciudadField.setText(cliente.getCiudad() != null ? cliente.getCiudad() : "");
            estadoField.setText(cliente.getEstadoRegion() != null ? cliente.getEstadoRegion() : "");
            cpField.setText(cliente.getCodigoPostal() != null ? cliente.getCodigoPostal() : "");
            paisField.setText(cliente.getPais() != null ? cliente.getPais() : "");
            tipoCombo.setValue(cliente.getTipo());
        }

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Telefono:"), 0, 1);
        grid.add(telefonoField, 1, 1);
        grid.add(new Label("Calle y numero:"), 0, 2);
        grid.add(calleField, 1, 2);
        grid.add(new Label("Ciudad:"), 0, 3);
        grid.add(ciudadField, 1, 3);
        grid.add(new Label("Estado/Provincia:"), 0, 4);
        grid.add(estadoField, 1, 4);
        grid.add(new Label("Codigo postal:"), 0, 5);
        grid.add(cpField, 1, 5);
        grid.add(new Label("Pais:"), 0, 6);
        grid.add(paisField, 1, 6);
        grid.add(new Label("Tipo:"), 0, 7);
        grid.add(tipoCombo, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == guardarBtn) {
                if (nombreField.getText().trim().isEmpty()) {
                    showAlert("Error", "El nombre es obligatorio.");
                    return null;
                }
                Cliente c = new Cliente();
                if (esEdicion) c.setId(cliente.getId());
                c.setNombre(nombreField.getText().trim());
                c.setTelefono(telefonoField.getText().trim());
                c.setCalle(calleField.getText().trim());
                c.setCiudad(ciudadField.getText().trim());
                c.setEstadoRegion(estadoField.getText().trim());
                c.setCodigoPostal(cpField.getText().trim());
                c.setPais(paisField.getText().trim());
                c.setTipo(tipoCombo.getValue());
                return c;
            }
            return null;
        });

        Optional<Cliente> result = dialog.showAndWait();
        result.ifPresent(c -> {
            boolean ok = esEdicion ? clienteDAO.updateCliente(c) : clienteDAO.createCliente(c);
            if (ok) {
                showAlert("Exito", esEdicion ? "Cliente actualizado." : "Cliente registrado.");
                cargarClientes();
            } else {
                showAlert("Error", "No se pudo guardar el cliente.");
            }
        });
    }

    private void eliminarCliente(Cliente c) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Eliminar cliente");
        confirm.setContentText("Desea eliminar al cliente: " + c.getNombre() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (clienteDAO.deleteCliente(c.getId())) {
                showAlert("Exito", "Cliente eliminado.");
                cargarClientes();
            } else {
                showAlert("Error", "No se pudo eliminar. El cliente tiene ventas registradas.");
            }
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