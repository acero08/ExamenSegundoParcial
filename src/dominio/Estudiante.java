package dominio;

public class Estudiante implements Usuario {
    private String nombre;
    public Estudiante(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }
    public String getRol() { return "ESTUDIANTE"; }
    public int getLimitePrestamos() { return 3; }
}
