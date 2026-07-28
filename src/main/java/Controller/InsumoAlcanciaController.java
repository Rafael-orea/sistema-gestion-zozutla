package Controller;

import Model.Alcancia;
import Model.DetalleAlcanciaInsumo;
import Model.DetalleAlcanciaInsumoDAO;
import Model.Insumo;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;

public class InsumoAlcanciaController {

    @FXML private TextField buscarAlcanciaField;
    @FXML private ListView<Alcancia> alcanciaListView;
    @FXML private TableView<DetalleAlcanciaInsumo> insumoTable;
    @FXML private TableColumn<DetalleAlcanciaInsumo, String> colNombre;
    @FXML private TableColumn<DetalleAlcanciaInsumo, Double> colCantidad;
    @FXML private TableColumn<DetalleAlcanciaInsumo, String> colUnidad;
    @FXML private TableColumn<DetalleAlcanciaInsumo, Double> colCosto;
    @FXML private TableColumn<DetalleAlcanciaInsumo, Double> colSubtotal;
    @FXML private TableColumn<DetalleAlcanciaInsumo, Void> colAcciones;
    @FXML private Label totalLabel;
    @FXML private Label alcanciaSeleccionadaLabel;

    private DetalleAlcanciaInsumoDAO detalleDAO = new DetalleAlcanciaInsumoDAO();
    private ObservableList<DetalleAlcanciaInsumo> insumoList = FXCollections.observableArrayList();
    private List<Alcancia> todasAlcancias;
    private Alcancia alcanciaActual;
    private boolean seleccionando = false;

    @FXML
    public void initialize() {
        setupColumnas();
        cargarAlcancias();
        insumoTable.setItems(insumoList);
    }

    private void cargarAlcancias() {
        todasAlcancias = detalleDAO.getAlcanciasDisponibles();
        ObservableList<Alcancia> alcanciasObs = FXCollections.observableArrayList(todasAlcancias);

        alcanciaListView.setItems(alcanciasObs);
        alcanciaListView.setCellFactory(lv -> new ListCell<Alcancia>() {
            @Override
            protected void updateItem(Alcancia a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getNombre());
            }
        });

        // Filtro en tiempo real
        buscarAlcanciaField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (seleccionando) return;
            ObservableList<Alcancia> filtradas = FXCollections.observableArrayList();
            for (Alcancia a : todasAlcancias) {
                if (newVal == null || newVal.isEmpty() ||
                        a.getNombre().toLowerCase().contains(newVal.toLowerCase())) {
                    filtradas.add(a);
                }
            }
            alcanciaListView.setItems(filtradas);
            alcanciaListView.setVisible(true);
            alcanciaListView.setManaged(true);
        });

        // Al seleccionar de la lista
        alcanciaListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, seleccionada) -> {
            if (seleccionada != null) {
                seleccionando = true;
                alcanciaActual = seleccionada;
                alcanciaSeleccionadaLabel.setText(seleccionada.getNombre());
                buscarAlcanciaField.setText(seleccionada.getNombre());
                buscarAlcanciaField.setEditable(true);
                alcanciaListView.setVisible(false);
                alcanciaListView.setManaged(false);
                refrescarTabla();
                javafx.application.Platform.runLater(() -> seleccionando = false);
            }
        });

        // Al hacer click en el campo abre la lista y limpia
        buscarAlcanciaField.setOnMouseClicked(e -> {
            seleccionando = false;
            alcanciaListView.setItems(alcanciasObs);
            alcanciaListView.setVisible(true);
            alcanciaListView.setManaged(true);
            javafx.application.Platform.runLater(() -> {
                buscarAlcanciaField.selectAll();
            });
        });

        buscarAlcanciaField.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.BACK_SPACE ||
                    e.getCode() == javafx.scene.input.KeyCode.DELETE) {
                seleccionando = false;
                alcanciaListView.setVisible(true);
                alcanciaListView.setManaged(true);
            }
        });


    }

    private void setupColumnas() {
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreInsumo()));

        colCantidad.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getCantidad()).asObject());

        colUnidad.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUnidad()));

        colCosto.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getCostoUnitario()).asObject());
        colCosto.setCellFactory(col -> new TableCell<DetalleAlcanciaInsumo, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        colSubtotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());
        colSubtotal.setCellFactory(col -> new TableCell<DetalleAlcanciaInsumo, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        colAcciones.setCellFactory(col -> new TableCell<DetalleAlcanciaInsumo, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setStyle("-fx-background-color: #C8A96E; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand;");

                btnEditar.setOnAction(e -> {
                    DetalleAlcanciaInsumo d = getTableView().getItems().get(getIndex());
                    mostrarDialogoEditar(d);
                });

                btnEliminar.setOnAction(e -> {
                    DetalleAlcanciaInsumo d = getTableView().getItems().get(getIndex());
                    eliminarInsumo(d);
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

    private void refrescarTabla() {
        if (alcanciaActual != null) {
            insumoList.clear();
            insumoList.addAll(detalleDAO.getInsumosPorAlcancia(alcanciaActual.getId()));
            insumoTable.refresh();
            actualizarTotal();
        }
    }

    private void cargarInsumos(int idAlcancia) {
        insumoList.clear();
        insumoList.addAll(detalleDAO.getInsumosPorAlcancia(idAlcancia));
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = insumoList.stream().mapToDouble(DetalleAlcanciaInsumo::getSubtotal).sum();
        totalLabel.setText(String.format("$%.2f", total));
    }

    @FXML
    private void handleAgregarInsumo() {
        if (alcanciaActual == null) {
            showAlert("Error", "Selecciona una alcancia primero.");
            return;
        }

        List<Insumo> insumosDisponibles = detalleDAO.getInsumosDisponibles();

        Dialog<DetalleAlcanciaInsumo> dialog = new Dialog<>();
        dialog.setTitle("Agregar Insumo");
        dialog.setHeaderText("Agregar insumo a: " + alcanciaActual.getNombre());

        ButtonType guardarBtn = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 20;");

        ComboBox<Insumo> insumoCombo = new ComboBox<>();
        insumoCombo.setItems(FXCollections.observableArrayList(insumosDisponibles));
        insumoCombo.setPrefWidth(280);
        insumoCombo.setConverter(new javafx.util.StringConverter<Insumo>() {
            @Override
            public String toString(Insumo i) { return i == null ? "" : i.getNombre() + " (" + i.getUnidad() + ")"; }
            @Override
            public Insumo fromString(String s) { return null; }
        });

        TextField cantidadField = new TextField();
        cantidadField.setPromptText("Cantidad necesaria");

        Label costoLabel = new Label("0.00");
        costoLabel.setStyle("-fx-font-weight: bold;");

        insumoCombo.setOnAction(e -> {
            Insumo sel = insumoCombo.getValue();
            if (sel != null) costoLabel.setText(String.format("$%.2f", sel.getPrecioUnitario()));
        });

        grid.add(new Label("Insumo:"), 0, 0);
        grid.add(insumoCombo, 1, 0);
        grid.add(new Label("Cantidad:"), 0, 1);
        grid.add(cantidadField, 1, 1);
        grid.add(new Label("Costo unitario:"), 0, 2);
        grid.add(costoLabel, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == guardarBtn) {
                Insumo seleccionado = insumoCombo.getValue();
                if (seleccionado == null) {
                    showAlert("Error", "Selecciona un insumo.");
                    return null;
                }
                try {
                    DetalleAlcanciaInsumo d = new DetalleAlcanciaInsumo();
                    d.setIdAlcancia(alcanciaActual.getId());
                    d.setIdInsumo(seleccionado.getId());
                    d.setNombreInsumo(seleccionado.getNombre());
                    d.setUnidad(seleccionado.getUnidad());
                    d.setCantidad(Double.parseDouble(cantidadField.getText().trim()));
                    d.setCostoUnitario(seleccionado.getPrecioUnitario());
                    d.setSubtotal(d.getCantidad() * d.getCostoUnitario());
                    return d;
                } catch (NumberFormatException e) {
                    showAlert("Error", "La cantidad debe ser numerica.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(d -> {
            if (detalleDAO.agregarInsumo(d)) {
                showAlert("Exito", "Insumo agregado.");
                refrescarTabla();
            } else {
                showAlert("Error", "No se pudo agregar. El insumo ya puede estar registrado para esta alcancia.");
            }
        });
    }

    private void mostrarDialogoEditar(DetalleAlcanciaInsumo detalle) {
        Dialog<DetalleAlcanciaInsumo> dialog = new Dialog<>();
        dialog.setTitle("Editar Insumo");
        dialog.setHeaderText("Editar: " + detalle.getNombreInsumo());

        ButtonType guardarBtn = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 20;");

        TextField cantidadField = new TextField(String.valueOf(detalle.getCantidad()));
        TextField costoField = new TextField(String.valueOf(detalle.getCostoUnitario()));

        grid.add(new Label("Insumo:"), 0, 0);
        grid.add(new Label(detalle.getNombreInsumo()), 1, 0);
        grid.add(new Label("Cantidad:"), 0, 1);
        grid.add(cantidadField, 1, 1);
        grid.add(new Label("Costo unitario:"), 0, 2);
        grid.add(costoField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == guardarBtn) {
                try {
                    DetalleAlcanciaInsumo d = new DetalleAlcanciaInsumo();
                    d.setIdAlcancia(detalle.getIdAlcancia());
                    d.setIdInsumo(detalle.getIdInsumo());
                    d.setCantidad(Double.parseDouble(cantidadField.getText().trim()));
                    d.setCostoUnitario(Double.parseDouble(costoField.getText().trim()));
                    return d;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Los valores deben ser numericos.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(d -> {
            if (detalleDAO.actualizarInsumo(d)) {
                showAlert("Exito", "Insumo actualizado.");
                refrescarTabla();
            } else {
                showAlert("Error", "No se pudo actualizar el insumo.");
            }
        });
    }

    private void eliminarInsumo(DetalleAlcanciaInsumo d) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Eliminar insumo");
        confirm.setContentText("Desea eliminar: " + d.getNombreInsumo() + " de esta alcancia?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (detalleDAO.eliminarInsumo(d.getIdAlcancia(), d.getIdInsumo())) {
                showAlert("Exito", "Insumo eliminado.");
                refrescarTabla();
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