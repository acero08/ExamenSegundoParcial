import dominio.*;
import patrones.*;
import infraestructura.*;
import usecases.*;

import java.util.*;

public class Main {

    static RepositorioUsuarios repoUsuarios   = new RepositorioUsuariosMemoria();
    static RepositorioPrestamos repoPrestamos = new RepositorioPrestamoMySQL();
    static ServicioNotificacion notificacion  = new NotificacionEmail();
    static CatalogoBiblioteca catalogo        = new CatalogoCETYSAdapter(new CatalogoCETYS_SOAP());
    static PagosAdapter pagos                 = new PagosAdapter(new SistemaPagosBancario());
    static ServicioMultas multas              = new ServicioMultas(pagos);
    static ServicioPrestamoLibros servicioLibros = new ServicioPrestamoLibros();
    static ServicioAutenticacion ldap         = new AutenticacionLDAP();
    static RegistrarUsuarioUseCase registrarUsuario   = new RegistrarUsuarioUseCase(repoUsuarios);
    static RegistrarPrestamoUseCase registrarPrestamo = new RegistrarPrestamoUseCase(repoPrestamos, notificacion);
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════");
        System.out.println(" SISTEMA BIBLIOTECA CETYS");
        System.out.println("═══════════════════════════════════════");

        boolean corriendo = true;
        while (corriendo) {
            System.out.println("\n1. Registrar usuario");
            System.out.println("2. Ver usuarios registrados");
            System.out.println("3. Buscar libro por ISBN");
            System.out.println("4. Registrar préstamo");
            System.out.println("5. Registrar multa");
            System.out.println("6. Préstamo y devolución de libro");
            System.out.println("7. Autenticar usuario (LDAP)");
            System.out.println("0. Salir");
            System.out.print("\nElige una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1": menuRegistrarUsuario(); break;
                case "2": menuVerUsuarios();      break;
                case "3": menuBuscarLibro();      break;
                case "4": menuRegistrarPrestamo();break;
                case "5": menuRegistrarMulta();   break;
                case "6": menuPrestamoDevolucion();break;
                case "7": menuAutenticar();       break;
                case "0": corriendo = false; System.out.println("\nHasta luego."); break;
                default:  System.out.println("Opción no válida.");
            }
        }
    }

    // ── 1. Registrar usuario ──
    static void menuRegistrarUsuario() {
        System.out.println("\n── Registrar Usuario ──");
        System.out.println("Tipos disponibles: ESTUDIANTE, BIBLIOTECARIO, ADMIN, POSGRADO");
        System.out.print("Tipo: ");
        String tipo = scanner.nextLine().trim().toUpperCase();
        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine().trim();

        try {
            Usuario u = registrarUsuario.ejecutar(tipo, nombre);
            System.out.println("Usuario registrado: " + u.getNombre() + " [" + u.getRol() + "] — Límite préstamos: " + u.getLimitePrestamos());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── 2. Ver usuarios ──
    static void menuVerUsuarios() {
        System.out.println("\n── Usuarios Registrados ──");
        List<Usuario> lista = repoUsuarios.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
        } else {
            for (Usuario u : lista) {
                System.out.println(" • " + u.getNombre() + " | " + u.getRol() + " | Límite: " + u.getLimitePrestamos());
            }
        }
    }

    // ── 3. Buscar libro ──
    static void menuBuscarLibro() {
        System.out.println("\n── Buscar Libro (Catálogo CETYS via SOAP) ──");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        try {
            Libro libro = catalogo.buscarLibro(isbn);
            System.out.println("Libro encontrado:");
            System.out.println("   Título: " + libro.titulo);
            System.out.println("   Autor:  " + libro.autor);
            System.out.println("   ISBN:   " + libro.isbn);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── 4. Registrar préstamo ──
    static void menuRegistrarPrestamo() {
        System.out.println("\n── Registrar Préstamo ──");

        List<Usuario> lista = repoUsuarios.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios registrados. Registra uno primero.");
            return;
        }

        System.out.println("Usuarios disponibles:");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + lista.get(i).getNombre() + " [" + lista.get(i).getRol() + "]");
        }
        System.out.print("Elige número de usuario: ");
        int idx;
        try { idx = Integer.parseInt(scanner.nextLine().trim()) - 1; }
        catch (Exception e) { System.out.println("Número inválido."); return; }
        if (idx < 0 || idx >= lista.size()) { System.out.println("Opción fuera de rango."); return; }
        Usuario estudiante = lista.get(idx);

        System.out.print("ISBN del libro: ");
        String isbn = scanner.nextLine().trim();
        Libro libro;
        try { libro = catalogo.buscarLibro(isbn); }
        catch (Exception e) { System.out.println("Libro no encontrado."); return; }

        System.out.print("Notas especiales (Enter para omitir): ");
        String notas = scanner.nextLine().trim();

        System.out.print("¿Renovación automática? (s/n): ");
        boolean renovacion = scanner.nextLine().trim().equalsIgnoreCase("s");

        int numRenovaciones = 1;
        if (renovacion) {
            System.out.print("Número de renovaciones: ");
            try { numRenovaciones = Integer.parseInt(scanner.nextLine().trim()); }
            catch (Exception e) { numRenovaciones = 1; }
        }

        try {
            SolicitudPrestamo solicitud = new SolicitudPrestamo.Builder()
                .estudiante(estudiante)
                .libro(libro)
                .fechaDevolucion(new Date())
                .notasEspeciales(notas.isEmpty() ? null : notas)
                .renovacionAutomatica(renovacion)
                .numRenovaciones(numRenovaciones)
                .construir();

            registrarPrestamo.ejecutar(estudiante, libro, new Date());
            System.out.println("Préstamo registrado para " + estudiante.getNombre());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── 5. Registrar multa ──
    static void menuRegistrarMulta() {
        System.out.println("\n── Registrar Multa ──");

        List<Usuario> lista = repoUsuarios.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.println("Usuarios:");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + lista.get(i).getNombre());
        }
        System.out.print("Elige número de usuario: ");
        int idx;
        try { idx = Integer.parseInt(scanner.nextLine().trim()) - 1; }
        catch (Exception e) { System.out.println("Número inválido."); return; }
        if (idx < 0 || idx >= lista.size()) { System.out.println("Opción fuera de rango."); return; }

        System.out.print("Monto de la multa (MXN): ");
        double monto;
        try { monto = Double.parseDouble(scanner.nextLine().trim()); }
        catch (Exception e) { System.out.println("Monto inválido."); return; }

        multas.registrarMulta(lista.get(idx), monto);
        System.out.println("Multa procesada.");
    }

    // ── 6. Préstamo y devolución ──
    static void menuPrestamoDevolucion() {
        System.out.println("\n── Préstamo / Devolución de Libro ──");

        List<Usuario> lista = repoUsuarios.listarTodos();
        if (lista.isEmpty()) { System.out.println("No hay usuarios registrados."); return; }

        System.out.println("Usuarios:");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + lista.get(i).getNombre());
        }
        System.out.print("Elige número de usuario: ");
        int idx;
        try { idx = Integer.parseInt(scanner.nextLine().trim()) - 1; }
        catch (Exception e) { System.out.println("Número inválido."); return; }
        if (idx < 0 || idx >= lista.size()) { System.out.println("Opción fuera de rango."); return; }
        Usuario usuario = lista.get(idx);

        System.out.print("ISBN del libro: ");
        String isbn = scanner.nextLine().trim();
        Libro libro;
        try { libro = catalogo.buscarLibro(isbn); }
        catch (Exception e) { System.out.println("Libro no encontrado."); return; }

        System.out.println("¿Qué deseas hacer?");
        System.out.println("  1. Prestar libro");
        System.out.println("  2. Devolver libro");
        System.out.print("Opción: ");
        String sub = scanner.nextLine().trim();

        if (sub.equals("1")) {
            servicioLibros.prestarLibro(usuario, libro);
            System.out.println("Libro prestado.");
        } else if (sub.equals("2")) {
            servicioLibros.devolverLibro(usuario, libro);
            System.out.println("Libro devuelto.");
        } else {
            System.out.println("Opción no válida.");
        }
    }

    // ── 7. Autenticar ──
    static void menuAutenticar() {
        System.out.println("\n── Autenticación LDAP ──");
        System.out.print("Usuario institucional: ");
        String usuario = scanner.nextLine().trim();
        System.out.print("Contraseña: ");
        String pass = scanner.nextLine().trim();

        boolean ok = ldap.autenticar(usuario, pass);
        if (ok) {
            System.out.println("Acceso concedido para: " + usuario);
        } else {
            System.out.println("Credenciales incorrectas.");
        }
    }
}
