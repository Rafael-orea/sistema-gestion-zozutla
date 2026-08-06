package Controller;

import Model.ReporteDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class ReporteController {

    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;
    @FXML private TabPane tabPane;

    @FXML private Label lblTotalVentas;
    @FXML private Label lblNumVentas;
    @FXML private Label lblDescuentos;
    @FXML private Label lblGananciaNet;

    @FXML private TableView<Object[]> topProductosTable;
    @FXML private TableColumn<Object[], String> colTopNombre;
    @FXML private TableColumn<Object[], Integer> colTopPiezas;
    @FXML private TableColumn<Object[], Double> colTopTotal;

    @FXML private TableView<Object[]> clientesTable;
    @FXML private TableColumn<Object[], String> colClienteNombre;
    @FXML private TableColumn<Object[], Integer> colClienteVentas;
    @FXML private TableColumn<Object[], Double> colClienteTotal;

    @FXML private Label lblPerdidasMerma;
    @FXML private Label lblPerdidasEnvio;
    @FXML private Label lblPerdidasTotal;

    @FXML private TableView<Object[]> mermasTable;
    @FXML private TableColumn<Object[], String> colMermaNombre;
    @FXML private TableColumn<Object[], Integer> colMermaCantidad;
    @FXML private TableColumn<Object[], String> colMermaMotivo;
    @FXML private TableColumn<Object[], String> colMermaFecha;
    @FXML private TableColumn<Object[], Double> colMermaValor;

    @FXML private TableView<Object[]> incidenciasTable;
    @FXML private TableColumn<Object[], String> colIncFolio;
    @FXML private TableColumn<Object[], String> colIncProducto;
    @FXML private TableColumn<Object[], Integer> colIncCantidad;
    @FXML private TableColumn<Object[], String> colIncResponsabilidad;
    @FXML private TableColumn<Object[], Double> colIncPctCliente;
    @FXML private TableColumn<Object[], Double> colIncPctZozutla;
    @FXML private TableColumn<Object[], Double> colIncPerdidaZozutla;
    @FXML private TableColumn<Object[], String> colIncFecha;

    private ReporteDAO reporteDAO = new ReporteDAO();

    @FXML
    public void initialize() {
        fechaInicioPicker.setValue(LocalDate.now().withDayOfMonth(1));
        fechaFinPicker.setValue(LocalDate.now());
        setupTablas();
        cargarReporte();
    }

    private void setupTablas() {
        colTopNombre.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[0]));
        colTopPiezas.setCellValueFactory(data ->
                new SimpleIntegerProperty((Integer) data.getValue()[1]).asObject());
        colTopTotal.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[2]).asObject());
        colTopTotal.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        colClienteNombre.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[0]));
        colClienteVentas.setCellValueFactory(data ->
                new SimpleIntegerProperty((Integer) data.getValue()[1]).asObject());
        colClienteTotal.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[2]).asObject());
        colClienteTotal.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        colMermaNombre.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[0]));
        colMermaCantidad.setCellValueFactory(data ->
                new SimpleIntegerProperty((Integer) data.getValue()[1]).asObject());
        colMermaMotivo.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[2]));
        colMermaFecha.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[3]));
        colMermaValor.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[4]).asObject());
        colMermaValor.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        colIncFolio.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[0]));
        colIncProducto.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[1]));
        colIncCantidad.setCellValueFactory(data ->
                new SimpleIntegerProperty((Integer) data.getValue()[2]).asObject());
        colIncResponsabilidad.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[3].equals("cliente") ? "Cliente 100%" : "Acuerdo"));
        colIncPctCliente.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[4]).asObject());
        colIncPctCliente.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.0f pct", item));
            }
        });
        colIncPctZozutla.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[5]).asObject());
        colIncPctZozutla.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.0f pct", item));
            }
        });
        colIncPerdidaZozutla.setCellValueFactory(data ->
                new SimpleDoubleProperty((Double) data.getValue()[9]).asObject());
        colIncPerdidaZozutla.setCellFactory(col -> new TableCell<Object[], Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    setText(String.format("$%.2f", item));
                    setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });
        colIncFecha.setCellValueFactory(data ->
                new SimpleStringProperty((String) data.getValue()[7]));
    }

    @FXML
    private void handleGenerar() {
        if (fechaInicioPicker.getValue() == null || fechaFinPicker.getValue() == null) {
            showAlert("Error", "Selecciona el periodo.");
            return;
        }
        if (fechaInicioPicker.getValue().isAfter(fechaFinPicker.getValue())) {
            showAlert("Error", "La fecha de inicio no puede ser mayor a la fecha final.");
            return;
        }
        cargarReporte();
    }

    private void cargarReporte() {
        LocalDate inicio = fechaInicioPicker.getValue();
        LocalDate fin = fechaFinPicker.getValue();

        double totalVentas = reporteDAO.getTotalVentasPeriodo(inicio, fin);
        int numVentas = reporteDAO.getTotalVentasCount(inicio, fin);
        double descuentos = reporteDAO.getTotalDescuentos(inicio, fin);
        double perdidasMerma = reporteDAO.getTotalPerdidasMerma(inicio, fin);
        double perdidasEnvio = reporteDAO.getTotalPerdidasEnvio(inicio, fin);
        double totalPerdidas = perdidasMerma + perdidasEnvio;
        double gananciaNet = totalVentas - totalPerdidas;

        lblTotalVentas.setText(String.format("$%.2f", totalVentas));
        lblNumVentas.setText(String.valueOf(numVentas));
        lblDescuentos.setText(String.format("$%.2f", descuentos));
        lblGananciaNet.setText(String.format("$%.2f", gananciaNet));
        lblGananciaNet.setStyle(gananciaNet >= 0
                ? "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #27ae60;"
                : "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        List<Object[]> topProductos = reporteDAO.getTopProductosVendidos(inicio, fin);
        topProductosTable.setItems(FXCollections.observableArrayList(topProductos));

        List<Object[]> clientes = reporteDAO.getVentasPorCliente(inicio, fin);
        clientesTable.setItems(FXCollections.observableArrayList(clientes));

        lblPerdidasMerma.setText(String.format("$%.2f", perdidasMerma));
        lblPerdidasEnvio.setText(String.format("$%.2f", perdidasEnvio));
        lblPerdidasTotal.setText(String.format("$%.2f", totalPerdidas));
        lblPerdidasTotal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        List<Object[]> mermas = reporteDAO.getDetalleMermas(inicio, fin);
        mermasTable.setItems(FXCollections.observableArrayList(mermas));

        List<Object[]> incidencias = reporteDAO.getDetalleIncidencias(inicio, fin);
        incidenciasTable.setItems(FXCollections.observableArrayList(incidencias));
    }

    @FXML
    private void handleExportarGanancias() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de ganancias");
        fileChooser.setInitialFileName("reporte_ganancias.xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        File archivo = fileChooser.showSaveDialog(
                fechaInicioPicker.getScene().getWindow());
        if (archivo == null) return;

        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Sheet resumen = wb.createSheet("Resumen");
            Row r0 = resumen.createRow(0);
            r0.createCell(0).setCellValue("REPORTE DE GANANCIAS");
            r0.getCell(0).setCellStyle(headerStyle);

            resumen.createRow(1).createCell(0).setCellValue("Periodo: " +
                    fechaInicioPicker.getValue() + " al " + fechaFinPicker.getValue());

            Row r3 = resumen.createRow(3);
            r3.createCell(0).setCellValue("Total vendido:");
            r3.createCell(1).setCellValue(lblTotalVentas.getText());

            Row r4 = resumen.createRow(4);
            r4.createCell(0).setCellValue("Num. ventas:");
            r4.createCell(1).setCellValue(lblNumVentas.getText());

            Row r5 = resumen.createRow(5);
            r5.createCell(0).setCellValue("Descuentos aplicados:");
            r5.createCell(1).setCellValue(lblDescuentos.getText());

            Row r6 = resumen.createRow(6);
            r6.createCell(0).setCellValue("Ganancia neta:");
            r6.createCell(1).setCellValue(lblGananciaNet.getText());

            Sheet productos = wb.createSheet("Top Productos");
            Row headerProd = productos.createRow(0);
            String[] colsProd = {"PRODUCTO", "PIEZAS VENDIDAS", "TOTAL"};
            for (int i = 0; i < colsProd.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerProd.createCell(i);
                cell.setCellValue(colsProd[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowProd = 1;
            for (Object[] dato : topProductosTable.getItems()) {
                Row row = productos.createRow(rowProd++);
                row.createCell(0).setCellValue((String) dato[0]);
                row.createCell(1).setCellValue((Integer) dato[1]);
                row.createCell(2).setCellValue((Double) dato[2]);
            }
            for (int i = 0; i < colsProd.length; i++) productos.autoSizeColumn(i);

            Sheet clientesSheet = wb.createSheet("Ventas por Cliente");
            Row headerCli = clientesSheet.createRow(0);
            String[] colsCli = {"CLIENTE", "NUM. VENTAS", "TOTAL"};
            for (int i = 0; i < colsCli.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerCli.createCell(i);
                cell.setCellValue(colsCli[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowCli = 1;
            for (Object[] dato : clientesTable.getItems()) {
                Row row = clientesSheet.createRow(rowCli++);
                row.createCell(0).setCellValue((String) dato[0]);
                row.createCell(1).setCellValue((Integer) dato[1]);
                row.createCell(2).setCellValue((Double) dato[2]);
            }
            for (int i = 0; i < colsCli.length; i++) clientesSheet.autoSizeColumn(i);

            // Hoja detalle de ventas
            Sheet ventas = wb.createSheet("Detalle Ventas");
            Row headerVentas = ventas.createRow(0);
            String[] colsVentas = {"FOLIO", "FECHA", "CLIENTE", "PRODUCTO",
                    "CANTIDAD", "PRECIO UNITARIO", "DESCUENTO", "SUBTOTAL", "TOTAL VENTA"};
            for (int i = 0; i < colsVentas.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerVentas.createCell(i);
                cell.setCellValue(colsVentas[i]);
                cell.setCellStyle(headerStyle);
            }

// Consultar detalle de ventas del periodo
            String queryDetalle =
                    "SELECT v.folio, DATE(v.fecha) as fecha, c.nombre as cliente, " +
                            "a.nombre as producto, dv.cantidad, dv.precio_unitario, " +
                            "(dv.precio_unitario * dv.cantidad - dv.subtotal) as descuento, " +
                            "dv.subtotal, v.total " +
                            "FROM venta v " +
                            "JOIN detalle_venta dv ON v.id_venta = dv.id_venta " +
                            "JOIN alcancia a ON dv.id_alcancia = a.id_alcancia " +
                            "JOIN cliente c ON v.id_cliente = c.id_cliente " +
                            "WHERE DATE(v.fecha) BETWEEN '" + fechaInicioPicker.getValue() +
                            "' AND '" + fechaFinPicker.getValue() + "' " +
                            "ORDER BY v.fecha DESC, v.folio";

            try (java.sql.Connection conn = Model.ConexionBD.conectar();
                 java.sql.PreparedStatement pstmt = conn.prepareStatement(queryDetalle);
                 java.sql.ResultSet rs = pstmt.executeQuery()) {

                int rowVenta = 1;
                while (rs.next()) {
                    Row row = ventas.createRow(rowVenta++);
                    row.createCell(0).setCellValue(rs.getString("folio"));
                    row.createCell(1).setCellValue(rs.getString("fecha"));
                    row.createCell(2).setCellValue(rs.getString("cliente"));
                    row.createCell(3).setCellValue(rs.getString("producto"));
                    row.createCell(4).setCellValue(rs.getInt("cantidad"));
                    row.createCell(5).setCellValue(rs.getDouble("precio_unitario"));
                    row.createCell(6).setCellValue(rs.getDouble("descuento"));
                    row.createCell(7).setCellValue(rs.getDouble("subtotal"));
                    row.createCell(8).setCellValue(rs.getDouble("total"));
                }
            }
            for (int i = 0; i < colsVentas.length; i++) ventas.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                wb.write(fos);
            }
            showAlert("Exito", "Reporte exportado en:\n" + archivo.getPath());

        } catch (Exception e) {
            showAlert("Error", "No se pudo exportar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportarPerdidas() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de perdidas");
        fileChooser.setInitialFileName("reporte_perdidas.xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        File archivo = fileChooser.showSaveDialog(
                fechaInicioPicker.getScene().getWindow());
        if (archivo == null) return;

        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Sheet resumen = wb.createSheet("Resumen");
            Row r0 = resumen.createRow(0);
            r0.createCell(0).setCellValue("REPORTE DE PERDIDAS");
            r0.getCell(0).setCellStyle(headerStyle);

            resumen.createRow(1).createCell(0).setCellValue("Periodo: " +
                    fechaInicioPicker.getValue() + " al " + fechaFinPicker.getValue());

            Row r3 = resumen.createRow(3);
            r3.createCell(0).setCellValue("Perdidas por merma:");
            r3.createCell(1).setCellValue(lblPerdidasMerma.getText());

            Row r4 = resumen.createRow(4);
            r4.createCell(0).setCellValue("Perdidas por envio (Zozutla):");
            r4.createCell(1).setCellValue(lblPerdidasEnvio.getText());

            Row r5 = resumen.createRow(5);
            r5.createCell(0).setCellValue("Total perdidas:");
            r5.createCell(1).setCellValue(lblPerdidasTotal.getText());

            Sheet mermas = wb.createSheet("Mermas");
            Row headerMerma = mermas.createRow(0);
            String[] colsMerma = {"PRODUCTO", "CANTIDAD", "MOTIVO", "FECHA", "VALOR"};
            for (int i = 0; i < colsMerma.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerMerma.createCell(i);
                cell.setCellValue(colsMerma[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowMerma = 1;
            for (Object[] dato : mermasTable.getItems()) {
                Row row = mermas.createRow(rowMerma++);
                row.createCell(0).setCellValue((String) dato[0]);
                row.createCell(1).setCellValue((Integer) dato[1]);
                row.createCell(2).setCellValue((String) dato[2]);
                row.createCell(3).setCellValue((String) dato[3]);
                row.createCell(4).setCellValue((Double) dato[4]);
            }
            for (int i = 0; i < colsMerma.length; i++) mermas.autoSizeColumn(i);

            Sheet incidencias = wb.createSheet("Incidencias");
            Row headerInc = incidencias.createRow(0);
            String[] colsInc = {"FOLIO", "PRODUCTO", "CANT", "RESPONSABILIDAD",
                    "PCT CLIENTE", "PCT ZOZUTLA", "PERDIDA ZOZUTLA", "FECHA"};
            for (int i = 0; i < colsInc.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerInc.createCell(i);
                cell.setCellValue(colsInc[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowInc = 1;
            for (Object[] dato : incidenciasTable.getItems()) {
                Row row = incidencias.createRow(rowInc++);
                row.createCell(0).setCellValue((String) dato[0]);
                row.createCell(1).setCellValue((String) dato[1]);
                row.createCell(2).setCellValue((Integer) dato[2]);
                row.createCell(3).setCellValue((String) dato[3]);
                row.createCell(4).setCellValue((Double) dato[4]);
                row.createCell(5).setCellValue((Double) dato[5]);
                row.createCell(6).setCellValue((Double) dato[9]);
                row.createCell(7).setCellValue((String) dato[7]);
            }
            for (int i = 0; i < colsInc.length; i++) incidencias.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                wb.write(fos);
            }
            showAlert("Exito", "Reporte exportado en:\n" + archivo.getPath());

        } catch (Exception e) {
            showAlert("Error", "No se pudo exportar: " + e.getMessage());
            e.printStackTrace();
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