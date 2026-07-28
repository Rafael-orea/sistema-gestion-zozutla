package Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoldeDAO {

    public List<Molde> getAllMoldes() {
        List<Molde> moldes = new ArrayList<>();
        String query = "SELECT * FROM molde ORDER BY id_molde";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                moldes.add(extraerMolde(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getAllMoldes: " + e.getMessage());
        }
        return moldes;
    }

    public List<Molde> searchMoldes(String termino) {
        List<Molde> moldes = new ArrayList<>();
        String query = "SELECT * FROM molde WHERE nombre LIKE ? ORDER BY id_molde";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + termino + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    moldes.add(extraerMolde(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searchMoldes: " + e.getMessage());
        }
        return moldes;
    }

    public boolean createMolde(Molde molde) {
        String query = "INSERT INTO molde (nombre, cantidad, estado, fecha_registro, id_usuario) VALUES (?, ?, ?, ?, 1)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, molde.getNombre());
            pstmt.setInt(2, molde.getCantidad());
            pstmt.setString(3, molde.getEstado());
            pstmt.setDate(4, Date.valueOf(molde.getFechaRegistro()));
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) molde.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error createMolde: " + e.getMessage());
        }
        return false;
    }

    public boolean updateMolde(Molde molde) {
        String query = "UPDATE molde SET nombre = ?, cantidad = ?, estado = ?, fecha_registro = ? WHERE id_molde = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, molde.getNombre());
            pstmt.setInt(2, molde.getCantidad());
            pstmt.setString(3, molde.getEstado());
            pstmt.setDate(4, Date.valueOf(molde.getFechaRegistro()));
            pstmt.setInt(5, molde.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updateMolde: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteMolde(int id) {
        String query = "DELETE FROM molde WHERE id_molde = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleteMolde: " + e.getMessage());
        }
        return false;
    }

    private Molde extraerMolde(ResultSet rs) throws SQLException {
        Molde molde = new Molde();
        molde.setId(rs.getInt("id_molde"));
        molde.setNombre(rs.getString("nombre"));
        molde.setCantidad(rs.getInt("cantidad"));
        molde.setEstado(rs.getString("estado"));
        Date fecha = rs.getDate("fecha_registro");
        if (fecha != null) molde.setFechaRegistro(fecha.toLocalDate());
        return molde;
    }
}
