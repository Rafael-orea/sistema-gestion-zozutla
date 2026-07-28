package Controller;

import Model.Molde;
import Model.MoldeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.Optional;

public class MoldeController {

    @FXML private TextField searchField;
    @FXML private TableView<Molde> moldeTable;
    @FXML private TableColumn<Molde, Integer> colId;
    @FXML private TableColumn<Molde, String> colNombre;
    @FXML private TableColumn<Molde, String> colEstado;
    @FXML private TableColumn<Molde, LocalDate> colFecha;
    @FXML private TableColumn<Molde, Void> colAcciones;
    @FXML private Label totalLabel;

    private MoldeDAO moldeDAO = new MoldeDAO();
    private ObservableList<Molde> moldeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumnas();
        cargarMoldes();
        setupBuscador();
    }

    private void setupColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));

        colEstado.setCellFactory(col -> new TableCell<Molde, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toUpperCase());
                    switch (item) {
                        case "bueno" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        case "dañado" -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        case "fuera_de_uso" -> setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });

        colAcciones.setCellFactory(col -> new TableCell<Molde, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setStyle("-fx-background-color: #C8A96E; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");

                btnEditar.setOnAction(e -> {
                    Molde molde = getTableView().getItems().get(getIndex());
                    mostrarDialogo(molde);
                });

                btnEliminar.setOnAction(e -> {
                    Molde molde = getTableView().getItems().get(getIndex());
                    eliminarMolde(molde);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, btnEditar, btnEliminar);
                    box.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    private void cargarMoldes() {
        moldeList.clear();
        moldeList.addAll(moldeDAO.getAllMoldes());
        moldeTable.setItems(moldeList);
        actualizarTotal();
    }

    private void setupBuscador() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                cargarMoldes();
            } else {
                moldeList.clear();
                moldeList.addAll(moldeDAO.searchMoldes(newVal));
                moldeTable.setItems(moldeList);
                actualizarTotal();
            }
        });
    }

    private void actualizarTotal() {
        if (totalLabel != null)
            totalLabel.setText(String.valueOf(moldeList.size()));
    }

    @FXML
    private void handleAgregarMolde() {
        mostrarDialogo(null);
    }

    private void mostrarDialogo(Molde molde) {
        boolean esEdicion = molde != null;

        Dialog<Molde> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Editar Molde" : "Agregar Molde");
        dialog.setHeaderText(esEdicion ? "Editar informacion del molde" : "Registrar nuevo molde");

        ButtonType guardarBtn = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 20;");

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre del molde");
        nombreField.setPrefWidth(280);

        TextField cantidadField = new TextField();
        cantidadField.setPromptText("Cantidad");

        ComboBox<String> estadoCombo = new ComboBox<>();
        estadoCombo.setItems(FXCollections.observableArrayList("bueno", "dañado", "fuera_de_uso"));
        estadoCombo.setValue("bueno");
        estadoCombo.setPrefWidth(280);

        DatePicker fechaPicker = new DatePicker(LocalDate.now());

        if (esEdicion) {
            nombreField.setText(molde.getNombre());
            cantidadField.setText(String.valueOf(molde.getCantidad()));
            estadoCombo.setValue(molde.getEstado());
            fechaPicker.setValue(molde.getFechaRegistro());
        }

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Cantidad:"), 0, 1);
        grid.add(cantidadField, 1, 1);
        grid.add(new Label("Estado:"), 0, 2);
        grid.add(estadoCombo, 1, 2);
        grid.add(new Label("Fecha de registro:"), 0, 3);
        grid.add(fechaPicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == guardarBtn) {
                if (nombreField.getText().trim().isEmpty()) {
                    showAlert("Error", "El nombre es obligatorio.");
                    return null;
                }
                try {
                    Molde m = new Molde();
                    if (esEdicion) m.setId(molde.getId());
                    m.setNombre(nombreField.getText().trim());
                    m.setCantidad(Integer.parseInt(cantidadField.getText().trim()));
                    m.setEstado(estadoCombo.getValue());
                    m.setFechaRegistro(fechaPicker.getValue());
                    return m;
                } catch (NumberFormatException e) {
                    showAlert("Error", "La cantidad debe ser un numero entero.");
                    return null;
                }
            }
            return null;
        });

        Optional<Molde> result = dialog.showAndWait();
        result.ifPresent(m -> {
            boolean ok = esEdicion ? moldeDAO.updateMolde(m) : moldeDAO.createMolde(m);
            if (ok) {
                showAlert("Exito", esEdicion ? "Molde actualizado." : "Molde registrado.");
                cargarMoldes();
            } else {
                showAlert("Error", "No se pudo guardar el molde.");
            }
        });
    }

    private void eliminarMolde(Molde molde) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Eliminar molde");
        confirm.setContentText("Desea eliminar el molde: " + molde.getNombre() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (moldeDAO.deleteMolde(molde.getId())) {
                showAlert("Exito", "Molde eliminado.");
                cargarMoldes();
            } else {
                showAlert("Error", "No se pudo eliminar el molde.");
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