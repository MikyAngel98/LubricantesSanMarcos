package org.example.Vista.MainControllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.utils.SessionManager;
import org.example.Vista.LoginController.LoginController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainController {

    @FXML private StackPane panelCentral;
    @FXML private Label lblFecha;
    @FXML private Label lblUsuario;
    @FXML private Button btnLogin;
    @FXML private VBox menuLateral;

    private static StackPane panelCentralStatic;

    @FXML
    public void initialize() {
        iniciarReloj();
        actualizarEstadoSesion();
        panelCentralStatic = panelCentral;
        mostrarMensajeSesionCerrada();
    }

    // ==================== MÉTODOS DEL MENÚ ====================

    @FXML
    private void abrirInventario() { cargarPanel("/fxml/InventarioView.fxml"); }

    @FXML
    private void abrirAceites() { cargarPanel("/fxml/AceiteFormView.fxml"); }

    @FXML
    private void abrirFiltros() { cargarPanel("/fxml/FiltroFormView.fxml"); }

    @FXML
    private void abrirFocos() { cargarPanel("/fxml/FocosView.fxml"); }

    @FXML
    private void abrirVentas() { cargarPanel("/fxml/VentasView.fxml"); }

    @FXML
    private void abrirCompras() { cargarPanel("/fxml/ComprasView.fxml"); }

    @FXML
    private void abrirNuevoProducto() { cargarPanel("/fxml/NuevoProductoView.fxml"); }

    @FXML
    private void abrirReportes() { cargarPanel("/fxml/ReportesView.fxml"); }

    @FXML
    private void abrirConfiguracion() {
            // Verificar que haya sesión
            if (!SessionManager.getInstance().isLoggedIn()) {
                mostrarMensajeTemporal("⚠️ Debe iniciar sesión primero");
                return;
            }

            // Verificar que sea ADMIN
            if (!SessionManager.getInstance().isAdmin()) {
                mostrarMensajeTemporal("⚠️ Solo administradores pueden acceder");
                return;
            }

            cargarPanel("/fxml/ConfiguracionView.fxml");
        }

    // ==================== LOGIN / LOGOUT ====================

    @FXML
    private void abrirLogin() {
        try {
            var resource = getClass().getResource("/fxml/LoginView.fxml");
            if (resource == null) {
                mostrarMensajeTemporal("No se encontró el archivo LoginView.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());

            LoginController loginController = loader.getController();
            loginController.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Iniciar Sesión");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            // Actualizar estado después del login
            actualizarEstadoSesion();

            // Mostrar bienvenida si inició sesión
            if (SessionManager.getInstance().isLoggedIn()) {
                mostrarBienvenidaStatic();
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensajeTemporal("Error al abrir login");
        } catch (NullPointerException e) {
            e.printStackTrace();
            mostrarMensajeTemporal("No se encontró el archivo LoginView.fxml");
        }
    }

    @FXML
    private void cerrarSesion() {
        SessionManager.getInstance().cerrarSesion();
        actualizarEstadoSesion();
        limpiarPanelCentral();
        mostrarMensajeSesionCerrada();
    }

    private void limpiarPanelCentral() {
        if (panelCentral != null) {
            panelCentral.getChildren().clear();
        }
    }

    // ==================== ESTADO DE SESIÓN ====================

    public void actualizarEstadoSesion() {
        boolean logueado = SessionManager.getInstance().isLoggedIn();

        if (logueado) {
            lblUsuario.setText("👤 " + SessionManager.getInstance().getNombreCompleto());
            btnLogin.setText("🔒 CERRAR SESIÓN");
            btnLogin.setOnAction(e -> cerrarSesion());
        } else {
            lblUsuario.setText("👤 No autenticado");
            btnLogin.setText("🔓 INICIAR SESIÓN");
            btnLogin.setOnAction(e -> abrirLogin());
        }

        // Habilitar/deshabilitar botones según rol
        if (menuLateral != null) {
            boolean esAdmin = SessionManager.getInstance().isAdmin();
            boolean esVendedor = SessionManager.getInstance().isVendedor();
            boolean logueado2 = SessionManager.getInstance().isLoggedIn();

            for (Node node : menuLateral.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    String texto = btn.getText();

                    if (!logueado2) {
                        btn.setDisable(true);
                    } else if (esAdmin) {
                        btn.setDisable(false);
                    } else if (esVendedor) {
                        // VENDEDOR: VENTAS, INVENTARIO, ACEITES, FILTROS, FOCOS habilitados
                        if (texto.contains("🛒 VENTAS") ||
                                texto.contains("📦 INVENTARIO") ||
                                texto.contains("🛢️ ACEITES") ||
                                texto.contains("🔧 FILTROS") ||
                                texto.contains("💡 FOCOS")) {
                            btn.setDisable(false);
                        } else {
                            btn.setDisable(true);
                        }
                    }
                }
            }
        }
    }

    private void mostrarMensajeSesionCerrada() {
        try {
            // 1. Cargar imagen de fondo
            Image fondo = new Image(getClass().getResourceAsStream("/images/Fondo App.png"));

            BackgroundImage bgImage = new BackgroundImage(
                    fondo,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            BackgroundSize.AUTO,
                            BackgroundSize.AUTO,
                            false,
                            false,
                            true,
                            true
                    )
            );

            // 2. Crear panel con fondo
            StackPane panel = new StackPane();
            panel.setBackground(new Background(bgImage));

            // 3. Agregar mensaje encima de la imagen
            Label mensaje = new Label("🔒 Sesión cerrada\nPor favor, inicie sesión para continuar");
            mensaje.setStyle(
                    "-fx-font-size: 24px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-text-fill: #2c3e50; " +
                            "-fx-background-color: rgba(255, 255, 255, 0.8); " +
                            "-fx-padding: 20 40; " +
                            "-fx-border-radius: 10; " +
                            "-fx-background-radius: 10;"
            );

            // Centrar el mensaje
            StackPane.setAlignment(mensaje, Pos.TOP_CENTER);

            // 4. Agregar todo al panel central
            panel.getChildren().add(mensaje);
            panelCentral.getChildren().setAll(panel);

        } catch (Exception e) {
            // Si falla la imagen, mostrar solo el mensaje
            Label mensaje = new Label("🔒 Sesión cerrada\nPor favor, inicie sesión para continuar");
            mensaje.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d; -fx-alignment: center;");
            panelCentral.getChildren().setAll(mensaje);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    public static void mostrarBienvenidaStatic() {
        if (panelCentralStatic == null) return;

        try {
            Image fondo = new Image(MainController.class.getResourceAsStream("/images/Fondo App.png"));

            BackgroundImage bgImage = new BackgroundImage(
                    fondo,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            BackgroundSize.AUTO,
                            BackgroundSize.AUTO,
                            false,
                            false,
                            true,
                            true
                    )
            );

            StackPane panel = new StackPane();
            panel.setBackground(new Background(bgImage));
            panelCentralStatic.getChildren().setAll(panel);

        } catch (Exception e) {
            Label label = new Label("Bienvenido a Lubricantes San Marcos");
            label.setStyle("-fx-font-size: 18px; -fx-text-fill: #7f8c8d;");
            panelCentralStatic.getChildren().setAll(label);
        }
    }

    private void cargarPanel(String fxmlPath) {
        try {
            var resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                mostrarMensajeTemporal("❌ No se encontró la vista: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Node panel = loader.load();
            panelCentral.getChildren().setAll(panel);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensajeTemporal("❌ Error al cargar: " + fxmlPath);
        }
    }

    private void iniciarReloj() {
        Timeline reloj = new Timeline(new KeyFrame(Duration.seconds(60), e -> actualizarFechaHora()));
        reloj.setCycleCount(Animation.INDEFINITE);
        reloj.play();
        actualizarFechaHora();
    }

    private void actualizarFechaHora() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.US);
        lblFecha.setText(ahora.format(formatter));
    }

    private void mostrarMensajeTemporal(String mensaje) {
        panelCentral.getChildren().clear();
        Label label = new Label(mensaje);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50; -fx-alignment: center;");
        panelCentral.getChildren().add(label);
    }
}