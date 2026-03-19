package org.example;

import org.example.Config.Conexion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        try {
            Connection conn = Conexion.getInstancia().getConnection();

            if (conn != null) {
                System.out.println("✅ Conexión exitosa");

                // Prueba real con SQL
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT 1");

                if (rs.next()) {
                    System.out.println("✅ Consulta ejecutada correctamente");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

    }
}