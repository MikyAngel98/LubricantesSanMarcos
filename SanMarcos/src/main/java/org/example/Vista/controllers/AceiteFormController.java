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
import org.example.Modelo.pojo.Aceite;
import org.example.Servicio.ProductoService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AceiteFormController {

    @FXML private TableColumn<Aceite, Integer> colId;
    @FXML private TableColumn<Aceite, String> colNombre;
    @FXML private TableColumn<Aceite, String> colViscosidad;
    @FXML private TableColumn<Aceite, String> colMarca;
    @FXML private TableColumn<Aceite, Float> colPrecio;
    @FXML private TableColumn<Aceite, Float> colStock;
    @FXML private TableColumn<Aceite, String> colPresentacion;
    @FXML private TableColumn<Aceite, String> colAgranel;
    @FXML private TableColumn<Aceite, String> colTipo;
    @FXML private TableColumn<Aceite, String> colUso;
    @FXML private TableColumn<Aceite, String> colDetalle;

    @FXML private TableView<Aceite> tablaAceites;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotal;

    private final ProductoService productoService = new ProductoService();
    private ObservableList<Aceite> listaAceites = FXCollections.observableArrayList();
    private List<Aceite> cacheAceites;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        colViscosidad.setCellValueFactory(new PropertyValueFactory<>("Viscosidad"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colPresentacion.setCellValueFactory(new PropertyValueFactory<>("presentacionNombre"));

        colAgranel.setCellValueFactory(cellData -> {
            boolean granel = cellData.getValue().isEsAgranel();
            return new javafx.beans.property.SimpleStringProperty(granel ? "Sí" : "No");
        });

        colTipo.setCellValueFactory(new PropertyValueFactory<>("TipoAceite"));
        colUso.setCellValueFactory(new PropertyValueFactory<>("Uso"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("Detalle"));

        tablaAceites.setItems(listaAceites);
    }

    private void cargarDatos() {
        try {
            List<Aceite> aceites = productoService.listarAceites();
            cacheAceites = aceites;
            listaAceites.setAll(cacheAceites);
            lblTotal.setText("Total aceites: " + cacheAceites.size());
        } catch (Exception e) {
            e.printStackTrace();
            lblTotal.setText("Error al cargar datos");
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                listaAceites.setAll(cacheAceites);
            } else {
                List<Aceite> filtrados = cacheAceites.stream()
                        .filter(a -> a.getNombre().toLowerCase().contains(newVal.toLowerCase()))
                        .toList();
                listaAceites.setAll(filtrados);
            }
        });
    }

    @FXML
    private void actualizarTabla() {
        cargarDatos();
    }

    @FXML
    private void abrirNuevoAceite() {
        abrirFormularioAceite(null);
    }

    @FXML
    private void editarAceite() {
        Aceite seleccionado = tablaAceites.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un aceite para editar");
            return;
        }
        abrirFormularioAceite(seleccionado);
    }

    @FXML
    private void eliminarAceite() {
        Aceite seleccionado = tablaAceites.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un aceite para eliminar");
            return;
        }

        if (seleccionado.getStock() > 0) {
            mostrarAlerta("No se puede eliminar el aceite porque tiene stock: " + seleccionado.getStock());
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Está seguro de eliminar este aceite?");
        confirm.setContentText("Producto: " + seleccionado.getNombre() + "\nEsta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                productoService.eliminarProducto(seleccionado.getId());
                mostrarInfo("Aceite eliminado correctamente");
                actualizarTabla();
            } catch (Exception e) {
                mostrarError("Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void abrirFormularioAceite(Aceite aceite) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NuevoProductoView.fxml"));
            Scene scene = new Scene(loader.load());

            NuevoProductoController controller = loader.getController();
            if (aceite != null) {
                controller.setProductoParaEditar(aceite);
            } else {
                controller.setPreseleccionarAceite();
            }

            Stage stage = new Stage();
            stage.setTitle(aceite == null ? "Nuevo Aceite" : "Editar Aceite");
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