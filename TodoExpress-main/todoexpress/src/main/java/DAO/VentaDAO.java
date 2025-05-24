/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.util.ArrayList;

/**
 *
 * @author alexa
 */
public class VentaDAO {
    private ClienteDAO cliente;
    private ArrayList<ProductoDAO> productos;
    private double subtotal;
    private double descuento;
    private double total;

    // Constructor completo
    public VentaDAO(ClienteDAO cliente, ArrayList<ProductoDAO> productos) {
        this.cliente = cliente;
        this.productos = productos;
    }

    // Constructor vacío
    public VentaDAO() {}

    // Getters y Setters
    public ClienteDAO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDAO cliente) {
        this.cliente = cliente;
    }

    public ArrayList<ProductoDAO> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<ProductoDAO> productos) {
        this.productos = productos;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
