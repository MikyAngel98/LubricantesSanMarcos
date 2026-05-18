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
import org.example.Modelo.pojo.*;
import org.example.Servicio.ProductoService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class InventarioController {

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Float> colStock;
    @FXML private TableColumn<Producto, Float> colPrecio;
    @FXML private TableColumn<Producto, String> colMarca;
    @FXML private TableColumn<Producto, String> colDetalle;

    @FXML private TextField txtBuscar;
    @FXML private Label lblTotalProductos;
    @FXML private Label lblBajoStock;

    private final ProductoService productoService = new ProductoService();
    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private List<Producto> cacheProductos;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("Detalle"));
        tablaProductos.setItems(listaProductos);
    }

    private void cargarDatos() {
        cacheProductos = productoService.listarProductosBase();
        listaProductos.setAll(cacheProductos);
        actualizarEstado();
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                listaProductos.setAll(cacheProductos);
            } else {
                List<Producto> filtrados = cacheProductos.stream()
                        .filter(p -> p.getNombre().toLowerCase().contains(newVal.toLowerCase()))
                        .toList();
                listaProductos.setAll(filtrados);
            }
        });
    }

    private void actualizarEstado() {
        lblTotalProductos.setText("Total productos: " + cacheProductos.size());

        long bajoStock = cacheProductos.stream()
                .filter(p -> p.getStock() <= 5)
                .count();
        lblBajoStock.setText("⚠️ Productos bajo stock: " + bajoStock);

        if (bajoStock > 0) {
            lblBajoStock.setStyle("-fx-text-fill: #e74c3c;");
        } else {
            lblBajoStock.setStyle("-fx-text-fill: #e67e22;");
        }
    }

    // ==================== MÉTODOS CRUD ====================

    @FXML
    private void actualizarTabla() {
        cargarDatos();
    }

    @FXML
    private void abrirNuevoProducto() {
        abrirFormularioProducto(null);
    }

    @FXML
    private void editarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto para editar");
            return;
        }
        abrirFormularioProducto(seleccionado);
    }

    @FXML
    private void eliminarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto para eliminar");
            return;
        }

        // Validar que el stock sea 0
        if (seleccionado.getStock() > 0) {
            mostrarAlerta("No se puede eliminar el producto porque tiene stock: " + seleccionado.getStock());
            return;
        }

        // Confirmar eliminación
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Está seguro de eliminar el producto?");
        confirm.setContentText("Producto: " + seleccionado.getNombre() + "\nEsta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                productoService.eliminarProducto(seleccionado.getId());
                mostrarInfo("Producto eliminado correctamente");
                actualizarTabla();
            } catch (Exception e) {
                mostrarError("Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void abrirFormularioProducto(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NuevoProductoView.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtener el controlador y pasar el producto para edición
            NuevoProductoController controller = loader.getController();
            if (producto != null) {
                controller.setProductoParaEditar(producto);
            }

            Stage stage = new Stage();
            stage.setTitle(producto == null ? "Registrar Producto" : "Editar Producto");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setOnHidden(event -> actualizarTabla());
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir el formulario de producto");
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