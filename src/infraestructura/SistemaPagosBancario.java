package infraestructura;

// Sistema externo — no se puede modificar
public class SistemaPagosBancario {
    public RespuestaBancaria procesarPago(String referencia, double monto, String moneda) {
        System.out.println("BANCO: procesando pago " + referencia + " por $" + monto + " " + moneda);
        return new RespuestaBancaria(true, "TXN-" + referencia);
    }
}
