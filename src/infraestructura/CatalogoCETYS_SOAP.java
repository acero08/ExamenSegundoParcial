//No hace nada para pues el examen pero solo es por la demostracion de la Architectura
package infraestructura;

public class CatalogoCETYS_SOAP {
    public ResultadoSOAP consultarObra(String codigoCETYS, String formato) {
        return new ResultadoSOAP(codigoCETYS, "Libro de Prueba", "Autor Ejemplo");
    }
}
