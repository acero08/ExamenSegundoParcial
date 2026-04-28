# Sección 2 — Patrones de diseño (código + UML)

---

## Pregunta 2A — Singleton · Registro de auditoría (10 pts)

El rector exige que exista un único registro de auditoría en todo el sistema. Implementa la clase AuditoriaLogger usando el patrón Singleton. Tu solución debe:

- Hacer imposible crear más de una instancia. Explica qué tuviste que hacer con el constructor y por qué.
- Exponer el método registrar(evento: String, usuario: String).
- Incluir el diagrama de clases UML resultante.
- Reflexión: ¿qué problema concreto de este sistema resuelve tener una sola instancia? ¿Qué pasaría si hubiera dos instancias simultáneas?

**¿Qué hice con el constructor?**

El constructor se declaró **privado** (`private AuditoriaLogger()`). Esto hace imposible que cualquier clase externa ejecute `new AuditoriaLogger()`. La única forma de obtener la instancia es a través del método estático `getInstancia()`. El campo `instancia` se declaró `volatile` para garantizar visibilidad correcta entre hilos concurrentes usando doble verificación con bloqueo (double-checked locking).

**Reflexión:**

Si existieran dos instancias simultáneas, cada una llevaría su propio registro parcial de eventos. Al consultar el log para una auditoría institucional los registros estarían incompletos e inconsistentes — el préstamo de un estudiante quedaría en la instancia 1 pero su multa en la instancia 2, haciendo imposible reconstruir su historial completo. El Singleton garantiza que todos los componentes del sistema escriben en el mismo log centralizado, cumpliendo la restricción crítica del rector.

| Pregunta                            | Archivo                             |
| ----------------------------------- | ----------------------------------- |
| 2A — Singleton · PlantUML           | `DiagramasUml/02A-singleton.puml`   |
| 2A — Singleton · diagrama exportado | `DiagramasUml/SINGLETON.png`        |
| 2A — Singleton · código             | `src/patrones/AuditoriaLogger.java` |

---

## Pregunta 2B — Factory · Creación de usuarios (10 pts)

El sistema debe crear objetos de tipo Usuario (Estudiante, Bibliotecario, Admin) sin que el código cliente conozca las clases concretas. Implementa una FabricaDeUsuarios y:

- Define la interfaz Usuario con métodos comunes.
- Implementa las tres clases concretas.
- Dibuja el diagrama de clases UML con la fábrica y las implementaciones.
- Muestra cómo agregar el tipo Posgrado en el futuro sin modificar el código existente. ¿Qué principio SOLID garantiza esto?

**Respuesta:**

Se definió la interfaz `Usuario` con tres métodos comunes: `getNombre()`, `getRol()` y `getLimitePrestamos()`. Se implementaron cuatro clases concretas: `Estudiante` con límite de 3 préstamos, `Bibliotecario` con límite de 10, `Admin` con límite de 0 ya que su rol es administrativo, y `Posgrado` con límite de 5.

Para agregar `Posgrado` en el futuro solo se creó la clase `Posgrado.java` que implementa `Usuario` y se añadió un caso en el switch de la fábrica. Las clases `Estudiante`, `Bibliotecario` y `Admin` no se tocaron en ningún momento.

**Principio SOLID que lo garantiza:** **OCP (Open/Closed Principle)** — el sistema está abierto para extensión mediante nuevas clases y cerrado para modificación del código que ya funciona.

| Pregunta                          | Archivo                               |
| --------------------------------- | ------------------------------------- |
| 2B — Factory · PlantUML           | `DiagramasUml/02B-factory.puml`       |
| 2B — Factory · diagrama exportado | `DiagramasUml/FACTORY.png`            |
| 2B — Factory · código             | `src/patrones/FabricaDeUsuarios.java` |

---

## Pregunta 2C — Adapter · Integración con el Catálogo CETYS (10 pts)

El sistema interno espera una interfaz CatalogoBiblioteca con el método:

`buscarLibro(isbn: String): Libro`

El Catálogo CETYS expone el método:

`consultarObra(codigoCETYS: String, formato: String): ResultadoSOAP`

Con base en lo anterior:

- Crea el adaptador que permita al sistema interno usar el catálogo CETYS sin modificar ninguna de las dos clases.
- Dibuja el diagrama UML con las tres clases involucradas y sus relaciones.
- Reflexión: si mañana CETYS cambia de proveedor de catálogo a uno con una interfaz completamente diferente, ¿cuánto código habría que modificar? ¿Por qué?

**Respuesta:**

Se creó `CatalogoCETYSAdapter` que implementa la interfaz interna `CatalogoBiblioteca` y contiene internamente una referencia a `CatalogoCETYS_SOAP`. Su método `buscarLibro` convierte el ISBN al formato de código CETYS, llama al servicio externo y transforma el `ResultadoSOAP` en un objeto `Libro` interno. Ninguna de las dos clases originales fue modificada.

**Reflexión:**

Si CETYS cambia de proveedor mañana, el costo de cambio es exactamente **una clase nueva** — el nuevo adaptador. El sistema interno, los casos de uso y el resto del sistema no se tocan. Esto es posible porque el Adapter actúa como un escudo: todo lo que sabe del sistema externo queda encapsulado en una sola clase.

| Pregunta                          | Archivo                                         |
| --------------------------------- | ----------------------------------------------- |
| 2C — Adapter · PlantUML           | `DiagramasUml/02C-adapter.puml`                 |
| 2C — Adapter · diagrama exportado | `DiagramasUml/ADAPTER.png`                      |
| 2C — Adapter · código             | `src/infraestructura/CatalogoCETYSAdapter.java` |

---

## Pregunta 2D — Builder · Solicitudes de préstamo (10 pts)

Una SolicitudPrestamo tiene los siguientes atributos:

| Atributo             | Tipo       | ¿Obligatorio? | Default |
| -------------------- | ---------- | ------------- | ------- |
| estudiante           | Estudiante | Sí            | —       |
| libro                | Libro      | Sí            | —       |
| fechaDevolucion      | Date       | Sí            | —       |
| notasEspeciales      | String     | No            | null    |
| renovacionAutomatica | boolean    | No            | false   |
| numRenovaciones      | int        | No            | 1       |

Con base en esto:

- Implementa un SolicitudPrestamoBuilder con métodos encadenables.
- El método construir() debe validar que los campos obligatorios estén presentes antes de crear el objeto.
- Muestra tres ejemplos de uso con distintas combinaciones de atributos opcionales.
- Reflexión: ¿por qué conviene que SolicitudPrestamo sea inmutable una vez construida? ¿Qué problemas evitamos?

**Tres ejemplos de uso:**

```java
// Ejemplo 1 — solo campos obligatorios
SolicitudPrestamo s1 = new SolicitudPrestamo.Builder()
    .estudiante(estudiante)
    .libro(libro)
    .fechaDevolucion(new Date())
    .construir();

// Ejemplo 2 — con notas especiales
SolicitudPrestamo s2 = new SolicitudPrestamo.Builder()
    .estudiante(estudiante)
    .libro(libro)
    .fechaDevolucion(new Date())
    .notasEspeciales("Requiere sala silenciosa")
    .construir();

// Ejemplo 3 — con renovación automática
SolicitudPrestamo s3 = new SolicitudPrestamo.Builder()
    .estudiante(posgrado)
    .libro(libro)
    .fechaDevolucion(new Date())
    .renovacionAutomatica(true)
    .numRenovaciones(3)
    .construir();
```

**Reflexión sobre inmutabilidad:**

Una `SolicitudPrestamo` inmutable evita que otro hilo modifique la fecha de devolución mientras el sistema de pagos está procesando la fianza — una condición de carrera real en este sistema. También garantiza que el log de auditoría refleje exactamente lo que fue aprobado originalmente. Si el objeto fuera mutable, alguien podría cambiar el libro después de que el log ya registró la solicitud original, creando inconsistencias imposibles de detectar.

| Pregunta                          | Archivo                              |
| --------------------------------- | ------------------------------------ |
| 2D — Builder · PlantUML           | `DiagramasUml/02D-builder.puml`      |
| 2D — Builder · diagrama exportado | `DiagramasUml/BUILDER.png`           |
| 2D — Builder · código             | `src/dominio/SolicitudPrestamo.java` |
