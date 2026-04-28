package infraestructura;

import dominio.Usuario;
import usecases.RepositorioUsuarios;
import java.util.*;

public class RepositorioUsuariosMemoria implements RepositorioUsuarios {

    private final Map<String, Usuario> almacen = new HashMap<>();

    @Override
    public void guardar(Usuario usuario) {
        almacen.put(usuario.getNombre(), usuario);
        System.out.println("Usuario guardado: " + usuario.getNombre() + " [" + usuario.getRol() + "]");
    }

    @Override
    public Usuario buscarPorNombre(String nombre) {
        return almacen.get(nombre);
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(almacen.values());
    }
}
