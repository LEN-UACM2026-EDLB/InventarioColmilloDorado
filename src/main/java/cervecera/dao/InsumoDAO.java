package cervecera.dao;

import cervecera.conexion.ConexionBD;
import cervecera.modelo.Insumo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsumoDAO {

    public List<Insumo> listar() throws SQLException {
        List<Insumo> insumos = new ArrayList<>();

        String sql = """
                SELECT id, nombre, tipo, cantidad_disponible, unidad_medida
                FROM Insumos
                ORDER BY tipo, nombre
                """;

        try (
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

    public Insumo obtenerPorId(int id) throws SQLException {
        String sql = """
                SELECT id, nombre, tipo, cantidad_disponible, unidad_medida
                FROM Insumos
                WHERE id = ?
                """;

        try (
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

    public void insertar(Insumo insumo) throws SQLException {
        String sql = """
                INSERT INTO Insumos (nombre, tipo, cantidad_disponible, unidad_medida)
                VALUES (?, ?, ?, ?)
                """;

        try (
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

    public void eliminar(int id) throws SQLException {
        String sql = """
                DELETE FROM Insumos
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