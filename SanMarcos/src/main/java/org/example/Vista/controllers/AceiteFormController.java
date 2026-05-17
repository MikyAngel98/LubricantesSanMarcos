package org.example.Vista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Modelo.pojo.Aceite;
import org.example.Servicio.ProductoService;

import java.util.List;

public class AceiteFormController {

    // Columnas
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
}