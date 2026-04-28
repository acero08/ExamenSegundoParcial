package infraestructura;

import usecases.ServicioAutenticacion;

public class AutenticacionLDAP implements ServicioAutenticacion {

    @Override
    public boolean autenticar(String usuario, String password) {
        // Simulación de llamada real a servidor LDAP institucional
        System.out.println("LDAP: autenticando usuario " + usuario);
        return usuario != null && !usuario.isEmpty() && password != null && !password.isEmpty();
    }
}
