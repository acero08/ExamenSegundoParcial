package dominio;

import java.util.Date;

public class SolicitudPrestamo {
    private final Usuario estudiante;
    private final Libro libro;
    private final Date fechaDevolucion;
    private final String notasEspeciales;
    private final boolean renovacionAutomatica;
    private final int numRenovaciones;

    private SolicitudPrestamo(Builder b) {
        this.estudiante           = b.estudiante;
        this.libro                = b.libro;
        this.fechaDevolucion      = b.fechaDevolucion;
        this.notasEspeciales      = b.notasEspeciales;
        this.renovacionAutomatica = b.renovacionAutomatica;
        this.numRenovaciones      = b.numRenovaciones;
    }

    public Usuario getEstudiante()           { return estudiante; }
    public Libro getLibro()                  { return libro; }
    public Date getFechaDevolucion()         { return fechaDevolucion; }
    public String getNotasEspeciales()       { return notasEspeciales; }
    public boolean isRenovacionAutomatica()  { return renovacionAutomatica; }
    public int getNumRenovaciones()          { return numRenovaciones; }

    public static class Builder {
        private Usuario estudiante;
        private Libro libro;
        private Date fechaDevolucion;
        private String notasEspeciales       = null;
        private boolean renovacionAutomatica = false;
        private int numRenovaciones          = 1;

        public Builder estudiante(Usuario e)           { this.estudiante = e; return this; }
        public Builder libro(Libro l)                  { this.libro = l; return this; }
        public Builder fechaDevolucion(Date f)         { this.fechaDevolucion = f; return this; }
        public Builder notasEspeciales(String n)       { this.notasEspeciales = n; return this; }
        public Builder renovacionAutomatica(boolean r) { this.renovacionAutomatica = r; return this; }
        public Builder numRenovaciones(int n)          { this.numRenovaciones = n; return this; }

        public SolicitudPrestamo construir() {
            if (estudiante == null)      throw new IllegalStateException("El estudiante es obligatorio.");
            if (libro == null)           throw new IllegalStateException("El libro es obligatorio.");
            if (fechaDevolucion == null) throw new IllegalStateException("La fecha de devolución es obligatoria.");
            return new SolicitudPrestamo(this);
        }
    }
}
