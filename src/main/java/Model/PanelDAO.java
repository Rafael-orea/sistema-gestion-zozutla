package Model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PanelDAO {

    public Panel getDatosPanel() {
        Panel panel = new Panel();
        panel.setTotalMoldes(getTotalMoldes());
        panel.setTotalInventario(getTotalInventario());
        panel.setVentasDia(getVentasDia());
        panel.setVentasMes(getVentasMes());
        panel.setGananciaEstimada(getGananciaEstimada());
        return panel;
    }

    private int getTotalMoldes() {
        String query = "SELECT COUNT(*) FROM molde";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getTotalMoldes: " + e.getMessage());
        }
        return 0;
    }

    private int getTotalInventario() {
        String query = "SELECT COALESCE(SUM(existencia), 0) FROM alcancia";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getTotalInventario: " + e.getMessage());
        }
        return 0;
    }

    private BigDecimal getVentasDia() {
        String query = "SELECT COALESCE(SUM(total), 0) FROM venta WHERE DATE(fecha) = CURDATE()";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            System.err.println("Error getVentasDia: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getVentasMes() {
        String query = "SELECT COALESCE(SUM(total), 0) FROM venta " +
                "WHERE MONTH(fecha) = MONTH(CURDATE()) AND YEAR(fecha) = YEAR(CURDATE())";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            System.err.println("Error getVentasMes: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getGananciaEstimada() {
        String query = "SELECT COALESCE(SUM(ganancia), 0) FROM costo_produccion";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            System.err.println("Error getGananciaEstimada: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
}