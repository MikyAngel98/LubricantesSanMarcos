package org.example.Vista.LoginController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.example.Modelo.jpa.Usuario;
import org.example.Servicio.UsuarioService;
import org.example.utils.SessionManager;
import org.example.Vista.MainControllers.MainController;  // ← Importar MainController

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasenia;
    @FXML private Label lblError;

    private final UsuarioService usuarioService = new UsuarioService();
    private MainController mainController;  // ← Referencia al MainController

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        txtUsuario.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                txtContrasenia.requestFocus();
            }
        });
        txtContrasenia.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                iniciarSesion();
            }
        });
    }

    @FXML
    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String contrasenia = txtContrasenia.getText().trim();

        if (usuario.isEmpty() || contrasenia.isEmpty()) {
            mostrarError("Ingrese usuario y contraseña");
            return;
        }

        try {
            Usuario user = usuarioService.autenticar(usuario, contrasenia);

            if (user == null) {
                mostrarError("Usuario o contraseña incorrectos");
                return;
            }

            SessionManager.getInstance().iniciarSesion(user);
            cerrarVentana();

        } catch (Exception e) {
            mostrarError("Error al iniciar sesión: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        System.exit(0);
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();

        // Actualizar MainController después del login
        if (mainController != null) {
            mainController.actualizarEstadoSesion();
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        txtContrasenia.clear();
        txtContrasenia.requestFocus();
    }
}