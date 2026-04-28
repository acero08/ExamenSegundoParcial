package usecases;

import dominio.Usuario;
import patrones.AuditoriaLogger;
import patrones.FabricaDeUsuarios;

public class RegistrarUsuarioUseCase {

    private final RepositorioUsuarios repositorio;
    private final AuditoriaLogger auditoria;

    public RegistrarUsuarioUseCase(RepositorioUsuarios repositorio) {
        this.repositorio = repositorio;
        this.auditoria   = AuditoriaLogger.getInstancia();
    }

    public Usuario ejecutar(String tipo, String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío.");
        }

        Usuario usuario = FabricaDeUsuarios.crear(tipo, nombre);
        repositorio.guardar(usuario);
        auditoria.registrar("USUARIO_REGISTRADO tipo=" + tipo, nombre);
        return usuario;
    }
}
