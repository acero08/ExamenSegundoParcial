package dominio;

public class Libro {
    public String isbn;
    public String titulo;
    public String autor;

    public Libro(String isbn, String titulo, String autor) {
        this.isbn   = isbn;
        this.titulo = titulo;
        this.autor  = autor;
    }
}
