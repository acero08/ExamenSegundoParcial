package infraestructura;

public class RespuestaBancaria {
    public boolean exitoso;
    public String idTransaccion;

    public RespuestaBancaria(boolean exitoso, String idTransaccion) {
        this.exitoso        = exitoso;
        this.idTransaccion  = idTransaccion;
    }
}
