package infraestructura;

import dominio.Libro;

public class CatalogoCETYSAdapter implements CatalogoBiblioteca {

    private final CatalogoCETYS_SOAP servicioExterno;

    public CatalogoCETYSAdapter(CatalogoCETYS_SOAP servicioExterno) {
        this.servicioExterno = servicioExterno;
    }

    @Override
    public Libro buscarLibro(String isbn) {
        String codigoCETYS = isbn.replace("-", "");
        ResultadoSOAP resultado = servicioExterno.consultarObra(codigoCETYS, "JSON");
        return new Libro(isbn, resultado.nombreObra, resultado.autorCompleto);
    }
}
