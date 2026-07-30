package Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public boolean registrarVenta(Venta venta) {
        String queryVenta = "INSERT INTO venta (id_usuario, id_cliente, folio, fecha, total) VALUES (1, ?, ?, ?, ?)";
        String queryDetalle = "INSERT INTO detalle_venta (id_venta, id_alcancia, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String queryInventario = "UPDATE alcancia SET existencia = existencia - ? WHERE id_alcancia = ?";

        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            String folio = "VTA-" + System.currentTimeMillis();
            venta.setFolio(folio);

            try (PreparedStatement pstmt = conn.prepareStatement(queryVenta, Statement.RETURN_GENERATED_KEYS)) {
                // Si es comprador normal se guarda con id_cliente = 1 (cliente generico)
                if (venta.isCompradorNormal() || venta.getIdCliente() == 0) {
                    pstmt.setInt(1, 1);
                } else {
                    pstmt.setInt(1, venta.getIdCliente());
                }
                pstmt.setString(2, folio);
                pstmt.setDate(3, Date.valueOf(venta.getFecha()));
                pstmt.setDouble(4, venta.getTotal());
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) venta.setId(rs.getInt(1));
                }
            }

            for (DetalleVenta detalle : venta.getDetalles()) {
                try (PreparedStatement pstmt = conn.prepareStatement(queryDetalle)) {
                    pstmt.setInt(1, venta.getId());
                    pstmt.setInt(2, detalle.getIdAlcancia());
                    pstmt.setInt(3, detalle.getCantidad());
                    pstmt.setDouble(4, detalle.getPrecioUnitario());
                    pstmt.setDouble(5, detalle.getSubtotal());
                    pstmt.executeUpdate();
                }

                try (PreparedStatement pstmt = conn.prepareStatement(queryInventario)) {
                    pstmt.setInt(1, detalle.getCantidad());
                    pstmt.setInt(2, detalle.getIdAlcancia());
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error registrarVenta: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error autocommit: " + e.getMessage());
            }
        }
    }

    public List<Cliente> getClientesDisponibles() {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT * FROM cliente ORDER BY nombre";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setPais(rs.getString("pais"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error getClientesDisponibles: " + e.getMessage());
        }
        return lista;
    }
}