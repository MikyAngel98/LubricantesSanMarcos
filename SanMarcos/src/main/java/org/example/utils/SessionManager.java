package org.example.utils;

import org.example.Modelo.jpa.Usuario;

public class SessionManager {

    private static SessionManager instance;
    private Usuario usuarioActual;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean isLoggedIn() {
        return usuarioActual != null;
    }

    public boolean isAdmin() {
        return usuarioActual != null && "ADMIN".equals(usuarioActual.getRol());
    }

    public boolean isVendedor() {
        return usuarioActual != null && "VENDEDOR".equals(usuarioActual.getRol());
    }

    public String getNombreUsuario() {
        return usuarioActual != null ? usuarioActual.getNombreUsuario() : "";
    }

    public String getNombreCompleto() {
        return usuarioActual != null ? usuarioActual.getNombreCompleto() : "";
    }
}
