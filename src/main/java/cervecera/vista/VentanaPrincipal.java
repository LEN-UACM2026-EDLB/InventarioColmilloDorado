package cervecera.vista;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

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

    private void configurarVentana() {
        setTitle("Sistema de Inventario - Cervecera Local");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

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