package org.example.Vista.MainControllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.DTO.*;
import org.example.Servicio.PdfExportService;
import org.example.Servicio.ReporteService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class ReportesController {

    // Botones
    @FXML private HBox panelFiltros;
    @FXML private StackPane panelTablas;
    @FXML private Button btnVerDetalle;
    @FXML private Button btnExportar;
    @FXML private Button btnCerrar;

    // Tabla VENTAS
    @FXML private TableView<ReporteVentaDTO> tablaVentas;
    @FXML private TableColumn<ReporteVentaDTO, Integer> colVentaId;
    @FXML private TableColumn<ReporteVentaDTO, LocalDate> colVentaFecha;
    @FXML private TableColumn<ReporteVentaDTO, String> colVentaCliente;
    @FXML private TableColumn<ReporteVentaDTO, Integer> colVentaTipos;
    @FXML private TableColumn<ReporteVentaDTO, Double> colVentaTotal;
    @FXML private TableColumn<ReporteVentaDTO, String> colVentaMetodo;

    // Tabla COMPRAS
    @FXML private TableView<HistorialCompraDTO> tablaCompras;
    @FXML private TableColumn<HistorialCompraDTO, Integer> colCompraId;
    @FXML private TableColumn<HistorialCompraDTO, LocalDate> colCompraFecha;
    @FXML private TableColumn<HistorialCompraDTO, String> colCompraProveedor;
    @FXML private TableColumn<HistorialCompraDTO, Integer> colCompraTipos;
    @FXML private TableColumn<HistorialCompraDTO, Double> colCompraTotal;

    // Tabla MÁS VENDIDOS
    @FXML private TableView<ProductoVendidoDTO> tablaMasVendidos;
    @FXML private TableColumn<ProductoVendidoDTO, Integer> colProdId;
    @FXML private TableColumn<ProductoVendidoDTO, String> colProdNombre;
    @FXML private TableColumn<ProductoVendidoDTO, String> colProdCategoria;
    @FXML private TableColumn<ProductoVendidoDTO, String> colProdMarca;
    @FXML private TableColumn<ProductoVendidoDTO, Float> colProdVendido;
    @FXML private TableColumn<ProductoVendidoDTO, Double> colProdFacturado;

    // Tabla BAJO STOCK
    @FXML private TableView<ProductoBajoStockDTO> tablaBajoStock;
    @FXML private TableColumn<ProductoBajoStockDTO, Integer> colStockId;
    @FXML private TableColumn<ProductoBajoStockDTO, String> colStockNombre;
    @FXML private TableColumn<ProductoBajoStockDTO, String> colStockCategoria;
    @FXML private TableColumn<ProductoBajoStockDTO, String> colStockMarca;
    @FXML private TableColumn<ProductoBajoStockDTO, Float> colStockCantidad;
    @FXML private TableColumn<ProductoBajoStockDTO, Float> colStockPrecio;

    private final ReporteService reporteService = new ReporteService();
    private final PdfExportService pdfService = new PdfExportService();

    private String reporteActual = "";
    private int idSeleccionado = -1;

    // Variables para filtros
    private LocalDate fechaInicioActual;
    private LocalDate fechaFinActual;
    private float stockMinimoActual = 5;
    private int topActual = 10;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarListenersSeleccion();
        abrirReporteVentas();
    }

    private void configurarListenersSeleccion() {
        tablaVentas.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                idSeleccionado = selected.getId();
            }
        });
        tablaCompras.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                idSeleccionado = selected.getId();
            }
        });
    }

    private void configurarColumnas() {
        // Configurar columnas VENTAS
        colVentaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVentaFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colVentaCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colVentaTipos.setCellValueFactory(new PropertyValueFactory<>("tiposProductos"));
        colVentaTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colVentaMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));

        // Configurar columnas COMPRAS
        colCompraId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCompraFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCompraProveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));
        colCompraTipos.setCellValueFactory(new PropertyValueFactory<>("tiposProductos"));
        colCompraTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Configurar columnas MÁS VENDIDOS
        colProdId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProdNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colProdCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colProdMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colProdVendido.setCellValueFactory(new PropertyValueFactory<>("totalVendido"));
        colProdFacturado.setCellValueFactory(new PropertyValueFactory<>("totalFacturado"));

        // Configurar columnas BAJO STOCK
        colStockId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStockNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colStockCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colStockMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colStockCantidad.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStockPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    private void mostrarTabla(String tabla) {
        tablaVentas.setVisible(false);
        tablaVentas.setManaged(false);
        tablaCompras.setVisible(false);
        tablaCompras.setManaged(false);
        tablaMasVendidos.setVisible(false);
        tablaMasVendidos.setManaged(false);
        tablaBajoStock.setVisible(false);
        tablaBajoStock.setManaged(false);

        switch (tabla) {
            case "VENTAS":
                tablaVentas.setVisible(true);
                tablaVentas.setManaged(true);
                break;
            case "COMPRAS":
                tablaCompras.setVisible(true);
                tablaCompras.setManaged(true);
                break;
            case "MAS_VENDIDOS":
                tablaMasVendidos.setVisible(true);
                tablaMasVendidos.setManaged(true);
                break;
            case "BAJO_STOCK":
                tablaBajoStock.setVisible(true);
                tablaBajoStock.setManaged(true);
                break;
        }
    }

    private void limpiarFiltros() {
        panelFiltros.getChildren().clear();
        idSeleccionado = -1;
    }

    // ==================== ABRIR REPORTES ====================

    @FXML
    private void abrirReporteVentas() {
        reporteActual = "VENTAS";
        limpiarFiltros();
        mostrarTabla("VENTAS");
        btnVerDetalle.setDisable(false);

        DatePicker dpInicio = new DatePicker();
        DatePicker dpFin = new DatePicker();
        Button btnConsultar = new Button("CONSULTAR");
        btnConsultar.setOnAction(e -> {
            fechaInicioActual = dpInicio.getValue();
            fechaFinActual = dpFin.getValue();
            cargarVentas(fechaInicioActual, fechaFinActual);
        });

        panelFiltros.getChildren().addAll(
                new Label("Fecha inicio:"), dpInicio,
                new Label("Fecha fin:"), dpFin,
                btnConsultar
        );

        fechaInicioActual = LocalDate.now().minusDays(30);
        fechaFinActual = LocalDate.now();
        dpInicio.setValue(fechaInicioActual);
        dpFin.setValue(fechaFinActual);
        cargarVentas(fechaInicioActual, fechaFinActual);

        tablaVentas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                verDetalle();
            }
        });
    }

    @FXML
    private void abrirReporteCompras() {
        reporteActual = "COMPRAS";
        limpiarFiltros();
        mostrarTabla("COMPRAS");
        btnVerDetalle.setDisable(false);

        DatePicker dpInicio = new DatePicker();
        DatePicker dpFin = new DatePicker();
        Button btnConsultar = new Button("CONSULTAR");
        btnConsultar.setOnAction(e -> {
            fechaInicioActual = dpInicio.getValue();
            fechaFinActual = dpFin.getValue();
            cargarCompras(fechaInicioActual, fechaFinActual);
        });

        panelFiltros.getChildren().addAll(
                new Label("Fecha inicio:"), dpInicio,
                new Label("Fecha fin:"), dpFin,
                btnConsultar
        );

        fechaInicioActual = LocalDate.now().minusDays(30);
        fechaFinActual = LocalDate.now();
        dpInicio.setValue(fechaInicioActual);
        dpFin.setValue(fechaFinActual);
        cargarCompras(fechaInicioActual, fechaFinActual);

        tablaCompras.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                verDetalle();
            }
        });
    }

    @FXML
    private void abrirReporteMasVendidos() {
        reporteActual = "MAS_VENDIDOS";
        limpiarFiltros();
        mostrarTabla("MAS_VENDIDOS");
        btnVerDetalle.setDisable(true);

        DatePicker dpInicio = new DatePicker();
        DatePicker dpFin = new DatePicker();
        TextField txtTop = new TextField("10");
        Button btnConsultar = new Button("CONSULTAR");
        btnConsultar.setOnAction(e -> {
            fechaInicioActual = dpInicio.getValue();
            fechaFinActual = dpFin.getValue();
            try {
                topActual = Integer.parseInt(txtTop.getText());
                cargarMasVendidos(fechaInicioActual, fechaFinActual, topActual);
            } catch (NumberFormatException ex) {
                mostrarAlerta("Top inválido");
            }
        });

        panelFiltros.getChildren().addAll(
                new Label("Fecha inicio:"), dpInicio,
                new Label("Fecha fin:"), dpFin,
                new Label("Top:"), txtTop,
                btnConsultar
        );

        fechaInicioActual = LocalDate.now().minusDays(30);
        fechaFinActual = LocalDate.now();
        topActual = 10;
        dpInicio.setValue(fechaInicioActual);
        dpFin.setValue(fechaFinActual);
        txtTop.setText("10");
        cargarMasVendidos(fechaInicioActual, fechaFinActual, topActual);
    }

    @FXML
    private void abrirReporteBajoStock() {
        reporteActual = "BAJO_STOCK";
        limpiarFiltros();
        mostrarTabla("BAJO_STOCK");
        btnVerDetalle.setDisable(true);

        TextField txtStockMinimo = new TextField("5");
        Button btnConsultar = new Button("CONSULTAR");
        btnConsultar.setOnAction(e -> {
            try {
                stockMinimoActual = Float.parseFloat(txtStockMinimo.getText());
                cargarBajoStock(stockMinimoActual);
            } catch (NumberFormatException ex) {
                mostrarAlerta("Stock mínimo inválido");
            }
        });

        panelFiltros.getChildren().addAll(
                new Label("Stock mínimo:"), txtStockMinimo,
                btnConsultar
        );

        stockMinimoActual = 5;
        cargarBajoStock(stockMinimoActual);
    }

    // ==================== CARGAR DATOS ====================

    private void cargarVentas(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            mostrarAlerta("Seleccione ambas fechas");
            return;
        }
        var lista = reporteService.obtenerReporteVentas(inicio, fin);
        tablaVentas.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarCompras(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            mostrarAlerta("Seleccione ambas fechas");
            return;
        }
        var lista = reporteService.obtenerHistorialCompras(inicio, fin);
        tablaCompras.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarMasVendidos(LocalDate inicio, LocalDate fin, int top) {
        if (inicio == null || fin == null) {
            mostrarAlerta("Seleccione ambas fechas");
            return;
        }
        var lista = reporteService.obtenerProductosMasVendidos(inicio, fin, top);
        tablaMasVendidos.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarBajoStock(float stockMinimo) {
        var lista = reporteService.obtenerProductosBajoStock(stockMinimo);
        tablaBajoStock.setItems(FXCollections.observableArrayList(lista));
    }

    private String periodoSugerido() {
        LocalDate ini = fechaInicioActual != null ? fechaInicioActual : LocalDate.now().minusDays(30);
        LocalDate fin = fechaFinActual != null ? fechaFinActual : LocalDate.now();
        return ini + "_a_" + fin;
    }

    // ==================== ACCIONES ====================

    @FXML
    private void verDetalle() {
        if (idSeleccionado == -1) {
            mostrarAlerta("Seleccione un registro");
            return;
        }

        if ("VENTAS".equals(reporteActual)) {
            abrirDetalleVenta(idSeleccionado);
        } else if ("COMPRAS".equals(reporteActual)) {
            abrirDetalleCompra(idSeleccionado);
        } else {
            mostrarAlerta("Este reporte no tiene detalle");
        }
    }

    @FXML
    private void exportarReporte() {
        // Verificar que hay datos
        switch (reporteActual) {
            case "VENTAS":
                if (tablaVentas.getItems().isEmpty()) {
                    mostrarAlerta("No hay datos para exportar");
                    return;
                }
                break;
            case "COMPRAS":
                if (tablaCompras.getItems().isEmpty()) {
                    mostrarAlerta("No hay datos para exportar");
                    return;
                }
                break;
            case "MAS_VENDIDOS":
                if (tablaMasVendidos.getItems().isEmpty()) {
                    mostrarAlerta("No hay datos para exportar");
                    return;
                }
                break;
            case "BAJO_STOCK":
                if (tablaBajoStock.getItems().isEmpty()) {
                    mostrarAlerta("No hay datos para exportar");
                    return;
                }
                break;
            default:
                mostrarAlerta("Reporte no válido para exportar");
                return;
        }

        // Generar nombre sugerido según el reporte
        String nombreSugerido = "";
        String periodo = periodoSugerido();
        switch (reporteActual) {
            case "VENTAS":
                nombreSugerido = "reporte_ventas_" + periodo + ".pdf";
                break;
            case "COMPRAS":
                nombreSugerido = "reporte_compras_" + periodo + ".pdf";
                break;
            case "MAS_VENDIDOS":
                nombreSugerido = "productos_mas_vendidos_" + periodo + ".pdf";
                break;
            case "BAJO_STOCK":
                nombreSugerido = "productos_bajo_stock_" + LocalDate.now() + ".pdf";
                break;
        }

        // Elegir ubicación
        File archivo = elegirUbicacion(nombreSugerido);
        if (archivo == null) {
            return; // Usuario canceló
        }

        try {
            switch (reporteActual) {
                case "VENTAS":
                    pdfService.exportarReporteVentas(tablaVentas.getItems(), fechaInicioActual, fechaFinActual, archivo.getAbsolutePath());
                    break;
                case "COMPRAS":
                    pdfService.exportarReporteCompras(tablaCompras.getItems(), fechaInicioActual, fechaFinActual, archivo.getAbsolutePath());
                    break;
                case "MAS_VENDIDOS":
                    pdfService.exportarProductosMasVendidos(tablaMasVendidos.getItems(), fechaInicioActual, fechaFinActual, archivo.getAbsolutePath());
                    break;
                case "BAJO_STOCK":
                    pdfService.exportarProductosBajoStock(tablaBajoStock.getItems(), stockMinimoActual, archivo.getAbsolutePath());
                    break;
            }
            mostrarInfo("PDF guardado en: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            mostrarError("Error al exportar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private File elegirUbicacion(String nombreSugerido) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.setInitialFileName(nombreSugerido);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );
        return fileChooser.showSaveDialog(btnExportar.getScene().getWindow());
    }

    @FXML
    private void cerrarVentana() {
        MainController.mostrarBienvenidaStatic();
    }

    private void abrirDetalleVenta(int idVenta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DetalleVentaView.fxml"));
            Scene scene = new Scene(loader.load());
            DetalleVentaController controller = loader.getController();
            controller.cargarDetalle(idVenta);

            Stage stage = new Stage();
            stage.setTitle("Detalle de Venta #" + idVenta);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            mostrarError("Error al abrir detalle de venta");
        }
    }

    private void abrirDetalleCompra(int idCompra) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DetalleCompraView.fxml"));
            Scene scene = new Scene(loader.load());
            DetalleCompraController controller = loader.getController();
            controller.cargarDetalle(idCompra);

            Stage stage = new Stage();
            stage.setTitle("Detalle de Compra #" + idCompra);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            mostrarError("Error al abrir detalle de compra");
        }
    }

    private void mostrarAlerta(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}