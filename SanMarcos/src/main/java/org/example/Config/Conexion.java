package org.example.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Conexion instancia;
    private Connection connection;

    private final String URL = "jdbc:sqlserver://localhost:1433;databaseName=LubricanteSanMarcos;encrypt=true;trustServerCertificate=true";
    private final String USER = "app_lubricantes";
    private final String PASSWORD = "adminLubricantes123!";

    private Conexion() {
        // Constructor vacío, no crear conexión aquí
    }

    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConnection() {
        try {
            // Siempre crear una conexión nueva
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión establecida");
            return connection;
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar: " + e.getMessage());
            return null;
        }
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}
