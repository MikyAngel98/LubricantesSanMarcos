package org.example.Vista.MainControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.DTO.DetalleVentaDTO;
import org.example.DTO.VentaInfoDTO;
import org.example.Servicio.PdfExportService;
import org.example.Servicio.ReporteService;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetalleVentaController {

    @FXML private Label lblTitulo;
    @FXML private Label lblFecha;
    @FXML private Label lblCliente;
    @FXML private Label lblMetodoPago;
    @FXML private Label lblTotal;
    @FXML private TableView<DetalleVentaDTO> tablaProductos;
    @FXML private TableColumn<DetalleVentaDTO, String> colProducto;
    @FXML private TableColumn<DetalleVentaDTO, String> colViscosidad;
    @FXML private TableColumn<DetalleVentaDTO, String> colCodigo;
    @FXML private TableColumn<DetalleVentaDTO, String> colMarca;
    @FXML private TableColumn<DetalleVentaDTO, String> colCategoria;
    @FXML private TableColumn<DetalleVentaDTO, Float> colCantidad;
    @FXML private TableColumn<DetalleVentaDTO, Float> colPrecio;
    @FXML private TableColumn<DetalleVentaDTO, Float> colSubtotal;
    @FXML private Button btnExportar;
    @FXML private Button btnCerrar;

    private final ReporteService reporteService = new ReporteService();
    private final PdfExportService pdfService = new PdfExportService();
    private int idVenta;
    private float total;
    private VentaInfoDTO infoVenta;

    public void cargarDetalle(int idVenta) {
        this.idVenta = idVenta;
        lblTitulo.setText("📋 DETALLE DE VENTA #" + idVenta);

        infoVenta = reporteService.obtenerInfoVenta(idVenta);
        if (infoVenta != null) {
            lblFecha.setText(infoVenta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            lblCliente.setText(infoVenta.getCliente());
            lblMetodoPago.setText(infoVenta.getMetodoPago());
        }

        List<DetalleVentaDTO> detalles = reporteService.obtenerDetalleVenta(idVenta);

        if (detalles != null && !detalles.isEmpty()) {
            colProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
            colViscosidad.setCellValueFactory(new PropertyValueFactory<>("viscosidad"));
            colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
            colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
            colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
            colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

            ObservableList<DetalleVentaDTO> items = FXCollections.observableArrayList(detalles);
            tablaProductos.setItems(items);

            total = 0;
            for (DetalleVentaDTO d : detalles) {
                total += d.getSubtotal();
            }
            lblTotal.setText(String.format("Bs %.2f", total));
        }
    }

    @FXML
    private void exportarPDF() {
        // Implementar exportación con nuevos datos
        mostrarInfo("Exportación a PDF en desarrollo");
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}