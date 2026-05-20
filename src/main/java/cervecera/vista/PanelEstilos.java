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

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

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

    private void limpiarFormulario() {
        estiloSeleccionadoId = 0;

        txtNombre.setText("");
        txtDescripcion.setText("");

        tablaEstilos.clearSelection();
        txtNombre.requestFocus();
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Validación",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarError(String mensaje, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                mensaje + ":\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}