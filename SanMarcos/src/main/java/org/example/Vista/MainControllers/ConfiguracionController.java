package org.example.Vista.MainControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.Modelo.jpa.Usuario;
import org.example.Servicio.UsuarioService;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.util.List;

public class ConfiguracionController {

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombreUsuario;
    @FXML private TableColumn<Usuario, String> colNombreCompleto;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colActivo;
    @FXML private Button btnNuevoUsuario, btnEditarUsuario, btnEliminarUsuario, btnCerrar;
    @FXML private Label lblUsuarioActual;

    private final UsuarioService usuarioService = new UsuarioService();
    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Verificar permisos (solo ADMIN)
        if (!SessionManager.getInstance().isAdmin()) {
            btnNuevoUsuario.setDisable(true);
            btnEditarUsuario.setDisable(true);
            btnEliminarUsuario.setDisable(true);
            lblUsuarioActual.setText("⚠️ Solo administradores pueden gestionar usuarios");
        } else {
            lblUsuarioActual.setText("👤 Usuario: " + SessionManager.getInstance().getNombreCompleto() + " (ADMIN)");
        }

        configurarTabla();
        cargarUsuarios();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colNombreCompleto.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colActivo.setCellValueFactory(cellData -> {
            boolean activo = cellData.getValue().getActivo();
            return new javafx.beans.property.SimpleStringProperty(activo ? "✅ Sí" : "❌ No");
        });

        tablaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaUsuarios.setItems(listaUsuarios);
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.buscarTodos();
            listaUsuarios.setAll(usuarios);
        } catch (Exception e) {
            mostrarError("Error al cargar usuarios: " + e.getMessage());
        }
    }

    @FXML
    private void abrirNuevoUsuario() {
        abrirFormularioUsuario(null);
    }

    @FXML
    private void editarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un usuario para editar");
            return;
        }
        abrirFormularioUsuario(seleccionado);
    }

    @FXML
    private void eliminarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un usuario para eliminar");
            return;
        }

        // No permitir eliminar al usuario actual
        if (seleccionado.getId().equals(SessionManager.getInstance().getUsuarioActual().getId())) {
            mostrarAlerta("No puede eliminar su propio usuario");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Está seguro de eliminar este usuario?");
        confirm.setContentText("Usuario: " + seleccionado.getNombreUsuario() + "\nRol: " + seleccionado.getRol());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                // Desactivar en lugar de eliminar
                seleccionado.setActivo(false);
                usuarioService.actualizar(seleccionado);
                mostrarInfo("Usuario desactivado correctamente");
                cargarUsuarios();
            } catch (Exception e) {
                mostrarError("Error al desactivar usuario: " + e.getMessage());
            }
        }
    }

    private void abrirFormularioUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UsuarioFormView.fxml"));
            Scene scene = new Scene(loader.load());

            UsuarioFormController controller = loader.getController();
            controller.setUsuario(usuario);
            controller.setOnGuardar(() -> cargarUsuarios());

            Stage stage = new Stage();
            stage.setTitle(usuario == null ? "Nuevo Usuario" : "Editar Usuario");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir formulario de usuario");
        }
    }

    @FXML
    private void cerrar() {
        MainController.mostrarBienvenidaStatic();
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
