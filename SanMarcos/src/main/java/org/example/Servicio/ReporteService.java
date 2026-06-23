package org.example.Servicio;

import org.example.Config.Conexion;
import org.example.DTO.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteService {

    private final Conexion conexion = Conexion.getInstancia();

    // ==================== REPORTE VENTAS ====================

    public List<ReporteVentaDTO> obtenerReporteVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReporteVentaDTO> lista = new ArrayList<>();
        String sql = "{CALL sp_ReporteVentas(?, ?)}";

        try (Connection conn = conexion.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ReporteVentaDTO dto = new ReporteVentaDTO();
                dto.setId(rs.getInt("Id"));
                dto.setFecha(rs.getDate("Fecha").toLocalDate());
                dto.setCliente(rs.getString("Cliente"));
                dto.setTiposProductos(rs.getInt("TiposProductos"));
                dto.setTotal(rs.getDouble("Total"));
                dto.setMetodoPago(rs.getString("MetodoPago"));
                lista.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener reporte de ventas: " + e.getMessage());
        }
        return lista;
    }

    // ==================== DETALLE VENTA ====================

    public List<DetalleVentaDTO> obtenerDetalleVenta(int idVenta) {
        List<DetalleVentaDTO> lista = new ArrayList<>();
        String sql = "{CALL sp_DetalleVenta(?)}";

        try (Connection conn = conexion.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idVenta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DetalleVentaDTO dto = new DetalleVentaDTO();
                dto.setProducto(rs.getString("Producto"));
                dto.setViscosidad(rs.getString("Viscosidad"));
                dto.setCodigo(rs.getString("Codigo"));
                dto.setMarca(rs.getString("Marca"));
                dto.setCategoria(rs.getString("Categoria"));
                dto.setCantidad(rs.getFloat("Cantidad"));
                dto.setPrecioVenta(rs.getFloat("PrecioVenta"));
                dto.setSubtotal(rs.getFloat("Subtotal"));
                lista.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener detalle de venta: " + e.getMessage());
        }
        return lista;
    }

    // ==================== HISTORIAL COMPRAS ====================

    public List<HistorialCompraDTO> obtenerHistorialCompras(LocalDate fechaInicio, LocalDate fechaFin) {
        List<HistorialCompraDTO> lista = new ArrayList<>();
        String sql = "{CALL sp_HistorialCompras(?, ?)}";

        try (Connection conn = conexion.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HistorialCompraDTO dto = new HistorialCompraDTO();
                dto.setId(rs.getInt("Id"));
                dto.setFecha(rs.getDate("Fecha").toLocalDate());
                dto.setProveedor(rs.getString("Proveedor"));
                dto.setTiposProductos(rs.getInt("TiposProductos"));
                dto.setTotal(rs.getDouble("Total"));
                lista.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener historial de compras: " + e.getMessage());
        }
        return lista;
    }

    // ==================== DETALLE COMPRA ====================

    public List<DetalleCompraDTO> obtenerDetalleCompra(int idCompra) {
        List<DetalleCompraDTO> lista = new ArrayList<>();
        String sql = "{CALL sp_DetalleCompra(?)}";

        try (Connection conn = conexion.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idCompra);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DetalleCompraDTO dto = new DetalleCompraDTO();
                dto.setProducto(rs.getString("Producto"));
                dto.setViscosidad(rs.getString("Viscosidad"));
                dto.setCodigo(rs.getString("Codigo"));
                dto.setMarca(rs.getString("Marca"));
                dto.setCategoria(rs.getString("Categoria"));
                dto.setCantidad(rs.getFloat("Cantidad"));
                dto.setPrecioCompra(rs.getFloat("PrecioCompra"));
                dto.setSubtotal(rs.getFloat("Subtotal"));
                lista.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener detalle de compra: " + e.getMessage());
        }
        return lista;
    }

    // ==================== PRODUCTOS MÁS VENDIDOS ====================

    public List<ProductoVendidoDTO> obtenerProductosMasVendidos(LocalDate fechaInicio, LocalDate fechaFin, int top) {
        List<ProductoVendidoDTO> lista = new ArrayList<>();
        String sql = "{CALL sp_ProductosMasVendidos(?, ?, ?)}";

        try (Connection conn = conexion.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            stmt.setInt(3, top);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductoVendidoDTO dto = new ProductoVendidoDTO();
                dto.setId(rs.getInt("Id"));
                dto.setNombre(rs.getString("Nombre"));
                dto.setCategoria(rs.getString("Categoria"));
                dto.setMarca(rs.getString("Marca"));
                dto.setTotalVendido(rs.getFloat("TotalVendido"));
                dto.setTotalFacturado(rs.getDouble("TotalFacturado"));
                lista.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos más vendidos: " + e.getMessage());
        }
        return lista;
    }

    // ==================== PRODUCTOS BAJO STOCK ====================

    public List<ProductoBajoStockDTO> obtenerProductosBajoStock(float stockMinimo) {
        List<ProductoBajoStockDTO> lista = new ArrayList<>();
        String sql = "{CALL sp_ProductosBajoStock(?)}";

        try (Connection conn = conexion.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setFloat(1, stockMinimo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductoBajoStockDTO dto = new ProductoBajoStockDTO();
                dto.setId(rs.getInt("Id"));
                dto.setNombre(rs.getString("Nombre"));
                dto.setCategoria(rs.getString("Categoria"));
                dto.setMarca(rs.getString("Marca"));
                dto.setStock(rs.getFloat("Stock"));
                dto.setPrecio(rs.getFloat("Precio"));
                lista.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos bajo stock: " + e.getMessage());
        }
        return lista;
    }

    // ==================== INFO VENTA ====================
    public VentaInfoDTO obtenerInfoVenta(int idVenta) {
        VentaInfoDTO info = null;
        String sql = "SELECT v.Fecha, v.MetodoPago, " +
                "ISNULL(p.Nombres + ' ' + p.Apellidos, 'CLIENTE OCASIONAL') AS Cliente " +
                "FROM Venta v " +
                "LEFT JOIN Cliente c ON v.IdCliente = c.Id " +
                "LEFT JOIN Persona p ON c.IdPersona = p.Id " +
                "WHERE v.Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                info = new VentaInfoDTO();
                info.setFecha(rs.getDate("Fecha").toLocalDate());
                info.setCliente(rs.getString("Cliente"));
                info.setMetodoPago(rs.getString("MetodoPago"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener info de venta: " + e.getMessage());
        }
        return info;
    }

    // ==================== INFO COMPRA ====================

    public CompraInfoDTO obtenerInfoCompra(int idCompra) {
        CompraInfoDTO info = null;
        String sql = "SELECT c.Fecha, pr.Empresa AS Proveedor " +
                "FROM Compra c " +
                "INNER JOIN Proveedor pr ON c.IdProveedor = pr.Id " +
                "WHERE c.Id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCompra);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                info = new CompraInfoDTO();
                info.setFecha(rs.getDate("Fecha").toLocalDate());
                info.setProveedor(rs.getString("Proveedor"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener info de compra: " + e.getMessage());
        }
        return info;
    }
}