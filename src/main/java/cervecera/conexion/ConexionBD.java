package cervecera.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Administra la configuración de conexión a SQL Server.
 *
 * Aplica el patrón Singleton para centralizar los datos de conexión y evitar
 * duplicar la URL, usuario y contraseña en diferentes clases del proyecto.
 */
public class ConexionBD {

    private static ConexionBD instancia;

    private final String url;
    private final String usuario;
    private final String contrasena;

    private ConexionBD() {
        this.url = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=CerveceraInventarioDB;"
                + "encrypt=true;"
                + "trustServerCertificate=true;";

        this.usuario = "Mike";
        this.contrasena = "1234";
    }

    /**
     * Obtiene la única instancia de configuración de conexión.
     *
     * @return instancia única de ConexionBD.
     */
    public static ConexionBD obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }

        return instancia;
    }

    /**
     * Crea una conexión nueva hacia SQL Server usando la configuración centralizada.
     *
     * @return conexión abierta a la base de datos.
     * @throws SQLException si SQL Server rechaza la conexión o los datos son incorrectos.
     */
    public Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, contrasena);
    }
}