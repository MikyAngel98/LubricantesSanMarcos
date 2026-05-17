package org.example.Vista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Modelo.pojo.Filtro;
import org.example.Servicio.ProductoService;

import java.util.List;

public class FiltroFormController {

    // Columnas
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

    private final ProductoService productoService = new ProductoService();
    private ObservableList<Filtro> listaFiltros = FXCollections.observableArrayList();
    private List<Filtro> cacheFiltros;

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
        colRosca.setCellValueFactory(new PropertyValueFactory<>("Rosca"));
        colUso.setCellValueFactory(new PropertyValueFactory<>("Uso"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marcaNombre"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("Detalle"));

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

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                listaFiltros.setAll(cacheFiltros);
            } else {
                List<Filtro> filtrados = cacheFiltros.stream()
                        .filter(f -> f.getNombre().toLowerCase().contains(newVal.toLowerCase()))
                        .toList();
                listaFiltros.setAll(filtrados);
            }
        });
    }

    @FXML
    private void actualizarTabla() {
        cargarDatos();
    }
}
