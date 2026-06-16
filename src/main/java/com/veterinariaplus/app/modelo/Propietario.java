package com.veterinariaplus.app.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Propietario (dueno) de una o mas mascotas registradas en la clinica.
 */
@Entity
@Table(name = "propietario")
public class Propietario extends EntidadBase {

    @NotBlank(message = "{propietario.nombre.requerido}")
    @Size(max = 80, message = "{propietario.nombre.tamano}")
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @NotBlank(message = "{propietario.apellido.requerido}")
    @Size(max = 80, message = "{propietario.apellido.tamano}")
    @Column(name = "apellido", nullable = false, length = 80)
    private String apellido;

    @NotBlank(message = "{propietario.telefono.requerido}")
    @Pattern(regexp = "^[0-9+\\-\\s()]{7,20}$", message = "{propietario.telefono.formato}")
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @NotBlank(message = "{propietario.email.requerido}")
    @Email(message = "{propietario.email.formato}")
    @Size(max = 120, message = "{propietario.email.tamano}")
    @Column(name = "email", nullable = false, length = 120, unique = true)
    private String email;

    @Size(max = 200, message = "{propietario.direccion.tamano}")
    @Column(name = "direccion", length = 200)
    private String direccion;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Mascota> mascotas = new ArrayList<>();

    public Propietario() {
    }

    public Propietario(String nombre, String apellido, String telefono, String email, String direccion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    /**
     * Nombre completo del propietario, util para mostrar en la vista.
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
