package infraestructura;

import dominio.*;
import patrones.AuditoriaLogger;

public class ServicioPrestamoLibros {

    public void prestarLibro(Usuario usuario, Libro libro) {
        System.out.println("Préstamo registrado: " + libro.titulo + " → " + usuario.getNombre());
        AuditoriaLogger.getInstancia().registrar("LIBRO_PRESTADO: " + libro.titulo, usuario.getNombre());
    }

    public void devolverLibro(Usuario usuario, Libro libro) {
        System.out.println("Devolución registrada: " + libro.titulo + " ← " + usuario.getNombre());
        AuditoriaLogger.getInstancia().registrar("LIBRO_DEVUELTO: " + libro.titulo, usuario.getNombre());
    }
}
