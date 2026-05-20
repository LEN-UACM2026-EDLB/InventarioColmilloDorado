import com.colmillodorado.config.ConexionDB;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando prueba de conexión para Colmillo Dorado...");

        // Invocamos el patrón Singleton
        Connection cn = ConexionDB.getInstancia();

        if (cn != null) {
            System.out.println("¡ÉXITO! El sistema está conectado a SQL Server.");
        } else {
            System.out.println("❌ FALLA: No se pudo establecer la conexión.");
        }
    }
}