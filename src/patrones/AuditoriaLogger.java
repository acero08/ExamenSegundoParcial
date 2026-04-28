package patrones;

import java.time.LocalDateTime;

public class AuditoriaLogger {

    private static volatile AuditoriaLogger instancia;

    private AuditoriaLogger() {}

    public static AuditoriaLogger getInstancia() {
        if (instancia == null) {
            synchronized (AuditoriaLogger.class) {
                if (instancia == null) {
                    instancia = new AuditoriaLogger();
                }
            }
        }
        return instancia;
    }

    public void registrar(String evento, String usuario) {
        String entrada = "[" + LocalDateTime.now() + "] "
                       + "usuario=" + usuario + " | evento=" + evento;
        System.out.println(entrada);
    }
}
