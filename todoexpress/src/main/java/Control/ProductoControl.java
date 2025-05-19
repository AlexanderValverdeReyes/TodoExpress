/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Control;

/**
 *
 * @author alexa
 */
import DAO.ProductoDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ProductoControl {
    public void mostrarProductos(JTable tablaClientes) {
        Conexion objetoConexion = new Conexion();

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID","NOMBRE DEL PRODUCTO", "DESCRIPCION", "PRECIO UNITARIO", "CANTIDAD", "CATEGORIA"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sql = "SELECT  ID, Nombre_del_producto, Descripcion, Precio_unitario, Cantidad, Categoria FROM Productos";

        try (Connection conn = objetoConexion.establecerConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String[] datos = new String[6];
                datos[0] = rs.getString("ID");
                datos[1] = rs.getString("Nombre_del_producto");
                datos[2] = rs.getString("Descripcion");
                datos[3] = rs.getString("Precio_unitario");
                datos[4] = rs.getString("Cantidad");
                datos[5] = rs.getString("Categoria");
                modelo.addRow(datos);
            }

            tablaClientes.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar: " + e);
        } finally {
            objetoConexion.cerrarConexion();
        }
    }
    
     public void agregarProducto(JTextField Nombre_del_producto , JTextField Descripcion, JTextField Precio_unitario, JTextField Cantidad, JTextField Categoria) {
    if (Nombre_del_producto.getText().isEmpty() || Descripcion.getText().isEmpty() || Precio_unitario.getText().isEmpty() || Cantidad.getText().isEmpty()|| Categoria.getText().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Todos los campos deben estar completos");
        return;
    }

    Conexion objetoConexion = new Conexion();
    //String verificarDniSQL = "SELECT COUNT(*) FROM Clientes WHERE DNI = ?";
    String consulta = "INSERT INTO Productos (Nombre_del_producto, Descripcion, Precio_unitario, Cantidad, Categoria) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = objetoConexion.establecerConexion()) {       
        // Insertar si el DNI no existe
        try (PreparedStatement psInsertar = conn.prepareStatement(consulta)) {
            psInsertar.setString(1, Nombre_del_producto.getText());
            psInsertar.setString(2, Descripcion.getText());
            psInsertar.setString(3, Precio_unitario.getText());
            psInsertar.setString(4, Cantidad.getText());
            psInsertar.setString(5, Categoria.getText());
            psInsertar.executeUpdate();
            JOptionPane.showMessageDialog(null, "Se guardó el registro correctamente");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al guardar el registro: " + e.toString());
    } finally {
        objetoConexion.cerrarConexion();
    }
}
     
     public void cargarProductos(JTable tablaProductos, JTextField ID, JTextField Nombre_del_producto, JTextField Descripcion, JTextField Precio_unitario, JTextField Cantidad, JTextField Categoria) {
    try {
           int fila = tablaProductos.getSelectedRow(); 
           if (fila != -1) {
               
            ID.setText(tablaProductos.getValueAt(fila, 0).toString());
            Nombre_del_producto.setText(tablaProductos.getValueAt(fila, 1).toString());
            Descripcion.setText(tablaProductos.getValueAt(fila, 2).toString());
            Precio_unitario.setText(tablaProductos.getValueAt(fila, 3).toString());
            Cantidad.setText(tablaProductos.getValueAt(fila, 4).toString());
            Categoria.setText(tablaProductos.getValueAt(fila, 5).toString());
        }   
           else{
               JOptionPane.showMessageDialog(null, "No se pudo seleccionar");
           }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error de seleccion:" +e.toString());
    }
       
    }
     
     public void modificarProducto(JTextField ID, JTextField Nombre_del_producto, JTextField Descripcion, JTextField Precio_unitario, JTextField Cantidad, JTextField Categoria) {
    if (ID.getText().isEmpty()|| Nombre_del_producto.getText().isEmpty() || Descripcion.getText().isEmpty() || Precio_unitario.getText().isEmpty() || Cantidad.getText().isEmpty()|| Categoria.getText().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Todos los campos deben estar completos");
        return;
    }

    Conexion objetoConexion = new Conexion();
    String consulta = "UPDATE Productos SET Nombre_del_producto = ?, Descripcion = ?, Precio_unitario = ?, Cantidad  =?, Categoria =? WHERE ID = ?;";

    try (Connection conn = objetoConexion.establecerConexion()) {
        try (PreparedStatement psInsertar = conn.prepareStatement(consulta)) {
            psInsertar.setString(1, Nombre_del_producto.getText());
            psInsertar.setString(2, Descripcion.getText());
            psInsertar.setString(3, Precio_unitario.getText());
            psInsertar.setString(4, Cantidad.getText());
            psInsertar.setString(5, Categoria.getText());
            psInsertar.setString(6, ID.getText());
            psInsertar.executeUpdate();
            JOptionPane.showMessageDialog(null, "Se modifico el registro correctamente");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al modificar el registro: " + e.toString());
    } finally {
        objetoConexion.cerrarConexion();
    }
}
     
     public void eliminarProducto(JTextField idproducto) {
    Conexion objetoConexion = new Conexion();

    String consulta = "DELETE FROM Productos WHERE ID = ?";

    try (Connection conn = objetoConexion.establecerConexion();
         PreparedStatement ps = conn.prepareStatement(consulta)) {

        if (idproducto.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un ID para eliminar.");
            return;
        }

        ps.setString(1, idproducto.getText());
        int filasAfectadas = ps.executeUpdate();

        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(null, "Se eliminó el registro correctamente.");
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró un cliente con ese ID.");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar el registro: " + e.toString());
    } finally {
        objetoConexion.cerrarConexion();
    }
}
     public void limpiarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
        }
    }
     
}
    
/*private ArrayList<ProductoDAO> listaProductos = new ArrayList<>();
   
// En ProductoControl
private DefaultTableModel modelo = new DefaultTableModel() {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // Todas las celdas no editables
    }
};


// Configura la cabecera de la tabla
public void cabeceraProductos(JTable tablaProductos) {
    String[] cabecera = {"NOMBRE", "DESCRIPCION", "PRECIO", "CANTIDAD", "CATEGORIA"};
    modelo.setColumnIdentifiers(cabecera);
    tablaProductos.setModel(modelo);
}


// Agrega un producto a la tabla y a la lista
public void agregarProducto(JTextField nombre, JTextField descripcion, JTextField precio, JTextField cantidad, JTextField categoria) {
    if (nombre.getText().isEmpty() || descripcion.getText().isEmpty() || 
        precio.getText().isEmpty() || cantidad.getText().isEmpty() || categoria.getText().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Complete todos los campos");
        return;
    }

    try {
        ProductoDAO producto = new ProductoDAO(
            nombre.getText(), 
            descripcion.getText(),
            Double.parseDouble(precio.getText()),
            Integer.parseInt(cantidad.getText()),
            categoria.getText()
        );

        modelo.addRow(new Object[]{
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getCantidad(),
            producto.getCategoria()
        });

        listaProductos.add(producto);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Cantidad y Precio deben ser números válidos");
    }
}

// Limpia los campos del formulario
public void limpiarCampos(JTextField... campos) {
    for (JTextField campo : campos) {
        campo.setText("");
    }
}

// Elimina la fila seleccionada
public void eliminarFilaProducto(JTable tablaProductos) {
    int fila = tablaProductos.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione una fila");
    } else {
        modelo.removeRow(fila);
        listaProductos.remove(fila);
    }
}
public void modificarProducto(JTable tablaProductos, JTextField nombre, JTextField descripcion, JTextField precio, JTextField cantidad, JTextField categoria) {
    int fila = tablaProductos.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione una fila para modificar");
        return;
    }

    try {
        ProductoDAO productoModificado = new ProductoDAO(
            nombre.getText(),
            descripcion.getText(),
            Double.parseDouble(precio.getText()),
            Integer.parseInt(cantidad.getText()),
            categoria.getText()
        );

        // Actualiza la fila en la tabla
        modelo.setValueAt(productoModificado.getNombre(), fila, 0);
        modelo.setValueAt(productoModificado.getDescripcion(), fila, 1);
        modelo.setValueAt(productoModificado.getPrecio(), fila, 2);
        modelo.setValueAt(productoModificado.getCantidad(), fila, 3);
        modelo.setValueAt(productoModificado.getCategoria(), fila, 4);

        // También actualiza la lista interna
        listaProductos.set(fila, productoModificado);

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Precio y Cantidad deben ser números válidos");
    }
}

}
*/
