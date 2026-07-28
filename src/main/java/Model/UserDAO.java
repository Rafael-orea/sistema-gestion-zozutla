package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User validarUser(String username, String password) {
        String query = "SELECT * FROM usuario WHERE usuario = ? AND contrasena = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_usuario"));
                    user.setUsername(rs.getString("usuario"));
                    user.setPassword(rs.getString("contrasena"));
                    user.setNombreCompleto(rs.getString("nombre"));
                    user.setRol(rs.getString("rol"));
                    return user;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
