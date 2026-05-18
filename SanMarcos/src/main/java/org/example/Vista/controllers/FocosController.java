package org.example.Vista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.Modelo.pojo.Foco;
import org.example.Modelo.pojo.Producto;
import org.example.Servicio.ProductoService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class FocosController {

    @FXML private TableView<Foco> tablaFocos;
    @FXML private TableColumn<Foco, Integer> colId;
    @FXML private TableColumn<Foco, String> colNombre;
    @FXML private TableColumn<Foco, String> colCodigo;
    @FXML private TableColumn<Foco, String> colMarca;
    @FXML private TableColumn<Foco, Float> colStock;
    @FXML private TableColumn<Foco, Float> colPrecio;
    @FXML private TableColumn<Foco, String> colDetalle;

    @FXML private TextField txtBuscar;
    @FXML private Label lblTotal;

    private final ProductoService productoService = new ProductoService();
    private ObservableList<Foco> listaFocos = FXCollections.observableArrayList();
    private List<Foco> cacheFocos;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("Codigo"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("Detalle"));

        tablaFocos.setItems(listaFocos);
    }

    private void cargarDatos() {
        try {
            List<Foco> focos = productoService.listarFocos();  // Usar método nuevo
            cacheFocos = focos;
            listaFocos.setAll(cacheFocos);
            lblTotal.setText("Total focos: " + cacheFocos.size());
        } catch (Exception e) {
            e.printStackTrace();
            lblTotal.setText("Error al cargar datos");
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                listaFocos.setAll(cacheFocos);
            } else {
                List<Foco> filtrados = cacheFocos.stream()
                        .filter(f -> f.getNombre().toLowerCase().contains(newVal.toLowerCase()))
                        .toList();
                listaFocos.setAll(filtrados);
            }
        });
    }

    @FXML
    private void actualizarTabla() {
        cargarDatos();
    }

    @FXML
    private void abrirNuevoFoco() {
        abrirFormularioProducto(null);
    }

    @FXML
    private void editarFoco() {
        Foco seleccionado = tablaFocos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un foco para editar");
            return;
        }
        abrirFormularioProducto(seleccionado);
    }

    @FXML
    private void eliminarFoco() {
        Foco seleccionado = tablaFocos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un foco para eliminar");
            return;
        }

        if (seleccionado.getStock() > 0) {
            mostrarAlerta("No se puede eliminar el foco porque tiene stock: " + seleccionado.getStock());
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Está seguro de eliminar este foco?");
        confirm.setContentText("Producto: " + seleccionado.getNombre() + "\nEsta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                productoService.eliminarProducto(seleccionado.getId());
                mostrarInfo("Foco eliminado correctamente");
                actualizarTabla();
            } catch (Exception e) {
                mostrarError("Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void abrirFormularioProducto(Foco foco) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NuevoProductoView.fxml"));
            Scene scene = new Scene(loader.load());

            NuevoProductoController controller = loader.getController();
            if (foco != null) {
                controller.setProductoParaEditar(foco);  // Pasar el objeto Foco
            } else {
                controller.setPreseleccionarFoco();
            }

            Stage stage = new Stage();
            stage.setTitle(foco == null ? "Nuevo Foco" : "Editar Foco");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setOnHidden(event -> actualizarTabla());
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir el formulario");
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
