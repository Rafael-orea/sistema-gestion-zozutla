package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsumoDAO {

    public List<Insumo> getAllInsumos() {
        List<Insumo> lista = new ArrayList<>();
        String query = "SELECT * FROM insumo ORDER BY id_insumo";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extraerInsumo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getAllInsumos: " + e.getMessage());
        }
        return lista;
    }

    public List<Insumo> searchInsumos(String termino) {
        List<Insumo> lista = new ArrayList<>();
        String query = "SELECT * FROM insumo WHERE nombre LIKE ? ORDER BY id_insumo";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + termino + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extraerInsumo(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searchInsumos: " + e.getMessage());
        }
        return lista;
    }

    public boolean createInsumo(Insumo i) {
        String query = "INSERT INTO insumo (nombre, cantidad, unidad, precio_unitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, i.getNombre());
            pstmt.setDouble(2, i.getCantidad());
            pstmt.setString(3, i.getUnidad());
            pstmt.setDouble(4, i.getPrecioUnitario());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) i.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error createInsumo: " + e.getMessage());
        }
        return false;
    }

    public boolean updateInsumo(Insumo i) {
        String query = "UPDATE insumo SET nombre=?, cantidad=?, unidad=?, precio_unitario=? WHERE id_insumo=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, i.getNombre());
            pstmt.setDouble(2, i.getCantidad());
            pstmt.setString(3, i.getUnidad());
            pstmt.setDouble(4, i.getPrecioUnitario());
            pstmt.setInt(5, i.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updateInsumo: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteInsumo(int id) {
        String query = "DELETE FROM insumo WHERE id_insumo=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleteInsumo: " + e.getMessage());
        }
        return false;
    }

    private Insumo extraerInsumo(ResultSet rs) throws SQLException {
        Insumo i = new Insumo();
        i.setId(rs.getInt("id_insumo"));
        i.setNombre(rs.getString("nombre"));
        i.setCantidad(rs.getDouble("cantidad"));
        i.setUnidad(rs.getString("unidad"));
        i.setPrecioUnitario(rs.getDouble("precio_unitario"));
        return i;
    }
}