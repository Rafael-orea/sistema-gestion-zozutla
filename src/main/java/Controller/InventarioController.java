package Controller;

import Model.Alcancia;
import Model.AlcanciaDAO;
import Model.Molde;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;

public class InventarioController {

    @FXML private TextField searchField;
    @FXML private TableView<Alcancia> alcanciaTable;
    @FXML private TableColumn<Alcancia, Integer> colId;
    @FXML private TableColumn<Alcancia, String> colNombre;
    @FXML private TableColumn<Alcancia, Integer> colExistencia;
    @FXML private TableColumn<Alcancia, Double> colPrecio;
    @FXML private TableColumn<Alcancia, Double> colPrecioMayoreo;
    @FXML private TableColumn<Alcancia, Double> colPrecioEspecial;
    @FXML private TableColumn<Alcancia, Integer> colMerma;
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

        colPrecio.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colPrecio.setCellFactory(col -> new TableCell<Alcancia, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        colPrecioMayoreo.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrecioMayoreo()).asObject());
        colPrecioMayoreo.setCellFactory(col -> new TableCell<Alcancia, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        colPrecioEspecial.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrecioEspecial()).asObject());
        colPrecioEspecial.setCellFactory(col -> new TableCell<Alcancia, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        colMerma.setCellValueFactory(new PropertyValueFactory<>("existenciaMerma"));
        colMerma.setCellFactory(col -> new TableCell<Alcancia, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    if (item > 0) setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                    else setStyle("");
                }
            }
        });

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
            private final Button btnMerma = new Button("Merma");

            {
                btnEditar.setStyle("-fx-background-color: #C8A96E; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");
                btnMerma.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");

                btnEditar.setOnAction(e -> {
                    Alcancia a = getTableView().getItems().get(getIndex());
                    mostrarDialogo(a);
                });
                btnEliminar.setOnAction(e -> {
                    Alcancia a = getTableView().getItems().get(getIndex());
                    eliminarAlcancia(a);
                });
                btnMerma.setOnAction(e -> {
                    Alcancia a = getTableView().getItems().get(getIndex());
                    mostrarDialogoMerma(a);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(4, btnEditar, btnMerma, btnEliminar);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        alcanciaTable.setItems(alcanciaList);
    }

    private void cargarAlcancias() {
        alcanciaList.clear();
        alcanciaList.addAll(alcanciaDAO.getAllAlcancias());
        actualizarTotal();
    }

    private void setupBuscador() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                cargarAlcancias();
            } else {
                alcanciaList.clear();
                alcanciaList.addAll(alcanciaDAO.searchAlcancias(newVal));
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
        dialog.setHeaderText(esEdicion ? "Editar informacion" : "Registrar nueva alcancia");

        ButtonType guardarBtn = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 20;");

        List<Molde> moldes = alcanciaDAO.getMoldesDisponibles();
        ComboBox<Molde> moldeCombo = new ComboBox<>();
        moldeCombo.setItems(FXCollections.observableArrayList(moldes));
        moldeCombo.setConverter(new javafx.util.StringConverter<Molde>() {
            @Override
            public String toString(Molde m) { return m == null ? "" : m.getNombre(); }
            @Override
            public Molde fromString(String s) { return null; }
        });
        moldeCombo.setPrefWidth(280);

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre de la alcancia");
        nombreField.setPrefWidth(280);

        TextField existenciaField = new TextField();
        existenciaField.setPromptText("Existencias");

        TextField precioField = new TextField();
        precioField.setPromptText("Precio normal");

        TextField precioMayoreoField = new TextField();
        precioMayoreoField.setPromptText("Precio mayoreo");

        ComboBox<String> estadoCombo = new ComboBox<>();
        estadoCombo.setItems(FXCollections.observableArrayList("disponible", "agotado"));
        estadoCombo.setValue("disponible");
        estadoCombo.setPrefWidth(280);

        if (esEdicion) {
            nombreField.setText(alcancia.getNombre());
            existenciaField.setText(String.valueOf(alcancia.getExistencia()));
            precioField.setText(String.valueOf(alcancia.getPrecio()));
            precioMayoreoField.setText(String.valueOf(alcancia.getPrecioMayoreo()));
            estadoCombo.setValue(alcancia.getEstado());
            if (alcancia.getIdMolde() == 0) {
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
        grid.add(new Label("Existencias:"), 0, 2);
        grid.add(existenciaField, 1, 2);
        grid.add(new Label("Precio normal:"), 0, 3);
        grid.add(precioField, 1, 3);
        grid.add(new Label("Precio mayoreo:"), 0, 4);
        grid.add(precioMayoreoField, 1, 4);
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
                    a.setExistencia(Integer.parseInt(existenciaField.getText().trim()));
                    a.setPrecio(Double.parseDouble(precioField.getText().trim()));
                    a.setPrecioMayoreo(Double.parseDouble(precioMayoreoField.getText().trim()));
                    a.setEstado(estadoCombo.getValue());
                    return a;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Existencia y precios deben ser numericos.");
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

    private void mostrarDialogoMerma(Alcancia alcancia) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Merma");
        dialog.setHeaderText("Merma de: " + alcancia.getNombre());

        ButtonType guardarBtn = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 20;");

        Spinner<Integer> cantidadSpinner = new Spinner<>(1, alcancia.getExistencia(), 1);
        cantidadSpinner.setEditable(true);
        cantidadSpinner.setPrefWidth(120);

        ToggleGroup grupo = new ToggleGroup();
        RadioButton rbArreglo = new RadioButton("Tiene arreglo (se vende a mitad de precio)");
        RadioButton rbPerdida = new RadioButton("Sin arreglo (registrar como perdida)");
        rbArreglo.setToggleGroup(grupo);
        rbPerdida.setToggleGroup(grupo);
        rbArreglo.setSelected(true);

        TextArea motivoArea = new TextArea();
        motivoArea.setPromptText("Describe el motivo o el dano...");
        motivoArea.setPrefRowCount(3);
        motivoArea.setPrefWidth(280);

        Label stockLabel = new Label("Existencias disponibles: " + alcancia.getExistencia());
        stockLabel.setStyle("-fx-text-fill: #6B5A45;");

        grid.add(stockLabel, 0, 0, 2, 1);
        grid.add(new Label("Cantidad:"), 0, 1);
        grid.add(cantidadSpinner, 1, 1);
        grid.add(rbArreglo, 0, 2, 2, 1);
        grid.add(rbPerdida, 0, 3, 2, 1);
        grid.add(new Label("Motivo:"), 0, 4);
        grid.add(motivoArea, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == guardarBtn) {
                boolean tieneArreglo = rbArreglo.isSelected();
                int cantidad = cantidadSpinner.getValue();
                String motivo = motivoArea.getText().trim();

                if (alcanciaDAO.registrarMerma(alcancia.getId(), cantidad, tieneArreglo, motivo)) {
                    if (tieneArreglo) {
                        showAlert("Exito", cantidad + " piezas movidas a merma con precio de $" +
                                String.format("%.2f", alcancia.getPrecio() * 0.5));
                    } else {
                        showAlert("Exito", cantidad + " piezas registradas como perdida.");
                    }
                    cargarAlcancias();
                } else {
                    showAlert("Error", "No se pudo registrar la merma.");
                }
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