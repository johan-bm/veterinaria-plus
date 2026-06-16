package com.veterinariaplus.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicacion Veterinaria+.
 *
 * Veterinaria+ es un sistema de gestion integral para clinicas veterinarias
 * que permite administrar mascotas, propietarios, veterinarios, citas medicas,
 * historiales clinicos y medicamentos.
 *
 * Arquitectura: monolitica MVC (Vista - Controlador - Servicio - Repositorio - Modelo).
 */
@SpringBootApplication
public class VeterinariaPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(VeterinariaPlusApplication.class, args);
    }

}
