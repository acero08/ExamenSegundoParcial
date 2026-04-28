package infraestructura;

import dominio.Usuario;
import patrones.AuditoriaLogger;

public class ServicioMultas {

    private final PagosAdapter pagosAdapter;

    public ServicioMultas(PagosAdapter pagosAdapter) {
        this.pagosAdapter = pagosAdapter;
    }

    public void registrarMulta(Usuario usuario, double monto) {
        boolean cobrado = pagosAdapter.cobrarFianza(usuario.getNombre(), monto);
        if (cobrado) {
            AuditoriaLogger.getInstancia().registrar("MULTA_COBRADA $" + monto, usuario.getNombre());
        } else {
            AuditoriaLogger.getInstancia().registrar("MULTA_FALLIDA $" + monto, usuario.getNombre());
        }
    }
}
