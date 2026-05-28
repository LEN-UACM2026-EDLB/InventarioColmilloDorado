package cervecera.vista;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * Ventana principal del sistema.
 *
 * Organiza los módulos de la aplicación mediante pestañas para mantener separadas
 * las responsabilidades de estilos, productos, insumos, movimientos y existencias.
 */
public class VentanaPrincipal extends JFrame {

    private JTabbedPane pestañas;

    private PanelEstilos panelEstilos;
    private PanelProductos panelProductos;
    private PanelInsumos panelInsumos;
    private PanelMovimientosProductos panelMovimientosProductos;
    private PanelMovimientosInsumos panelMovimientosInsumos;
    private PanelExistencias panelExistencias;

    public VentanaPrincipal() {
        configurarVentana();
        inicializarComponentes();
    }

    /**
     * Define propiedades básicas de la ventana principal.
     */
    private void configurarVentana() {
        setTitle("Sistema de Inventario - Cervecera Local");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    /**
     * Crea componentes visuales, los agrega al contenedor y registra eventos.
     */
    private void inicializarComponentes() {
        pestañas = new JTabbedPane();

        panelEstilos = new PanelEstilos();
        panelProductos = new PanelProductos();
        panelInsumos = new PanelInsumos();
        panelMovimientosProductos = new PanelMovimientosProductos();
        panelMovimientosInsumos = new PanelMovimientosInsumos();
        panelExistencias = new PanelExistencias();

        pestañas.addTab("Estilos", panelEstilos);
        pestañas.addTab("Productos", panelProductos);
        pestañas.addTab("Insumos", panelInsumos);
        pestañas.addTab("Mov. Productos", panelMovimientosProductos);
        pestañas.addTab("Mov. Insumos", panelMovimientosInsumos);
        pestañas.addTab("Existencias", panelExistencias);

        pestañas.addChangeListener(e -> actualizarDatosDePestaña());

        add(pestañas, BorderLayout.CENTER);
    }

    /**
     * Actualiza datos dependientes cuando el usuario cambia de pestaña.
     */
    private void actualizarDatosDePestaña() {
        int indice = pestañas.getSelectedIndex();

        if (indice < 0) {
            return;
        }

        String titulo = pestañas.getTitleAt(indice);

        if (titulo.equals("Productos")) {
            panelProductos.recargarDatos();
        }
    }
}