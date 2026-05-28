package cervecera.dao;

import cervecera.conexion.ConexionBD;
import cervecera.modelo.Estilo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO encargada de las operaciones de acceso a datos para los estilos.
 *
 * Separa las consultas SQL de la interfaz gráfica y del modelo, manteniendo una
 * responsabilidad clara: leer y modificar registros de la tabla Estilos.
 */
public class EstiloDAO {

    /**
     * Consulta los estilos activos ordenados por nombre.
     *
     * @return lista de estilos disponibles para asociar a productos.
     * @throws SQLException si ocurre un error al consultar la base de datos.
     */
    public List<Estilo> listarActivos() throws SQLException {
        List<Estilo> estilos = new ArrayList<>();

        String sql = """
                SELECT id, nombre, descripcion, activo
                FROM Estilos
                WHERE activo = 1
                ORDER BY nombre
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()
        ) {
            while (resultado.next()) {
                estilos.add(construirEstilo(resultado));
            }
        }

        return estilos;
    }

    /**
     * Busca un estilo por su identificador.
     *
     * @param id identificador del estilo.
     * @return estilo encontrado o null si no existe.
     * @throws SQLException si ocurre un error al consultar la base de datos.
     */
    public Estilo obtenerPorId(int id) throws SQLException {
        String sql = """
                SELECT id, nombre, descripcion, activo
                FROM Estilos
                WHERE id = ?
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setInt(1, id);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return construirEstilo(resultado);
                }
            }
        }

        return null;
    }

    /**
     * Inserta un nuevo estilo en la base de datos.
     *
     * @param estilo objeto con los datos que se van a guardar.
     * @throws SQLException si ocurre un error durante la inserción.
     */
    public void insertar(Estilo estilo) throws SQLException {
        String sql = """
                INSERT INTO Estilos (nombre, descripcion, activo)
                VALUES (?, ?, ?)
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setString(1, estilo.getNombre());
            comando.setString(2, estilo.getDescripcion());
            comando.setBoolean(3, estilo.isActivo());

            comando.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de un estilo existente.
     *
     * @param estilo objeto con el identificador y los nuevos valores.
     * @throws SQLException si ocurre un error durante la actualización.
     */
    public void actualizar(Estilo estilo) throws SQLException {
        String sql = """
                UPDATE Estilos
                SET nombre = ?,
                    descripcion = ?,
                    activo = ?
                WHERE id = ?
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setString(1, estilo.getNombre());
            comando.setString(2, estilo.getDescripcion());
            comando.setBoolean(3, estilo.isActivo());
            comando.setInt(4, estilo.getId());

            comando.executeUpdate();
        }
    }

    /**
     * Realiza una baja lógica del registro indicado.
     *
     * @param id identificador del registro que se va a desactivar.
     * @throws SQLException si ocurre un error durante la actualización.
     */
    public void desactivar(int id) throws SQLException {
        String sql = """
                UPDATE Estilos
                SET activo = 0
                WHERE id = ?
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Estilo.
     */
    private Estilo construirEstilo(ResultSet resultado) throws SQLException {
        return new Estilo(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("descripcion"),
                resultado.getBoolean("activo")
        );
    }
}