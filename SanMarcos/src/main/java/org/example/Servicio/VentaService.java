package org.example.Servicio;

import org.example.DAO.jdbc.VentaDAO;
import org.example.DAO.jdbc.ProductoDAO;
import org.example.Modelo.pojo.DetalleVenta;
import org.example.Modelo.pojo.Venta;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class VentaService {

    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;

    public VentaService() {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
    }

    // ==================== CREATE ====================

    public Venta registrarVenta(Venta venta) {
        validarVenta(venta);

        // Validar stock antes de registrar
        for (DetalleVenta detalle : venta.getDetalles()) {
            float stockActual = productoDAO.obtenerStock(detalle.getIdProducto());
            if (stockActual < detalle.getCantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente para producto ID: " + detalle.getIdProducto() +
                                ". Stock actual: " + stockActual + ", solicitado: " + detalle.getCantidad()
                );
            }
        }

        if (ventaDAO.registrarVenta(venta)) {
            return venta;
        }
        throw new RuntimeException("Error al registrar la venta");
    }

    // ==================== READ ====================

    public Optional<Venta> buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return ventaDAO.obtenerPorId(id);
    }

    public List<Venta> listarTodas() {
        return ventaDAO.obtenerTodos();
    }

    public List<Venta> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha inicio no puede ser mayor a la fecha fin");
        }
        return ventaDAO.buscarPorFecha(fechaInicio, fechaFin);
    }

    // ==================== DELETE ====================

    public void eliminarVenta(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Venta> venta = ventaDAO.obtenerPorId(id);
        if (venta.isEmpty()) {
            throw new IllegalStateException("No existe venta con ID: " + id);
        }

        if (!ventaDAO.eliminar(id)) {
            throw new RuntimeException("Error al eliminar la venta");
        }
    }

    // ==================== REPORTES ====================

    public float obtenerTotalVentasDelDia() {
        LocalDate hoy = LocalDate.now();
        List<Venta> ventas = ventaDAO.buscarPorFecha(hoy, hoy);
        return (float) ventas.stream()
                .mapToDouble(Venta::getTotal)
                .sum();
    }

    // ==================== VALIDACIONES ====================

    private void validarVenta(Venta venta) {
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un detalle");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            if (detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
            }
            if (detalle.getPrecioVenta() <= 0) {
                throw new IllegalArgumentException("El precio de venta debe ser mayor a 0");
            }
            if (detalle.getIdProducto() <= 0) {
                throw new IllegalArgumentException("Producto inválido");
            }
        }
    }
}