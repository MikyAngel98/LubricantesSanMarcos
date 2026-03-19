package org.example.Config;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Conexion instancia;
    private Connection connection;

    //datos de conexion
    private final String URL = "jdbc:sqlserver://localhost:1433;databaseName=LubricanteSanMarcos;encrypt=false";
    private final String USER = "app_lubricantes";
    private final String PASSWORD = "adminLubricantes123!";

    //Constructor privado
    private Conexion(){
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexion a la base de Datos Exitosa");
        } catch (SQLException e) {
            System.out.println("❌ ERROR al conetar: " + e.getMessage());
        }
    }

    //aqui se obtien la instancia
    public static Conexion getInstancia(){
        if (instancia == null)
            instancia = new Conexion();

        return instancia;
    }

    // Obtener la conexion
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("🔄 Reconectado a la base de datos");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al reconectar: " + e.getMessage());
        }
        return connection;
    }
}
