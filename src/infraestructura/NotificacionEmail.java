package infraestructura;

import usecases.ServicioNotificacion;

public class NotificacionEmail implements ServicioNotificacion {
    public void enviar(String destinatario, String mensaje) {
        System.out.println("EMAIL a " + destinatario + ": " + mensaje);
    }
}
