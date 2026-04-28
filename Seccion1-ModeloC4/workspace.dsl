workspace "Sistema Biblioteca CETYS" "Arquitectura de Software — Parcial Práctico" {
    !identifiers hierarchical

    model {

        // ── Actores ──
        estudiante  = person "Estudiante"  "Solicita préstamos y reservas de salas"
        bibliotecario = person "Bibliotecario" "Gestiona préstamos y devoluciones"
        admin       = person "Admin"       "Administra el sistema y genera reportes"

        // ── Sistemas Externos ──
        catalogoCETYS = softwareSystem "Catálogo CETYS" "Expone libros vía SOAP" {
            tags "External"
        }
        sistemaPageos = softwareSystem "Sistema de Pagos Bancario" "Cobra fianzas y multas vía REST" {
            tags "External"
        }
        directorioLDAP = softwareSystem "Directorio Estudiantil" "Autentica usuarios vía LDAP" {
            tags "External"
        }

        // ── Sistema Principal ──
        biblioteca = softwareSystem "Sistema Biblioteca CETYS" "Gestiona préstamos de libros, reservas de salas y multas a estudiantes" {

            // Contenedores
            webapp = container "Web App" "Interfaz de usuario para todos los actores" "React" {
                tags "WebApp"
            }

            api = container "API Backend" "Lógica de negocio, patrones de diseño y orquestación" "Spring Boot / Java" {

                // ── Dominio ──
                usuario         = component "Usuario"          "Interfaz común para todos los tipos de usuario" "Java Interface"
                estudianteComp  = component "Estudiante"       "Usuario con límite de 3 préstamos. Implementa Usuario" "Java Class"
                bibliotecarioComp = component "Bibliotecario"  "Usuario con límite de 10 préstamos. Implementa Usuario" "Java Class"
                adminComp       = component "Admin"            "Usuario administrativo sin límite de préstamos. Implementa Usuario" "Java Class"
                posgradoComp    = component "Posgrado"         "Usuario con límite de 5 préstamos. Agregado sin modificar clases existentes (OCP)" "Java Class"
                libro           = component "Libro"            "Entidad de dominio con isbn, titulo y autor" "Java Class"
                solicitud       = component "SolicitudPrestamo" "Objeto inmutable construido con el patrón Builder. Campos obligatorios: estudiante, libro, fechaDevolucion" "Java Class (Builder Pattern)"

                // ── Patrones ──
                auditoriaLogger   = component "AuditoriaLogger"    "Registro centralizado de auditoría. Una sola instancia en todo el sistema" "Singleton Pattern"
                fabricaUsuarios   = component "FabricaDeUsuarios"  "Crea instancias de Usuario sin que el cliente conozca las clases concretas" "Factory Pattern"

                // ── Use Cases ──
                registrarPrestamoUC = component "RegistrarPrestamoUseCase" "Orquesta el flujo completo de un préstamo. Independiente de BD y framework" "Use Case"
                registrarUsuarioUC  = component "RegistrarUsuarioUseCase"  "Valida y registra nuevos usuarios usando la fábrica" "Use Case"

                // ── Interfaces (Use Cases layer) ──
                repoPrestamos   = component "RepositorioPrestamos"   "Interfaz de persistencia para préstamos. Ubicada en capa de dominio" "Java Interface"
                repoUsuarios    = component "RepositorioUsuarios"    "Interfaz de persistencia para usuarios. Ubicada en capa de dominio" "Java Interface"
                svcNotificacion = component "ServicioNotificacion"   "Interfaz de notificaciones. Desacopla canal de envío" "Java Interface"
                svcAutenticacion = component "ServicioAutenticacion" "Interfaz de autenticación. Desacopla implementación LDAP" "Java Interface"

                // ── Infraestructura: Adapters ──
                catalogoAdapter = component "CatalogoCETYSAdapter"  "Traduce buscarLibro(isbn) al SOAP consultarObra(codigoCETYS, formato)" "Adapter Pattern"
                pagosAdapter    = component "PagosAdapter"          "Traduce cobrarFianza(id, monto) a la firma REST bancaria procesarPago(ref, monto, moneda)" "Adapter Pattern"

                // ── Infraestructura: Implementaciones ──
                notificacionEmail  = component "NotificacionEmail"          "Envía notificaciones por correo electrónico" "Implements ServicioNotificacion"
                autenticacionLDAP  = component "AutenticacionLDAP"          "Autentica contra el servidor LDAP institucional" "Implements ServicioAutenticacion"
                servicioMultas     = component "ServicioMultas"             "Registra y cobra multas. Responsabilidad única (SRP)" "Java Class"
                servicioLibros     = component "ServicioPrestamoLibros"     "Gestiona préstamo y devolución física de libros. Responsabilidad única (SRP)" "Java Class"
                repoMySQL          = component "RepositorioPrestamoMySQL"   "Persiste préstamos en MySQL" "Implements RepositorioPrestamos"
                repoMongo          = component "RepositorioPrestamoMongoDB" "Persiste préstamos en MongoDB. Intercambiable sin tocar el UseCase" "Implements RepositorioPrestamos"
                repoMemoria        = component "RepositorioUsuariosMemoria" "Almacena usuarios en memoria. Usado en demo y pruebas" "Implements RepositorioUsuarios"

                // ── Main ──
                main = component "Main" "Punto de entrada. Menú interactivo con 7 opciones. Conecta todos los servicios mediante inyección de dependencias" "Java Main Class"
            }

            db = container "Base de Datos" "Almacena préstamos, reservas, multas y usuarios" "MySQL" {
                tags "Database"
            }

            worker = container "Worker Notificaciones" "Envía emails y alertas de vencimiento de préstamos de forma asíncrona" "Java" {
                tags "Worker"
            }
        }

        // ── Relaciones: Actores → Sistema ──
        estudiante    -> biblioteca.webapp "Solicita préstamos y reservas" "HTTPS"
        bibliotecario -> biblioteca.webapp "Gestiona préstamos y multas" "HTTPS"
        admin         -> biblioteca.webapp "Administra y genera reportes" "HTTPS"

        // ── Relaciones: Sistema → Externos ──
        biblioteca -> catalogoCETYS  "Consulta disponibilidad de libros" "SOAP"
        biblioteca -> sistemaPageos  "Cobra fianzas y multas" "REST"
        biblioteca -> directorioLDAP "Autentica y obtiene datos del alumno" "LDAP"

        // ── Relaciones: Contenedores ──
        biblioteca.webapp -> biblioteca.api    "Envía peticiones HTTP" "REST/JSON"
        biblioteca.api    -> biblioteca.db     "Lee y escribe datos" "JDBC"
        biblioteca.api    -> biblioteca.worker "Dispara notificaciones" "Async"

        // ── Relaciones: Sistemas Externos → API ──
        biblioteca.api.catalogoAdapter -> catalogoCETYS  "Consulta obras" "SOAP"
        biblioteca.api.pagosAdapter    -> sistemaPageos  "Procesa pagos" "REST"
        biblioteca.api.autenticacionLDAP -> directorioLDAP "Autentica usuario" "LDAP"

        // ── Relaciones internas del API ──
        biblioteca.api.main -> biblioteca.api.registrarUsuarioUC  "Ejecuta"
        biblioteca.api.main -> biblioteca.api.registrarPrestamoUC "Ejecuta"
        biblioteca.api.main -> biblioteca.api.autenticacionLDAP   "Autentica"
        biblioteca.api.main -> biblioteca.api.servicioLibros       "Presta/devuelve"
        biblioteca.api.main -> biblioteca.api.servicioMultas       "Registra multa"

        biblioteca.api.registrarUsuarioUC  -> biblioteca.api.fabricaUsuarios  "Crea usuario"
        biblioteca.api.registrarUsuarioUC  -> biblioteca.api.repoUsuarios      "Guarda usuario"
        biblioteca.api.registrarUsuarioUC  -> biblioteca.api.auditoriaLogger   "Registra evento"

        biblioteca.api.registrarPrestamoUC -> biblioteca.api.solicitud         "Construye con Builder"
        biblioteca.api.registrarPrestamoUC -> biblioteca.api.catalogoAdapter   "Busca libro"
        biblioteca.api.registrarPrestamoUC -> biblioteca.api.pagosAdapter      "Cobra fianza"
        biblioteca.api.registrarPrestamoUC -> biblioteca.api.repoPrestamos     "Guarda préstamo"
        biblioteca.api.registrarPrestamoUC -> biblioteca.api.svcNotificacion   "Envía notificación"
        biblioteca.api.registrarPrestamoUC -> biblioteca.api.auditoriaLogger   "Registra evento"

        biblioteca.api.fabricaUsuarios -> biblioteca.api.estudianteComp       "Crea"
        biblioteca.api.fabricaUsuarios -> biblioteca.api.bibliotecarioComp    "Crea"
        biblioteca.api.fabricaUsuarios -> biblioteca.api.adminComp            "Crea"
        biblioteca.api.fabricaUsuarios -> biblioteca.api.posgradoComp         "Crea"

        biblioteca.api.solicitud -> biblioteca.api.usuario "Referencia estudiante"
        biblioteca.api.solicitud -> biblioteca.api.libro   "Referencia libro"

        biblioteca.api.notificacionEmail  -> biblioteca.api.svcNotificacion   "Implementa"
        biblioteca.api.autenticacionLDAP  -> biblioteca.api.svcAutenticacion  "Implementa"
        biblioteca.api.repoMySQL          -> biblioteca.api.repoPrestamos      "Implementa"
        biblioteca.api.repoMongo          -> biblioteca.api.repoPrestamos      "Implementa"
        biblioteca.api.repoMemoria        -> biblioteca.api.repoUsuarios       "Implementa"

        biblioteca.api.servicioMultas  -> biblioteca.api.pagosAdapter      "Cobra vía"
        biblioteca.api.servicioMultas  -> biblioteca.api.auditoriaLogger   "Registra evento"
        biblioteca.api.servicioLibros  -> biblioteca.api.auditoriaLogger   "Registra evento"

        biblioteca.api.estudianteComp    -> biblioteca.api.usuario "Implementa"
        biblioteca.api.bibliotecarioComp -> biblioteca.api.usuario "Implementa"
        biblioteca.api.adminComp         -> biblioteca.api.usuario "Implementa"
        biblioteca.api.posgradoComp      -> biblioteca.api.usuario "Implementa"
    }

    views {

        // Nivel 1 — Contexto
        systemContext biblioteca "C4_Nivel1_Contexto" {
            include *
            title "C4 Nivel 1 — Contexto del Sistema"
        }

        // Nivel 2 — Contenedores
        container biblioteca "C4_Nivel2_Contenedores" {
            include *
            title "C4 Nivel 2 — Contenedores"
        }

        // Nivel 3 — Componentes del API Backend
        component biblioteca.api "C4_Nivel3_Componentes" {
            include *
            title "C4 Nivel 3 — Componentes del API Backend"
        }

        styles {
            element "Element" {
                color #ffffff
                stroke #f8289c
                strokeWidth 5
                shape roundedbox
                background #1a1a2e
            }
            element "Person" {
                shape person
                background #16213e
                stroke #f8289c
            }
            element "Database" {
                shape cylinder
                background #0f3460
                stroke #00d4ff
            }
            element "External" {
                background #3d0000
                stroke #ff4444
            }
            element "WebApp" {
                background #003d1a
                stroke #00ff88
            }
            element "Worker" {
                background #1a1a00
                stroke #ffff00
            }
            element "Software System" {
                background #1a1a2e
                stroke #f8289c
            }
            element "Container" {
                background #16213e
                stroke #00d4ff
            }
            element "Component" {
                background #0f3460
                stroke #f8289c
            }
            element "Boundary" {
                strokeWidth 4
                stroke #f8289c
            }
            relationship "Relationship" {
                thickness 3
                color #f8289c
            }
        }
    }

    configuration {
        scope softwaresystem
    }
}