package usecases;

import dominio.*;
import patrones.AuditoriaLogger;
import java.util.Date;

public class RegistrarPrestamoUseCase {

    private final RepositorioPrestamos repositorio;
    private final ServicioNotificacion notificacion;
    private final AuditoriaLogger auditoria;

    public RegistrarPrestamoUseCase(RepositorioPrestamos repositorio,
                                     ServicioNotificacion notificacion) {
        this.repositorio  = repositorio;
        this.notificacion = notificacion;
        this.auditoria    = AuditoriaLogger.getInstancia();
    }

    public SolicitudPrestamo ejecutar(Usuario estudiante, Libro libro, Date fechaDevolucion) {
        SolicitudPrestamo solicitud = new SolicitudPrestamo.Builder()
            .estudiante(estudiante)
            .libro(libro)
            .fechaDevolucion(fechaDevolucion)
            .construir();

        repositorio.guardar(solicitud);
        auditoria.registrar("PRESTAMO_REGISTRADO", estudiante.getNombre());
        notificacion.enviar(estudiante.getNombre(),
            "Préstamo registrado. Devuelve antes de: " + fechaDevolucion);

        return solicitud;
    }
}
