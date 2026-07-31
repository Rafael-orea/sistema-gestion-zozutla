package Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public boolean registrarVenta(Venta venta) {
        String queryVenta = "INSERT INTO venta (id_usuario, id_cliente, folio, fecha, total) VALUES (1, ?, ?, ?, ?)";
        String queryDetalle = "INSERT INTO detalle_venta (id_venta, id_alcancia, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            String folio = "VTA-" + System.currentTimeMillis();
            venta.setFolio(folio);

            // Insertar venta
            try (PreparedStatement pstmt = conn.prepareStatement(queryVenta, Statement.RETURN_GENERATED_KEYS)) {
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

            // Insertar detalles y descontar inventario
            for (DetalleVenta detalle : venta.getDetalles()) {

                // Insertar detalle
                try (PreparedStatement pstmt = conn.prepareStatement(queryDetalle)) {
                    pstmt.setInt(1, venta.getId());
                    pstmt.setInt(2, detalle.getIdAlcancia());
                    pstmt.setInt(3, detalle.getCantidad());
                    pstmt.setDouble(4, detalle.getPrecioUnitario());
                    pstmt.setDouble(5, detalle.getSubtotal());
                    pstmt.executeUpdate();
                }

                // Descontar del inventario correcto
                String queryInv = detalle.isEsMerma()
                        ? "UPDATE alcancia SET existencia_merma = existencia_merma - ? WHERE id_alcancia = ?"
                        : "UPDATE alcancia SET existencia = existencia - ? WHERE id_alcancia = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(queryInv)) {
                    pstmt.setInt(1, detalle.getCantidad());
                    pstmt.setInt(2, detalle.getIdAlcancia());
                    pstmt.executeUpdate();
                }

                // Actualizar estado si se agota
                String queryEstado =
                        "UPDATE alcancia SET estado = CASE " +
                                "WHEN existencia = 0 AND existencia_merma = 0 THEN 'agotado' " +
                                "ELSE 'disponible' END " +
                                "WHERE id_alcancia = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(queryEstado)) {
                    pstmt.setInt(1, detalle.getIdAlcancia());
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

    public List<Alcancia> getAlcanciasDisponibles() {
        List<Alcancia> lista = new ArrayList<>();
        String query = "SELECT * FROM alcancia WHERE existencia > 0 ORDER BY nombre";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Alcancia a = new Alcancia();
                a.setId(rs.getInt("id_alcancia"));
                a.setNombre(rs.getString("nombre"));
                a.setPrecio(rs.getDouble("precio"));
                a.setPrecioMayoreo(rs.getDouble("precio_mayoreo"));
                a.setExistencia(rs.getInt("existencia"));
                a.setExistenciaMerma(rs.getInt("existencia_merma"));
                lista.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error getAlcanciasDisponibles: " + e.getMessage());
        }
        return lista;
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
                c.setTipo(rs.getString("tipo"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error getClientesDisponibles: " + e.getMessage());
        }
        return lista;
    }
}