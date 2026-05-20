package com.colmillodorado.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // 1. Atributo estático privado que almacenará la única instancia de la conexión
    private static Connection conexion = null;

    // 2. Parámetros de configuración para Microsoft SQL Server (Instancia: HP\MYSQL)
    // Usamos Autenticación de Windows integrada (integratedSecurity=true)
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=colmillo_dorado_db;user=sa;password=1;trustServerCertificate=true;";

    // 3. Constructor PRIVADO: Evita que otros compañeros usen el operador 'new' fuera de esta clase
    private ConexionDB() {}

    // 4. Método público estático que controla el acceso a la instancia (Punto de acceso global)
    public static Connection getInstancia() {
        try {
            // Si no existe una conexión o fue cerrada por el servidor, la creamos/abrimos una sola vez
            if (conexion == null || conexion.isClosed()) {
                synchronized (ConexionDB.class) { // Hilo seguro (Thread-safe)
                    if (conexion == null || conexion.isClosed()) {
                        // Cargar dinámicamente el driver JDBC de Microsoft SQL Server
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        conexion = DriverManager.getConnection(URL);
                        System.out.println("Cervecería Colmillo Dorado: ¡Conexión exitosa a SQL Server!");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el Driver JDBC de SQL Server. Revisa tu pom.xml");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al intentar conectar a la base de datos SQL Server");
            e.printStackTrace();
        }
        return conexion;
    }
}