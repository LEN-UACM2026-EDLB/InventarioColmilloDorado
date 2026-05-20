package cervecera.dao;

import cervecera.conexion.ConexionBD;
import cervecera.modelo.Estilo;
import cervecera.modelo.Producto;
import cervecera.modelo.ExistenciaProducto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

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

    public void insertar(Producto producto) throws SQLException {
        String sql = """
                INSERT INTO Productos (nombre, descripcion, precio, estilo_id, activo)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
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

    public void desactivar(int id) throws SQLException {
        String sql = """
                UPDATE Productos
                SET activo = 0
                WHERE id = ?
                """;

        try (
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }

    public BigDecimal obtenerExistencia(int productoId) throws SQLException {
        String sql = """
                SELECT existencia
                FROM vwExistenciasProductos
                WHERE id = ?
                """;

        try (
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