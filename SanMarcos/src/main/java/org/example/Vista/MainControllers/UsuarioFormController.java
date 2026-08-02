package org.example.Vista.MainControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Modelo.jpa.Usuario;
import org.example.Servicio.UsuarioService;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioFormController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNombreUsuario, txtNombreCompleto;
    @FXML private PasswordField txtContrasenia, txtConfirmarContrasenia;
    @FXML private ComboBox<String> cbRol;
    @FXML private CheckBox chkActivo;
    @FXML private Button btnGuardar, btnCancelar;

    private final UsuarioService usuarioService = new UsuarioService();
    private Usuario usuarioEditando;
    private Runnable onGuardar;

    public void setUsuario(Usuario usuario) {
        this.usuarioEditando = usuario;
        if (usuario != null) {
            lblTitulo.setText("✏️ EDITAR USUARIO");
            txtNombreUsuario.setText(usuario.getNombreUsuario());
            txtNombreUsuario.setDisable(true);
            txtNombreCompleto.setText(usuario.getNombreCompleto());
            cbRol.setValue(usuario.getRol());
            chkActivo.setSelected(usuario.getActivo());
            txtContrasenia.setPromptText("Dejar en blanco para mantener");
            txtConfirmarContrasenia.setPromptText("Dejar en blanco para mantener");
        } else {
            lblTitulo.setText("👤 NUEVO USUARIO");
            cbRol.setValue("VENDEDOR");
        }
    }

    public void setOnGuardar(Runnable onGuardar) {
        this.onGuardar = onGuardar;
    }

    @FXML
    private void guardar() {
        String nombreUsuario = txtNombreUsuario.getText().trim();
        String contrasenia = txtContrasenia.getText().trim();
        String confirmar = txtConfirmarContrasenia.getText().trim();
        String nombreCompleto = txtNombreCompleto.getText().trim();
        String rol = cbRol.getValue();
        boolean activo = chkActivo.isSelected();

        // Validaciones
        if (nombreUsuario.isEmpty()) {
            mostrarAlerta("El nombre de usuario es obligatorio");
            return;
        }

        if (nombreCompleto.isEmpty()) {
            mostrarAlerta("El nombre completo es obligatorio");
            return;
        }

        if (rol == null || rol.isEmpty()) {
            mostrarAlerta("Seleccione un rol");
            return;
        }

        try {
            if (usuarioEditando == null) {
                // Nuevo usuario
                if (contrasenia.isEmpty()) {
                    mostrarAlerta("La contraseña es obligatoria");
                    return;
                }
                if (!contrasenia.equals(confirmar)) {
                    mostrarAlerta("Las contraseñas no coinciden");
                    return;
                }
                if (contrasenia.length() < 6) {
                    mostrarAlerta("La contraseña debe tener al menos 6 caracteres");
                    return;
                }

                // Crear usuario con contraseña encriptada
                usuarioService.crearUsuario(nombreUsuario, contrasenia, nombreCompleto, rol);

            } else {
                // Editar usuario
                boolean llenoContrasenia = !contrasenia.isEmpty();
                boolean llenoConfirmar = !confirmar.isEmpty();

                if (llenoContrasenia != llenoConfirmar) {
                    mostrarAlerta("Debe ingresar ambas contraseñas para cambiarla");
                    return;
                }

                if (llenoContrasenia) {
                    if (!contrasenia.equals(confirmar)) {
                        mostrarAlerta("Las contraseñas no coinciden");
                        return;
                    }
                    if (contrasenia.length() < 6) {
                        mostrarAlerta("La contraseña debe tener al menos 6 caracteres");
                        return;
                    }
                    // Encriptar nueva contraseña
                    usuarioEditando.setContrasenia(BCrypt.hashpw(contrasenia, BCrypt.gensalt()));
                }

                usuarioEditando.setNombreCompleto(nombreCompleto);
                usuarioEditando.setRol(rol);
                usuarioEditando.setActivo(activo);
                usuarioService.actualizar(usuarioEditando);
            }

            mostrarInfo("Usuario guardado correctamente");

            if (onGuardar != null) {
                onGuardar.run();
            }
            cerrar();

        } catch (Exception e) {
            mostrarError("Error al guardar usuario: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
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