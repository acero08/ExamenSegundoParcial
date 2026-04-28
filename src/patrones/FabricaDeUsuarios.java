package patrones;

import dominio.*;

public class FabricaDeUsuarios {

    public static Usuario crear(String tipo, String nombre) {
        switch (tipo.toUpperCase()) {
            case "ESTUDIANTE":    return new Estudiante(nombre);
            case "BIBLIOTECARIO": return new Bibliotecario(nombre);
            case "ADMIN":         return new Admin(nombre);
            case "POSGRADO":      return new Posgrado(nombre);
            default: throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        }
    }
}
