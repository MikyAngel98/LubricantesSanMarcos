package org.example.Vista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainController {

    @FXML private StackPane panelCentral;
    @FXML private Label lblFecha;

    @FXML
    public void initialize() {
        lblFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        abrirInventario();  // Mostrar inventario por defecto
    }

    // ==================== MÉTODOS DEL MENÚ ====================

    @FXML
    private void abrirInventario() {
        cargarPanel("/fxml/InventarioView.fxml");
    }

    @FXML
    private void abrirAceites() {
        cargarPanel("/fxml/AceiteFormView.fxml");
    }

    @FXML
    private void abrirFiltros() {
        cargarPanel("/fxml/FiltroFormView.fxml");
    }

    @FXML
    private void abrirFocos() {cargarPanel("/fxml/FocosView.fxml");}

    @FXML
    private void abrirVentas() {
        cargarPanel("/fxml/VentasView.fxml");
    }

    @FXML
    private void abrirCompras() { cargarPanel("/fxml/ComprasView.fxml");}

    @FXML
    private void abrirNuevoProducto() {
        cargarPanel("/fxml/NuevoProductoView.fxml");
    }

    @FXML
    private void abrirReportes() {
        mostrarMensajeTemporal("📊 REPORTES\n(Próximamente)");
    }

    @FXML
    private void abrirConfiguracion() {
        mostrarMensajeTemporal("⚙️ CONFIGURACIÓN\n(Próximamente)");
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void cargarPanel(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node panel = loader.load();
            panelCentral.getChildren().setAll(panel);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensajeTemporal("❌ Error al cargar: " + fxmlPath);
        }
    }

    private void mostrarMensajeTemporal(String mensaje) {
        panelCentral.getChildren().clear();
        Label label = new Label(mensaje);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50; -fx-alignment: center;");
        panelCentral.getChildren().add(label);
    }
}