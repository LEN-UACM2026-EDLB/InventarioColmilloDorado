package cervecera.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public static ConexionBD obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }

        return instancia;
    }

    public Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, contrasena);
    }
}