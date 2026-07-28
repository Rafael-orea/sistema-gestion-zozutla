package Controller;

import Model.Alcancia;
import Model.AlcanciaDAO;
import Model.Molde;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.util.List;
import java.util.Optional;

public class InventarioController {

    @FXML private TextField searchField;
    @FXML private TableView<Alcancia> alcanciaTable;
    @FXML private TableColumn<Alcancia, Integer> colId;
    @FXML private TableColumn<Alcancia, String> colNombre;
    @FXML private TableColumn<Alcancia, Integer> colExistencia;
    @FXML private TableColumn<Alcancia, Double> colPrecio;
    @FXML private TableColumn<Alcancia, String> colDescripcion;
    @FXML private TableColumn<Alcancia, String> colEstado;
    @FXML private TableColumn<Alcancia, Void> colAcciones;
    @FXML private Label totalLabel;

    private AlcanciaDAO alcanciaDAO = new AlcanciaDAO();
    private ObservableList<Alcancia> alcanciaList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumnas();
        cargarAlcancias();
        setupBuscador();
    }

    private void setupColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(col -> new TableCell<Alcancia, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    switch (item) {
                        case "disponible" -> {
                            setText("Disponible");
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        }
                        case "agotado" -> {
                            setText("Agotado");
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        }
                        default -> {
                            setText(item);
                            setStyle("");
                        }
                    }
                }
            }
        });

        colAcciones.setCellFactory(col -> new TableCell<Alcancia, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setStyle("-fx-background-color: #C8A96E; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");

                btnEditar.setOnAction(e -> {
                    Alcancia a = getTableView().getItems().get(getIndex());
                    mostrarDialogo(a);
                });

                btnEliminar.setOnAction(e -> {
                    Alcancia a = getTableView().getItems().get(getIndex());
                    eliminarAlcancia(a);
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
    }

    private void cargarAlcancias() {
        alcanciaList.clear();
        alcanciaList.addAll(alcanciaDAO.getAllAlcancias());
        alcanciaTable.setItems(alcanciaList);
        actualizarTotal();
    }

    private void setupBuscador() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                cargarAlcancias();
            } else {
                alcanciaList.clear();
                alcanciaList.addAll(alcanciaDAO.searchAlcancias(newVal));
                alcanciaTable.setItems(alcanciaList);
                actualizarTotal();
            }
        });
    }

    private void actualizarTotal() {
        if (totalLabel != null)
            totalLabel.setText(String.valueOf(alcanciaList.size()));
    }

    @FXML
    private void handleAgregarAlcancia() {
        mostrarDialogo(null);
    }

    private void mostrarDialogo(Alcancia alcancia) {
        boolean esEdicion = alcancia != null;

        Dialog<Alcancia> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Editar Alcancia" : "Agregar Alcancia");
        dialog.setHeaderText(esEdicion ? "Editar informacion de la alcancia" : "Registrar nueva alcancia");

        ButtonType guardarBtn = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 20;");

        List<Molde> moldes = alcanciaDAO.getMoldesDisponibles();
        // Por esto:
        ObservableList<Molde> moldesObservable = FXCollections.observableArrayList(moldes);

        ComboBox<Molde> moldeCombo = new ComboBox<>(moldesObservable);
        moldeCombo.setPrefWidth(280);
        moldeCombo.setEditable(true);

        javafx.util.StringConverter<Molde> converter = new javafx.util.StringConverter<Molde>() {
            @Override
            public String toString(Molde m) { return m == null ? "" : m.getNombre(); }
            @Override
            public Molde fromString(String s) { return null; }
        };
        moldeCombo.setConverter(converter);

// Filtro en tiempo real mientras escribe
        moldeCombo.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                moldeCombo.setItems(moldesObservable);
            } else {
                ObservableList<Molde> filtrados = FXCollections.observableArrayList();
                for (Molde m : moldes) {
                    if (m.getNombre().toLowerCase().contains(newVal.toLowerCase())) {
                        filtrados.add(m);
                    }
                }
                moldeCombo.setItems(filtrados);
                if (!filtrados.isEmpty()) {
                    moldeCombo.show();
                }
            }
        });
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre de la alcancia");
        nombreField.setPrefWidth(280);

        TextArea descripcionArea = new TextArea();
        descripcionArea.setPromptText("Descripcion");
        descripcionArea.setPrefRowCount(3);
        descripcionArea.setPrefWidth(280);

        TextField existenciaField = new TextField();
        existenciaField.setPromptText("Existencias");

        TextField precioField = new TextField();
        precioField.setPromptText("Precio unitario");

        ComboBox<String> estadoCombo = new ComboBox<>();
        estadoCombo.setItems(FXCollections.observableArrayList("disponible", "agotado"));
        estadoCombo.setValue("disponible");
        estadoCombo.setPrefWidth(280);

        if (esEdicion) {
            nombreField.setText(alcancia.getNombre());
            descripcionArea.setText(alcancia.getDescripcion());
            existenciaField.setText(String.valueOf(alcancia.getExistencia()));
            precioField.setText(String.valueOf(alcancia.getPrecio()));
            estadoCombo.setValue(alcancia.getEstado());

            if (alcancia.getIdMolde() == 0) {
                // Sin molde, selecciona el primer elemento
                moldeCombo.getSelectionModel().selectFirst();
            } else {
                moldes.stream()
                        .filter(m -> m.getId() == alcancia.getIdMolde())
                        .findFirst()
                        .ifPresent(moldeCombo::setValue);
            }
        }

        grid.add(new Label("Molde:"), 0, 0);
        grid.add(moldeCombo, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(nombreField, 1, 1);
        grid.add(new Label("Descripcion:"), 0, 2);
        grid.add(descripcionArea, 1, 2);
        grid.add(new Label("Existencias:"), 0, 3);
        grid.add(existenciaField, 1, 3);
        grid.add(new Label("Precio unitario:"), 0, 4);
        grid.add(precioField, 1, 4);
        grid.add(new Label("Estado:"), 0, 5);
        grid.add(estadoCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == guardarBtn) {
                if (nombreField.getText().trim().isEmpty()) {
                    showAlert("Error", "El nombre es obligatorio.");
                    return null;
                }
                try {
                    Alcancia a = new Alcancia();
                    if (esEdicion) a.setId(alcancia.getId());
                    Molde moldeSeleccionado = moldeCombo.getValue();
                    if (moldeSeleccionado != null) a.setIdMolde(moldeSeleccionado.getId());
                    a.setNombre(nombreField.getText().trim());
                    a.setDescripcion(descripcionArea.getText().trim());
                    a.setExistencia(Integer.parseInt(existenciaField.getText().trim()));
                    a.setPrecio(Double.parseDouble(precioField.getText().trim()));
                    a.setEstado(estadoCombo.getValue());
                    return a;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Existencia y precio deben ser numericos.");
                    return null;
                }
            }
            return null;
        });

        Optional<Alcancia> result = dialog.showAndWait();
        result.ifPresent(a -> {
            boolean ok = esEdicion ? alcanciaDAO.updateAlcancia(a) : alcanciaDAO.createAlcancia(a);
            if (ok) {
                showAlert("Exito", esEdicion ? "Alcancia actualizada." : "Alcancia registrada.");
                cargarAlcancias();
            } else {
                showAlert("Error", "No se pudo guardar la alcancia.");
            }
        });
    }

    private void eliminarAlcancia(Alcancia a) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Eliminar alcancia");
        confirm.setContentText("Desea eliminar la alcancia: " + a.getNombre() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (alcanciaDAO.deleteAlcancia(a.getId())) {
                showAlert("Exito", "Alcancia eliminada.");
                cargarAlcancias();
            } else {
                showAlert("Error", "No se pudo eliminar la alcancia.");
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