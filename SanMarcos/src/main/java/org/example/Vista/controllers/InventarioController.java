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

public class InventarioController {

    // Componentes de la tabla
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Float> colStock;
    @FXML private TableColumn<Producto, Float> colPrecio;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colMarca;
    @FXML private TableColumn<Producto, String> colDetalle;

    // Componentes de búsqueda y estado
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotalProductos;
    @FXML private Label lblBajoStock;

    // Servicios y datos
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
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaNombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("Detalle"));



        tablaProductos.setItems(listaProductos);
    }

    private void cargarDatos() {
        cacheProductos = productoService.listarTodos();
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

        // Si hay bajo stock, cambiar color a rojo
        if (bajoStock > 0) {
            lblBajoStock.setStyle("-fx-text-fill: #e74c3c;");
        } else {
            lblBajoStock.setStyle("-fx-text-fill: #e67e22;");
        }
    }

    @FXML
    private void actualizarTabla() {
        cargarDatos();
    }

    @FXML
    private void abrirNuevoProducto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NuevoProductoView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Registrar Producto");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Recargar datos al cerrar
            stage.setOnHidden(event -> actualizarTabla());

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir el formulario de producto");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}