package infraestructura;

public class ResultadoSOAP {
    public String codigoCETYS;
    public String nombreObra;
    public String autorCompleto;

    public ResultadoSOAP(String codigoCETYS, String nombreObra, String autorCompleto) {
        this.codigoCETYS   = codigoCETYS;
        this.nombreObra    = nombreObra;
        this.autorCompleto = autorCompleto;
    }
}
