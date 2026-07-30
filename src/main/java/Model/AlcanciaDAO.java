package Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlcanciaDAO {

    public List<Alcancia> getAllAlcancias() {
        List<Alcancia> lista = new ArrayList<>();
        String query = "SELECT * FROM alcancia ORDER BY id_alcancia";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extraerAlcancia(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getAllAlcancias: " + e.getMessage());
        }
        return lista;
    }

    public List<Alcancia> searchAlcancias(String termino) {
        List<Alcancia> lista = new ArrayList<>();
        String query = "SELECT * FROM alcancia WHERE nombre LIKE ? ORDER BY id_alcancia";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + termino + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extraerAlcancia(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searchAlcancias: " + e.getMessage());
        }
        return lista;
    }

    public boolean createAlcancia(Alcancia a) {
        String query = "INSERT INTO alcancia (id_molde, nombre, existencia, precio, precio_mayoreo, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            if (a.getIdMolde() == 0) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, a.getIdMolde());
            }
            pstmt.setString(2, a.getNombre());
            pstmt.setInt(3, a.getExistencia());
            pstmt.setDouble(4, a.getPrecio());
            pstmt.setDouble(5, a.getPrecioMayoreo());
            pstmt.setString(6, a.getEstado());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) a.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error createAlcancia: " + e.getMessage());
        }
        return false;
    }
    public boolean agregarStock(int idAlcancia, int cantidad) {
        String query = "UPDATE alcancia SET existencia = existencia + ?, estado = 'disponible' WHERE id_alcancia = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, idAlcancia);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error agregarStock: " + e.getMessage());
        }
        return false;
    }

    public boolean updateAlcancia(Alcancia a) {
        String query = "UPDATE alcancia SET id_molde=?, nombre=?, existencia=?, precio=?, precio_mayoreo=?, estado=? WHERE id_alcancia=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (a.getIdMolde() == 0) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, a.getIdMolde());
            }
            pstmt.setString(2, a.getNombre());
            pstmt.setInt(3, a.getExistencia());
            pstmt.setDouble(4, a.getPrecio());
            pstmt.setDouble(5, a.getPrecioMayoreo());
            pstmt.setString(6, a.getEstado());
            pstmt.setInt(7, a.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updateAlcancia: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteAlcancia(int id) {
        String query = "DELETE FROM alcancia WHERE id_alcancia=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleteAlcancia: " + e.getMessage());
        }
        return false;
    }

    public boolean registrarMerma(int idAlcancia, int cantidad, boolean tieneArreglo, String motivo) {
        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            if (tieneArreglo) {
                // Descontar de existencia normal y agregar a merma
                String queryMerma = "UPDATE alcancia SET existencia = existencia - ?, existencia_merma = existencia_merma + ? WHERE id_alcancia = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(queryMerma)) {
                    pstmt.setInt(1, cantidad);
                    pstmt.setInt(2, cantidad);
                    pstmt.setInt(3, idAlcancia);
                    pstmt.executeUpdate();
                }
            } else {
                // Descontar de existencia y registrar perdida
                String queryExistencia = "UPDATE alcancia SET existencia = existencia - ? WHERE id_alcancia = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(queryExistencia)) {
                    pstmt.setInt(1, cantidad);
                    pstmt.setInt(2, idAlcancia);
                    pstmt.executeUpdate();
                }

                String queryPerdida = "INSERT INTO perdida (id_alcancia, cantidad, motivo, fecha) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(queryPerdida)) {
                    pstmt.setInt(1, idAlcancia);
                    pstmt.setInt(2, cantidad);
                    pstmt.setString(3, motivo);
                    pstmt.setDate(4, Date.valueOf(LocalDate.now()));
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error registrarMerma: " + e.getMessage());
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

    public List<Molde> getMoldesDisponibles() {
        List<Molde> moldes = new ArrayList<>();
        Molde sinMolde = new Molde();
        sinMolde.setId(0);
        sinMolde.setNombre("Sin molde (por encargo)");
        moldes.add(sinMolde);
        String query = "SELECT * FROM molde ORDER BY nombre";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Molde m = new Molde();
                m.setId(rs.getInt("id_molde"));
                m.setNombre(rs.getString("nombre"));
                moldes.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error getMoldesDisponibles: " + e.getMessage());
        }
        return moldes;
    }

    private Alcancia extraerAlcancia(ResultSet rs) throws SQLException {
        Alcancia a = new Alcancia();
        a.setId(rs.getInt("id_alcancia"));
        int idMolde = rs.getInt("id_molde");
        if (rs.wasNull()) {
            a.setIdMolde(0);
        } else {
            a.setIdMolde(idMolde);
        }
        a.setNombre(rs.getString("nombre"));
        a.setExistencia(rs.getInt("existencia"));
        a.setExistenciaMerma(rs.getInt("existencia_merma"));
        a.setPrecio(rs.getDouble("precio"));
        a.setPrecioMayoreo(rs.getDouble("precio_mayoreo"));
        a.setEstado(rs.getString("estado"));
        return a;
    }
}