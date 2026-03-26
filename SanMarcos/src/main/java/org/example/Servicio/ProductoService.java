package org.example.Servicio;

import org.example.DAO.jdbc.ProductoDAO;
import org.example.Modelo.pojo.*;

import java.util.List;
import java.util.Optional;

public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService() {
        this.productoDAO = new ProductoDAO();
    }

    // ==================== PRODUCTO BASE ====================

    public Producto guardarProducto(Producto producto) {
        validarProductoBase(producto);

        if (productoDAO.insertar(producto)) {
            return producto;
        }
        throw new RuntimeException("Error al guardar el producto");
    }

    // ==================== ACEITE ====================

    public Aceite guardarAceite(Aceite aceite) {
        validarAceite(aceite);

        if (productoDAO.insertarAceite(aceite)) {
            return aceite;
        }
        throw new RuntimeException("Error al guardar el aceite");
    }

    // ==================== FILTRO ====================

    public Filtro guardarFiltro(Filtro filtro) {
        validarFiltro(filtro);

        if (productoDAO.insertarFiltro(filtro)) {
            return filtro;
        }
        throw new RuntimeException("Error al guardar el filtro");
    }

    // ==================== FOCO ====================

    public Foco guardarFoco(Foco foco) {
        validarFoco(foco);

        if (productoDAO.insertarFoco(foco)) {
            return foco;
        }
        throw new RuntimeException("Error al guardar el foco");
    }

    // ==================== READ ====================

    public Optional<Producto> buscarProductoPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return productoDAO.obtenerPorId(id);
    }

    public Optional<Aceite> buscarAceitePorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return productoDAO.obtenerAceitePorId(id);
    }

    public Optional<Filtro> buscarFiltroPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return productoDAO.obtenerFiltroPorId(id);
    }

    public List<Producto> listarTodos() {
        return productoDAO.obtenerTodos();
    }

    public List<Producto> buscar(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return listarTodos();
        }
        return productoDAO.buscar(criterio);
    }

    public List<Producto> obtenerProductosBajoStock(float limite) {
        if (limite < 0) {
            throw new IllegalArgumentException("El límite no puede ser negativo");
        }
        return productoDAO.obtenerProductosBajoStock(limite);
    }

    // ==================== UPDATE ====================

    public Producto actualizarProducto(Producto producto) {
        if (producto.getId() <= 0) {
            throw new IllegalArgumentException("ID inválido para actualizar");
        }
        validarProductoBase(producto);

        if (productoDAO.actualizar(producto)) {
            return producto;
        }
        throw new RuntimeException("Error al actualizar el producto");
    }

    public boolean actualizarStock(int idProducto, float cantidad) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        float stockActual = productoDAO.obtenerStock(idProducto);
        if (stockActual + cantidad < 0) {
            throw new IllegalStateException("Stock insuficiente. Stock actual: " + stockActual);
        }

        return productoDAO.actualizarStock(idProducto, cantidad);
    }

    // ==================== DELETE ====================

    public void eliminarProducto(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Producto> producto = productoDAO.obtenerPorId(id);
        if (producto.isEmpty()) {
            throw new IllegalStateException("No existe producto con ID: " + id);
        }

        if (!productoDAO.eliminar(id)) {
            throw new RuntimeException("Error al eliminar el producto");
        }
    }

    // ==================== VALIDACIONES ====================

    private void validarProductoBase(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (producto.getIdCategoria() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una categoría");
        }
        if (producto.getIdMarca() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una marca");
        }
    }

    private void validarAceite(Aceite aceite) {
        validarProductoBase(aceite);

        if (aceite.getViscosidad() == null || aceite.getViscosidad().trim().isEmpty()) {
            throw new IllegalArgumentException("La viscosidad es obligatoria");
        }
        if (aceite.getTipoAceite() == null || aceite.getTipoAceite().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de aceite es obligatorio");
        }
        if (aceite.getIdPresentacion() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una presentación");
        }
    }

    private void validarFiltro(Filtro filtro) {
        validarProductoBase(filtro);

        if (filtro.getCodigo() == null || filtro.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (filtro.getRosca() == null || filtro.getRosca().trim().isEmpty()) {
            throw new IllegalArgumentException("La rosca es obligatoria");
        }
    }

    private void validarFoco(Foco foco) {
        validarProductoBase(foco);

        if (foco.getCodigo() == null || foco.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
    }
}