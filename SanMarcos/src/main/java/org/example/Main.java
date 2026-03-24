package org.example;

import org.example.Config.Conexion;
import org.example.Config.JpaUtil;
import org.example.Modelo.jpa.Categoria;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1. Insertar una categoría de prueba
            Categoria cat = new Categoria();
            cat.setNombre("PRUEBA");
            em.persist(cat);

            tx.commit();
            System.out.println("✅ Categoría insertada: ID = " + cat.getId());

            // 2. Listar todas las categorías
            List<Categoria> categorias = em.createQuery("SELECT c FROM Categoria c", Categoria.class)
                    .getResultList();

            System.out.println("\n📋 Categorías en BD:");
            for (Categoria c : categorias) {
                System.out.println("   - " + c.getId() + ": " + c.getNombre());
            }

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            JpaUtil.close();
        }

    }
}