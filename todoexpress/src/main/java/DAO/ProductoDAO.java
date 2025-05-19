/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

/**
 *
 * @author alexa
 */
public final class ProductoDAO {
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;
    private String categoria;
    // Constructor completo
    public ProductoDAO(String nombre, int cantidad, double precio) {
    setNombre(nombre);
    setDescripcion(descripcion); 
    setPrecio(precio);
    setCantidad(cantidad);
    setCategoria(categoria);      
}


    // Constructor vacío
    public ProductoDAO() {}

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        // Formatea la primera letra en mayúscula
        if (nombre != null && nombre.trim().length() > 0) {
        nombre = nombre.trim();
        this.nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1).toLowerCase();
    } else {
        this.nombre = "";
    }
        
        
    }
    public String getDescripcion(){
        return descripcion;

    }
    public void setDescripcion(String descripcion){
        
    if (descripcion != null && descripcion.trim().length() > 0) {
        descripcion = descripcion.trim();
        this.descripcion = descripcion.substring(0, 1).toUpperCase() + descripcion.substring(1).toLowerCase();
    } else {
        this.descripcion = "";
    }   
    } 
               
    public String getCategoria(){
        return categoria;

    }
    public void setCategoria(String categoria){
    if (categoria != null && categoria.trim().length() > 0) {
        categoria = categoria.trim();    
        this.categoria = categoria.substring(0, 1).toUpperCase() + categoria.substring(1).toLowerCase();
    } else {
        this.categoria = "";
    }    
    }
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("La cantidad no puede ser negativa");
    this.cantidad = cantidad;
        
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    private void setCantidad(String cantidad) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
