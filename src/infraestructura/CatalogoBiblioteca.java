package infraestructura;

import dominio.Libro;

public interface CatalogoBiblioteca {
    Libro buscarLibro(String isbn);
}
