package Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistorialVentaDAO {

    public List<HistorialVenta> getHistorial(LocalDate fecha, String idCliente, String idModelo) {
        List<HistorialVenta> lista = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT v.id_venta, v.fecha, v.folio, v.total, " +
                        "a.nombre as modelo, SUM(dv.cantidad) as cantidad, " +
                        "c.nombre as cliente " +
                        "FROM venta v " +
                        "JOIN detalle_venta dv ON v.id_venta = dv.id_venta " +
                        "JOIN alcancia a ON dv.id_alcancia = a.id_alcancia " +
                        "JOIN cliente c ON v.id_cliente = c.id_cliente " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (fecha != null) {
            query.append("AND DATE(v.fecha) = ? ");
            params.add(Date.valueOf(fecha));
        }
        if (idCliente != null && !idCliente.isEmpty()) {
            query.append("AND v.id_cliente = ? ");
            params.add(Integer.parseInt(idCliente));
        }
        if (idModelo != null && !idModelo.isEmpty()) {
            query.append("AND a.id_alcancia = ? ");
            params.add(Integer.parseInt(idModelo));
        }

        query.append("GROUP BY v.id_venta, v.fecha, v.folio, v.total, a.nombre, c.nombre ");
        query.append("ORDER BY v.fecha DESC");

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HistorialVenta h = new HistorialVenta();
                    h.setId(rs.getInt("id_venta"));
                    h.setFolio(rs.getString("folio"));
                    Date f = rs.getDate("fecha");
                    if (f != null) h.setFecha(f.toLocalDate());
                    h.setModelo(rs.getString("modelo"));
                    h.setCantidad(rs.getInt("cantidad"));
                    h.setTotal(rs.getDouble("total"));
                    h.setCliente(rs.getString("cliente"));
                    lista.add(h);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getHistorial: " + e.getMessage());
        }
        return lista;
    }

    public List<HistorialVenta> getTodo() {
        return getHistorial(null, null, null);
    }

    public List<Alcancia> getAlcanciasCombo() {
        List<Alcancia> lista = new ArrayList<>();
        String query = "SELECT id_alcancia, nombre FROM alcancia ORDER BY nombre";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Alcancia a = new Alcancia();
                a.setId(rs.getInt("id_alcancia"));
                a.setNombre(rs.getString("nombre"));
                lista.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error getAlcanciasCombo: " + e.getMessage());
        }
        return lista;
    }

    public List<Cliente> getClientesCombo() {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT id_cliente, nombre FROM cliente ORDER BY nombre";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error getClientesCombo: " + e.getMessage());
        }
        return lista;
    }
}