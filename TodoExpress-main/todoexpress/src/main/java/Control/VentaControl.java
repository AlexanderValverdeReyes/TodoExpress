package Control;


import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class VentaControl {
    
private DefaultTableModel modelo;

    // Configura la cabecera de la tabla de ventas
    public void cabeceraVentas(JTable tablaVentas) {
        String[] cabecera = {"PRODUCTO", "SUBTOTAL"};//array
        modelo = new DefaultTableModel(cabecera, 0);//tabla
        tablaVentas.setModel(modelo);
    }

    // Calcula el subtotal de un producto
    public double calcularSubtotal(JTextField cantidad, JTable tablaProductos) {
        if (tablaProductos.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto");
            return 0;
        }

        try {
            double precio = Double.parseDouble(tablaProductos.getValueAt(tablaProductos.getSelectedRow(), 2).toString());
            int cant = Integer.parseInt(cantidad.getText());
            return precio * cant;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Cantidad debe ser un número válido");
            return 0;
        }
    }

    // Calcula el total de la venta
    public double calcularTotal(JTable tablaVentas) {
        double total = 0;
        for (int i = 0; i < tablaVentas.getRowCount(); i++) {
            total += Double.parseDouble(tablaVentas.getValueAt(i, 1).toString());
        }
        return total;
    }

    // Aplica descuento según el monto total
    public double aplicarDescuento(double total) {
        if (total >= 100 && total <= 250) {
            return total * 0.2;
        } else if (total > 250 && total <= 350) {
            return total * 0.4;
        } else if (total > 350) {
            return total * 0.5;
        }
        return 0;
    }

    // Agrega un producto a la venta
    public void agregarProductoAVenta(JTextField cantidad, JTable tablaProductos, JTable tablaVentas,
                                     JTextField txtSubtotal, JTextField txtDescuento, JTextField txtTotal,
                                     JLabel lblTipoDescuento) {
        if (cantidad.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese la cantidad");
            return;
        }

        double subtotal = calcularSubtotal(cantidad, tablaProductos);
        if (subtotal > 0) {
            modelo.addRow(new Object[]{ //array
                tablaProductos.getValueAt(tablaProductos.getSelectedRow(), 0),
                subtotal
            });

            double total = calcularTotal(tablaVentas);
            double descuento = aplicarDescuento(total);
            double totalConDescuento = total - descuento;

            txtSubtotal.setText(String.valueOf(total));
            txtDescuento.setText(String.valueOf(descuento));
            txtTotal.setText(String.valueOf(totalConDescuento));
            lblTipoDescuento.setText(descuento > 0 ? (descuento == total * 0.2 ? "20%" : 
                                                     descuento == total * 0.4 ? "40%" : "50%") : "0%");
            cantidad.setText("");
        }
    }

    // Elimina un producto de la venta
    public void eliminarProductoDeVenta(JTable tablaVentas, JTextField txtSubtotal,
                                       JTextField txtDescuento, JTextField txtTotal,
                                       JLabel lblTipoDescuento) {
        int fila = tablaVentas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione una fila");
        } else {
            modelo.removeRow(fila);
            double total = calcularTotal(tablaVentas);
            double descuento = aplicarDescuento(total);
            double totalConDescuento = total - descuento;

            txtSubtotal.setText(String.valueOf(total));
            txtDescuento.setText(String.valueOf(descuento));
            txtTotal.setText(String.valueOf(totalConDescuento));
            lblTipoDescuento.setText(descuento > 0 ? (descuento == total * 0.2 ? "20%" : 
                                                     descuento == total * 0.4 ? "40%" : "50%") : "0%");
        }
    }
}