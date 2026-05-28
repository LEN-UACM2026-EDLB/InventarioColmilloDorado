package cervecera.dao;

import cervecera.conexion.ConexionBD;
import cervecera.modelo.Insumo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO encargada de las operaciones de acceso a datos para los insumos.
 *
 * Permite consultar, insertar, actualizar y eliminar insumos desde SQL Server sin
 * mezclar instrucciones SQL dentro de las pantallas de Swing.
 */
public class InsumoDAO {

    /**
     * Consulta todos los insumos registrados.
     *
     * @return lista de insumos ordenados por tipo y nombre.
     * @throws SQLException si ocurre un error al consultar la base de datos.
     */
    public List<Insumo> listar() throws SQLException {
        List<Insumo> insumos = new ArrayList<>();

        String sql = """
                SELECT id, nombre, tipo, cantidad_disponible, unidad_medida
                FROM Insumos
                ORDER BY tipo, nombre
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()
        ) {
            while (resultado.next()) {
                insumos.add(construirInsumo(resultado));
            }
        }

        return insumos;
    }

    /**
     * Busca un insumo por su identificador.
     *
     * @param id identificador del insumo.
     * @return insumo encontrado o null si no existe.
     * @throws SQLException si ocurre un error al consultar la base de datos.
     */
    public Insumo obtenerPorId(int id) throws SQLException {
        String sql = """
                SELECT id, nombre, tipo, cantidad_disponible, unidad_medida
                FROM Insumos
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
                    return construirInsumo(resultado);
                }
            }
        }

        return null;
    }

    /**
     * Inserta un nuevo insumo en la base de datos.
     *
     * @param insumo objeto con nombre, tipo, cantidad disponible y unidad de medida.
     * @throws SQLException si ocurre un error durante la inserción.
     */
    public void insertar(Insumo insumo) throws SQLException {
        String sql = """
                INSERT INTO Insumos (nombre, tipo, cantidad_disponible, unidad_medida)
                VALUES (?, ?, ?, ?)
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setString(1, insumo.getNombre());
            comando.setString(2, insumo.getTipo());
            comando.setBigDecimal(3, insumo.getCantidadDisponible());
            comando.setString(4, insumo.getUnidadMedida());

            comando.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de un insumo existente.
     *
     * @param insumo objeto con el identificador y los nuevos valores.
     * @throws SQLException si ocurre un error durante la actualización.
     */
    public void actualizar(Insumo insumo) throws SQLException {
        String sql = """
                UPDATE Insumos
                SET nombre = ?,
                    tipo = ?,
                    cantidad_disponible = ?,
                    unidad_medida = ?
                WHERE id = ?
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setString(1, insumo.getNombre());
            comando.setString(2, insumo.getTipo());
            comando.setBigDecimal(3, insumo.getCantidadDisponible());
            comando.setString(4, insumo.getUnidadMedida());
            comando.setInt(5, insumo.getId());

            comando.executeUpdate();
        }
    }

    /**
     * Elimina físicamente un insumo.
     *
     * Debe usarse con cuidado si el insumo tiene movimientos relacionados.
     *
     * @param id identificador del insumo.
     * @throws SQLException si existe una restricción de llave foránea u otro error SQL.
     */
    public void eliminar(int id) throws SQLException {
        String sql = """
                DELETE FROM Insumos
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
     * Convierte la fila actual del ResultSet en un objeto Insumo.
     */
    private Insumo construirInsumo(ResultSet resultado) throws SQLException {
        return new Insumo(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("tipo"),
                resultado.getBigDecimal("cantidad_disponible"),
                resultado.getString("unidad_medida")
        );
    }
}