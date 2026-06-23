package org.example.Vista.MainControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.Modelo.pojo.Filtro;
import org.example.Servicio.ProductoService;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class FiltroFormController {

    @FXML private TableColumn<Filtro, Integer> colId;
    @FXML private TableColumn<Filtro, String> colNombre;
    @FXML private TableColumn<Filtro, String> colCodigo;
    @FXML private TableColumn<Filtro, String> colRosca;
    @FXML private TableColumn<Filtro, String> colUso;
    @FXML private TableColumn<Filtro, String> colMarca;
    @FXML private TableColumn<Filtro, Float> colStock;
    @FXML private TableColumn<Filtro, Float> colPrecio;
    @FXML private TableColumn<Filtro, String> colDetalle;

    @FXML private TableView<Filtro> tablaFiltros;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotal;
    @FXML private ComboBox<String> cbCriterio;

    // Botones
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnActualizar;


    private final ProductoService productoService = new ProductoService();
    private ObservableList<Filtro> listaFiltros = FXCollections.observableArrayList();
    private List<Filtro> cacheFiltros;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
        configurarBusqueda();
        aplicarPermisos();

        cbCriterio.getSelectionModel().select("Nombre");
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("Codigo"));
        colRosca.setCellValueFactory(new PropertyValueFactory<>("Rosca"));
        colUso.setCellValueFactory(new PropertyValueFactory<>("Uso"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("Detalle"));

        tablaFiltros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaFiltros.setItems(listaFiltros);

    }

    private void cargarDatos() {
        try {
            List<Filtro> filtros = productoService.listarFiltros();
            cacheFiltros = filtros;
            listaFiltros.setAll(cacheFiltros);
            lblTotal.setText("Total filtros: " + cacheFiltros.size());
        } catch (Exception e) {
            e.printStackTrace();
            lblTotal.setText("Error al cargar datos");
        }
    }

    private void aplicarPermisos() {
        boolean esVendedor = SessionManager.getInstance().isVendedor();

        btnNuevo.setDisable(esVendedor);
        btnEditar.setDisable(esVendedor);
        btnEliminar.setDisable(esVendedor);
    }

    private void configurarBusqueda() {
        // Escuchar cambios en el texto y en el criterio
        txtBuscar.textProperty().addListener((obs, old, newVal) -> buscar());
        cbCriterio.valueProperty().addListener((obs, old, newVal) -> buscar());
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        String criterio = cbCriterio.getValue();

        if (criterio == null) {
            listaFiltros.setAll(cacheFiltros);
            return;
        }

        if (texto.isEmpty()) {
            listaFiltros.setAll(cacheFiltros);
            return;
        }

        List<Filtro> filtrados = cacheFiltros.stream()
                .filter(f -> {
                    switch (criterio) {
                        case "Nombre":
                            return f.getNombre().toLowerCase().contains(texto);
                        case "Código":
                            return f.getCodigo() != null && f.getCodigo().toLowerCase().contains(texto);
                        case "Rosca":
                            return f.getRosca() != null && f.getRosca().toLowerCase().contains(texto);
                        default:
                            return false;
                    }
                })
                .toList();

        listaFiltros.setAll(filtrados);
    }


    @FXML
    private void actualizarTabla() {
        cargarDatos();
    }

    @FXML
    private void abrirNuevoFiltro() {
        abrirFormularioFiltro(null);
    }

    @FXML
    private void editarFiltro() {
        Filtro seleccionado = tablaFiltros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un filtro para editar");
            return;
        }
        abrirFormularioFiltro(seleccionado);
    }

    @FXML
    private void eliminarFiltro() {
        Filtro seleccionado = tablaFiltros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un filtro para eliminar");
            return;
        }

        if (seleccionado.getStock() > 0) {
            mostrarAlerta("No se puede eliminar el filtro porque tiene stock: " + seleccionado.getStock());
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Está seguro de eliminar este filtro?");
        confirm.setContentText("Producto: " + seleccionado.getNombre() + "\nEsta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                productoService.eliminarProducto(seleccionado.getId());
                mostrarInfo("Filtro eliminado correctamente");
                actualizarTabla();
            } catch (Exception e) {
                mostrarError("Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void abrirFormularioFiltro(Filtro filtro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NuevoProductoView.fxml"));
            Scene scene = new Scene(loader.load());

            NuevoProductoController controller = loader.getController();
            if (filtro != null) {
                controller.setProductoParaEditar(filtro);
            } else {
                controller.setPreseleccionarFiltro();
            }

            Stage stage = new Stage();
            stage.setTitle(filtro == null ? "Nuevo Filtro" : "Editar Filtro");
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