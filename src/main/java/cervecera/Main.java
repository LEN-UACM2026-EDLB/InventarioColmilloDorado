package cervecera;

import cervecera.conexion.ConexionBD;
import cervecera.fabrica.FabricaMovimientoInsumo;
import cervecera.fabrica.FabricaMovimientoProducto;
import cervecera.modelo.Estilo;
import cervecera.modelo.Insumo;
import cervecera.modelo.MovimientoInsumo;
import cervecera.modelo.MovimientoProducto;
import cervecera.modelo.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        probarConexion();
        probarModelo();
    }

    private static void probarConexion() {
        try (Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion()) {
            System.out.println("Conexión correcta a SQL Server.");
        } catch (SQLException ex) {
            System.out.println("Error al conectar con SQL Server:");
            System.out.println(ex.getMessage());
        }
    }

    private static void probarModelo() {
        Estilo estilo = new Estilo(
                1,
                "IPA",
                "Cerveza con aroma intenso a lúpulo.",
                true
        );

        Producto producto = new Producto(
                1,
                "Lúpulo Salvaje",
                "Cerveza artesanal estilo IPA en botella de 355 ml.",
                new BigDecimal("85.00"),
                estilo,
                true
        );

        Insumo insumo = new Insumo(
                1,
                "Malta Pale Ale",
                "Malta",
                new BigDecimal("50.000"),
                "kg"
        );

        FabricaMovimientoInsumo fabricaInsumo = new FabricaMovimientoInsumo();

        MovimientoInsumo entradaInsumo = fabricaInsumo.crearMovimiento(
                "E",
                insumo,
                new BigDecimal("10.000"),
                "Compra adicional de malta."
        );

        entradaInsumo.aplicarMovimiento();

        System.out.println("Cantidad disponible de insumo:");
        System.out.println(insumo.getCantidadDisponible() + " " + insumo.getUnidadMedida());

        FabricaMovimientoProducto fabricaProducto = new FabricaMovimientoProducto();

        MovimientoProducto entradaProducto = fabricaProducto.crearMovimiento(
                "E",
                producto,
                new BigDecimal("120.000"),
                "Producción inicial."
        );

        System.out.println("Producto:");
        System.out.println(producto.getNombre());

        System.out.println("Tipo de movimiento:");
        System.out.println(entradaProducto.obtenerTipoMovimiento());

        System.out.println("Cantidad con signo:");
        System.out.println(entradaProducto.obtenerCantidadConSigno());
    }
}