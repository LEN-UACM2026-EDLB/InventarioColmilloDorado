package cervecera.dao;

import cervecera.conexion.ConexionBD;
import cervecera.modelo.Estilo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstiloDAO {

    public List<Estilo> listarActivos() throws SQLException {
        List<Estilo> estilos = new ArrayList<>();

        String sql = """
                SELECT id, nombre, descripcion, activo
                FROM Estilos
                WHERE activo = 1
                ORDER BY nombre
                """;

        try (
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

    public Estilo obtenerPorId(int id) throws SQLException {
        String sql = """
                SELECT id, nombre, descripcion, activo
                FROM Estilos
                WHERE id = ?
                """;

        try (
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

    public void insertar(Estilo estilo) throws SQLException {
        String sql = """
                INSERT INTO Estilos (nombre, descripcion, activo)
                VALUES (?, ?, ?)
                """;

        try (
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setString(1, estilo.getNombre());
            comando.setString(2, estilo.getDescripcion());
            comando.setBoolean(3, estilo.isActivo());

            comando.executeUpdate();
        }
    }

    public void actualizar(Estilo estilo) throws SQLException {
        String sql = """
                UPDATE Estilos
                SET nombre = ?,
                    descripcion = ?,
                    activo = ?
                WHERE id = ?
                """;

        try (
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

    public void desactivar(int id) throws SQLException {
        String sql = """
                UPDATE Estilos
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

    private Estilo construirEstilo(ResultSet resultado) throws SQLException {
        return new Estilo(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("descripcion"),
                resultado.getBoolean("activo")
        );
    }
}