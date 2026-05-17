package org.example.Vista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Modelo.jpa.Cliente;
import org.example.Modelo.jpa.Contacto;
import org.example.Modelo.jpa.Persona;
import org.example.Modelo.pojo.*;
import org.example.Servicio.ClienteService;
import org.example.Servicio.ProductoService;
import org.example.Servicio.VentaService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VentasController {

    // Datos del cliente (opcional)
    @FXML private TextField txtClienteNombre, txtClienteCelular;

    // Tabla de productos
    @FXML private TextField txtBuscar;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colMarca;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Float> colStock;
    @FXML private TableColumn<Producto, Float> colPrecio;

    // Cantidad
    @FXML private TextField txtCantidad;
    @FXML private Button btnDisminuir, btnAumentar;
    @FXML private Button btnAgregar;
    @FXML private Label lblProductoSeleccionado;

    // Carrito
    @FXML private TableView<ItemVenta> tblCarrito;
    @FXML private TableColumn<ItemVenta, String> colCarritoProducto;
    @FXML private TableColumn<ItemVenta, Float> colCarritoCantidad;
    @FXML private TableColumn<ItemVenta, Float> colCarritoPrecio;
    @FXML private TableColumn<ItemVenta, Float> colCarritoSubtotal;
    @FXML private Button btnQuitar;
    @FXML private Button btnLimpiarCarrito;
    @FXML private Label lblTotal;
    @FXML private Button btnFinalizar;

    // Metodo de pago
    @FXML private ToggleGroup pagoGrupo;
    @FXML private RadioButton rbEfectivo, rbQr;

    // Servicios
    private final ProductoService productoService = new ProductoService();
    private final VentaService ventaService = new VentaService();
    private final ClienteService clienteService = new ClienteService();

    // Datos
    private ObservableList<ItemVenta> itemsCarrito = FXCollections.observableArrayList();
    private ObservableList<Producto> productosList = FXCollections.observableArrayList();
    private List<Producto> cacheProductos = new ArrayList<>();
    private Producto productoSeleccionado;

    @FXML
    public void initialize() {


        configurarTablas();
        cargarProductosIniciales();
        configurarBusqueda();
        configurarEventos();

    }

    private void configurarTablas() {
        // Configurar columnas de la tabla de productos
        colNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));

        // Para Marca y Categoría necesitas agregar estos atributos a Producto (POJO)
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaNombre"));

        tblProductos.setItems(productosList);

        // Configurar tabla del carrito
        colCarritoProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCarritoCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCarritoPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCarritoSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tblCarrito.setItems(itemsCarrito);
    }

    private void cargarProductosIniciales() {
        cacheProductos = productoService.listarTodos();
        productosList.setAll(cacheProductos);
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                productosList.setAll(cacheProductos);
                return;
            }
            String buscar = newVal.toLowerCase();
            List<Producto> filtrados = cacheProductos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(buscar) ||
                            (p.getMarcaNombre() != null && p.getMarcaNombre().toLowerCase().contains(buscar)) ||
                            (p.getCategoriaNombre() != null && p.getCategoriaNombre().toLowerCase().contains(buscar)))
                    .toList();
            productosList.setAll(filtrados);
        });

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                productoSeleccionado = newVal;
                lblProductoSeleccionado.setText("Seleccionado: " + productoSeleccionado.getNombre() +
                        " | Marca: " + productoSeleccionado.getMarcaNombre() +
                        " | Stock: " + productoSeleccionado.getStock());
                txtCantidad.setText("1");
            }
        });
    }

    private void configurarEventos() {
        btnDisminuir.setOnAction(e -> {
            int cant = Integer.parseInt(txtCantidad.getText());
            if (cant > 1) txtCantidad.setText(String.valueOf(cant - 1));
        });
        btnAumentar.setOnAction(e -> {
            int cant = Integer.parseInt(txtCantidad.getText());
            txtCantidad.setText(String.valueOf(cant + 1));
        });

        btnAgregar.setOnAction(e -> agregarAlCarrito());
        btnQuitar.setOnAction(e -> quitarDelCarrito());
        btnLimpiarCarrito.setOnAction(e -> limpiarCarrito());
        btnFinalizar.setOnAction(e -> finalizarVenta());
    }

    private void agregarAlCarrito() {
        if (productoSeleccionado == null) {
            mostrarAlerta("Seleccione un producto de la tabla");
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

        if (productoSeleccionado.getStock() < cantidad) {
            mostrarAlerta("Stock insuficiente. Stock actual: " + productoSeleccionado.getStock());
            return;
        }

        for (ItemVenta item : itemsCarrito) {
            if (item.getIdProducto() == productoSeleccionado.getId()) {
                item.setCantidad(item.getCantidad() + cantidad);
                item.calcularSubtotal();
                tblCarrito.refresh();
                calcularTotal();
                return;
            }
        }

        ItemVenta nuevoItem = new ItemVenta(
                productoSeleccionado.getId(),
                productoSeleccionado.getNombre(),
                cantidad,
                productoSeleccionado.getPrecio()
        );
        itemsCarrito.add(nuevoItem);
        calcularTotal();
    }

    private void quitarDelCarrito() {
        ItemVenta seleccionado = tblCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto del carrito");
            return;
        }
        itemsCarrito.remove(seleccionado);
        calcularTotal();
    }

    private void limpiarCarrito() {
        itemsCarrito.clear();
        calcularTotal();
    }

    private void calcularTotal() {
        float total = 0;
        for (ItemVenta item : itemsCarrito) {
            total += item.getSubtotal();
        }
        // Forzar formato con punto decimal
        lblTotal.setText(String.format(Locale.US, "Bs %.2f", total));
    }

    private Integer registrarClienteOpcional() {
        String nombre = txtClienteNombre.getText().trim();
        String celular = txtClienteCelular.getText().trim();

        if (nombre.isEmpty() && celular.isEmpty()) {
            return null;
        }

        if (nombre.isEmpty()) {
            mostrarAlerta("Si desea registrar cliente, debe ingresar el nombre");
            return null;
        }

        Contacto contacto = new Contacto();
        contacto.setCelular(celular.isEmpty() ? "S/C" : celular);

        Persona persona = new Persona();
        persona.setNombres(nombre);
        persona.setApellidos("");
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

        try {
            ventaService.registrarVenta(venta);
            String clienteInfo = (idCliente != null) ? "\nCliente: " + txtClienteNombre.getText() : "";
            mostrarInfo("✅ Venta registrada correctamente\nMétodo de pago: " + metodoPago + clienteInfo);
            limpiarCarrito();
            txtClienteNombre.clear();
            txtClienteCelular.clear();
            cargarProductosIniciales();
        } catch (Exception e) {
            mostrarError("Error al registrar venta: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}