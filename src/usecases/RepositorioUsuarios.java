package usecases;

import dominio.Usuario;
import java.util.List;

public interface RepositorioUsuarios {
    void guardar(Usuario usuario);
    Usuario buscarPorNombre(String nombre);
    List<Usuario> listarTodos();
}
