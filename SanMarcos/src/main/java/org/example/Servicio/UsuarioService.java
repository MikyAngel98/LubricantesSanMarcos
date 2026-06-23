package org.example.Servicio;

import org.example.DAO.jpa.UsuarioDAO;
import org.example.Modelo.jpa.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ==================== AUTENTICACIÓN ====================

    public Usuario autenticar(String nombreUsuario, String contrasenia) {
        Optional<Usuario> usuarioOpt = usuarioDAO.findByNombreUsuario(nombreUsuario);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar que el usuario esté activo
        if (!usuario.getActivo()) {
            return null;
        }

        // Verificar contraseña con BCrypt
        if (BCrypt.checkpw(contrasenia, usuario.getContrasenia())) {
            return usuario;
        }

        return null;
    }

    // ==================== CRUD ====================

    public Usuario crearUsuario(String nombreUsuario, String contrasenia, String nombreCompleto, String rol) {
        if (usuarioDAO.existeUsuario(nombreUsuario)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setContrasenia(BCrypt.hashpw(contrasenia, BCrypt.gensalt()));
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());

        return usuarioDAO.save(usuario);
    }

    public Optional<Usuario> buscarPorId(int id) {
        return usuarioDAO.findById(id);
    }

    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioDAO.findByNombreUsuario(nombreUsuario);
    }

    public boolean existeUsuario(String nombreUsuario) {
        return usuarioDAO.existeUsuario(nombreUsuario);
    }

    // ==================== CAMBIAR CONTRASEÑA ====================

    public boolean cambiarContrasenia(String nombreUsuario, String contraseniaActual, String contraseniaNueva) {
        Optional<Usuario> usuarioOpt = usuarioDAO.findByNombreUsuario(nombreUsuario);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();

        if (!BCrypt.checkpw(contraseniaActual, usuario.getContrasenia())) {
            return false;
        }

        usuario.setContrasenia(BCrypt.hashpw(contraseniaNueva, BCrypt.gensalt()));
        usuarioDAO.update(usuario);
        return true;
    }

    public List<Usuario> buscarTodos() {
        return usuarioDAO.findAll();
    }

    public Usuario actualizar(Usuario usuario) {
        return usuarioDAO.update(usuario);
    }
}
