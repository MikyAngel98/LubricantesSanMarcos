package org.example.Vista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.Modelo.jpa.Marca;
import org.example.Servicio.MarcaService;

public class NuevaMarcaDialogController {

    @FXML private TextField txtNombreMarca;
    @FXML private Button btnGuardarMarca;
    @FXML private Button btnCancelarMarca;

    private final MarcaService marcaService = new MarcaService();

    @FXML
    public void initialize() {
        // Permitir guardar con Enter
        txtNombreMarca.setOnAction(e -> guardarMarca());

        btnGuardarMarca.setOnAction(e -> guardarMarca());
        btnCancelarMarca.setOnAction(e -> cerrarDialogo());
    }

    private void guardarMarca() {
        String nombre = txtNombreMarca.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("El nombre de la marca es obligatorio");
            txtNombreMarca.requestFocus();
            return;
        }

        try {
            // Verificar si ya existe
            if (marcaService.existePorNombre(nombre)) {
                mostrarAlerta("Ya existe una marca con el nombre: " + nombre);
                txtNombreMarca.requestFocus();
                return;
            }

            Marca marca = new Marca();
            marca.setNombre(nombre);
            marcaService.guardar(marca);

            mostrarInfo("Marca guardada correctamente");
            cerrarDialogo();
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    private void cerrarDialogo() {
        Stage stage = (Stage) txtNombreMarca.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}