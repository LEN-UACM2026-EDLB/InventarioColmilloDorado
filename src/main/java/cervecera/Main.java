package cervecera;

import cervecera.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la aplicación.
 *
 * Inicia la interfaz gráfica en el hilo de eventos de Swing para evitar bloqueos
 * o comportamientos inconsistentes al construir los componentes visuales.
 */
public class Main {

    /**
     * Inicia la aplicación y muestra la ventana principal.
     *
     * @param args argumentos de línea de comandos no utilizados.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}