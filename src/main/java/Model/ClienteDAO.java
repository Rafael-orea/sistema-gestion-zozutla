package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> getAllClientes() {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT * FROM cliente ORDER BY id_cliente";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extraerCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getAllClientes: " + e.getMessage());
        }
        return lista;
    }

    public List<Cliente> searchClientes(String termino) {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT * FROM cliente WHERE nombre LIKE ? ORDER BY id_cliente";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + termino + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extraerCliente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searchClientes: " + e.getMessage());
        }
        return lista;
    }

    public boolean createCliente(Cliente c) {
        String query = "INSERT INTO cliente (nombre, telefono, pais, tipo, calle, ciudad, estado_region, codigo_postal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, c.getNombre());
            pstmt.setString(2, c.getTelefono());
            pstmt.setString(3, c.getPais());
            pstmt.setString(4, c.getTipo());
            pstmt.setString(5, c.getCalle());
            pstmt.setString(6, c.getCiudad());
            pstmt.setString(7, c.getEstadoRegion());
            pstmt.setString(8, c.getCodigoPostal());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) c.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error createCliente: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCliente(Cliente c) {
        String query = "UPDATE cliente SET nombre=?, telefono=?, pais=?, tipo=?, calle=?, ciudad=?, estado_region=?, codigo_postal=? WHERE id_cliente=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, c.getNombre());
            pstmt.setString(2, c.getTelefono());
            pstmt.setString(3, c.getPais());
            pstmt.setString(4, c.getTipo());
            pstmt.setString(5, c.getCalle());
            pstmt.setString(6, c.getCiudad());
            pstmt.setString(7, c.getEstadoRegion());
            pstmt.setString(8, c.getCodigoPostal());
            pstmt.setInt(9, c.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updateCliente: " + e.getMessage());
        }
        return false;
    }

    private Cliente extraerCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setTelefono(rs.getString("telefono"));
        c.setPais(rs.getString("pais"));
        c.setTipo(rs.getString("tipo"));
        c.setCalle(rs.getString("calle"));
        c.setCiudad(rs.getString("ciudad"));
        c.setEstadoRegion(rs.getString("estado_region"));
        c.setCodigoPostal(rs.getString("codigo_postal"));
        return c;
    }

    public boolean deleteCliente(int id) {
        String query = "DELETE FROM cliente WHERE id_cliente=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleteCliente: " + e.getMessage());
        }
        return false;
    }

}