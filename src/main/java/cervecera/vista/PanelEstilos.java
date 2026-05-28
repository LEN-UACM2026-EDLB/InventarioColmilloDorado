package cervecera.vista;

import cervecera.dao.EstiloDAO;
import cervecera.modelo.Estilo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel Swing para administrar el catálogo de estilos de cerveza.
 *
 * Contiene el formulario, la tabla y los eventos necesarios para crear, actualizar,
 * desactivar y recargar estilos.
 */
public class PanelEstilos extends JPanel {

    private JTextField txtNombre;
    private JTextField txtDescripcion;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnDesactivar;
    private JButton btnRecargar;

    private JTable tablaEstilos;
    private DefaultTableModel modeloTabla;

    private EstiloDAO estiloDAO;

    private int estiloSeleccionadoId;

    public PanelEstilos() {
        estiloDAO = new EstiloDAO();
        estiloSeleccionadoId = 0;

        configurarPanel();
        inicializarComponentes();
        cargarEstilos();
    }

    /**
     * Define el layout y márgenes generales del panel.
     */
    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Crea componentes visuales, los agrega al contenedor y registra eventos.
     */
    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del estilo"));

        txtNombre = new JTextField();
        txtDescripcion = new JTextField();

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(txtDescripcion);

        JPanel panelBotones = new JPanel();

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnDesactivar = new JButton("Desactivar");
        btnRecargar = new JButton("Recargar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnDesactivar);
        panelBotones.add(btnRecargar);

        panelFormulario.add(new JLabel(""));
        panelFormulario.add(panelBotones);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción", "Activo"},
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaEstilos = new JTable(modeloTabla);

        add(panelFormulario, BorderLayout.NORTH);
        add(new JScrollPane(tablaEstilos), BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarEstilo());
        btnActualizar.addActionListener(e -> actualizarEstilo());
        btnDesactivar.addActionListener(e -> desactivarEstilo());
        btnRecargar.addActionListener(e -> cargarEstilos());

        tablaEstilos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarEstilo();
            }
        });
    }

    /**
     * Recarga la tabla de estilos desde la base de datos.
     */
    public void cargarEstilos() {
        try {
            modeloTabla.setRowCount(0);

            List<Estilo> estilos = estiloDAO.listarActivos();

            for (Estilo estilo : estilos) {
                modeloTabla.addRow(new Object[]{
                        estilo.getId(),
                        estilo.getNombre(),
                        estilo.getDescripcion(),
                        estilo.isActivo()
                });
            }

        } catch (SQLException ex) {
            mostrarError("Error al cargar estilos", ex);
        }
    }

    /**
     * Valida los datos capturados y solicita al DAO guardar un estilo nuevo.
     */
    private void guardarEstilo() {
        try {
            Estilo estilo = construirEstiloDesdeFormulario();
            estiloDAO.insertar(estilo);

            JOptionPane.showMessageDialog(this, "Estilo guardado correctamente.");

            limpiarFormulario();
            cargarEstilos();

        } catch (SQLException ex) {
            mostrarError("Error al guardar estilo", ex);

        } catch (IllegalArgumentException ex) {
            mostrarAdvertencia(ex.getMessage());
        }
    }

    /**
     * Actualiza el estilo seleccionado usando los datos actuales del formulario.
     */
    private void actualizarEstilo() {
        if (estiloSeleccionadoId == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un estilo para actualizar.");
            return;
        }

        try {
            Estilo estilo = construirEstiloDesdeFormulario();
            estilo.setId(estiloSeleccionadoId);

            estiloDAO.actualizar(estilo);

            JOptionPane.showMessageDialog(this, "Estilo actualizado correctamente.");

            limpiarFormulario();
            cargarEstilos();

        } catch (SQLException ex) {
            mostrarError("Error al actualizar estilo", ex);

        } catch (IllegalArgumentException ex) {
            mostrarAdvertencia(ex.getMessage());
        }
    }

    /**
     * Realiza una baja lógica del estilo seleccionado tras confirmación del usuario.
     */
    private void desactivarEstilo() {
        if (estiloSeleccionadoId == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un estilo para desactivar.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas desactivar el estilo seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            estiloDAO.desactivar(estiloSeleccionadoId);

            JOptionPane.showMessageDialog(this, "Estilo desactivado correctamente.");

            limpiarFormulario();
            cargarEstilos();

        } catch (SQLException ex) {
            mostrarError("Error al desactivar estilo", ex);
        }
    }

    /**
     * Convierte los valores del formulario en un objeto Estilo válido.
     *
     * @return estilo listo para ser enviado al DAO.
     */
    private Estilo construirEstiloDesdeFormulario() {
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();

        return new Estilo(
                0,
                nombre,
                descripcion,
                true
        );
    }

    /**
     * Carga en el formulario los datos del estilo seleccionado en la tabla.
     */
    private void seleccionarEstilo() {
        int fila = tablaEstilos.getSelectedRow();

        if (fila < 0) {
            return;
        }

        estiloSeleccionadoId = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());

        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());

        Object descripcion = modeloTabla.getValueAt(fila, 2);
        txtDescripcion.setText(descripcion == null ? "" : descripcion.toString());
    }

    /**
     * Limpia los campos y reinicia la selección del formulario.
     */
    private void limpiarFormulario() {
        estiloSeleccionadoId = 0;

        txtNombre.setText("");
        txtDescripcion.setText("");

        tablaEstilos.clearSelection();
        txtNombre.requestFocus();
    }

    /**
     * Muestra advertencias de validación sin tratarse como errores del sistema.
     *
     * @param mensaje texto que se mostrará al usuario.
     */
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Validación",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Muestra errores técnicos en un cuadro de diálogo para informar al usuario.
     *
     * @param mensaje descripción general del error.
     * @param ex excepción original con el detalle técnico.
     */
    private void mostrarError(String mensaje, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                mensaje + ":\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}