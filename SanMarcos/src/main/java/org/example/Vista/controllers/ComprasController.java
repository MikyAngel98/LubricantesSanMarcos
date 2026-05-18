package org.example.Vista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.Modelo.jpa.Proveedor;
import org.example.Modelo.pojo.*;
import org.example.Servicio.CompraService;
import org.example.Servicio.ProductoService;
import org.example.Servicio.ProveedorService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComprasController {

    // Proveedor
    @FXML private ComboBox<Proveedor> cbProveedor;
    @FXML private Button btnNuevoProveedor;

    // Tabla de productos
    @FXML private TextField txtBuscar;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colMarca;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Float> colStock;
    @FXML private TableColumn<Producto, Float> colPrecio;

    // Cantidad y precio
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecioCompra;
    @FXML private Button btnDisminuir, btnAumentar;
    @FXML private Button btnAgregar;
    @FXML private Label lblProductoSeleccionado;

    @FXML private ComboBox<String> cbCategoria;

    // Carrito
    @FXML private TableView<ItemCompra> tblCarrito;
    @FXML private TableColumn<ItemCompra, String> colCarritoProducto;
    @FXML private TableColumn<ItemCompra, Float> colCarritoCantidad;
    @FXML private TableColumn<ItemCompra, Float> colCarritoPrecio;
    @FXML private TableColumn<ItemCompra, Float> colCarritoSubtotal;
    @FXML private Button btnQuitar;
    @FXML private Button btnLimpiarCarrito;
    @FXML private Label lblTotal;
    @FXML private Button btnRegistrar;

    // Servicios
    private final ProductoService productoService = new ProductoService();
    private final ProveedorService proveedorService = new ProveedorService();
    private final CompraService compraService = new CompraService();

    // Datos
    private ObservableList<ItemCompra> itemsCarrito = FXCollections.observableArrayList();
    private ObservableList<Producto> productosList = FXCollections.observableArrayList();
    private List<Producto> cacheProductos = new ArrayList<>();
    private Producto productoSeleccionado;

    @FXML
    public void initialize() {
        configurarTablas();
        cargarProveedores();
        cargarProductosIniciales();
        configurarBusqueda();
        configurarEventos();
    }

    private void configurarTablas() {
        // Configurar columnas de productos
        colNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaNombre"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        tblProductos.setItems(productosList);

        // Configurar columnas del carrito
        colCarritoProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCarritoCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCarritoPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCarritoSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tblCarrito.setItems(itemsCarrito);
    }

    private void cargarProveedores() {
        cbProveedor.getItems().addAll(proveedorService.listarTodos());
    }

    private void cargarProductosIniciales() {
        cacheProductos = productoService.listarTodos();
        productosList.setAll(cacheProductos);
    }

    private void configurarBusqueda() {
        cbCategoria.getItems().addAll("TODOS", "PRODUCTOS", "ACEITES", "FILTROS", "FOCOS");
        cbCategoria.getSelectionModel().select("TODOS");

        txtBuscar.textProperty().addListener((obs, old, newVal) -> filtrarProductos());
        cbCategoria.valueProperty().addListener((obs, old, newVal) -> filtrarProductos());
    }

    private void filtrarProductos() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        String categoria = cbCategoria.getValue();

        List<Producto> filtrados = cacheProductos.stream()
                .filter(p -> {
                    // 1. Filtrar por categoría usando idCategoria
                    if (categoria != null && !categoria.equals("TODOS")) {
                        switch (categoria) {
                            case "PRODUCTOS":
                                if (p.getIdCategoria() != 1) return false;
                                break;
                            case "ACEITES":
                                if (p.getIdCategoria() != 2) return false;
                                break;
                            case "FILTROS":
                                if (p.getIdCategoria() != 3) return false;
                                break;
                            case "FOCOS":
                                if (p.getIdCategoria() != 4) return false;
                                break;
                        }
                    }
                    // 2. Filtrar por texto
                    if (!texto.isEmpty()) {
                        return p.getNombre().toLowerCase().contains(texto);
                    }
                    return true;
                })
                .toList();

        productosList.setAll(filtrados);
    }

    private void configurarEventos() {
        // Botones de cantidad - AHORA DE 1 EN 1
        btnDisminuir.setOnAction(e -> {
            int cant = Integer.parseInt(txtCantidad.getText());
            if (cant > 1) {
                txtCantidad.setText(String.valueOf(cant - 1));
            }
        });
        btnAumentar.setOnAction(e -> {
            int cant = Integer.parseInt(txtCantidad.getText());
            txtCantidad.setText(String.valueOf(cant + 1));
        });

        btnAgregar.setOnAction(e -> agregarAlCarrito());
        btnQuitar.setOnAction(e -> quitarDelCarrito());
        btnLimpiarCarrito.setOnAction(e -> limpiarCarrito());
        btnRegistrar.setOnAction(e -> registrarCompra());
        btnNuevoProveedor.setOnAction(e -> abrirDialogoNuevoProveedor());
    }

    private void agregarAlCarrito() {
        if (productoSeleccionado == null) {
            mostrarAlerta("Seleccione un producto");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Cantidad inválida");
            return;
        }

        float precioCompra;
        try {
            precioCompra = Float.parseFloat(txtPrecioCompra.getText().trim());
            if (precioCompra <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Precio de compra inválido");
            return;
        }

        // Buscar si ya existe en el carrito
        for (ItemCompra item : itemsCarrito) {
            if (item.getIdProducto() == productoSeleccionado.getId()) {
                item.setCantidad(item.getCantidad() + cantidad);
                item.calcularSubtotal();
                tblCarrito.refresh();
                calcularTotal();
                txtCantidad.setText("1");
                txtPrecioCompra.clear();
                return;
            }
        }

        // Agregar nuevo item
        ItemCompra nuevoItem = new ItemCompra(
                productoSeleccionado.getId(),
                productoSeleccionado.getNombre(),
                cantidad,
                precioCompra
        );
        itemsCarrito.add(nuevoItem);
        calcularTotal();
        txtCantidad.setText("1");
        txtPrecioCompra.clear();
    }

    private void quitarDelCarrito() {
        ItemCompra seleccionado = tblCarrito.getSelectionModel().getSelectedItem();
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
        for (ItemCompra item : itemsCarrito) {
            total += item.getSubtotal();
        }
        // Usar Locale.US para forzar punto decimal
        lblTotal.setText(String.format(Locale.US, "Bs %.2f", total));
    }

    private void registrarCompra() {
        if (cbProveedor.getValue() == null) {
            mostrarAlerta("Seleccione un proveedor");
            return;
        }
        if (itemsCarrito.isEmpty()) {
            mostrarAlerta("No hay productos en el carrito");
            return;
        }

        Compra compra = new Compra();
        compra.setFecha(LocalDate.now());
        compra.setIdProveedor(cbProveedor.getValue().getId());

        // Ya no necesita replace(",", ".") porque viene con punto desde calcularTotal()
        String totalTexto = lblTotal.getText().replace("Bs ", "");
        compra.setTotal(Float.parseFloat(totalTexto));

        List<DetalleCompra> detalles = new ArrayList<>();
        for (ItemCompra item : itemsCarrito) {
            DetalleCompra detalle = new DetalleCompra();
            detalle.setIdProducto(item.getIdProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioCompra(item.getPrecio());
            detalles.add(detalle);
        }
        compra.setDetalles(detalles);

        try {
            compraService.registrarCompra(compra);
            mostrarInfo("✅ Compra registrada correctamente");
            limpiarCarrito();
            cargarProductosIniciales();
            productoSeleccionado = null;
            lblProductoSeleccionado.setText("");
        } catch (Exception e) {
            mostrarError("Error al registrar compra: " + e.getMessage());
        }
    }

    private void abrirDialogoNuevoProveedor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NuevoProveedorDialog.fxml"));
            Pane root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nuevo Proveedor");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            // Recargar proveedores
            cbProveedor.getItems().clear();
            cbProveedor.getItems().addAll(proveedorService.listarTodos());
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir diálogo de nuevo proveedor");
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