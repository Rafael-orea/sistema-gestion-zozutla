package Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaEnvioDAO {

    public boolean registrarIncidencia(IncidenciaEnvio inc) {
        String queryInc = "INSERT INTO incidencia_envio (id_envio, id_alcancia, cantidad_afectada, faltantes, rotas, descripcion, fecha, responsabilidad, porcentaje_cliente, porcentaje_zozutla) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String queryPerdida = "INSERT INTO perdida (id_alcancia, cantidad, motivo, fecha) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            // Registrar incidencia
            try (PreparedStatement pstmt = conn.prepareStatement(queryInc, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, inc.getIdEnvio());
                pstmt.setInt(2, inc.getIdAlcancia());
                pstmt.setInt(3, inc.getCantidadAfectada());
                pstmt.setInt(4, inc.getFaltantes());
                pstmt.setInt(5, inc.getRotas());
                pstmt.setString(6, inc.getDescripcion());
                pstmt.setDate(7, Date.valueOf(LocalDate.now()));
                pstmt.setString(8, inc.getResponsabilidad());
                pstmt.setDouble(9, inc.getPorcentajeCliente());
                pstmt.setDouble(10, inc.getPorcentajeZozutla());
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) inc.setId(rs.getInt(1));
                }
            }

            // Registrar pérdida en tabla perdida
            String motivo = "Incidencia de envio - " + inc.getResponsabilidad() +
                    " - Cliente: " + inc.getPorcentajeCliente() + "%" +
                    " - Zozutla: " + inc.getPorcentajeZozutla() + "%";
            try (PreparedStatement pstmt = conn.prepareStatement(queryPerdida)) {
                pstmt.setInt(1, inc.getIdAlcancia());
                pstmt.setInt(2, inc.getCantidadAfectada());
                pstmt.setString(3, motivo);
                pstmt.setDate(4, Date.valueOf(LocalDate.now()));
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error registrarIncidencia: " + e.getMessage());
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

    public List<IncidenciaEnvio> getIncidenciasPorEnvio(int idEnvio) {
        List<IncidenciaEnvio> lista = new ArrayList<>();
        String query =
                "SELECT i.*, a.nombre as nombre_alcancia " +
                        "FROM incidencia_envio i " +
                        "LEFT JOIN alcancia a ON i.id_alcancia = a.id_alcancia " +
                        "WHERE i.id_envio = ? " +
                        "ORDER BY i.fecha DESC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idEnvio);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    IncidenciaEnvio inc = new IncidenciaEnvio();
                    inc.setId(rs.getInt("id_incidencia"));
                    inc.setIdEnvio(rs.getInt("id_envio"));
                    inc.setIdAlcancia(rs.getInt("id_alcancia"));
                    inc.setNombreAlcancia(rs.getString("nombre_alcancia"));
                    inc.setFaltantes(rs.getInt("faltantes"));
                    inc.setRotas(rs.getInt("rotas"));
                    inc.setCantidadAfectada(rs.getInt("cantidad_afectada"));
                    inc.setDescripcion(rs.getString("descripcion"));
                    inc.setFecha(rs.getString("fecha"));
                    inc.setResponsabilidad(rs.getString("responsabilidad"));
                    inc.setPorcentajeCliente(rs.getDouble("porcentaje_cliente"));
                    inc.setPorcentajeZozutla(rs.getDouble("porcentaje_zozutla"));
                    lista.add(inc);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getIncidenciasPorEnvio: " + e.getMessage());
        }
        return lista;
    }

    public boolean tieneIncidencias(int idEnvio) {
        String query = "SELECT COUNT(*) FROM incidencia_envio WHERE id_envio = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idEnvio);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error tieneIncidencias: " + e.getMessage());
        }
        return false;
    }
}