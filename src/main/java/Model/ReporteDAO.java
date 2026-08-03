package Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    // ==================== GANANCIAS ====================

    public double getTotalVentasPeriodo(LocalDate inicio, LocalDate fin) {
        String query = "SELECT COALESCE(SUM(total), 0) FROM venta " +
                "WHERE DATE(fecha) BETWEEN ? AND ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(inicio));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getTotalVentasPeriodo: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalVentasCount(LocalDate inicio, LocalDate fin) {
        String query = "SELECT COUNT(*) FROM venta WHERE DATE(fecha) BETWEEN ? AND ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(inicio));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getTotalVentasCount: " + e.getMessage());
        }
        return 0;
    }

    public double getTotalDescuentos(LocalDate inicio, LocalDate fin) {
        String query =
                "SELECT COALESCE(SUM(dv.precio_unitario * dv.cantidad - dv.subtotal), 0) " +
                        "FROM detalle_venta dv " +
                        "JOIN venta v ON dv.id_venta = v.id_venta " +
                        "JOIN cliente c ON v.id_cliente = c.id_cliente " +
                        "WHERE DATE(v.fecha) BETWEEN ? AND ? " +
                        "AND c.nombre != 'Comprador General'";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(inicio));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getTotalDescuentos: " + e.getMessage());
        }
        return 0;
    }

    public List<Object[]> getTopProductosVendidos(LocalDate inicio, LocalDate fin) {
        List<Object[]> lista = new ArrayList<>();
        String query =
                "SELECT a.nombre, SUM(dv.cantidad) as total_piezas, SUM(dv.subtotal) as total_vendido " +
                        "FROM detalle_venta dv " +
                        "JOIN venta v ON dv.id_venta = v.id_venta " +
                        "JOIN alcancia a ON dv.id_alcancia = a.id_alcancia " +
                        "WHERE DATE(v.fecha) BETWEEN ? AND ? " +
                        "GROUP BY a.id_alcancia, a.nombre " +
                        "ORDER BY total_piezas DESC " +
                        "LIMIT 10";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(inicio));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getString("nombre"),
                            rs.getInt("total_piezas"),
                            rs.getDouble("total_vendido")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getTopProductosVendidos: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> getVentasPorCliente(LocalDate inicio, LocalDate fin) {
        List<Object[]> lista = new ArrayList<>();
        String query =
                "SELECT c.nombre, COUNT(v.id_venta) as num_ventas, SUM(v.total) as total " +
                        "FROM venta v " +
                        "JOIN cliente c ON v.id_cliente = c.id_cliente " +
                        "WHERE DATE(v.fecha) BETWEEN ? AND ? " +
                        "GROUP BY c.id_cliente, c.nombre " +
                        "ORDER BY total DESC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(inicio));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getString("nombre"),
                            rs.getInt("num_ventas"),
                            rs.getDouble("total")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getVentasPorCliente: " + e.getMessage());
        }
        return lista;
    }

    // ==================== PERDIDAS ====================

    public double getTotalPerdidasMerma(LocalDate inicio, LocalDate fin) {
        String query =
                "SELECT COALESCE(SUM(p.cantidad * a.precio), 0) " +
                        "FROM perdida p " +
                        "JOIN alcancia a ON p.id_alcancia = a.id_alcancia " +
                        "WHERE p.fecha BETWEEN ? AND ? " +
                        "AND p.motivo NOT LIKE 'Incidencia%'";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(inicio));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getTotalPerdidasMerma: " + e.getMessage());
        }
        return 0;
    }

    public double getTotalPerdidasEnvio(LocalDate inicio, LocalDate fin) {
        String query =
                "SELECT COALESCE(SUM(p.cantidad * a.precio * (i.porcentaje_zozutla / 100)), 0) " +
                        "FROM perdida p " +
                        "JOIN alcancia a ON p.id_alcancia = a.id_alcancia " +
                        "JOIN incidencia_envio i ON p.motivo LIKE CONCAT('Incidencia%') " +
                        "WHERE p.fecha BETWEEN ? AND ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(inicio));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getTotalPerdidasEnvio: " + e.getMessage());
        }
        return 0;
    }

    public List<Object[]> getDetalleMermas(LocalDate inicio, LocalDate fin) {
        List<Object[]> lista = new ArrayList<>();
        String query =
                "SELECT a.nombre, p.cantidad, p.motivo, p.fecha, " +
                        "(p.cantidad * a.precio) as valor " +
                        "FROM perdida p " +
                        "JOIN alcancia a ON p.id_alcancia = a.id_alcancia " +
                        "WHERE p.fecha BETWEEN ? AND ? " +
                        "ORDER BY p.fecha DESC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(inicio));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getString("nombre"),
                            rs.getInt("cantidad"),
                            rs.getString("motivo"),
                            rs.getString("fecha"),
                            rs.getDouble("valor")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getDetalleMermas: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> getDetalleIncidencias(LocalDate inicio, LocalDate fin) {
        List<Object[]> lista = new ArrayList<>();
        String query =
                "SELECT v.folio, a.nombre, i.cantidad_afectada, " +
                        "i.responsabilidad, i.porcentaje_cliente, i.porcentaje_zozutla, " +
                        "i.descripcion, i.fecha, " +
                        "(i.cantidad_afectada * alc.precio) as valor_total, " +
                        "(i.cantidad_afectada * alc.precio * i.porcentaje_zozutla / 100) as perdida_zozutla " +
                        "FROM incidencia_envio i " +
                        "JOIN envio e ON i.id_envio = e.id_envio " +
                        "JOIN venta v ON e.id_venta = v.id_venta " +
                        "JOIN alcancia alc ON i.id_alcancia = alc.id_alcancia " +
                        "JOIN alcancia a ON i.id_alcancia = a.id_alcancia " +
                        "WHERE i.fecha BETWEEN ? AND ? " +
                        "ORDER BY i.fecha DESC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(inicio));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getString("folio"),
                            rs.getString("nombre"),
                            rs.getInt("cantidad_afectada"),
                            rs.getString("responsabilidad"),
                            rs.getDouble("porcentaje_cliente"),
                            rs.getDouble("porcentaje_zozutla"),
                            rs.getString("descripcion"),
                            rs.getString("fecha"),
                            rs.getDouble("valor_total"),
                            rs.getDouble("perdida_zozutla")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getDetalleIncidencias: " + e.getMessage());
        }
        return lista;
    }
}