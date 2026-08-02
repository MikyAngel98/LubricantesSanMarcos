package org.example.Vista.MainControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.example.Modelo.jpa.Cliente;
import org.example.Modelo.jpa.Contacto;
import org.example.Modelo.jpa.Persona;
import org.example.Modelo.pojo.*;
import org.example.Servicio.ClienteService;
import org.example.Servicio.PdfExportService;
import org.example.Servicio.ProductoService;
import org.example.Servicio.VentaService;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class VentasController {

    // Botones de categoría
    @FXML private Button btnAceites, btnFiltros, btnFocos, btnProductos;
    @FXML private TextField txtBuscar;
    @FXML private ScrollPane scrollTablas;
    @FXML private StackPane panelTablas;

    // Tablas
    @FXML private TableView<Aceite> tablaAceites;
    @FXML private TableView<Filtro> tablaFiltros;
    @FXML private TableView<Foco> tablaFocos;
    @FXML private TableView<Producto> tablaProductosBase;

    // Columnas ACEITES
    @FXML private TableColumn<Aceite, Integer> colAceiteId;
    @FXML private TableColumn<Aceite, String> colAceiteNombre;
    @FXML private TableColumn<Aceite, String> colAceiteViscosidad;
    @FXML private TableColumn<Aceite, String> colAceiteMarca;
    @FXML private TableColumn<Aceite, Float> colAceitePrecio;
    @FXML private TableColumn<Aceite, Float> colAceiteStock;
    @FXML private TableColumn<Aceite, String> colAceitePresentacion;
    @FXML private TableColumn<Aceite, String> colAceiteAgranel;
    @FXML private TableColumn<Aceite, String> colAceiteTipo;
    @FXML private TableColumn<Aceite, String> colAceiteUso;
    @FXML private TableColumn<Aceite, String> colAceiteDetalle;

    // Columnas FILTROS
    @FXML private TableColumn<Filtro, Integer> colFiltroId;
    @FXML private TableColumn<Filtro, String> colFiltroNombre;
    @FXML private TableColumn<Filtro, String> colFiltroCodigo;
    @FXML private TableColumn<Filtro, String> colFiltroRosca;
    @FXML private TableColumn<Filtro, String> colFiltroMarca;
    @FXML private TableColumn<Filtro, Float> colFiltroStock;
    @FXML private TableColumn<Filtro, Float> colFiltroPrecio;
    @FXML private TableColumn<Filtro, String> colFiltroDetalle;

    // Columnas FOCOS
    @FXML private TableColumn<Foco, Integer> colFocoId;
    @FXML private TableColumn<Foco, String> colFocoNombre;
    @FXML private TableColumn<Foco, String> colFocoCodigo;
    @FXML private TableColumn<Foco, String> colFocoMarca;
    @FXML private TableColumn<Foco, Float> colFocoStock;
    @FXML private TableColumn<Foco, Float> colFocoPrecio;
    @FXML private TableColumn<Foco, String> colFocoDetalle;

    // Columnas PRODUCTOS BASE
    @FXML private TableColumn<Producto, Integer> colProdId;
    @FXML private TableColumn<Producto, String> colProdNombre;
    @FXML private TableColumn<Producto, String> colProdMarca;
    @FXML private TableColumn<Producto, Float> colProdStock;
    @FXML private TableColumn<Producto, Float> colProdPrecio;
    @FXML private TableColumn<Producto, String> colProdDetalle;

    // Cliente
    @FXML private TextField txtClienteNombre, txtClienteCelular;
    @FXML private TextField txtCantidad;
    @FXML private Button btnDisminuir, btnAumentar, btnAgregar;
    @FXML private Label lblProductoSeleccionado;

    // Carrito
    @FXML private TableView<ItemVenta> tblCarrito;
    @FXML private TableColumn<ItemVenta, String> colCarritoProducto;
    @FXML private TableColumn<ItemVenta, String> colCarritoMarca;
    @FXML private TableColumn<ItemVenta, Float> colCarritoCantidad;
    @FXML private TableColumn<ItemVenta, Float> colCarritoPrecio;
    @FXML private TableColumn<ItemVenta, Float> colCarritoSubtotal;
    @FXML private Button btnQuitar, btnLimpiarCarrito;
    @FXML private Label lblTotal;
    @FXML private Button btnFinalizar;

    // Método de pago
    @FXML private ToggleGroup pagoGrupo;
    @FXML private RadioButton rbEfectivo, rbQr;

    private final ProductoService productoService = new ProductoService();
    private final VentaService ventaService = new VentaService();
    private final ClienteService clienteService = new ClienteService();
    private final PdfExportService pdfExportService = new PdfExportService();

    private ObservableList<ItemVenta> itemsCarrito = FXCollections.observableArrayList();

    private List<Aceite> cacheAceites = new ArrayList<>();
    private List<Filtro> cacheFiltros = new ArrayList<>();
    private List<Foco> cacheFocos = new ArrayList<>();
    private List<Producto> cacheProductosBase = new ArrayList<>();

    // Stock original de cada producto (tal como está en la BD), para mostrar
    // el stock disponible restando lo que ya está en el carrito.
    private final Map<Integer, Float> stockOriginalPorProducto = new HashMap<>();

    private Producto productoSeleccionado;
    private boolean aceiteSeleccionadoEsGranel = false;
    private String categoriaActiva = "ACEITES";

    @FXML
    public void initialize() {
        cargarCacheCompleto();
        configurarCarrito();
        configurarTablas();
        configurarSeleccion();
        configurarBusquedaGlobal();
        configurarEventos();

        // Configurar scroll
        scrollTablas.setFitToWidth(true);
        scrollTablas.setFitToHeight(true);

        // Cargar ACEITES por defecto
        cargarAceites();
    }

    private void cargarCacheCompleto() {
        try {
            cacheAceites = productoService.listarAceites();
            cacheFiltros = productoService.listarFiltros();
            cacheFocos = productoService.listarFocos();
            cacheProductosBase = productoService.listarProductosBase();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar datos: " + e.getMessage());
        }

        // Registrar el stock real de la BD y reflejar lo que está en el carrito
        registrarStockOriginal(cacheAceites);
        registrarStockOriginal(cacheFiltros);
        registrarStockOriginal(cacheFocos);
        registrarStockOriginal(cacheProductosBase);
        aplicarStockCarrito();
    }

    private void registrarStockOriginal(List<? extends Producto> lista) {
        for (Producto p : lista) {
            stockOriginalPorProducto.put(p.getId(), p.getStock());
        }
    }

    private float cantidadEnCarrito(int idProducto) {
        for (ItemVenta item : itemsCarrito) {
            if (item.getIdProducto() == idProducto) {
                return item.getCantidad();
            }
        }
        return 0;
    }

    private float stockDisponible(int idProducto) {
        Float original = stockOriginalPorProducto.get(idProducto);
        float base = original != null ? original : 0;
        return base - cantidadEnCarrito(idProducto);
    }

    // Actualiza el stock mostrado en las tablas restando lo que está en el carrito.
    private void aplicarStockCarrito() {
        for (Aceite a : cacheAceites) a.setStock(stockDisponible(a.getId()));
        for (Filtro f : cacheFiltros) f.setStock(stockDisponible(f.getId()));
        for (Foco f : cacheFocos) f.setStock(stockDisponible(f.getId()));
        for (Producto p : cacheProductosBase) p.setStock(stockDisponible(p.getId()));

        tablaAceites.refresh();
        tablaFiltros.refresh();
        tablaFocos.refresh();
        tablaProductosBase.refresh();
    }

    private void configurarCarrito() {
        colCarritoProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCarritoMarca.setCellValueFactory(new PropertyValueFactory<>("marcaProducto"));
        colCarritoCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCarritoPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCarritoSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tblCarrito.setItems(itemsCarrito);
    }

    private void configurarTablas() {
        // ACEITES
        colAceiteId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colAceiteNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        colAceiteViscosidad.setCellValueFactory(new PropertyValueFactory<>("Viscosidad"));
        colAceiteMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colAceitePrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        colAceiteStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colAceitePresentacion.setCellValueFactory(new PropertyValueFactory<>("presentacionNombre"));
        colAceiteAgranel.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().isEsAgranel() ? "Sí" : "No"));
        colAceiteTipo.setCellValueFactory(new PropertyValueFactory<>("TipoAceite"));
        colAceiteUso.setCellValueFactory(new PropertyValueFactory<>("Uso"));
        colAceiteDetalle.setCellValueFactory(new PropertyValueFactory<>("Detalle"));

        // FILTROS
        colFiltroId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colFiltroNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        colFiltroCodigo.setCellValueFactory(new PropertyValueFactory<>("Codigo"));
        colFiltroRosca.setCellValueFactory(new PropertyValueFactory<>("Rosca"));
        colFiltroMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colFiltroStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colFiltroPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        colFiltroDetalle.setCellValueFactory(new PropertyValueFactory<>("Detalle"));

        // FOCOS
        colFocoId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colFocoNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        colFocoCodigo.setCellValueFactory(new PropertyValueFactory<>("Codigo"));
        colFocoMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colFocoStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colFocoPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        colFocoDetalle.setCellValueFactory(new PropertyValueFactory<>("Detalle"));

        // PRODUCTOS BASE
        colProdId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colProdNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        colProdMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colProdStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colProdPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        colProdDetalle.setCellValueFactory(new PropertyValueFactory<>("Detalle"));
    }

    // Listeners de selección registrados una sola vez
    private void configurarSeleccion() {
        tablaAceites.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                productoSeleccionado = newVal;
                aceiteSeleccionadoEsGranel = newVal.isEsAgranel();
                lblProductoSeleccionado.setText("Seleccionado: " + newVal.getNombre() + " | Stock: " + newVal.getStock());
                txtCantidad.setText(aceiteSeleccionadoEsGranel ? "0.25" : "1");
                configurarBotonesCantidad();
            }
        });

        tablaFiltros.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                productoSeleccionado = newVal;
                lblProductoSeleccionado.setText("Seleccionado: " + newVal.getNombre() + " | Stock: " + newVal.getStock());
                txtCantidad.setText("1");
                configurarBotonesCantidad();
            }
        });

        tablaFocos.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                productoSeleccionado = newVal;
                lblProductoSeleccionado.setText("Seleccionado: " + newVal.getNombre() + " | Stock: " + newVal.getStock());
                txtCantidad.setText("1");
                configurarBotonesCantidad();
            }
        });

        tablaProductosBase.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                productoSeleccionado = newVal;
                lblProductoSeleccionado.setText("Seleccionado: " + newVal.getNombre() + " | Stock: " + newVal.getStock());
                txtCantidad.setText("1");
                configurarBotonesCantidad();
            }
        });
    }

    // Listener de búsqueda único: filtra la tabla activa
    private void configurarBusquedaGlobal() {
        txtBuscar.textProperty().addListener((obs, old, newVal) -> aplicarFiltroBusqueda());
    }

    private void aplicarFiltroBusqueda() {
        String filtro = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();

        switch (categoriaActiva) {
            case "ACEITES":
                tablaAceites.setItems(FXCollections.observableArrayList(
                        filtro.isEmpty() ? cacheAceites :
                                cacheAceites.stream()
                                        .filter(a -> a.getNombre().toLowerCase().contains(filtro))
                                        .toList()));
                break;
            case "FILTROS":
                tablaFiltros.setItems(FXCollections.observableArrayList(
                        filtro.isEmpty() ? cacheFiltros :
                                cacheFiltros.stream()
                                        .filter(f -> f.getNombre().toLowerCase().contains(filtro))
                                        .toList()));
                break;
            case "FOCOS":
                tablaFocos.setItems(FXCollections.observableArrayList(
                        filtro.isEmpty() ? cacheFocos :
                                cacheFocos.stream()
                                        .filter(f -> f.getNombre().toLowerCase().contains(filtro))
                                        .toList()));
                break;
            case "PRODUCTOS":
                tablaProductosBase.setItems(FXCollections.observableArrayList(
                        filtro.isEmpty() ? cacheProductosBase :
                                cacheProductosBase.stream()
                                        .filter(p -> p.getNombre().toLowerCase().contains(filtro))
                                        .toList()));
                break;
        }
    }

    private void mostrarTabla(String tabla) {
        // Ocultar todas
        tablaAceites.setVisible(false);
        tablaAceites.setManaged(false);
        tablaFiltros.setVisible(false);
        tablaFiltros.setManaged(false);
        tablaFocos.setVisible(false);
        tablaFocos.setManaged(false);
        tablaProductosBase.setVisible(false);
        tablaProductosBase.setManaged(false);

        // Limpiar y agregar la seleccionada
        panelTablas.getChildren().clear();

        switch (tabla) {
            case "ACEITES":
                panelTablas.getChildren().add(tablaAceites);
                tablaAceites.setVisible(true);
                tablaAceites.setManaged(true);
                break;
            case "FILTROS":
                panelTablas.getChildren().add(tablaFiltros);
                tablaFiltros.setVisible(true);
                tablaFiltros.setManaged(true);
                break;
            case "FOCOS":
                panelTablas.getChildren().add(tablaFocos);
                tablaFocos.setVisible(true);
                tablaFocos.setManaged(true);
                break;
            case "PRODUCTOS":
                panelTablas.getChildren().add(tablaProductosBase);
                tablaProductosBase.setVisible(true);
                tablaProductosBase.setManaged(true);
                break;
        }
    }

    private void configurarBotonesCantidad() {
        if (productoSeleccionado != null && productoSeleccionado instanceof Aceite && aceiteSeleccionadoEsGranel) {
            // Modo granel: incrementos de 0.25
            btnDisminuir.setOnAction(e -> {
                String texto = txtCantidad.getText().replace(",", ".");
                float cant = Float.parseFloat(texto);
                if (cant > 0.25f) {
                    txtCantidad.setText(String.format(Locale.US, "%.2f", cant - 0.25f));
                } else if (cant == 0.25f) {
                    txtCantidad.setText("0.25");
                }
            });
            btnAumentar.setOnAction(e -> {
                String texto = txtCantidad.getText().replace(",", ".");
                float cant = Float.parseFloat(texto);
                txtCantidad.setText(String.format(Locale.US, "%.2f", cant + 0.25f));
            });
        } else {
            // Modo normal: incrementos de 1
            btnDisminuir.setOnAction(e -> {
                String texto = txtCantidad.getText().replace(",", ".");
                int cant = Integer.parseInt(texto);
                if (cant > 1) {
                    txtCantidad.setText(String.valueOf(cant - 1));
                }
            });
            btnAumentar.setOnAction(e -> {
                String texto = txtCantidad.getText().replace(",", ".");
                int cant = Integer.parseInt(texto);
                txtCantidad.setText(String.valueOf(cant + 1));
            });
        }
    }

    @FXML
    private void cargarAceites() {
        mostrarTabla("ACEITES");
        categoriaActiva = "ACEITES";

        productoSeleccionado = null;
        lblProductoSeleccionado.setText("");

        cacheAceites = productoService.listarAceites();

        registrarStockOriginal(cacheAceites);
        aplicarStockCarrito();
        txtBuscar.clear();
        aplicarFiltroBusqueda();
    }

    @FXML
    private void cargarFiltros() {
        mostrarTabla("FILTROS");
        categoriaActiva = "FILTROS";

        productoSeleccionado = null;
        lblProductoSeleccionado.setText("");

        cacheFiltros = productoService.listarFiltros();

        registrarStockOriginal(cacheFiltros);
        aplicarStockCarrito();
        txtBuscar.clear();
        aplicarFiltroBusqueda();
    }

    @FXML
    private void cargarFocos() {
        mostrarTabla("FOCOS");
        categoriaActiva = "FOCOS";

        productoSeleccionado = null;
        lblProductoSeleccionado.setText("");

        cacheFocos = productoService.listarFocos();

        registrarStockOriginal(cacheFocos);
        aplicarStockCarrito();
        txtBuscar.clear();
        aplicarFiltroBusqueda();
    }

    @FXML
    private void cargarProductos() {
        mostrarTabla("PRODUCTOS");
        categoriaActiva = "PRODUCTOS";

        productoSeleccionado = null;
        lblProductoSeleccionado.setText("");

        cacheProductosBase = productoService.listarProductosBase();

        registrarStockOriginal(cacheProductosBase);
        aplicarStockCarrito();
        txtBuscar.clear();
        aplicarFiltroBusqueda();
    }

    private void configurarEventos() {
        btnAgregar.setOnAction(e -> agregarAlCarrito());
        btnQuitar.setOnAction(e -> quitarDelCarrito());
        btnLimpiarCarrito.setOnAction(e -> limpiarCarrito());
        btnFinalizar.setOnAction(e -> finalizarVenta());
    }

    private void agregarAlCarrito() {
        if (productoSeleccionado == null) {
            mostrarAlerta("Seleccione un producto");
            return;
        }

        float cantidad;
        try {
            cantidad = Float.parseFloat(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Cantidad inválida");
            return;
        }

        int idProducto = productoSeleccionado.getId();
        float disponible = stockDisponible(idProducto);

        if (disponible < cantidad) {
            mostrarAlerta("Stock insuficiente. Stock disponible: " + disponible);
            return;
        }

        float precio = productoSeleccionado.getPrecio();
        String nombre = productoSeleccionado.getNombre();

        // OBTENER MARCA
        String marca = productoSeleccionado.getMarcaNombre() != null ?
                productoSeleccionado.getMarcaNombre() : "N/A";

        for (ItemVenta item : itemsCarrito) {
            if (item.getIdProducto() == idProducto) {
                float nuevaCantidad = item.getCantidad() + cantidad;
                Float original = stockOriginalPorProducto.get(idProducto);
                float stockTotal = original != null ? original : (disponible + item.getCantidad());
                if (stockTotal < nuevaCantidad) {
                    mostrarAlerta("Stock insuficiente. Stock disponible: " + (stockTotal - item.getCantidad()));
                    return;
                }
                item.setCantidad(nuevaCantidad);
                item.calcularSubtotal();
                tblCarrito.refresh();
                aplicarStockCarrito();
                calcularTotal();
                return;
            }
        }

        ItemVenta nuevoItem = new ItemVenta(idProducto, nombre, marca, cantidad, precio);
        itemsCarrito.add(nuevoItem);
        aplicarStockCarrito();
        calcularTotal();
    }

    private void quitarDelCarrito() {
        ItemVenta seleccionado = tblCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto del carrito");
            return;
        }
        itemsCarrito.remove(seleccionado);
        aplicarStockCarrito();
        calcularTotal();
    }

    private void limpiarCarrito() {
        itemsCarrito.clear();
        aplicarStockCarrito();
        calcularTotal();
    }

    private void calcularTotal() {
        float total = 0;
        for (ItemVenta item : itemsCarrito) {
            total += item.getSubtotal();
        }
        lblTotal.setText(String.format(Locale.US, "Bs %.2f", total));
    }

    private Integer registrarClienteOpcional() {
        String nombre = txtClienteNombre.getText().trim();
        String celular = txtClienteCelular.getText().trim();

        if (nombre.isEmpty() && celular.isEmpty()) return null;
        if (nombre.isEmpty()) {
            mostrarAlerta("Si desea registrar cliente, debe ingresar el nombre");
            return null;
        }

        Contacto contacto = new Contacto();
        contacto.setCelular(celular.isEmpty() ? "S/C" : celular);

        Persona persona = new Persona();
        persona.setNombres(nombre);
        persona.setApellidos("S/A");
        persona.setContacto(contacto);

        Cliente cliente = new Cliente();
        cliente.setPersona(persona);

        try {
            Cliente guardado = clienteService.guardar(cliente);
            return guardado.getId();
        } catch (Exception e) {
            mostrarAlerta("Error al guardar cliente: " + e.getMessage());
            return null;
        }
    }

    private void finalizarVenta() {
        if (itemsCarrito.isEmpty()) {
            mostrarAlerta("No hay productos en el carrito");
            return;
        }

        String metodoPago = rbEfectivo.isSelected() ? "EFECTIVO" : "QR";
        Integer idCliente = registrarClienteOpcional();

        // Obtener nombre del cliente
        String nombreCliente = txtClienteNombre.getText().trim();
        if (nombreCliente.isEmpty()) {
            nombreCliente = "S/N";
        }

        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());

        String totalTexto = lblTotal.getText().replace("Bs ", "");
        venta.setTotal(Float.parseFloat(totalTexto));
        venta.setIdCliente(idCliente);
        venta.setMetodoPago(metodoPago);

        List<DetalleVenta> detalles = new ArrayList<>();
        for (ItemVenta item : itemsCarrito) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(item.getIdProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioVenta(item.getPrecio());
            detalles.add(detalle);
        }
        venta.setDetalles(detalles);

        boolean registrada = false;
        try {
            ventaService.registrarVenta(venta);
            registrada = true;

            List<ItemVenta> copiaCarrito = new ArrayList<>(itemsCarrito);

            String clienteInfo = (idCliente != null) ? "\nCliente: " + txtClienteNombre.getText() : "";
            mostrarInfoConRecibo("✅ Venta registrada correctamente\nMétodo de pago: " + metodoPago + clienteInfo, venta, copiaCarrito, nombreCliente);

        } catch (Exception e) {
            mostrarError("Error al registrar venta: " + e.getMessage());
        } finally {
            if (registrada) {
                limpiarCarrito();
                txtClienteNombre.clear();
                txtClienteCelular.clear();
                txtCantidad.setText("1");
                cargarCacheCompleto();
                cargarAceites();
            }
        }
    }

    private void mostrarInfoConRecibo(String mensaje, Venta venta, List<ItemVenta> itemsCarrito, String nombreCliente) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Venta registrada");
        alert.setHeaderText(mensaje);
        alert.setContentText("¿Desea generar el recibo de venta?");

        ButtonType btnSi = new ButtonType("Sí, generar recibo");
        ButtonType btnNo = new ButtonType("No, gracias");

        alert.getButtonTypes().setAll(btnSi, btnNo);

        Optional<ButtonType> respuesta = alert.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == btnSi) {
            generarRecibo(venta, itemsCarrito, nombreCliente);
        }
    }

    private void generarRecibo(Venta venta, List<ItemVenta> items, String nombreCliente) {
        try {
            String rutaEscritorio = System.getProperty("user.home") + "/Desktop";
            String rutaCarpeta = rutaEscritorio + "/Ventas San Marcos";

            File carpeta = new File(rutaCarpeta);
            if (!carpeta.exists()) {
                carpeta.mkdir();
            }

            String nombreArchivo = "recibo_venta_" + venta.getId() + ".pdf";
            String rutaCompleta = rutaCarpeta + "/" + nombreArchivo;

            pdfExportService.generarReciboVenta(venta, items, rutaCompleta, nombreCliente);
            mostrarInfo("✅ Recibo guardado en:\n" + rutaCompleta);

            try {
                java.awt.Desktop.getDesktop().open(carpeta);
            } catch (Exception ex) {
                // No se pudo abrir la carpeta
            }

        } catch (Exception e) {
            mostrarError("Error al generar recibo: " + e.getMessage());
        }
    }

    // Méetodo para abrir la carpeta en el explorador de archivos (opcional)
    private void abrirCarpeta(String ruta) {
        try {
            File carpeta = new File(ruta);
            if (carpeta.exists()) {
                java.awt.Desktop.getDesktop().open(carpeta);
            }
        } catch (Exception e) {
            System.err.println("No se pudo abrir la carpeta: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}