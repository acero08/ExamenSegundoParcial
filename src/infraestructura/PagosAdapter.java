package infraestructura;

public class PagosAdapter {

    private final SistemaPagosBancario sistemaBancario;

    public PagosAdapter(SistemaPagosBancario sistemaBancario) {
        this.sistemaBancario = sistemaBancario;
    }

    public boolean cobrarFianza(String idEstudiante, double monto) {
        String referencia = "FIANZA-" + idEstudiante + "-" + System.currentTimeMillis();
        RespuestaBancaria respuesta = sistemaBancario.procesarPago(referencia, monto, "MXN");
        return respuesta.exitoso;
    }
}
