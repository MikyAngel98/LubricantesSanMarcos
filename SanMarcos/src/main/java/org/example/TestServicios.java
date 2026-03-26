package org.example;

import org.example.Modelo.jpa.*;
import org.example.Modelo.pojo.*;
import org.example.Servicio.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestServicios {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DE SERVICIOS ===\n");

        // ==================== 1. PROBAR CATÁLOGOS ====================
        System.out.println("--- 1. CATÁLOGOS ---");
        probarCategoriaService();
        probarMarcaService();
        probarPresentacionService();

        // ==================== 2. PROBAR CLIENTES Y PROVEEDORES ====================
        System.out.println("\n--- 2. CLIENTES Y PROVEEDORES ---");
        probarClienteService();
        probarProveedorService();

        // ==================== 3. PROBAR PRODUCTOS ====================
        System.out.println("\n--- 3. PRODUCTOS ---");
        probarProductoService();

        // ==================== 4. PROBAR COMPRA Y VENTA ====================
        System.out.println("\n--- 4. COMPRA Y VENTA ---");
        probarCompraService();
        probarVentaService();

        System.out.println("\n=== PRUEBAS FINALIZADAS ===");
    }

    // ==================== CATÁLOGOS ====================

    private static void probarCategoriaService() {
        CategoriaService service = new CategoriaService();

        System.out.println("Categorías existentes:");
        service.listarTodos().forEach(c ->
                System.out.println("   ID: " + c.getId() + " - " + c.getNombre())
        );

        Categoria nueva = new Categoria();
        nueva.setNombre("PRUEBA_CATEGORIA_" + System.currentTimeMillis());

        try {
            Categoria guardada = service.guardar(nueva);
            System.out.println("✅ Categoría creada: ID=" + guardada.getId() + ", Nombre=" + guardada.getNombre());
            service.eliminar(guardada.getId());
            System.out.println("   Categoría de prueba eliminada");
        } catch (Exception e) {
            System.err.println("❌ Error en categoría: " + e.getMessage());
        }
    }

    private static void probarMarcaService() {
        MarcaService service = new MarcaService();

        System.out.println("Marcas existentes:");
        service.listarTodos().forEach(m ->
                System.out.println("   ID: " + m.getId() + " - " + m.getNombre())
        );

        Marca nueva = new Marca();
        nueva.setNombre("PRUEBA_MARCA_" + System.currentTimeMillis());

        try {
            Marca guardada = service.guardar(nueva);
            System.out.println("✅ Marca creada: ID=" + guardada.getId() + ", Nombre=" + guardada.getNombre());
            service.eliminar(guardada.getId());
            System.out.println("   Marca de prueba eliminada");
        } catch (Exception e) {
            System.err.println("❌ Error en marca: " + e.getMessage());
        }
    }

    private static void probarPresentacionService() {
        PresentacionService service = new PresentacionService();

        System.out.println("Presentaciones existentes:");
        service.listarTodos().forEach(p ->
                System.out.println("   ID: " + p.getId() + " - " + p.getNombre() + " (" + p.getLitros() + ")")
        );

        Presentacion nueva = new Presentacion();
        nueva.setNombre("PRUEBA");
        nueva.setLitros("1L");

        try {
            Presentacion guardada = service.guardar(nueva);
            System.out.println("✅ Presentación creada: ID=" + guardada.getId() + ", " + guardada.getNombre() + " (" + guardada.getLitros() + ")");
            service.eliminar(guardada.getId());
            System.out.println("   Presentación de prueba eliminada");
        } catch (Exception e) {
            System.err.println("❌ Error en presentación: " + e.getMessage());
        }
    }

    // ==================== CLIENTES Y PROVEEDORES ====================

    private static void probarClienteService() {
        ClienteService service = new ClienteService();

        System.out.println("Clientes existentes:");
        service.listarTodos().forEach(c -> {
            Persona p = c.getPersona();
            System.out.println("   ID: " + c.getId() + " - " + p.getNombreCompleto() + " - " + p.getContacto().getCelular());
        });

        try {
            Cliente nuevo = service.guardarConDatos(
                    "Prueba",
                    "Cliente",
                    "777-12345"
            );
            System.out.println("✅ Cliente creado: ID=" + nuevo.getId() + ", " + nuevo.getPersona().getNombreCompleto());
            service.eliminar(nuevo.getId());
            System.out.println("   Cliente de prueba eliminado");
        } catch (Exception e) {
            System.err.println("❌ Error en cliente: " + e.getMessage());
        }
    }

    private static void probarProveedorService() {
        ProveedorService service = new ProveedorService();

        System.out.println("Proveedores existentes:");
        service.listarTodos().forEach(p -> {
            Persona persona = p.getPersona();
            System.out.println("   ID: " + p.getId() + " - " + p.getEmpresa() + " - " + persona.getNombreCompleto());
        });

        try {
            Proveedor nuevo = service.guardarConDatos(
                    "Prueba",
                    "Proveedor",
                    "777-54321",
                    "Proveedor Test S.R.L."
            );
            System.out.println("✅ Proveedor creado: ID=" + nuevo.getId() + ", " + nuevo.getEmpresa());
            service.eliminar(nuevo.getId());
            System.out.println("   Proveedor de prueba eliminado");
        } catch (Exception e) {
            System.err.println("❌ Error en proveedor: " + e.getMessage());
        }
    }

    // ==================== PRODUCTOS ====================

    private static void probarProductoService() {
        ProductoService service = new ProductoService();

        CategoriaService catService = new CategoriaService();
        MarcaService marService = new MarcaService();

        List<Categoria> categorias = catService.listarTodos();
        List<Marca> marcas = marService.listarTodos();

        if (categorias.isEmpty() || marcas.isEmpty()) {
            System.out.println("⚠️ No hay categorías o marcas disponibles para probar productos");
            return;
        }

        int idCategoria = categorias.get(0).getId();
        int idMarca = marcas.get(0).getId();

        // Producto base
        Producto producto = new Producto();
        producto.setNombre("Producto Prueba Base");
        producto.setPrecio(45.50f);  // Bs 45.50
        producto.setStock(50f);
        producto.setDetalle("Producto de prueba");
        producto.setIdCategoria(idCategoria);
        producto.setIdMarca(idMarca);

        try {
            Producto guardado = service.guardarProducto(producto);
            System.out.println("✅ Producto base creado: ID=" + guardado.getId() + ", Nombre=" + guardado.getNombre() + ", Precio: Bs " + guardado.getPrecio());

            Optional<Producto> encontrado = service.buscarProductoPorId(guardado.getId());
            if (encontrado.isPresent()) {
                System.out.println("   Producto encontrado: " + encontrado.get().getNombre());
            }

            service.eliminarProducto(guardado.getId());
            System.out.println("   Producto base eliminado");
        } catch (Exception e) {
            System.err.println("❌ Error en producto base: " + e.getMessage());
        }

        // Aceite
        PresentacionService presService = new PresentacionService();
        List<Presentacion> presentaciones = presService.listarTodos();
        int idPresentacion = presentaciones.isEmpty() ? 1 : presentaciones.get(0).getId();

        Aceite aceite = new Aceite();
        aceite.setNombre("Aceite Prueba 20W-50");
        aceite.setPrecio(85.00f);  // Bs 85.00
        aceite.setStock(30f);
        aceite.setDetalle("Aceite mineral para motor");
        aceite.setIdCategoria(idCategoria);
        aceite.setIdMarca(idMarca);
        aceite.setViscosidad("20W-50");
        aceite.setTipoAceite("Mineral");
        aceite.setUso("Motor");
        aceite.setEsAgranel(false);
        aceite.setIdPresentacion(idPresentacion);

        try {
            Aceite guardado = service.guardarAceite(aceite);
            System.out.println("✅ Aceite creado: ID=" + guardado.getId() + ", Viscosidad=" + guardado.getViscosidad() + ", Precio: Bs " + guardado.getPrecio());
            service.eliminarProducto(guardado.getId());
            System.out.println("   Aceite eliminado");
        } catch (Exception e) {
            System.err.println("❌ Error en aceite: " + e.getMessage());
        }

        // Filtro
        Filtro filtro = new Filtro();
        filtro.setNombre("Filtro Prueba");
        filtro.setPrecio(35.00f);  // Bs 35.00
        filtro.setStock(20f);
        filtro.setDetalle("Filtro de aceite");
        filtro.setIdCategoria(idCategoria);
        filtro.setIdMarca(idMarca);
        filtro.setCodigo("FL-910");
        filtro.setRosca("3/4-16");
        filtro.setUso("Aceite");

        try {
            Filtro guardado = service.guardarFiltro(filtro);
            System.out.println("✅ Filtro creado: ID=" + guardado.getId() + ", Código=" + guardado.getCodigo() + ", Precio: Bs " + guardado.getPrecio());
            service.eliminarProducto(guardado.getId());
            System.out.println("   Filtro eliminado");
        } catch (Exception e) {
            System.err.println("❌ Error en filtro: " + e.getMessage());
        }

        // Foco
        Foco foco = new Foco();
        foco.setNombre("Foco Prueba H4");
        foco.setPrecio(28.50f);  // Bs 28.50
        foco.setStock(15f);
        foco.setDetalle("Foco halógeno");
        foco.setIdCategoria(idCategoria);
        foco.setIdMarca(idMarca);
        foco.setCodigo("H4-12V");

        try {
            Foco guardado = service.guardarFoco(foco);
            System.out.println("✅ Foco creado: ID=" + guardado.getId() + ", Código=" + guardado.getCodigo() + ", Precio: Bs " + guardado.getPrecio());
            service.eliminarProducto(guardado.getId());
            System.out.println("   Foco eliminado");
        } catch (Exception e) {
            System.err.println("❌ Error en foco: " + e.getMessage());
        }
    }

    // ==================== COMPRA ====================

    private static void probarCompraService() {
        CompraService compraService = new CompraService();
        ProductoService productoService = new ProductoService();
        ProveedorService proveedorService = new ProveedorService();

        List<Proveedor> proveedores = proveedorService.listarTodos();
        if (proveedores.isEmpty()) {
            System.out.println("⚠️ No hay proveedores disponibles para probar compra");
            return;
        }

        List<Producto> productos = productoService.listarTodos();
        if (productos.isEmpty()) {
            System.out.println("⚠️ No hay productos disponibles para probar compra");
            return;
        }

        int idProveedor = proveedores.get(0).getId();
        int idProducto = productos.get(0).getId();
        float stockAntes = productoService.buscarProductoPorId(idProducto).map(Producto::getStock).orElse(0f);

        Compra compra = new Compra();
        compra.setFecha(LocalDate.now());
        compra.setIdProveedor(idProveedor);

        List<DetalleCompra> detalles = new ArrayList<>();
        DetalleCompra detalle = new DetalleCompra();
        detalle.setCantidad(10f);
        detalle.setPrecioCompra(70.00f);  // Bs 70.00 precio compra
        detalle.setIdProducto(idProducto);
        detalles.add(detalle);
        compra.setDetalles(detalles);

        try {
            Compra registrada = compraService.registrarCompra(compra);
            System.out.println("✅ Compra registrada: ID=" + registrada.getId() + ", Total: Bs " + registrada.getTotal());

            float stockDespues = productoService.buscarProductoPorId(idProducto).map(Producto::getStock).orElse(0f);
            System.out.println("   Stock antes: " + stockAntes + " unidades, después: " + stockDespues + " unidades");

            compraService.eliminarCompra(registrada.getId());
            float stockRestaurado = productoService.buscarProductoPorId(idProducto).map(Producto::getStock).orElse(0f);
            System.out.println("   Stock restaurado: " + stockRestaurado + " unidades");

        } catch (Exception e) {
            System.err.println("❌ Error en compra: " + e.getMessage());
        }
    }

    // ==================== VENTA ====================

    private static void probarVentaService() {
        VentaService ventaService = new VentaService();
        ProductoService productoService = new ProductoService();
        ClienteService clienteService = new ClienteService();

        List<Cliente> clientes = clienteService.listarTodos();
        Integer idCliente = clientes.isEmpty() ? null : clientes.get(0).getId();

        List<Producto> productos = productoService.listarTodos();
        if (productos.isEmpty()) {
            System.out.println("⚠️ No hay productos disponibles para probar venta");
            return;
        }

        int idProducto = productos.get(0).getId();
        float stockAntes = productoService.buscarProductoPorId(idProducto).map(Producto::getStock).orElse(0f);

        if (stockAntes <= 0) {
            System.out.println("⚠️ Producto sin stock para probar venta");
            return;
        }

        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setIdCliente(idCliente);

        List<DetalleVenta> detalles = new ArrayList<>();
        DetalleVenta detalle = new DetalleVenta();
        detalle.setCantidad(2f);
        detalle.setPrecioVenta(95.00f);  // Bs 95.00 precio venta
        detalle.setIdProducto(idProducto);
        detalles.add(detalle);
        venta.setDetalles(detalles);

        try {
            Venta registrada = ventaService.registrarVenta(venta);
            System.out.println("✅ Venta registrada: ID=" + registrada.getId() + ", Total: Bs " + registrada.getTotal());

            float stockDespues = productoService.buscarProductoPorId(idProducto).map(Producto::getStock).orElse(0f);
            System.out.println("   Stock antes: " + stockAntes + " unidades, después: " + stockDespues + " unidades");

            ventaService.eliminarVenta(registrada.getId());
            float stockRestaurado = productoService.buscarProductoPorId(idProducto).map(Producto::getStock).orElse(0f);
            System.out.println("   Stock restaurado: " + stockRestaurado + " unidades");

        } catch (Exception e) {
            System.err.println("❌ Error en venta: " + e.getMessage());
        }
    }
}
