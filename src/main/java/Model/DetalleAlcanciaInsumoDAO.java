package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleAlcanciaInsumoDAO {

    public List<DetalleAlcanciaInsumo> getInsumosPorAlcancia(int idAlcancia) {
        List<DetalleAlcanciaInsumo> lista = new ArrayList<>();
        String query =
                "SELECT dai.id_alcancia, dai.id_insumo, i.nombre as nombre_insumo, " +
                        "i.unidad, dai.cantidad, dai.costo_unitario, " +
                        "(dai.cantidad * dai.costo_unitario) as subtotal " +
                        "FROM detalle_alcancia_insumo dai " +
                        "JOIN insumo i ON dai.id_insumo = i.id_insumo " +
                        "WHERE dai.id_alcancia = ? " +
                        "ORDER BY i.nombre";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idAlcancia);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetalleAlcanciaInsumo d = new DetalleAlcanciaInsumo();
                    d.setIdAlcancia(rs.getInt("id_alcancia"));
                    d.setIdInsumo(rs.getInt("id_insumo"));
                    d.setNombreInsumo(rs.getString("nombre_insumo"));
                    d.setUnidad(rs.getString("unidad"));
                    d.setCantidad(rs.getDouble("cantidad"));
                    d.setCostoUnitario(rs.getDouble("costo_unitario"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    lista.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getInsumosPorAlcancia: " + e.getMessage());
        }
        return lista;
    }

    public boolean agregarInsumo(DetalleAlcanciaInsumo d) {
        String query = "INSERT INTO detalle_alcancia_insumo (id_alcancia, id_insumo, cantidad, costo_unitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, d.getIdAlcancia());
            pstmt.setInt(2, d.getIdInsumo());
            pstmt.setDouble(3, d.getCantidad());
            pstmt.setDouble(4, d.getCostoUnitario());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error agregarInsumo: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizarInsumo(DetalleAlcanciaInsumo d) {
        String query = "UPDATE detalle_alcancia_insumo SET cantidad=?, costo_unitario=? WHERE id_alcancia=? AND id_insumo=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, d.getCantidad());
            pstmt.setDouble(2, d.getCostoUnitario());
            pstmt.setInt(3, d.getIdAlcancia());
            pstmt.setInt(4, d.getIdInsumo());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizarInsumo: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminarInsumo(int idAlcancia, int idInsumo) {
        String query = "DELETE FROM detalle_alcancia_insumo WHERE id_alcancia=? AND id_insumo=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idAlcancia);
            pstmt.setInt(2, idInsumo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarInsumo: " + e.getMessage());
        }
        return false;
    }

    public List<Insumo> getInsumosDisponibles() {
        List<Insumo> lista = new ArrayList<>();
        String query = "SELECT * FROM insumo ORDER BY nombre";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Insumo i = new Insumo();
                i.setId(rs.getInt("id_insumo"));
                i.setNombre(rs.getString("nombre"));
                i.setUnidad(rs.getString("unidad"));
                i.setPrecioUnitario(rs.getDouble("precio_unitario"));
                lista.add(i);
            }
        } catch (SQLException e) {
            System.err.println("Error getInsumosDisponibles: " + e.getMessage());
        }
        return lista;
    }

    public List<Alcancia> getAlcanciasDisponibles() {
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
            System.err.println("Error getAlcanciasDisponibles: " + e.getMessage());
        }
        return lista;
    }
}