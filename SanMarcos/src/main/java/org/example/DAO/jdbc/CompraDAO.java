package org.example.DAO.jdbc;

import org.example.Config.Conexion;
import org.example.Modelo.pojo.DetalleCompra;
import org.example.Modelo.pojo.Compra;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompraDAO {

    private final Conexion conexion = Conexion.getInstancia();
    private final ProductoDAO productoDAO = new ProductoDAO();

    // ==================== CREATE ====================

    public boolean registrarCompra(Compra compra) {
        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);

            // 1. Insertar compra
            int idCompra = insertarCompra(conn, compra);
            if (idCompra == -1) {
                conn.rollback();
                return false;
            }
            compra.setId(idCompra);

            // 2. Insertar detalles y actualizar stock
            float total = 0;
            for (DetalleCompra detalle : compra.getDetalles()) {
                detalle.setIdCompra(idCompra);
                insertarDetalleCompra(conn, detalle);

                // Actualizar stock (aumentar)
                productoDAO.actualizarStock(detalle.getIdProducto(), detalle.getCantidad());

                total += detalle.getCantidad() * detalle.getPrecioCompra();
            }

            // 3. Actualizar total de la compra
            actualizarTotalCompra(conn, idCompra, total);
            compra.setTotal(total);

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar compra: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== READ ====================

    public Optional<Compra> obtenerPorId(int id) {
        String sql = "SELECT * FROM Compra WHERE Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Compra compra = new Compra();
                compra.setId(rs.getInt("Id"));
                compra.setFecha(rs.getDate("Fecha").toLocalDate());
                compra.setTotal(rs.getFloat("Total"));
                compra.setIdProveedor(rs.getInt("IdProveedor"));

                // Cargar detalles
                compra.setDetalles(obtenerDetallesPorCompra(conn, id));

                return Optional.of(compra);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener compra: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Compra> obtenerTodos() {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM Compra ORDER BY Fecha DESC";

        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Compra compra = new Compra();
                compra.setId(rs.getInt("Id"));
                compra.setFecha(rs.getDate("Fecha").toLocalDate());
                compra.setTotal(rs.getFloat("Total"));
                compra.setIdProveedor(rs.getInt("IdProveedor"));

                compra.setDetalles(obtenerDetallesPorCompra(conn, compra.getId()));
                compras.add(compra);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener compras: " + e.getMessage());
        }
        return compras;
    }

    public List<Compra> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM Compra WHERE Fecha BETWEEN ? AND ? ORDER BY Fecha DESC";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Compra compra = new Compra();
                compra.setId(rs.getInt("Id"));
                compra.setFecha(rs.getDate("Fecha").toLocalDate());
                compra.setTotal(rs.getFloat("Total"));
                compra.setIdProveedor(rs.getInt("IdProveedor"));

                compra.setDetalles(obtenerDetallesPorCompra(conn, compra.getId()));
                compras.add(compra);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar compras: " + e.getMessage());
        }
        return compras;
    }

    public List<Compra> buscarPorProveedor(int idProveedor) {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM Compra WHERE IdProveedor = ? ORDER BY Fecha DESC";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProveedor);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Compra compra = new Compra();
                compra.setId(rs.getInt("Id"));
                compra.setFecha(rs.getDate("Fecha").toLocalDate());
                compra.setTotal(rs.getFloat("Total"));
                compra.setIdProveedor(rs.getInt("IdProveedor"));

                compra.setDetalles(obtenerDetallesPorCompra(conn, compra.getId()));
                compras.add(compra);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar compras por proveedor: " + e.getMessage());
        }
        return compras;
    }

    // ==================== DELETE ====================

    public boolean eliminar(int id) {
        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);

            // Obtener detalles para restaurar stock
            List<DetalleCompra> detalles = obtenerDetallesPorCompra(conn, id);

            // Eliminar detalles
            String sqlDetalle = "DELETE FROM DetalleCompra WHERE IdCompra = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDetalle)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            // Restaurar stock (restar lo que se había sumado)
            for (DetalleCompra detalle : detalles) {
                productoDAO.actualizarStock(detalle.getIdProducto(), -detalle.getCantidad());
            }

            // Eliminar compra
            String sqlCompra = "DELETE FROM Compra WHERE Id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCompra)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar compra: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private int insertarCompra(Connection conn, Compra compra) throws SQLException {
        String sql = "INSERT INTO Compra (Fecha, Total, IdProveedor) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, Date.valueOf(compra.getFecha() != null ? compra.getFecha() : LocalDate.now()));
            pstmt.setFloat(2, 0); // Total temporal
            pstmt.setInt(3, compra.getIdProveedor());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private void insertarDetalleCompra(Connection conn, DetalleCompra detalle) throws SQLException {
        String sql = "INSERT INTO DetalleCompra (Cantidad, PrecioCompra, IdProducto, IdCompra) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setFloat(1, detalle.getCantidad());
            pstmt.setFloat(2, detalle.getPrecioCompra());
            pstmt.setInt(3, detalle.getIdProducto());
            pstmt.setInt(4, detalle.getIdCompra());

            pstmt.executeUpdate();
        }
    }

    private List<DetalleCompra> obtenerDetallesPorCompra(Connection conn, int idCompra) throws SQLException {
        List<DetalleCompra> detalles = new ArrayList<>();
        String sql = "SELECT * FROM DetalleCompra WHERE IdCompra = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCompra);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DetalleCompra detalle = new DetalleCompra();
                detalle.setId(rs.getInt("Id"));
                detalle.setCantidad(rs.getFloat("Cantidad"));
                detalle.setPrecioCompra(rs.getFloat("PrecioCompra"));
                detalle.setIdProducto(rs.getInt("IdProducto"));
                detalle.setIdCompra(rs.getInt("IdCompra"));
                detalles.add(detalle);
            }
        }
        return detalles;
    }

    private void actualizarTotalCompra(Connection conn, int idCompra, float total) throws SQLException {
        String sql = "UPDATE Compra SET Total = ? WHERE Id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setFloat(1, total);
            pstmt.setInt(2, idCompra);
            pstmt.executeUpdate();
        }
    }
}