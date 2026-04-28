package infraestructura;

public class CatalogoCETYS_SOAP {
    public ResultadoSOAP consultarObra(String codigoCETYS, String formato) {
        return new ResultadoSOAP(codigoCETYS, "Libro de Prueba", "Autor Ejemplo");
    }
}
