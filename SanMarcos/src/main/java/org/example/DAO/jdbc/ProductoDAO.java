package org.example.DAO.jdbc;

import org.example.Config.Conexion;
import org.example.Modelo.pojo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDAO {

    private final Conexion conexion = Conexion.getInstancia();

    // ==================== CREATE ====================

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Producto (Nombre, Precio, Stock, Detalle, IdCategoria, IdMarca) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = conexion.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, producto.getNombre());
                pstmt.setFloat(2, producto.getPrecio());
                pstmt.setFloat(3, producto.getStock());
                pstmt.setString(4, producto.getDetalle());
                pstmt.setInt(5, producto.getIdCategoria());
                pstmt.setInt(6, producto.getIdMarca());

                int filas = pstmt.executeUpdate();
                if (filas > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        producto.setId(rs.getInt(1));
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
        } finally {
            if (conn != null) {
                conexion.closeConnection(conn);
            }
        }
        return false;
    }

    public boolean insertarAceite(Aceite aceite) {
        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);

            // Insertar en Producto
            if (!insertarEnProducto(conn, aceite)) {
                conn.rollback();
                return false;
            }

            // Insertar en Aceite
            String sql = "INSERT INTO Aceite (Viscosidad, TipoAceite, Uso, EsAgrenel, IdPresentacion, IdProducto) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, aceite.getViscosidad());
                pstmt.setString(2, aceite.getTipoAceite());
                pstmt.setString(3, aceite.getUso());
                pstmt.setBoolean(4, aceite.isEsAgranel());
                pstmt.setInt(5, aceite.getIdPresentacion());
                pstmt.setInt(6, aceite.getId());

                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar aceite: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    public boolean insertarFiltro(Filtro filtro) {
        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);

            if (!insertarEnProducto(conn, filtro)) {
                conn.rollback();
                return false;
            }

            String sql = "INSERT INTO Filtro (Codigo, Rosca, Uso, IdProducto) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, filtro.getCodigo());
                pstmt.setString(2, filtro.getRosca());
                pstmt.setString(3, filtro.getUso());
                pstmt.setInt(4, filtro.getId());

                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar filtro: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    public boolean insertarFoco(Foco foco) {
        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);

            if (!insertarEnProducto(conn, foco)) {
                conn.rollback();
                return false;
            }

            String sql = "INSERT INTO Foco (Codigo, IdProducto) VALUES (?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, foco.getCodigo());
                pstmt.setInt(2, foco.getId());

                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar foco: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== READ ====================

    public Optional<Producto> obtenerPorId(int id) {
        String sql = "SELECT * FROM Producto WHERE Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("Id"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setPrecio(rs.getFloat("Precio"));
                producto.setStock(rs.getFloat("Stock"));
                producto.setDetalle(rs.getString("Detalle"));
                producto.setIdCategoria(rs.getInt("IdCategoria"));
                producto.setIdMarca(rs.getInt("IdMarca"));
                return Optional.of(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener producto: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Aceite> obtenerAceitePorId(int id) {
        String sql = "SELECT p.*, a.Viscosidad, a.TipoAceite, a.Uso, a.EsAgrenel, a.IdPresentacion " +
                "FROM Producto p " +
                "INNER JOIN Aceite a ON p.Id = a.IdProducto " +
                "WHERE p.Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Aceite aceite = new Aceite();
                aceite.setId(rs.getInt("Id"));
                aceite.setNombre(rs.getString("Nombre"));
                aceite.setPrecio(rs.getFloat("Precio"));
                aceite.setStock(rs.getFloat("Stock"));
                aceite.setDetalle(rs.getString("Detalle"));
                aceite.setIdCategoria(rs.getInt("IdCategoria"));
                aceite.setIdMarca(rs.getInt("IdMarca"));
                aceite.setViscosidad(rs.getString("Viscosidad"));
                aceite.setTipoAceite(rs.getString("TipoAceite"));
                aceite.setUso(rs.getString("Uso"));
                aceite.setEsAgranel(rs.getBoolean("EsAgrenel"));
                aceite.setIdPresentacion(rs.getInt("IdPresentacion"));
                return Optional.of(aceite);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener aceite: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto ORDER BY Nombre";

        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("Id"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setPrecio(rs.getFloat("Precio"));
                producto.setStock(rs.getFloat("Stock"));
                producto.setDetalle(rs.getString("Detalle"));
                producto.setIdCategoria(rs.getInt("IdCategoria"));
                producto.setIdMarca(rs.getInt("IdMarca"));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return productos;
    }

    public List<Producto> buscar(String criterio) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Nombre LIKE ? ORDER BY Nombre";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + criterio + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("Id"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setPrecio(rs.getFloat("Precio"));
                producto.setStock(rs.getFloat("Stock"));
                producto.setDetalle(rs.getString("Detalle"));
                producto.setIdCategoria(rs.getInt("IdCategoria"));
                producto.setIdMarca(rs.getInt("IdMarca"));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }
        return productos;
    }

    public List<Producto> obtenerProductosBajoStock(float limite) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Stock <= ? ORDER BY Stock ASC";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setFloat(1, limite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("Id"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setPrecio(rs.getFloat("Precio"));
                producto.setStock(rs.getFloat("Stock"));
                producto.setDetalle(rs.getString("Detalle"));
                producto.setIdCategoria(rs.getInt("IdCategoria"));
                producto.setIdMarca(rs.getInt("IdMarca"));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos bajo stock: " + e.getMessage());
        }
        return productos;
    }

    public float obtenerStock(int idProducto) {
        String sql = "SELECT Stock FROM Producto WHERE Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getFloat("Stock");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener stock: " + e.getMessage());
        }
        return 0;
    }

    // ==================== UPDATE ====================

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Producto SET Nombre = ?, Precio = ?, Stock = ?, " +
                "Detalle = ?, IdCategoria = ?, IdMarca = ? WHERE Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getNombre());
            pstmt.setFloat(2, producto.getPrecio());
            pstmt.setFloat(3, producto.getStock());
            pstmt.setString(4, producto.getDetalle());
            pstmt.setInt(5, producto.getIdCategoria());
            pstmt.setInt(6, producto.getIdMarca());
            pstmt.setInt(7, producto.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarStock(int idProducto, float cantidad) {
        String sql = "UPDATE Producto SET Stock = Stock + ? WHERE Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setFloat(1, cantidad);
            pstmt.setInt(2, idProducto);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    // ==================== DELETE ====================

    public boolean eliminar(int id) {
        eliminarDeTablasEspecificas(id);

        String sql = "DELETE FROM Producto WHERE Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private boolean insertarEnProducto(Connection conn, Producto producto) throws SQLException {
        String sql = "INSERT INTO Producto (Nombre, Precio, Stock, Detalle, IdCategoria, IdMarca) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setFloat(2, producto.getPrecio());
            pstmt.setFloat(3, producto.getStock());
            pstmt.setString(4, producto.getDetalle());
            pstmt.setInt(5, producto.getIdCategoria());
            pstmt.setInt(6, producto.getIdMarca());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                producto.setId(rs.getInt(1));
                return true;
            }
        }
        return false;
    }

    private void eliminarDeTablasEspecificas(int idProducto) {
        String[] tablas = {"Aceite", "Filtro", "Foco"};

        try (Connection conn = conexion.getConnection()) {
            for (String tabla : tablas) {
                String sql = "DELETE FROM " + tabla + " WHERE IdProducto = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, idProducto);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar de tablas específicas: " + e.getMessage());
        }
    }

    // ==================== UPDATE - FILTRO ====================

    public boolean actualizarFiltro(Filtro filtro) {
        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);

            // Actualizar Producto
            String sqlProducto = "UPDATE Producto SET Nombre = ?, Precio = ?, Stock = ?, " +
                    "Detalle = ?, IdCategoria = ?, IdMarca = ? WHERE Id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlProducto)) {
                pstmt.setString(1, filtro.getNombre());
                pstmt.setFloat(2, filtro.getPrecio());
                pstmt.setFloat(3, filtro.getStock());
                pstmt.setString(4, filtro.getDetalle());
                pstmt.setInt(5, filtro.getIdCategoria());
                pstmt.setInt(6, filtro.getIdMarca());
                pstmt.setInt(7, filtro.getId());
                pstmt.executeUpdate();
            }

            // Actualizar Filtro
            String sqlFiltro = "UPDATE Filtro SET Codigo = ?, Rosca = ?, Uso = ? WHERE IdProducto = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlFiltro)) {
                pstmt.setString(1, filtro.getCodigo());
                pstmt.setString(2, filtro.getRosca());
                pstmt.setString(3, filtro.getUso());
                pstmt.setInt(4, filtro.getId());
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar filtro: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== READ - FILTRO ====================

    public Optional<Filtro> obtenerFiltroPorId(int id) {
        String sql = "SELECT p.*, f.Codigo, f.Rosca, f.Uso " +
                "FROM Producto p " +
                "INNER JOIN Filtro f ON p.Id = f.IdProducto " +
                "WHERE p.Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Filtro filtro = new Filtro();
                filtro.setId(rs.getInt("Id"));
                filtro.setNombre(rs.getString("Nombre"));
                filtro.setPrecio(rs.getFloat("Precio"));
                filtro.setStock(rs.getFloat("Stock"));
                filtro.setDetalle(rs.getString("Detalle"));
                filtro.setIdCategoria(rs.getInt("IdCategoria"));
                filtro.setIdMarca(rs.getInt("IdMarca"));
                filtro.setCodigo(rs.getString("Codigo"));
                filtro.setRosca(rs.getString("Rosca"));
                filtro.setUso(rs.getString("Uso"));
                return Optional.of(filtro);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener filtro: " + e.getMessage());
        }
        return Optional.empty();
    }

}
