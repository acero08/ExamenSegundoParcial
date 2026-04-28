//No hace nada para pues el examen pero solo es por la demostracion de la Architectura
package infraestructura;

import dominio.SolicitudPrestamo;
import usecases.RepositorioPrestamos;
import java.util.List;

public class RepositorioPrestamoMySQL implements RepositorioPrestamos {
    public void guardar(SolicitudPrestamo s)                      { /* INSERT INTO prestamos... */ }
    public SolicitudPrestamo buscarPorId(String id)               { return null; }
    public List<SolicitudPrestamo> listarPorEstudiante(String id) { return null; }
}
