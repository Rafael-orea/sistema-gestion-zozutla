package Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoldeDAO {

    public List<Molde> getMoldesFiltrados(String termino, String estado) {
        List<Molde> moldes = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT * FROM molde WHERE 1=1 ");

        if (termino != null && !termino.isEmpty()) {
            query.append("AND nombre LIKE ? ");
        }
        switch (estado) {
            case "Bueno" -> query.append("AND estado = 'bueno' ");
            case "Dañado" -> query.append("AND estado = 'dañado' ");
            case "Fuera de uso" -> query.append("AND estado = 'fuera_de_uso' ");
        }
        query.append("ORDER BY id_molde");

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            if (termino != null && !termino.isEmpty()) {
                pstmt.setString(1, "%" + termino + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    moldes.add(extraerMolde(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getMoldesFiltrados: " + e.getMessage());
        }
        return moldes;
    }

    public List<Molde> getAllMoldes() {
        return getMoldesFiltrados("", "");
    }

    public List<Molde> searchMoldes(String termino) {
        return getMoldesFiltrados(termino, "");
    }

    public int countPorEstado(String estado) {
        String query = "SELECT COUNT(*) FROM molde WHERE estado = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, estado);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error countPorEstado: " + e.getMessage());
        }
        return 0;
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
        String query = "UPDATE molde SET nombre=?, cantidad=?, estado=?, fecha_registro=? WHERE id_molde=?";
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
        String query = "DELETE FROM molde WHERE id_molde=?";
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