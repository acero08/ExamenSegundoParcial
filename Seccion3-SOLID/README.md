# Sección 3 — SOLID y Clean Architecture

---

## Pregunta 3A — Análisis de violaciones (8 pts)

Se te entrega el siguiente diseño problemático. La clase GestorBiblioteca contiene los métodos:

- prestarLibro()
- devolverLibro()
- registrarMulta()
- enviarEmailNotificacion()
- generarReportePDF()
- autenticarUsuario()
- consultarCatalogoCETYS()

Con base en lo anterior:

1. Identifica qué principios SOLID viola esta clase y explica por qué en cada caso.
2. Propón una refactorización: divide la clase en las entidades correctas. Dibuja el diagrama de clases resultante.
3. ¿Cómo se relaciona esta refactorización con la Dependency Rule de Clean Architecture?

---

## Pregunta 3B — Diseño de la capa de Use Cases (12 pts)

Siguiendo Clean Architecture, el caso de uso "Registrar préstamo" debe ser independiente de la base de datos y del framework web.

1. Define la interfaz del repositorio RepositorioPrestamos y ubícala en la capa arquitectónica correcta. Justifica la ubicación.
2. Implementa la clase RegistrarPrestamoUseCase que use esa interfaz (no la implementación concreta).
3. Explica cómo la Dependency Rule garantiza que cambiar de MySQL a MongoDB no requiera tocar el use case.
4. ¿Qué patrón de los estudiados en clase aparece implícitamente en este diseño?

---

| Pregunta                                     | Archivo                                                                                                                                                                                     |
| -------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 3A — Refactor · PlantUML                     | `DiagramaUml/03A-solid-refactorizacion.puml`                                                                                                                                                |
| 3A — Refactor · diagrama exportado           | `DiagramaUml/03A.png`                                                                                                                                                                       |
| 3A — Código refactorizado                    | `src/infraestructura/ServicioPrestamoLibros.java` · `src/infraestructura/ServicioMultas.java` · `src/infraestructura/AutenticacionLDAP.java` · `src/infraestructura/NotificacionEmail.java` |
| 3B — Clean Architecture · PlantUML           | `DiagramaUml/03B-clean-architecture.puml`                                                                                                                                                   |
| 3B — Clean Architecture · diagrama exportado | `DiagramaUml/3B.png`                                                                                                                                                                        |
| 3B — Puerto de persistencia                  | `src/usecases/RepositorioPrestamos.java`                                                                                                                                                    |
| 3B — Use Case                                | `src/usecases/RegistrarPrestamoUseCase.java`                                                                                                                                                |

---

## Respuesta 3A — Violaciones y refactor

**SRP (responsabilidad única):** `GestorBiblioteca` concentra préstamos, devoluciones, multas, correos, reportes, autenticación y catálogo en una sola clase — mezcla 7 motivos de cambio distintos. Si cambia el envío de correo, no debería tocarse la lógica de préstamos. En el proyecto esto se resolvió creando `ServicioPrestamoLibros` para préstamos y devoluciones, `ServicioMultas` para multas, `NotificacionEmail` para notificaciones y `AutenticacionLDAP` para autenticación — cada clase con una sola responsabilidad.

**OCP (abierto/cerrado):** al añadir otro canal de notificación como SMS, una clase gigante obliga a modificarla. En el proyecto se definió la interfaz `ServicioNotificacion` — para agregar SMS solo se crea una nueva implementación sin tocar `NotificacionEmail` ni ninguna clase existente.

**DIP (inversión de dependencias):** depender de concretos como SMTP, PDF, LDAP y SOAP acopla el núcleo a detalles de infraestructura. En el proyecto los use cases dependen de interfaces (`ServicioAutenticacion`, `ServicioNotificacion`, `CatalogoBiblioteca`) — las implementaciones concretas `AutenticacionLDAP` y `CatalogoCETYSAdapter` quedan en infraestructura.

**Relación con Clean Architecture:** al extraer servicios y puertos, la Dependency Rule se respeta: `RegistrarPrestamoUseCase` no conoce JDBC, PDF ni LDAP — solo interfaces. Las flechas de dependencia apuntan siempre hacia adentro.

---

## Respuesta 3B — RepositorioPrestamos y el caso de uso

La interfaz `RepositorioPrestamos` se ubica en la capa de **use cases** (`src/usecases/`), junto a `RegistrarPrestamoUseCase`, porque expresa una necesidad del negocio (persistir préstamos), no una tecnología concreta. Si se ubicara en infraestructura, el dominio dependería de infraestructura, violando la Dependency Rule.

`RegistrarPrestamoUseCase` recibe `RepositorioPrestamos` por constructor y nunca conoce si por debajo hay MySQL o MongoDB. Cambiar de motor de base de datos equivale a crear una clase nueva en infraestructura (`RepositorioPrestamoMongoDB`) y cambiar una línea de configuración. El UseCase no se toca.

El patrón que aparece implícitamente es el **Repository Pattern** — abstrae el mecanismo de persistencia detrás de una interfaz orientada al dominio.
