package usecases;

import dominio.SolicitudPrestamo;
import java.util.List;

public interface RepositorioPrestamos {
    void guardar(SolicitudPrestamo solicitud);
    SolicitudPrestamo buscarPorId(String id);
    List<SolicitudPrestamo> listarPorEstudiante(String idEstudiante);
}
