package org.example.Vista.MainControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.DTO.CompraInfoDTO;
import org.example.DTO.DetalleCompraDTO;
import org.example.Servicio.PdfExportService;
import org.example.Servicio.ReporteService;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetalleCompraController {

    @FXML private Label lblTitulo;
    @FXML private Label lblFecha;
    @FXML private Label lblProveedor;
    @FXML private Label lblTotal;
    @FXML private TableView<DetalleCompraDTO> tablaProductos;
    @FXML private TableColumn<DetalleCompraDTO, String> colProducto;
    @FXML private TableColumn<DetalleCompraDTO, String> colViscosidad;
    @FXML private TableColumn<DetalleCompraDTO, String> colCodigo;
    @FXML private TableColumn<DetalleCompraDTO, String> colMarca;
    @FXML private TableColumn<DetalleCompraDTO, String> colCategoria;
    @FXML private TableColumn<DetalleCompraDTO, Float> colCantidad;
    @FXML private TableColumn<DetalleCompraDTO, Float> colPrecio;
    @FXML private TableColumn<DetalleCompraDTO, Float> colSubtotal;
    @FXML private Button btnExportar;
    @FXML private Button btnCerrar;

    private final ReporteService reporteService = new ReporteService();
    private final PdfExportService pdfService = new PdfExportService();
    private int idCompra;
    private float total;
    private CompraInfoDTO infoCompra;

    public void cargarDetalle(int idCompra) {
        this.idCompra = idCompra;
        lblTitulo.setText("📋 DETALLE DE COMPRA #" + idCompra);

        // Cargar información de la compra
        infoCompra = reporteService.obtenerInfoCompra(idCompra);
        if (infoCompra != null) {
            lblFecha.setText(infoCompra.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            lblProveedor.setText(infoCompra.getProveedor());
        }

        // Cargar detalles de productos
        List<DetalleCompraDTO> detalles = reporteService.obtenerDetalleCompra(idCompra);

        if (detalles != null && !detalles.isEmpty()) {
            // Configurar columnas
            colProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
            colViscosidad.setCellValueFactory(new PropertyValueFactory<>("viscosidad"));
            colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
            colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
            colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
            colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

            ObservableList<DetalleCompraDTO> items = FXCollections.observableArrayList(detalles);
            tablaProductos.setItems(items);

            // Calcular total
            total = 0;
            for (DetalleCompraDTO d : detalles) {
                total += d.getSubtotal();
            }
            lblTotal.setText(String.format("Bs %.2f", total));
        }
    }

    @FXML
    private void exportarPDF() {
        try {
            pdfService.exportarDetalleCompra(
                    idCompra,
                    tablaProductos.getItems(),
                    infoCompra != null ? infoCompra.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "",
                    infoCompra != null ? infoCompra.getProveedor() : "",
                    total,
                    System.getProperty("user.home") + "/Desktop/detalle_compra_" + idCompra + ".pdf"
            );
            mostrarInfo("PDF exportado en el escritorio");
        } catch (Exception e) {
            mostrarError("Error al exportar PDF: " + e.getMessage());
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}