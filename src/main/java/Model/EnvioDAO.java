package Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnvioDAO {

    public List<Envio> getAllEnvios() {
        List<Envio> lista = new ArrayList<>();
        String query =
                "SELECT e.id_envio, e.destino, e.fecha, e.estado, " +
                        "v.folio, c.nombre as cliente " +
                        "FROM envio e " +
                        "JOIN venta v ON e.id_venta = v.id_venta " +
                        "JOIN cliente c ON v.id_cliente = c.id_cliente " +
                        "ORDER BY e.fecha DESC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extraerEnvio(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getAllEnvios: " + e.getMessage());
        }
        return lista;
    }

    public List<Envio> searchEnvios(LocalDate fecha, String idCliente) {
        List<Envio> lista = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT e.id_envio, e.destino, e.fecha, e.estado, " +
                        "v.folio, c.nombre as cliente " +
                        "FROM envio e " +
                        "JOIN venta v ON e.id_venta = v.id_venta " +
                        "JOIN cliente c ON v.id_cliente = c.id_cliente " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (fecha != null) {
            query.append("AND DATE(e.fecha) = ? ");
            params.add(Date.valueOf(fecha));
        }
        if (idCliente != null && !idCliente.isEmpty()) {
            query.append("AND v.id_cliente = ? ");
            params.add(Integer.parseInt(idCliente));
        }
        query.append("ORDER BY e.fecha DESC");

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extraerEnvio(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searchEnvios: " + e.getMessage());
        }
        return lista;
    }

    public boolean registrarEnvio(Envio envio) {
        String query = "INSERT INTO envio (id_venta, id_usuario, destino, fecha, estado) VALUES (?, 1, ?, ?, 'en_proceso')";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, envio.getIdVenta());
            pstmt.setString(2, envio.getDestino());
            pstmt.setDate(3, Date.valueOf(envio.getFecha()));
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) envio.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error registrarEnvio: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizarEstado(int idEnvio, String estado) {
        String query = "UPDATE envio SET estado = ? WHERE id_envio = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, estado);
            pstmt.setInt(2, idEnvio);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizarEstado: " + e.getMessage());
        }
        return false;
    }

    public List<Venta> getVentasSinEnvio() {
        List<Venta> lista = new ArrayList<>();
        String query =
                "SELECT v.id_venta, v.folio, v.fecha, v.total, c.nombre as cliente " +
                        "FROM venta v " +
                        "JOIN cliente c ON v.id_cliente = c.id_cliente " +
                        "WHERE v.id_venta NOT IN (SELECT id_venta FROM envio) " +
                        "ORDER BY v.fecha DESC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Venta v = new Venta();
                v.setId(rs.getInt("id_venta"));
                v.setFolio(rs.getString("folio"));
                v.setNombreCliente(rs.getString("cliente"));
                v.setTotal(rs.getDouble("total"));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error getVentasSinEnvio: " + e.getMessage());
        }
        return lista;
    }

    public List<DetalleVenta> getProductosDeVenta(int idVenta) {
        List<DetalleVenta> lista = new ArrayList<>();
        String query =
                "SELECT dv.cantidad, dv.precio_unitario, dv.subtotal, a.nombre " +
                        "FROM detalle_venta dv " +
                        "JOIN alcancia a ON dv.id_alcancia = a.id_alcancia " +
                        "WHERE dv.id_venta = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idVenta);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta d = new DetalleVenta();
                    d.setNombreAlcancia(rs.getString("nombre"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    lista.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getProductosDeVenta: " + e.getMessage());
        }
        return lista;
    }

    public List<Cliente> getClientesDisponibles() {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT id_cliente, nombre FROM cliente ORDER BY nombre";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error getClientesDisponibles: " + e.getMessage());
        }
        return lista;
    }

    private Envio extraerEnvio(ResultSet rs) throws SQLException {
        Envio e = new Envio();
        e.setId(rs.getInt("id_envio"));
        e.setFolio(rs.getString("folio"));
        e.setCliente(rs.getString("cliente"));
        e.setDestino(rs.getString("destino"));
        e.setEstado(rs.getString("estado"));
        Date fecha = rs.getDate("fecha");
        if (fecha != null) e.setFecha(fecha.toLocalDate());
        return e;
    }
}