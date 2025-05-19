/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

/**
 *
 * @author alexa
 */
public final class ClienteDAO {
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;

    // Constructor completo
    public ClienteDAO(String nombre, String dni) {
        setNombres(nombre);
        setDNI(dni);

    }

    // Constructor vacío (para inicialización)
    public ClienteDAO() {}

    // Getters y Setters (validaciones incluidas)
    public String getNombres() {
        return nombre;
    }

    public void setNombres(String nombre) {
        this.nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1).toLowerCase();
    }

    public String getApellidos() {
        return apellido;
    }

    public void setApellidos(String apellido) {
        this.apellido = apellido.substring(0, 1).toUpperCase() + apellido.substring(1).toLowerCase();
    }

    public String getDNI() {
        return dni;
    }

    public void setDNI(String dni) {
      if (dni == null) {
        throw new IllegalArgumentException("El DNI no puede ser null.");
    }
    if (dni.matches("\\d{8}")) {
        this.dni = dni;
    } else {
        throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
    }
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
       if (telefono.matches("\\d{9}")) {
    this.telefono = telefono;
} else {
    throw new IllegalArgumentException("El Telefono 8 dígitos numéricos.");
}
    }

}
