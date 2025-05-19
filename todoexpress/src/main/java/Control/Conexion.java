/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Control;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author alexa
 */
public class Conexion {
    Connection conectar = null; 
    String bd = "DBtiendaexpress.db";
    String cadena = "jdbc:sqlite:"+System.getProperty("user.dir")+"/"+bd; 
    
    public Connection establecerConexion() {
    
        Connection conectar = null; 
        try {
            Class.forName("org.sqlite.JDBC");
            conectar = DriverManager.getConnection(cadena);
            //JOptionPane.showMessageDialog(null,"Se conecto a la base de datos");
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "No se pudo conectar a la base de datos:"+e.toString());       
    }
        
        
        return conectar; 
    }
public void cerrarConexion() {

    try{
        if (conectar !=null){
            conectar.close();
            JOptionPane.showMessageDialog(null, "Se cerro la conexion exitoasamente");
        }
        
    }catch(Exception e){
        JOptionPane.showMessageDialog(null, "No se pudo cerrar la conexion");
    }
}
}