package Model;

import java.sql.*;
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
        String query = "INSERT INTO alcancia (id_molde, nombre, descripcion, existencia, precio, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Si no tiene molde se guarda NULL
            if (a.getIdMolde() == 0) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, a.getIdMolde());
            }

            pstmt.setString(2, a.getNombre());
            pstmt.setString(3, a.getDescripcion());
            pstmt.setInt(4, a.getExistencia());
            pstmt.setDouble(5, a.getPrecio());
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

    public boolean updateAlcancia(Alcancia a) {
        String query = "UPDATE alcancia SET id_molde=?, nombre=?, descripcion=?, existencia=?, precio=?, estado=? WHERE id_alcancia=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Si no tiene molde se guarda NULL
            if (a.getIdMolde() == 0) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, a.getIdMolde());
            }

            pstmt.setString(2, a.getNombre());
            pstmt.setString(3, a.getDescripcion());
            pstmt.setInt(4, a.getExistencia());
            pstmt.setDouble(5, a.getPrecio());
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

    public List<Molde> getMoldesDisponibles() {
        List<Molde> moldes = new ArrayList<>();

        // Primer elemento: Sin molde (por encargo)
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

        // id_molde puede ser NULL
        int idMolde = rs.getInt("id_molde");
        if (rs.wasNull()) {
            a.setIdMolde(0);
        } else {
            a.setIdMolde(idMolde);
        }

        a.setNombre(rs.getString("nombre"));
        a.setDescripcion(rs.getString("descripcion"));
        a.setExistencia(rs.getInt("existencia"));
        a.setPrecio(rs.getDouble("precio"));
        a.setEstado(rs.getString("estado"));
        return a;
    }
}