package org.example.Vista.MainControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

    // Proveedor
    @FXML private ComboBox<Proveedor> cbProveedor;
    @FXML private Button btnNuevoProveedor;

    // Cantidad y precio
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecioCompra;
    @FXML private Button btnDisminuir, btnAumentar;
    @FXML private Button btnAgregar;
    @FXML private Label lblProductoSeleccionado;

    // Carrito
    @FXML private TableView<ItemCompra> tblCarrito;
    @FXML private TableColumn<ItemCompra, String> colCarritoProducto;
    @FXML private TableColumn<ItemCompra, Float> colCarritoCantidad;
    @FXML private TableColumn<ItemCompra, Float> colCarritoPrecio;
    @FXML private TableColumn<ItemCompra, Float> colCarritoSubtotal;
    @FXML private Button btnQuitar, btnLimpiarCarrito;
    @FXML private Label lblTotal;
    @FXML private Button btnRegistrar;

    // Servicios
    private final ProductoService productoService = new ProductoService();
    private final ProveedorService proveedorService = new ProveedorService();
    private final CompraService compraService = new CompraService();

    // Datos
    private ObservableList<ItemCompra> itemsCarrito = FXCollections.observableArrayList();

    private List<Aceite> cacheAceites = new ArrayList<>();
    private List<Filtro> cacheFiltros = new ArrayList<>();
    private List<Foco> cacheFocos = new ArrayList<>();
    private List<Producto> cacheProductosBase = new ArrayList<>();

    private Producto productoSeleccionado;

    @FXML
    public void initialize() {
        cargarCacheCompleto();
        configurarCarrito();
        configurarTablas();
        configurarEventos();

        scrollTablas.setFitToWidth(true);
        scrollTablas.setFitToHeight(true);

        cargarProveedores();
        cargarAceites();
    }

    private void cargarCacheCompleto() {
        try {
            cacheAceites = productoService.listarAceites();
            cacheFiltros = productoService.listarFiltros();
            cacheFocos = productoService.listarFocos();
            cacheProductosBase = productoService.listarProductosBase();

            System.out.println("=== DATOS CARGADOS COMPRAS ===");
            System.out.println("Aceites: " + cacheAceites.size());
            System.out.println("Filtros: " + cacheFiltros.size());
            System.out.println("Focos: " + cacheFocos.size());
            System.out.println("Productos Base: " + cacheProductosBase.size());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    private void cargarProveedores() {
        cbProveedor.getItems().addAll(proveedorService.listarTodos());
    }

    private void configurarCarrito() {
        colCarritoProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
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

    private void mostrarTabla(String tabla) {
        // Ocultar todas las tablas
        tablaAceites.setVisible(false);
        tablaAceites.setManaged(false);
        tablaFiltros.setVisible(false);
        tablaFiltros.setManaged(false);
        tablaFocos.setVisible(false);
        tablaFocos.setManaged(false);
        tablaProductosBase.setVisible(false);
        tablaProductosBase.setManaged(false);

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

    @FXML
    private void cargarAceites() {
        mostrarTabla("ACEITES");
        productoSeleccionado = null;
        lblProductoSeleccionado.setText("");

        tablaAceites.setItems(FXCollections.observableArrayList(cacheAceites));

        // Configurar búsqueda
        txtBuscar.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                tablaAceites.setItems(FXCollections.observableArrayList(cacheAceites));
            } else {
                List<Aceite> filtrados = cacheAceites.stream()
                        .filter(a -> a.getNombre().toLowerCase().contains(newVal.toLowerCase()))
                        .toList();
                tablaAceites.setItems(FXCollections.observableArrayList(filtrados));
            }
        });

        tablaAceites.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                productoSeleccionado = newVal;
                lblProductoSeleccionado.setText("Seleccionado: " + newVal.getNombre() + " | Stock: " + newVal.getStock());
                txtCantidad.setText("1");
                txtPrecioCompra.clear();
                configurarBotonesCantidad();
            }
        });
    }

    @FXML
    private void cargarFiltros() {
        mostrarTabla("FILTROS");
        productoSeleccionado = null;
        lblProductoSeleccionado.setText("");

        tablaFiltros.setItems(FXCollections.observableArrayList(cacheFiltros));

        txtBuscar.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                tablaFiltros.setItems(FXCollections.observableArrayList(cacheFiltros));
            } else {
                List<Filtro> filtrados = cacheFiltros.stream()
                        .filter(f -> f.getNombre().toLowerCase().contains(newVal.toLowerCase()))
                        .toList();
                tablaFiltros.setItems(FXCollections.observableArrayList(filtrados));
            }
        });

        tablaFiltros.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                productoSeleccionado = newVal;
                lblProductoSeleccionado.setText("Seleccionado: " + newVal.getNombre() + " | Stock: " + newVal.getStock());
                txtCantidad.setText("1");
                txtPrecioCompra.clear();
                configurarBotonesCantidad();
            }
        });
    }

    @FXML
    private void cargarFocos() {
        mostrarTabla("FOCOS");
        productoSeleccionado = null;
        lblProductoSeleccionado.setText("");

        tablaFocos.setItems(FXCollections.observableArrayList(cacheFocos));

        txtBuscar.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                tablaFocos.setItems(FXCollections.observableArrayList(cacheFocos));
            } else {
                List<Foco> filtrados = cacheFocos.stream()
                        .filter(f -> f.getNombre().toLowerCase().contains(newVal.toLowerCase()))
                        .toList();
                tablaFocos.setItems(FXCollections.observableArrayList(filtrados));
            }
        });

        tablaFocos.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                productoSeleccionado = newVal;
                lblProductoSeleccionado.setText("Seleccionado: " + newVal.getNombre() + " | Stock: " + newVal.getStock());
                txtCantidad.setText("1");
                txtPrecioCompra.clear();
                configurarBotonesCantidad();
            }
        });
    }

    @FXML
    private void cargarProductos() {
        mostrarTabla("PRODUCTOS");
        productoSeleccionado = null;
        lblProductoSeleccionado.setText("");

        tablaProductosBase.setItems(FXCollections.observableArrayList(cacheProductosBase));

        txtBuscar.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                tablaProductosBase.setItems(FXCollections.observableArrayList(cacheProductosBase));
            } else {
                List<Producto> filtrados = cacheProductosBase.stream()
                        .filter(p -> p.getNombre().toLowerCase().contains(newVal.toLowerCase()))
                        .toList();
                tablaProductosBase.setItems(FXCollections.observableArrayList(filtrados));
            }
        });

        tablaProductosBase.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                productoSeleccionado = newVal;
                lblProductoSeleccionado.setText("Seleccionado: " + newVal.getNombre() + " | Stock: " + newVal.getStock());
                txtCantidad.setText("1");
                txtPrecioCompra.clear();
                configurarBotonesCantidad();
            }
        });
    }

    private void configurarBotonesCantidad() {
        // Para todas las tablas, incrementos/decrementos de 1
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
    }

    private void configurarEventos() {
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
            cargarCacheCompleto();
            cargarAceites();
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