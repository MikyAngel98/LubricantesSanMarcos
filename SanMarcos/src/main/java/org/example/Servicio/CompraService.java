package org.example.Servicio;

import org.example.DAO.jdbc.CompraDAO;
import org.example.DAO.jdbc.ProductoDAO;
import org.example.Modelo.pojo.DetalleCompra;
import org.example.Modelo.pojo.Compra;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CompraService {

    private final CompraDAO compraDAO;
    private final ProductoDAO productoDAO;

    public CompraService() {
        this.compraDAO = new CompraDAO();
        this.productoDAO = new ProductoDAO();
    }

    // ==================== CREATE ====================

    public Compra registrarCompra(Compra compra) {
        validarCompra(compra);

        if (compraDAO.registrarCompra(compra)) {
            return compra;
        }
        throw new RuntimeException("Error al registrar la compra");
    }

    // ==================== READ ====================

    public Optional<Compra> buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return compraDAO.obtenerPorId(id);
    }

    public List<Compra> listarTodas() {
        return compraDAO.obtenerTodos();
    }

    public List<Compra> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha inicio no puede ser mayor a la fecha fin");
        }
        return compraDAO.buscarPorFecha(fechaInicio, fechaFin);
    }

    public List<Compra> buscarPorProveedor(int idProveedor) {
        if (idProveedor <= 0) {
            throw new IllegalArgumentException("ID de proveedor inválido");
        }
        return compraDAO.buscarPorProveedor(idProveedor);
    }

    // ==================== DELETE ====================

    public void eliminarCompra(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Compra> compra = compraDAO.obtenerPorId(id);
        if (compra.isEmpty()) {
            throw new IllegalStateException("No existe compra con ID: " + id);
        }

        if (!compraDAO.eliminar(id)) {
            throw new RuntimeException("Error al eliminar la compra");
        }
    }

    // ==================== VALIDACIONES ====================

    private void validarCompra(Compra compra) {
        if (compra.getIdProveedor() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un proveedor");
        }

        if (compra.getDetalles() == null || compra.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La compra debe tener al menos un detalle");
        }

        for (DetalleCompra detalle : compra.getDetalles()) {
            if (detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
            }
            if (detalle.getPrecioCompra() <= 0) {
                throw new IllegalArgumentException("El precio de compra debe ser mayor a 0");
            }
            if (detalle.getIdProducto() <= 0) {
                throw new IllegalArgumentException("Producto inválido");
            }
        }
    }
}