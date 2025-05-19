/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Control.Conexion;
import DAO.ClienteDAO;
import DAO.ProductoDAO;
import DAO.VentaDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author alexa
 */
public class Ventaform extends javax.swing.JFrame {
    
private final VentaDAO ventaActual = new VentaDAO();

    public Ventaform() {
        initComponents();
        setLocationRelativeTo(null);      
        DefaultTableModel modeloTabla = new DefaultTableModel();
    modeloTabla.addColumn("Producto");
    modeloTabla.addColumn("Precio");
    modeloTabla.addColumn("Cantidad");

    tablaVenta.setModel(modeloTabla);
    }
    private void agregarProductoATabla() {
    String producto = txtproducto.getText();
String precioStr = txtprecio.getText();     // Precio unitario
String cantidadStr = txtcantcompra.getText();
String stockStr = txtcantidad.getText();       // Stock disponible (asumo que este campo existe)

if (producto.isEmpty() || precioStr.isEmpty() || cantidadStr.isEmpty() || stockStr.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Por favor, asegúrese de que todos los campos estén llenos.");
    return;
}

try {
    double precio = Double.parseDouble(precioStr);
    int cantidad = Integer.parseInt(cantidadStr);
    int stockDisponible = Integer.parseInt(stockStr);

    if (cantidad > stockDisponible) {
        JOptionPane.showMessageDialog(this, "Cantidad solicitada excede el stock disponible.");
        return;
    }

    DefaultTableModel modelo = (DefaultTableModel) tablaVenta.getModel();
    boolean productoExiste = false;

    for (int i = 0; i < modelo.getRowCount(); i++) {
        String nombreTabla = modelo.getValueAt(i, 0).toString();

        if (nombreTabla.equals(producto)) {
            // Producto ya existe: actualizar cantidad
            int cantidadActual = (int) modelo.getValueAt(i, 2);
            modelo.setValueAt(cantidadActual + cantidad, i, 2);
            productoExiste = true;
            break;
        }
    }

    if (!productoExiste) {
        // Producto no está en la tabla: se agrega una nueva fila
        modelo.addRow(new Object[]{producto, precio, cantidad});
    }

    // Reducir stock mostrado
    int nuevoStock = stockDisponible - cantidad;
    txtcantidad.setText(String.valueOf(nuevoStock));

    calcularTotales(); // Actualiza totales

} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "Precio, cantidad o stock inválido.");
      }
    }
    
    private void calcularTotales() {
    DefaultTableModel modelo = (DefaultTableModel) tablaVenta.getModel();
    double subtotal = 0;

    for (int i = 0; i < modelo.getRowCount(); i++) {
        double precio = (double) modelo.getValueAt(i, 1);
        int cantidad = (int) modelo.getValueAt(i, 2);
        subtotal += precio * cantidad;
    }

    double descuento = 0;
    if (subtotal > 100) { //  si el subtotal supera 100 se aplica 10%
        descuento = subtotal * 0.10;
    }

    double total = subtotal - descuento;

    txtsubtotal.setText(String.format("%.2f", subtotal));
    txtdescuento.setText(String.format("%.2f", descuento));
    txttotal.setText(String.format("%.2f", total));
}

public void setCliente(ClienteDAO cliente) {
    ventaActual.setCliente(cliente);
    txtDNI.setText(cliente.getDNI());
    txtnombre.setText(cliente.getNombres());
}

public void agregarProducto(ProductoDAO producto) {
    if (ventaActual.getProductos() == null) {
        ventaActual.setProductos(new ArrayList<>());
    }
    ventaActual.getProductos().add(producto);
    txtproducto.setText(producto.getNombre());
    txtcantidad.setText(String.valueOf(producto.getCantidad()));
    txtprecio.setText(String.valueOf(producto.getPrecio()));
}
    


private int obtenerIdClientePorDni(String dni) {
    int idCliente = -1;
    Conexion cn = new Conexion();
    String sql = "SELECT ID FROM Clientes WHERE DNI = ?";
    
    try (Connection con = cn.establecerConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, dni);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            idCliente = rs.getInt("ID");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return idCliente;
}
private int insertarVenta(int idCliente, double total, double descuento) {
    int idVenta = -1;
    Conexion cn = new Conexion();
    String sql = "INSERT INTO Venta (ID_cliente, Fecha, Total, descuento) VALUES (?, CURRENT_DATE, ?, ?)";

    try (Connection con = cn.establecerConexion();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, idCliente);
        ps.setDouble(2, total);
        ps.setDouble(3, descuento);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            idVenta = rs.getInt(1);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return idVenta;
}
private int obtenerIdProductoPorNombre(String nombre) {
    int idProducto = -1;
    Conexion cn = new Conexion();
    String sql = "SELECT ID_producto FROM Productos WHERE Nombre_del_producto = ?";

    try (Connection con = cn.establecerConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            idProducto = rs.getInt("ID_producto");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return idProducto;
}

private void insertarDetalleVenta(int idVenta, int idProducto, int cantidad, double precioUnitario, double subtotal) {
    Conexion cn = new Conexion();
    String sql = "INSERT INTO Detalle_venta (ID_venta, ID_producto, Cantidad, Precio_unitario, Subtotal) VALUES (?, ?, ?, ?, ?)";

    try (Connection con = cn.establecerConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idVenta);
        ps.setInt(2, idProducto);
        ps.setInt(3, cantidad);
        ps.setDouble(4, precioUnitario);
        ps.setDouble(5, subtotal);
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void actualizarStock(int idProducto, int cantidadVendida) {
    Conexion cn = new Conexion();
    String sql = "UPDATE Productos SET Cantidad = Cantidad - ? WHERE ID_producto = ?";

    try (Connection con = cn.establecerConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, cantidadVendida);
        ps.setInt(2, idProducto);
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}


private void limpiarFormulario() {
    txtnombre.setText("");
    txtDNI.setText("");
    txtsubtotal.setText("");
    txtdescuento.setText("");
    txttotal.setText("");
    txtproducto.setText("");
    txtprecio.setText("");
    txtcantidad.setText("");
    txtcantcompra.setText("");

    DefaultTableModel modelo = (DefaultTableModel) tablaVenta.getModel();
    modelo.setRowCount(0); // Limpia tabla
}




    // Método estático para agregar producto a la tabla
   
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaVenta = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtnombre = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDNI = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnbuscarproducto = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtproducto = new javax.swing.JTextField();
        btnagregar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtsubtotal = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txttotal = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtdescuento = new javax.swing.JTextField();
        btnregistrar = new javax.swing.JButton();
        btncancelar = new javax.swing.JButton();
        checkbox1 = new java.awt.Checkbox();
        btnvolver = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        btnbuscarcliente = new javax.swing.JButton();
        txtcantidad = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtcantcompra = new javax.swing.JTextField();
        txtprecio = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Venta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 12))); // NOI18N

        tablaVenta.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 12)); // NOI18N
        tablaVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tablaVenta);

        jLabel2.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel2.setText("Seleccionar Cliente");

        jLabel3.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel3.setText("Nombre:");

        txtnombre.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        txtnombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnombreActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel4.setText("DNI:");

        txtDNI.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel5.setText("Seleccionar Productos");

        btnbuscarproducto.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        btnbuscarproducto.setText("Bucar");
        btnbuscarproducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbuscarproductoActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel6.setText("Producto:");

        txtproducto.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N

        btnagregar.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        btnagregar.setText("Agregar");
        btnagregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnagregarActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel7.setText("Subtotal");

        txtsubtotal.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel8.setText("Total");

        txttotal.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        txttotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttotalActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel9.setText("Descuento");

        txtdescuento.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N

        btnregistrar.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        btnregistrar.setText("Registrar Venta");
        btnregistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnregistrarActionPerformed(evt);
            }
        });

        btncancelar.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        btncancelar.setText("Cancelar");

        checkbox1.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        checkbox1.setLabel("Comprobante de pago");

        btnvolver.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 12)); // NOI18N
        btnvolver.setText("Volver");
        btnvolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnvolverActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel10.setText("Stock");

        btnbuscarcliente.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        btnbuscarcliente.setText("Buscar");
        btnbuscarcliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbuscarclienteActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel1.setText("Cantidad");

        txtcantcompra.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 12)); // NOI18N

        jLabel11.setFont(new java.awt.Font("BigBlueTerm437 Nerd Font", 0, 14)); // NOI18N
        jLabel11.setText("Precio");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnbuscarcliente)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnvolver))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtproducto, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtcantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtprecio, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtcantcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnbuscarproducto, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 751, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(63, 63, 63)
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(412, Short.MAX_VALUE)
                        .addComponent(checkbox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(172, 172, 172))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(btnregistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(txtsubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(64, 64, 64)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(txtdescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(89, 89, 89)
                                .addComponent(jLabel8)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btncancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txttotal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnagregar)
                .addGap(344, 344, 344))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(btnbuscarcliente)))
                    .addComponent(btnvolver))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(btnbuscarproducto))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtproducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtcantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtprecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtcantcompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(btnagregar)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtsubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txttotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtdescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnregistrar)
                    .addComponent(btncancelar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(checkbox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtnombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnombreActionPerformed

    private void btnregistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnregistrarActionPerformed
        // TODO add your handling code here:
     if (tablaVenta.getRowCount() == 0 || txtDNI.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debe ingresar productos y el DNI del cliente.");
        return;
    }

    int idCliente = obtenerIdClientePorDni(txtDNI.getText());
    if (idCliente == -1) {
        JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
        return;
    }

    double total = Double.parseDouble(txttotal.getText().replace(",", "."));
    double descuento = Double.parseDouble(txtdescuento.getText().replace(",","." ));

    int idVenta = insertarVenta(idCliente, total, descuento);
    if (idVenta == -1) {
        JOptionPane.showMessageDialog(this, "Error al registrar la venta.");
        return;
    }

    for (int i = 0; i < tablaVenta.getRowCount(); i++) {
        String nombreProd = tablaVenta.getValueAt(i, 0).toString();
        double precio = Double.parseDouble(tablaVenta.getValueAt(i, 1).toString());
        int cantidad = Integer.parseInt(tablaVenta.getValueAt(i, 2).toString());

        int idProducto = obtenerIdProductoPorNombre(nombreProd);
        double subtotal = precio * cantidad;

        insertarDetalleVenta(idVenta, idProducto, cantidad, precio, subtotal);
        actualizarStock(idProducto, cantidad);
    }

    JOptionPane.showMessageDialog(this, "Venta procesada correctamente.");
    limpiarFormulario(); // opcional   
    }//GEN-LAST:event_btnregistrarActionPerformed

    private void btnvolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnvolverActionPerformed
        // TODO add your handling code here:
        Principal principal = new Principal();
    principal.setVisible(true);
    this.dispose();
    }//GEN-LAST:event_btnvolverActionPerformed

    private void btnbuscarclienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbuscarclienteActionPerformed
        // TODO add your handling code here:
        Busquedas dialogo = new Busquedas(this, this, true); // <-- referencia doble
    dialogo.setTipoBusqueda("cliente");
    dialogo.setVisible(true);
    }//GEN-LAST:event_btnbuscarclienteActionPerformed

    private void btnbuscarproductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbuscarproductoActionPerformed
        // TODO add your handling code here:
        Busquedas dialogo = new Busquedas(this, this, true);
    dialogo.setTipoBusqueda("producto");
    dialogo.setVisible(true);
    
    }//GEN-LAST:event_btnbuscarproductoActionPerformed

    private void btnagregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnagregarActionPerformed
        // TODO add your handling code here:
        agregarProductoATabla();
    
    }//GEN-LAST:event_btnagregarActionPerformed

    private void txttotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txttotalActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnagregar;
    private javax.swing.JButton btnbuscarcliente;
    private javax.swing.JButton btnbuscarproducto;
    private javax.swing.JButton btncancelar;
    private javax.swing.JButton btnregistrar;
    private javax.swing.JButton btnvolver;
    private java.awt.Checkbox checkbox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaVenta;
    private javax.swing.JTextField txtDNI;
    private javax.swing.JTextField txtcantcompra;
    private javax.swing.JTextField txtcantidad;
    private javax.swing.JTextField txtdescuento;
    private javax.swing.JTextField txtnombre;
    private javax.swing.JTextField txtprecio;
    private javax.swing.JTextField txtproducto;
    private javax.swing.JTextField txtsubtotal;
    private javax.swing.JTextField txttotal;
    // End of variables declaration//GEN-END:variables

    
}
