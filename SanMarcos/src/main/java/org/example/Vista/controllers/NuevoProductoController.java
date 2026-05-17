package org.example.Vista.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.Modelo.jpa.Marca;
import org.example.Modelo.jpa.Presentacion;
import org.example.Modelo.pojo.*;
import org.example.Servicio.MarcaService;
import org.example.Servicio.PresentacionService;
import org.example.Servicio.ProductoService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NuevoProductoController {

    @FXML private ToggleGroup categoriaGrupo, agranelGrupo;
    @FXML private RadioButton rbProducto, rbAceite, rbFiltro, rbFoco;
    @FXML private TextField txtNombre, txtPrecio, txtStock, txtUso, txtCodigo, txtRosca;
    @FXML private TextArea txtDetalle;
    @FXML private ComboBox<Marca> cbMarca;
    @FXML private ComboBox<String> cbViscosidad, cbTipoAceite;
    @FXML private ComboBox<Presentacion> cbPresentacion;
    @FXML private Button btnNuevaMarca;
    @FXML private RadioButton rbAgranelSi, rbAgranelNo;

    @FXML private Label lblViscosidad, lblTipoAceite, lblPresentacion, lblAgranel;
    @FXML private Label lblUso, lblCodigo, lblRosca;

    private final MarcaService marcaService = new MarcaService();
    private final ProductoService productoService = new ProductoService();
    private final PresentacionService presentacionService = new PresentacionService();

    @FXML
    public void initialize() {
        cargarMarcas();
        cargarValoresFijos();
        configurarEventos();
        setCamposProductoBase();
        cargarPresentaciones();
        cbMarca.setEditable(false);
        rbProducto.setSelected(true);
    }

    private void cargarMarcas() {
        cbMarca.getItems().addAll(marcaService.listarTodos());
    }

    private void cargarPresentaciones() {
        List<Presentacion> presentaciones = presentacionService.listarTodos();
        cbPresentacion.getItems().addAll(presentaciones);
    }

    private void cargarValoresFijos() {
        cbViscosidad.setItems(FXCollections.observableArrayList(
                "5W20", "5W30", "10W30", "10W40", "15W40", "SAE 40", "20W50", "25W50", "80W90", "SAE 140", "85W140", "SAE 250"));
        cbTipoAceite.setItems(FXCollections.observableArrayList(
                "RECICLADO", "MINERAL", "SEMI SINTETICO", "SINTETICO", "FULL SINTETICO"));
    }

    private void configurarEventos() {
        categoriaGrupo.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal == rbProducto) setCamposProductoBase();
            else if (newVal == rbAceite) setCamposAceite();
            else if (newVal == rbFiltro) setCamposFiltro();
            else if (newVal == rbFoco) setCamposFoco();
        });

        btnNuevaMarca.setOnAction(e -> abrirDialogoNuevaMarca());
    }

    private void setCamposProductoBase() {
        // Deshabilitar campos específicos
        cbViscosidad.setDisable(true);
        cbTipoAceite.setDisable(true);
        cbPresentacion.setDisable(true);
        rbAgranelSi.setDisable(true);
        rbAgranelNo.setDisable(true);
        txtUso.setDisable(true);
        txtCodigo.setDisable(true);
        txtRosca.setDisable(true);
    }

    private void setCamposAceite() {
        cbViscosidad.setDisable(false);
        cbTipoAceite.setDisable(false);
        cbPresentacion.setDisable(false);
        rbAgranelSi.setDisable(false);
        rbAgranelNo.setDisable(false);
        txtUso.setDisable(false);
        txtCodigo.setDisable(true);
        txtRosca.setDisable(true);
    }

    private void setCamposFiltro() {
        cbViscosidad.setDisable(true);
        cbTipoAceite.setDisable(true);
        cbPresentacion.setDisable(true);
        rbAgranelSi.setDisable(true);
        rbAgranelNo.setDisable(true);
        txtUso.setDisable(false);
        txtCodigo.setDisable(false);
        txtRosca.setDisable(false);
    }

    private void setCamposFoco() {
        cbViscosidad.setDisable(true);
        cbTipoAceite.setDisable(true);
        cbPresentacion.setDisable(true);
        rbAgranelSi.setDisable(true);
        rbAgranelNo.setDisable(true);
        txtUso.setDisable(true);
        txtCodigo.setDisable(false);
        txtRosca.setDisable(true);
    }

    private void abrirDialogoNuevaMarca() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NuevaMarcaDialog.fxml"));
            Pane root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nueva Marca");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            cbMarca.getItems().clear();
            cbMarca.getItems().addAll(marcaService.listarTodos());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarProducto() {
        if (!validarCamposComunes()) return;

        RadioButton selected = (RadioButton) categoriaGrupo.getSelectedToggle();
        if (selected == null) {
            mostrarAlerta("Seleccione una categoría");
            return;
        }

        String categoria = selected.getText().trim(); // "PRODUCTO", "ACEITE", "FILTRO", "FOCO"

        try {
            switch (categoria) {
                case "PRODUCTO":
                    guardarProductoBase();
                    break;
                case "ACEITE":
                    guardarAceite();
                    break;
                case "FILTRO":
                    guardarFiltro();
                    break;
                case "FOCO":
                    guardarFoco();
                    break;
                default:
                    mostrarAlerta("Categoría no válida: " + categoria);
                    return;
            }
            mostrarInfo("Producto guardado correctamente");
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Marca obtenerMarcaSeleccionada() {
        Marca marca = cbMarca.getSelectionModel().getSelectedItem();
        if (marca == null) {
            throw new RuntimeException("Debe seleccionar una marca");
        }
        return marca;
    }

    private void guardarProductoBase() {
        Producto p = new Producto();
        llenarProductoBase(p);
        productoService.guardarProducto(p);
    }

    private void guardarAceite() {
        validarCamposAceite();
        Aceite a = new Aceite();
        llenarProductoBase(a);
        a.setViscosidad(cbViscosidad.getValue());
        a.setTipoAceite(cbTipoAceite.getValue());
        a.setUso(txtUso.getText().trim());
        a.setEsAgranel(rbAgranelSi.isSelected());

        // Obtener presentación seleccionada
        Presentacion presentacion = cbPresentacion.getSelectionModel().getSelectedItem();
        if (presentacion != null) {
            a.setIdPresentacion(presentacion.getId());
        }

        productoService.guardarAceite(a);
    }

    private void guardarFiltro() {
        validarCamposFiltro();
        Filtro f = new Filtro();
        llenarProductoBase(f);
        f.setCodigo(txtCodigo.getText().trim());
        f.setRosca(txtRosca.getText().trim());
        f.setUso(txtUso.getText().trim());
        productoService.guardarFiltro(f);
    }

    private void guardarFoco() {
        validarCamposFoco();
        Foco f = new Foco();
        llenarProductoBase(f);
        f.setCodigo(txtCodigo.getText().trim());
        productoService.guardarFoco(f);
    }

    private void llenarProductoBase(Producto p) {
        p.setNombre(txtNombre.getText().trim());
        p.setPrecio(Float.parseFloat(txtPrecio.getText().trim()));
        p.setStock(Float.parseFloat(txtStock.getText().trim()));
        p.setDetalle(txtDetalle.getText().trim());

        // Obtener marca
        Marca marca = obtenerMarcaSeleccionada();
        p.setIdMarca(marca.getId());

        // Asignar categoría según el tipo seleccionado
        RadioButton selected = (RadioButton) categoriaGrupo.getSelectedToggle();
        String categoria = selected.getText();

        switch (categoria) {
            case "PRODUCTO":
                p.setIdCategoria(1);
                break;
            case "ACEITE":
                p.setIdCategoria(2);
                break;
            case "FILTRO":
                p.setIdCategoria(3);
                break;
            case "FOCO":
                p.setIdCategoria(4);
                break;
            default:
                p.setIdCategoria(1); // PRODUCTO por defecto
                break;
        }
    }
    private boolean validarCamposComunes() {
        if (txtNombre.getText().trim().isEmpty()) { mostrarAlerta("El nombre es obligatorio"); return false; }
        if (cbMarca.getValue() == null) { mostrarAlerta("Seleccione una marca"); return false; }
        return true;
    }

    private void validarCamposAceite() {
        if (cbViscosidad.getValue() == null) throw new RuntimeException("Seleccione viscosidad");
        if (cbTipoAceite.getValue() == null) throw new RuntimeException("Seleccione tipo de aceite");
        if (txtUso.getText().trim().isEmpty()) throw new RuntimeException("El uso es obligatorio");
    }

    private void validarCamposFiltro() {
        if (txtCodigo.getText().trim().isEmpty()) throw new RuntimeException("Código obligatorio");
        if (txtRosca.getText().trim().isEmpty()) throw new RuntimeException("Rosca obligatoria");
        if (txtUso.getText().trim().isEmpty()) throw new RuntimeException("Uso obligatorio");
    }

    private void limpiarFormulario() {
        // Limpiar campos de texto
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtDetalle.clear();
        txtUso.clear();
        txtCodigo.clear();
        txtRosca.clear();

        // Limpiar selecciones de ComboBox
        cbMarca.getSelectionModel().clearSelection();
        cbViscosidad.getSelectionModel().clearSelection();
        cbTipoAceite.getSelectionModel().clearSelection();
        cbPresentacion.getSelectionModel().clearSelection();

        // Resetear RadioButtons
        rbProducto.setSelected(true);
        rbAgranelNo.setSelected(true);

        // Resetear estado de campos
        setCamposProductoBase();

        // Enfocar primer campo
        txtNombre.requestFocus();
    }

    private void validarCamposFoco() {
        if (txtCodigo.getText().trim().isEmpty()) throw new RuntimeException("Código obligatorio");
    }

    @FXML private void cancelar() { limpiarFormulario(); }

    private void mostrarAlerta(String msg) { new Alert(Alert.AlertType.WARNING, msg).showAndWait(); }
    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void mostrarInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
}