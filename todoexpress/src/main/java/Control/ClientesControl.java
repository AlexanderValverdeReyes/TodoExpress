package Control;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ClientesControl {

    // Muestra los datos de la base de datos en la JTable
    public void mostrarclientes(JTable tablaClientes) {
        Conexion objetoConexion = new Conexion();

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID","NOMBRES", "APELLIDOS", "DNI", "TELEFONO"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sql = "SELECT  ID, Nombres, Apellidos, DNI, Telefono FROM Clientes";

        try (Connection conn = objetoConexion.establecerConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String[] datos = new String[5];
                datos[0] = rs.getString("ID");
                datos[1] = rs.getString("Nombres");
                datos[2] = rs.getString("Apellidos");
                datos[3] = rs.getString("DNI");
                datos[4] = rs.getString("Telefono");
                modelo.addRow(datos);
            }

            tablaClientes.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar: " + e);
        } finally {
            objetoConexion.cerrarConexion();
        }
    }

    // Agrega un nuevo cliente a la base de datos
   public void agregarCliente(JTextField Nombres, JTextField Apellidos, JTextField DNI, JTextField Telefono) {
    if (Nombres.getText().isEmpty() || Apellidos.getText().isEmpty() || DNI.getText().isEmpty() || Telefono.getText().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Todos los campos deben estar completos");
        return;
    }

    Conexion objetoConexion = new Conexion();
    String verificarDniSQL = "SELECT COUNT(*) FROM Clientes WHERE DNI = ?";
    String consulta = "INSERT INTO Clientes (Nombres, Apellidos, DNI, Telefono) VALUES (?, ?, ?, ?)";

    try (Connection conn = objetoConexion.establecerConexion()) {

        // Verificar si el DNI ya existe
        try (PreparedStatement psVerificar = conn.prepareStatement(verificarDniSQL)) {
            psVerificar.setString(1, DNI.getText());
            ResultSet rs = psVerificar.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "El DNI ya está registrado. No se puede duplicar.");
                return;
            }
        }

        // Insertar si el DNI no existe
        try (PreparedStatement psInsertar = conn.prepareStatement(consulta)) {
            psInsertar.setString(1, Nombres.getText());
            psInsertar.setString(2, Apellidos.getText());
            psInsertar.setString(3, DNI.getText());
            psInsertar.setString(4, Telefono.getText());

            psInsertar.executeUpdate();
            JOptionPane.showMessageDialog(null, "Se guardó el registro correctamente");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al guardar el registro: " + e.toString());
    } finally {
        objetoConexion.cerrarConexion();
    }
}
public void cargarClienteEnCampos(JTable tablaClientes, JTextField ID, JTextField Nombres, JTextField Apellidos, JTextField DNI, JTextField Telefono) {
    try {
           int fila = tablaClientes.getSelectedRow(); 
           if (fila != -1) {
               
            ID.setText(tablaClientes.getValueAt(fila, 0).toString());
            Nombres.setText(tablaClientes.getValueAt(fila, 1).toString());
            Apellidos.setText(tablaClientes.getValueAt(fila, 2).toString());
            DNI.setText(tablaClientes.getValueAt(fila, 3).toString());
            Telefono.setText(tablaClientes.getValueAt(fila, 4).toString());
        }   
           else{
               JOptionPane.showMessageDialog(null, "No se pudo seleccionar");
           }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error de seleccion:" +e.toString());
    }
        
        
           
    }

    public void modificarCliente(JTextField ID, JTextField Nombres, JTextField Apellidos, JTextField DNI, JTextField Telefono) {
    if (ID.getText().isEmpty()|| Nombres.getText().isEmpty() || Apellidos.getText().isEmpty() || DNI.getText().isEmpty() || Telefono.getText().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Todos los campos deben estar completos");
        return;
    }

    Conexion objetoConexion = new Conexion();
    //String verificarDniSQL = "SELECT COUNT(*) FROM Clientes WHERE DNI = ?";
    String consulta = "UPDATE Clientes SET Nombres = ?, Apellidos = ?, DNI = ?, Telefono  =? WHERE ID = ?;";

    try (Connection conn = objetoConexion.establecerConexion()) {
        try (PreparedStatement psInsertar = conn.prepareStatement(consulta)) {
            psInsertar.setString(1, Nombres.getText());
            psInsertar.setString(2, Apellidos.getText());
            psInsertar.setString(3, DNI.getText());
            psInsertar.setString(4, Telefono.getText());
            psInsertar.setString(5, ID.getText());
            psInsertar.executeUpdate();
            JOptionPane.showMessageDialog(null, "Se modifico el registro correctamente");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al modificar el registro: " + e.toString());
    } finally {
        objetoConexion.cerrarConexion();
    }
}
    
    
    public void eliminarCliente(JTextField id) {
    Conexion objetoConexion = new Conexion();

    String consulta = "DELETE FROM Clientes WHERE ID = ?";

    try (Connection conn = objetoConexion.establecerConexion();
         PreparedStatement ps = conn.prepareStatement(consulta)) {

        if (id.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un ID para eliminar.");
            return;
        }

        ps.setString(1, id.getText());
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
                