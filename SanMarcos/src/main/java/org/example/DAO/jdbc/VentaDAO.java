package org.example.DAO.jdbc;

import org.example.Config.Conexion;
import org.example.Modelo.pojo.DetalleVenta;
import org.example.Modelo.pojo.Venta;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentaDAO {

    private final Conexion conexion = Conexion.getInstancia();
    private final ProductoDAO productoDAO = new ProductoDAO();

    // ==================== CREATE ====================

    public boolean registrarVenta(Venta venta) {
        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);

            // 1. Insertar venta
            int idVenta = insertarVenta(conn, venta);
            if (idVenta == -1) {
                conn.rollback();
                return false;
            }
            venta.setId(idVenta);

            // 2. Insertar detalles y actualizar stock
            float total = 0;
            for (DetalleVenta detalle : venta.getDetalles()) {
                detalle.setIdVenta(idVenta);
                insertarDetalleVenta(conn, detalle);

                // Actualizar stock (restar)
                actualizarStockEnTransaccion(conn, detalle.getIdProducto(), -detalle.getCantidad());

                total += detalle.getCantidad() * detalle.getPrecioVenta();
            }

            // 3. Actualizar total de la venta
            actualizarTotalVenta(conn, idVenta, total);
            venta.setTotal(total);

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar venta: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) {}
                conexion.closeConnection(conn);
            }
        }
    }

    private void actualizarStockEnTransaccion(Connection conn, int idProducto, float cantidad) throws SQLException {
        String sql = "UPDATE Producto SET Stock = Stock + ? WHERE Id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setFloat(1, cantidad);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();
        }
    }

    // ==================== READ ====================

    public Optional<Venta> obtenerPorId(int id) {
        String sql = "SELECT * FROM Venta WHERE Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Venta venta = new Venta();
                venta.setId(rs.getInt("Id"));
                venta.setFecha(rs.getDate("Fecha").toLocalDate());
                venta.setTotal(rs.getFloat("Total"));
                venta.setIdCliente(rs.getInt("IdCliente"));
                if (rs.wasNull()) venta.setIdCliente(null);

                // Cargar detalles
                venta.setDetalles(obtenerDetallesPorVenta(conn, id));

                return Optional.of(venta);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener venta: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Venta> obtenerTodos() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM Venta ORDER BY Fecha DESC";

        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Venta venta = new Venta();
                venta.setId(rs.getInt("Id"));
                venta.setFecha(rs.getDate("Fecha").toLocalDate());
                venta.setTotal(rs.getFloat("Total"));
                venta.setIdCliente(rs.getInt("IdCliente"));
                if (rs.wasNull()) venta.setIdCliente(null);

                venta.setDetalles(obtenerDetallesPorVenta(conn, venta.getId()));
                ventas.add(venta);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
        }
        return ventas;
    }

    public List<Venta> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM Venta WHERE Fecha BETWEEN ? AND ? ORDER BY Fecha DESC";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Venta venta = new Venta();
                venta.setId(rs.getInt("Id"));
                venta.setFecha(rs.getDate("Fecha").toLocalDate());
                venta.setTotal(rs.getFloat("Total"));
                venta.setIdCliente(rs.getInt("IdCliente"));
                if (rs.wasNull()) venta.setIdCliente(null);

                venta.setDetalles(obtenerDetallesPorVenta(conn, venta.getId()));
                ventas.add(venta);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar ventas: " + e.getMessage());
        }
        return ventas;
    }

    // ==================== DELETE ====================

    public boolean eliminar(int id) {
        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);

            // Obtener detalles para restaurar stock
            List<DetalleVenta> detalles = obtenerDetallesPorVenta(conn, id);

            // Eliminar detalles
            String sqlDetalle = "DELETE FROM DetalleVenta WHERE IdVenta = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDetalle)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            // Restaurar stock
            for (DetalleVenta detalle : detalles) {
                productoDAO.actualizarStock(detalle.getIdProducto(), detalle.getCantidad());
            }

            // Eliminar venta
            String sqlVenta = "DELETE FROM Venta WHERE Id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlVenta)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar venta: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private int insertarVenta(Connection conn, Venta venta) throws SQLException {
        String sql = "INSERT INTO Venta (Fecha, Total, IdCliente) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, Date.valueOf(venta.getFecha() != null ? venta.getFecha() : LocalDate.now()));
            pstmt.setFloat(2, 0); // Total temporal
            if (venta.getIdCliente() != null) {
                pstmt.setInt(3, venta.getIdCliente());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private void insertarDetalleVenta(Connection conn, DetalleVenta detalle) throws SQLException {
        String sql = "INSERT INTO DetalleVenta (Cantidad, PrecioVenta, IdProducto, IdVenta) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setFloat(1, detalle.getCantidad());
            pstmt.setFloat(2, detalle.getPrecioVenta());
            pstmt.setInt(3, detalle.getIdProducto());
            pstmt.setInt(4, detalle.getIdVenta());

            pstmt.executeUpdate();
        }
    }

    private List<DetalleVenta> obtenerDetallesPorVenta(Connection conn, int idVenta) throws SQLException {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT * FROM DetalleVenta WHERE IdVenta = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVenta);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setId(rs.getInt("Id"));
                detalle.setCantidad(rs.getFloat("Cantidad"));
                detalle.setPrecioVenta(rs.getFloat("PrecioVenta"));
                detalle.setIdProducto(rs.getInt("IdProducto"));
                detalle.setIdVenta(rs.getInt("IdVenta"));
                detalles.add(detalle);
            }
        }
        return detalles;
    }

    private void actualizarTotalVenta(Connection conn, int idVenta, float total) throws SQLException {
        String sql = "UPDATE Venta SET Total = ? WHERE Id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setFloat(1, total);
            pstmt.setInt(2, idVenta);
            pstmt.executeUpdate();
        }
    }
}