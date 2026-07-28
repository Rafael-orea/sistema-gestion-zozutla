package Model;

import java.sql.*;
import java.time.LocalDate;

public class IncidenciaEnvioDAO {

    public boolean registrarIncidencia(IncidenciaEnvio inc) {
        String query = "INSERT INTO incidencia_envio (id_envio, faltantes, rotas, descripcion, fecha) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, inc.getIdEnvio());
            pstmt.setInt(2, inc.getFaltantes());
            pstmt.setInt(3, inc.getRotas());
            pstmt.setString(4, inc.getDescripcion());
            pstmt.setDate(5, Date.valueOf(LocalDate.now()));
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) inc.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error registrarIncidencia: " + e.getMessage());
        }
        return false;
    }
}