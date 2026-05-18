package org.example.Vista.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

    // Variable para saber si estamos editando
    private Producto productoEditando;
    private boolean esEdicion = false;

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

    // ==================== METODO PARA EDITAR ====================

    public void setProductoParaEditar(Producto producto) {
        this.productoEditando = producto;
        this.esEdicion = true;

        // Deshabilitar RadioButtons para que no se pueda cambiar el tipo
        rbProducto.setDisable(true);
        rbAceite.setDisable(true);
        rbFiltro.setDisable(true);
        rbFoco.setDisable(true);

        cargarDatosProducto();
    }
    public void setPreseleccionarFoco() {
        this.esEdicion = false;
        this.productoEditando = null;
        rbFoco.setSelected(true);
        rbProducto.setDisable(false);
        rbAceite.setDisable(false);
        rbFiltro.setDisable(false);
        rbFoco.setDisable(false);
        setCamposFoco();
    }

    public void setPreseleccionarAceite() {
        this.esEdicion = false;
        this.productoEditando = null;
        rbAceite.setSelected(true);
        setCamposAceite();
    }

    public void setPreseleccionarFiltro() {
        this.esEdicion = false;
        this.productoEditando = null;
        rbFiltro.setSelected(true);
        setCamposFiltro();
    }

    private void cargarDatosProducto() {
        if (productoEditando == null) return;

        // Cargar datos comunes
        txtNombre.setText(productoEditando.getNombre());
        txtPrecio.setText(String.valueOf(productoEditando.getPrecio()));
        txtStock.setText(String.valueOf(productoEditando.getStock()));
        txtDetalle.setText(productoEditando.getDetalle());

        // Seleccionar marca
        for (Marca m : cbMarca.getItems()) {
            if (m.getId() == productoEditando.getIdMarca()) {
                cbMarca.getSelectionModel().select(m);
                break;
            }
        }

        // Identificar tipo de producto y cargar datos específicos
        if (productoEditando instanceof Aceite) {
            rbAceite.setSelected(true);
            Aceite a = (Aceite) productoEditando;
            cbViscosidad.setValue(a.getViscosidad());
            cbTipoAceite.setValue(a.getTipoAceite());
            txtUso.setText(a.getUso());
            rbAgranelSi.setSelected(a.isEsAgranel());
            rbAgranelNo.setSelected(!a.isEsAgranel());

            // Seleccionar presentación
            for (Presentacion p : cbPresentacion.getItems()) {
                if (p.getId() == a.getIdPresentacion()) {
                    cbPresentacion.getSelectionModel().select(p);
                    break;
                }
            }
        }
        else if (productoEditando instanceof Filtro) {
            rbFiltro.setSelected(true);
            Filtro f = (Filtro) productoEditando;
            txtCodigo.setText(f.getCodigo());
            txtRosca.setText(f.getRosca());
            txtUso.setText(f.getUso());
        }
        else if (productoEditando instanceof Foco) {
            rbFoco.setSelected(true);
            Foco f = (Foco) productoEditando;
            txtCodigo.setText(f.getCodigo());
        }
        else {
            rbProducto.setSelected(true);
        }

        // Aplicar visibilidad de campos según el tipo
        RadioButton selected = (RadioButton) categoriaGrupo.getSelectedToggle();
        if (selected == rbAceite) setCamposAceite();
        else if (selected == rbFiltro) setCamposFiltro();
        else if (selected == rbFoco) setCamposFoco();
        else setCamposProductoBase();
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

        String categoria = selected.getText().trim();

        try {
            if (esEdicion) {
                // Si estamos editando, actualizar
                actualizarProducto(categoria);
            } else {
                // Si es nuevo, guardar
                switch (categoria) {
                    case "PRODUCTO": guardarProductoBase(); break;
                    case "ACEITE": guardarAceite(); break;
                    case "FILTRO": guardarFiltro(); break;
                    case "FOCO": guardarFoco(); break;
                    default: mostrarAlerta("Categoría no válida"); return;
                }
            }
            mostrarInfo(esEdicion ? "Producto actualizado correctamente" : "Producto guardado correctamente");

            if (!esEdicion) {
                limpiarFormulario();
            } else {
                cerrarVentana();
            }
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarProducto(String categoria) {
        switch (categoria) {
            case "PRODUCTO":
                actualizarProductoBase();
                break;
            case "ACEITE":
                actualizarAceite();
                break;
            case "FILTRO":
                actualizarFiltro();
                break;
            case "FOCO":
                actualizarFoco();
                break;
        }
    }

    private void actualizarProductoBase() {
        productoEditando.setNombre(txtNombre.getText().trim());
        productoEditando.setPrecio(Float.parseFloat(txtPrecio.getText().trim()));
        productoEditando.setDetalle(txtDetalle.getText().trim());
        productoEditando.setIdMarca(obtenerMarcaSeleccionada().getId());
        // El stock NO se actualiza aquí (solo con compras/ventas)

        productoService.actualizarProducto(productoEditando);
    }

    private void actualizarAceite() {
        actualizarProductoBase();
        Aceite a = (Aceite) productoEditando;
        a.setViscosidad(cbViscosidad.getValue());
        a.setTipoAceite(cbTipoAceite.getValue());
        a.setUso(txtUso.getText().trim());
        a.setEsAgranel(rbAgranelSi.isSelected());
        if (cbPresentacion.getValue() != null) {
            a.setIdPresentacion(cbPresentacion.getValue().getId());
        }
        productoService.actualizarAceite(a);
    }

    private void actualizarFiltro() {
        actualizarProductoBase();
        Filtro f = (Filtro) productoEditando;
        f.setCodigo(txtCodigo.getText().trim());
        f.setRosca(txtRosca.getText().trim());
        f.setUso(txtUso.getText().trim());
        productoService.actualizarFiltro(f);
    }

    private void actualizarFoco() {
        actualizarProductoBase();
        Foco f = (Foco) productoEditando;
        f.setCodigo(txtCodigo.getText().trim());
        productoService.actualizarFoco(f);
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

        Marca marca = obtenerMarcaSeleccionada();
        p.setIdMarca(marca.getId());

        RadioButton selected = (RadioButton) categoriaGrupo.getSelectedToggle();
        String categoria = selected.getText();

        switch (categoria) {
            case "PRODUCTO": p.setIdCategoria(1); break;
            case "ACEITE": p.setIdCategoria(2); break;
            case "FILTRO": p.setIdCategoria(3); break;
            case "FOCO": p.setIdCategoria(4); break;
            default: p.setIdCategoria(1); break;
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

    private void validarCamposFoco() {
        if (txtCodigo.getText().trim().isEmpty()) throw new RuntimeException("Código obligatorio");
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtDetalle.clear();
        txtUso.clear();
        txtCodigo.clear();
        txtRosca.clear();

        cbMarca.getSelectionModel().clearSelection();
        cbViscosidad.getSelectionModel().clearSelection();
        cbTipoAceite.getSelectionModel().clearSelection();
        cbPresentacion.getSelectionModel().clearSelection();

        rbProducto.setSelected(true);
        rbAgranelNo.setSelected(true);

        setCamposProductoBase();
        txtNombre.requestFocus();

        esEdicion = false;
        productoEditando = null;
    }

    @FXML private void cancelar() { cerrarVentana(); }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String msg) { new Alert(Alert.AlertType.WARNING, msg).showAndWait(); }
    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void mostrarInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
}