package dominio;

public class Bibliotecario implements Usuario {
    private String nombre;
    public Bibliotecario(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }
    public String getRol() { return "BIBLIOTECARIO"; }
    public int getLimitePrestamos() { return 10; }
}
