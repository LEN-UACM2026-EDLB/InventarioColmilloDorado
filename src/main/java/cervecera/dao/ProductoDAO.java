package cervecera.dao;

import cervecera.conexion.ConexionBD;
import cervecera.modelo.Estilo;
import cervecera.modelo.Producto;
import cervecera.modelo.ExistenciaProducto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO encargada de las operaciones de acceso a datos para los productos.
 *
 * Construye objetos Producto con su Estilo asociado y consulta las existencias
 * calculadas desde las vistas de SQL Server.
 */
public class ProductoDAO {

    /**
     * Consulta los productos activos junto con su estilo asociado.
     *
     * @return lista de productos disponibles en el sistema.
     * @throws SQLException si ocurre un error al consultar la base de datos.
     */
    public List<Producto> listarActivos() throws SQLException {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT 
                    P.id,
                    P.nombre,
                    P.descripcion,
                    P.precio,
                    P.activo,
                    E.id AS estilo_id,
                    E.nombre AS estilo_nombre,
                    E.descripcion AS estilo_descripcion,
                    E.activo AS estilo_activo
                FROM Productos P
                INNER JOIN Estilos E ON P.estilo_id = E.id
                WHERE P.activo = 1
                ORDER BY P.nombre
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()
        ) {
            while (resultado.next()) {
                productos.add(construirProducto(resultado));
            }
        }

        return productos;
    }

    /**
     * Busca un producto por su identificador.
     *
     * @param id identificador del producto.
     * @return producto encontrado o null si no existe.
     * @throws SQLException si ocurre un error al consultar la base de datos.
     */
    public Producto obtenerPorId(int id) throws SQLException {
        String sql = """
                SELECT 
                    P.id,
                    P.nombre,
                    P.descripcion,
                    P.precio,
                    P.activo,
                    E.id AS estilo_id,
                    E.nombre AS estilo_nombre,
                    E.descripcion AS estilo_descripcion,
                    E.activo AS estilo_activo
                FROM Productos P
                INNER JOIN Estilos E ON P.estilo_id = E.id
                WHERE P.id = ?
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setInt(1, id);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return construirProducto(resultado);
                }
            }
        }

        return null;
    }

    /**
     * Inserta un nuevo producto terminado.
     *
     * @param producto objeto con nombre, descripción, precio y estilo.
     * @throws SQLException si ocurre un error durante la inserción.
     */
    public void insertar(Producto producto) throws SQLException {
        String sql = """
                INSERT INTO Productos (nombre, descripcion, precio, estilo_id, activo)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setString(1, producto.getNombre());
            comando.setString(2, producto.getDescripcion());
            comando.setBigDecimal(3, producto.getPrecio());
            comando.setInt(4, producto.getEstilo().getId());
            comando.setBoolean(5, producto.isActivo());

            comando.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param producto objeto con el identificador y los nuevos valores.
     * @throws SQLException si ocurre un error durante la actualización.
     */
    public void actualizar(Producto producto) throws SQLException {
        String sql = """
                UPDATE Productos
                SET nombre = ?,
                    descripcion = ?,
                    precio = ?,
                    estilo_id = ?,
                    activo = ?
                WHERE id = ?
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setString(1, producto.getNombre());
            comando.setString(2, producto.getDescripcion());
            comando.setBigDecimal(3, producto.getPrecio());
            comando.setInt(4, producto.getEstilo().getId());
            comando.setBoolean(5, producto.isActivo());
            comando.setInt(6, producto.getId());

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
                UPDATE Productos
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
     * Obtiene la existencia actual de un producto usando la vista de SQL Server.
     *
     * @param productoId identificador del producto.
     * @return existencia calculada; cero si no se encuentra el producto.
     * @throws SQLException si ocurre un error al consultar la vista.
     */
    public BigDecimal obtenerExistencia(int productoId) throws SQLException {
        String sql = """
                SELECT existencia
                FROM vwExistenciasProductos
                WHERE id = ?
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setInt(1, productoId);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getBigDecimal("existencia");
                }
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Producto con su Estilo.
     */
    private Producto construirProducto(ResultSet resultado) throws SQLException {
        Estilo estilo = new Estilo(
                resultado.getInt("estilo_id"),
                resultado.getString("estilo_nombre"),
                resultado.getString("estilo_descripcion"),
                resultado.getBoolean("estilo_activo")
        );

        return new Producto(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("descripcion"),
                resultado.getBigDecimal("precio"),
                estilo,
                resultado.getBoolean("activo")
        );
    }

    /**
     * Consulta las existencias calculadas de todos los productos activos.
     *
     * @return lista de existencias para mostrar en la interfaz.
     * @throws SQLException si ocurre un error al consultar la vista.
     */
    public List<ExistenciaProducto> listarExistencias() throws SQLException {
        List<ExistenciaProducto> existencias = new ArrayList<>();

        String sql = """
            SELECT
                id,
                nombre,
                descripcion,
                precio,
                estilo,
                existencia
            FROM vwExistenciasProductos
            ORDER BY nombre
            """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()
        ) {
            while (resultado.next()) {
                existencias.add(new ExistenciaProducto(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getString("descripcion"),
                        resultado.getBigDecimal("precio"),
                        resultado.getString("estilo"),
                        resultado.getBigDecimal("existencia")
                ));
            }
        }

        return existencias;
    }
}