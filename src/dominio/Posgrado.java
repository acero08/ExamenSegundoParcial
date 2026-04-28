package dominio;

public class Posgrado implements Usuario {
    private String nombre;
    public Posgrado(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }
    public String getRol() { return "POSGRADO"; }
    public int getLimitePrestamos() { return 5; }
}
