package dominio;

public class Admin implements Usuario {
    private String nombre;
    public Admin(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }
    public String getRol() { return "ADMIN"; }
    public int getLimitePrestamos() { return 0; }
}
