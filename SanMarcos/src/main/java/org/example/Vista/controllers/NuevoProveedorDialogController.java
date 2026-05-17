package org.example.Vista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.Modelo.jpa.Contacto;
import org.example.Modelo.jpa.Persona;
import org.example.Modelo.jpa.Proveedor;
import org.example.Servicio.ProveedorService;

public class NuevoProveedorDialogController {

    @FXML private TextField txtEmpresa, txtNombre, txtCelular;
    @FXML private Button btnGuardar, btnCancelar;

    private final ProveedorService proveedorService = new ProveedorService();

    @FXML
    public void initialize() {
        btnGuardar.setOnAction(e -> guardarProveedor());
        btnCancelar.setOnAction(e -> cerrarDialogo());
    }

    private void guardarProveedor() {
        String empresa = txtEmpresa.getText().trim();
        String nombre = txtNombre.getText().trim();
        String celular = txtCelular.getText().trim();

        if (empresa.isEmpty()) {
            mostrarAlerta("El nombre de la empresa es obligatorio");
            return;
        }
        if (nombre.isEmpty()) {
            mostrarAlerta("El nombre del contacto es obligatorio");
            return;
        }

        try {
            Contacto contacto = new Contacto();
            contacto.setCelular(celular.isEmpty() ? "S/C" : celular);

            Persona persona = new Persona();
            persona.setNombres(nombre);
            persona.setApellidos("");
            persona.setContacto(contacto);

            Proveedor proveedor = new Proveedor();
            proveedor.setEmpresa(empresa);
            proveedor.setPersona(persona);

            proveedorService.guardar(proveedor);
            mostrarInfo("Proveedor guardado correctamente");
            cerrarDialogo();
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    private void cerrarDialogo() {
        Stage stage = (Stage) txtEmpresa.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}