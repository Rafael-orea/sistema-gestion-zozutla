package Controller;

import Model.Insumo;
import Model.InsumoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class InsumoController {

    @FXML private TextField searchField;
    @FXML private TableView<Insumo> insumoTable;
    @FXML private TableColumn<Insumo, Integer> colId;
    @FXML private TableColumn<Insumo, String> colNombre;
    @FXML private TableColumn<Insumo, Double> colCantidad;
    @FXML private TableColumn<Insumo, String> colUnidad;
    @FXML private TableColumn<Insumo, Double> colPrecio;
    @FXML private TableColumn<Insumo, Void> colAcciones;
    @FXML private Label totalLabel;

    private InsumoDAO insumoDAO = new InsumoDAO();
    private ObservableList<Insumo> insumoList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumnas();
        cargarInsumos();
        setupBuscador();
    }

    private void setupColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("unidad"));

        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecio.setCellFactory(col -> new TableCell<Insumo, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        colAcciones.setCellFactory(col -> new TableCell<Insumo, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setStyle("-fx-background-color: #C8A96E; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");

                btnEditar.setOnAction(e -> {
                    Insumo insumo = getTableView().getItems().get(getIndex());
                    mostrarDialogo(insumo);
                });

                btnEliminar.setOnAction(e -> {
                    Insumo insumo = getTableView().getItems().get(getIndex());
                    eliminarInsumo(insumo);
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

        insumoTable.setItems(insumoList);
    }

    private void cargarInsumos() {
        insumoList.clear();
        insumoList.addAll(insumoDAO.getAllInsumos());
        actualizarTotal();
    }

    private void setupBuscador() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                cargarInsumos();
            } else {
                insumoList.clear();
                insumoList.addAll(insumoDAO.searchInsumos(newVal));
                actualizarTotal();
            }
        });
    }

    private void actualizarTotal() {
        if (totalLabel != null)
            totalLabel.setText(String.valueOf(insumoList.size()));
    }

    @FXML
    private void handleAgregarInsumo() {
        mostrarDialogo(null);
    }

    private void mostrarDialogo(Insumo insumo) {
        boolean esEdicion = insumo != null;

        Dialog<Insumo> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Editar Insumo" : "Agregar Insumo");
        dialog.setHeaderText(esEdicion ? "Editar informacion del insumo" : "Registrar nuevo insumo");

        ButtonType guardarBtn = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 20;");

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre del insumo");
        nombreField.setPrefWidth(280);

        TextField cantidadField = new TextField();
        cantidadField.setPromptText("Cantidad disponible");

        ComboBox<String> unidadCombo = new ComboBox<>();
        unidadCombo.setItems(FXCollections.observableArrayList(
                "kg", "g", "litros", "ml", "piezas", "metros", "rollos"
        ));
        unidadCombo.setValue("kg");
        unidadCombo.setPrefWidth(280);

        TextField precioField = new TextField();
        precioField.setPromptText("Precio por unidad");

        if (esEdicion) {
            nombreField.setText(insumo.getNombre());
            cantidadField.setText(String.valueOf(insumo.getCantidad()));
            unidadCombo.setValue(insumo.getUnidad());
            precioField.setText(String.valueOf(insumo.getPrecioUnitario()));
        }

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Cantidad:"), 0, 1);
        grid.add(cantidadField, 1, 1);
        grid.add(new Label("Unidad:"), 0, 2);
        grid.add(unidadCombo, 1, 2);
        grid.add(new Label("Precio unitario:"), 0, 3);
        grid.add(precioField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == guardarBtn) {
                if (nombreField.getText().trim().isEmpty()) {
                    showAlert("Error", "El nombre es obligatorio.");
                    return null;
                }
                try {
                    Insumo i = new Insumo();
                    if (esEdicion) i.setId(insumo.getId());
                    i.setNombre(nombreField.getText().trim());
                    i.setCantidad(Double.parseDouble(cantidadField.getText().trim()));
                    i.setUnidad(unidadCombo.getValue());
                    i.setPrecioUnitario(Double.parseDouble(precioField.getText().trim()));
                    return i;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Cantidad y precio deben ser numericos.");
                    return null;
                }
            }
            return null;
        });

        Optional<Insumo> result = dialog.showAndWait();
        result.ifPresent(i -> {
            boolean ok = esEdicion ? insumoDAO.updateInsumo(i) : insumoDAO.createInsumo(i);
            if (ok) {
                showAlert("Exito", esEdicion ? "Insumo actualizado." : "Insumo registrado.");
                cargarInsumos();
            } else {
                showAlert("Error", "No se pudo guardar el insumo.");
            }
        });
    }

    private void eliminarInsumo(Insumo insumo) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Eliminar insumo");
        confirm.setContentText("Desea eliminar el insumo: " + insumo.getNombre() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (insumoDAO.deleteInsumo(insumo.getId())) {
                showAlert("Exito", "Insumo eliminado.");
                cargarInsumos();
            } else {
                showAlert("Error", "No se pudo eliminar el insumo.");
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