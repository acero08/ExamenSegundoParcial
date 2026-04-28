# Sección 4 — Flujo del préstamo, patrones y decisión de arquitectura

---

## Pregunta 4 — Flujo completo (10 pts)

Traza el flujo completo de la operación:

> "Un estudiante solicita un préstamo y el sistema cobra la fianza al Sistema de Pagos Bancario."

Tu respuesta debe:

1. Mostrar qué clases o componentes participan en orden cronológico.
2. Señalar en qué punto del flujo actúa cada uno de los 4 patrones estudiados.
3. Indicar en qué nivel C4 "vive" cada interacción del flujo.
4. Identificar una decisión arquitectónica que tomaste y justificar por qué es la correcta.

Esta pregunta evalúa que todos los conceptos funcionen en conjunto, no de forma aislada. No hay una única respuesta correcta si la justificación es sólida.

| Pregunta                             | Archivo                   |
| ------------------------------------ | ------------------------- |
| 4 — Diagrama de secuencia · PlantUML | `04-secuencia-flujo.puml` |
| 4 — Diagrama de secuencia exportado  | `SECUENCIA.png`           |

---

## Pasos del flujo (resumen operativo)

1. El **Estudiante** inicia la solicitud en la interfaz web.
2. La **Web App** recibe la acción y la envía al API Backend.
3. **FabricaDeUsuarios** crea el objeto de tipo `Estudiante` correcto. ← **Factory**
4. **RegistrarPrestamoUseCase** coordina el flujo principal.
5. **CatalogoCETYSAdapter** consulta el libro en el catálogo SOAP externo. ← **Adapter**
6. **SolicitudPrestamo.Builder** construye una solicitud válida e inmutable. ← **Builder**
7. **PagosAdapter** cobra la fianza en el sistema bancario REST externo. ← **Adapter**
8. **RepositorioPrestamos** persiste el préstamo en la base de datos.
9. La base de datos almacena la información.
10. **AuditoriaLogger** registra la acción en el log centralizado. ← **Singleton**
11. **ServicioNotificacion** envía confirmación al estudiante.

---

## Patrones en el flujo

| Patrón        | Dónde                                                                                               |
| ------------- | --------------------------------------------------------------------------------------------------- |
| **Singleton** | `AuditoriaLogger` — una sola instancia registra todos los eventos del sistema                       |
| **Factory**   | `FabricaDeUsuarios` — crea el tipo correcto de usuario sin que el cliente conozca la clase concreta |
| **Adapter**   | `CatalogoCETYSAdapter` — traduce SOAP · `PagosAdapter` — traduce REST bancario                      |
| **Builder**   | `SolicitudPrestamo.Builder` — construye la solicitud validando campos obligatorios antes de crearla |

---

## Niveles C4 de cada interacción

| Interacción                                         | Nivel C4               |
| --------------------------------------------------- | ---------------------- |
| Estudiante → Web App                                | Nivel 1 — Contexto     |
| Web App → API Backend                               | Nivel 2 — Contenedores |
| API Backend → Catálogo CETYS                        | Nivel 2 — Contenedores |
| API Backend → Sistema de Pagos                      | Nivel 2 — Contenedores |
| UseCase → CatalogoCETYSAdapter → CatalogoCETYS_SOAP | Nivel 3 — Componentes  |
| UseCase → PagosAdapter → SistemaPagosBancario       | Nivel 3 — Componentes  |
| UseCase → SolicitudPrestamo.Builder                 | Nivel 3 — Componentes  |
| UseCase → AuditoriaLogger                           | Nivel 3 — Componentes  |

---

## Decisión de arquitectura

Los Adapters (`CatalogoCETYSAdapter` y `PagosAdapter`) se colocaron en la capa de **infraestructura**, no en los use cases. El dominio define las interfaces `CatalogoBiblioteca` y `ServicioPagos`; la infraestructura las implementa. Si el banco cambia su API REST mañana, `RegistrarPrestamoUseCase` no se toca — solo se actualiza `PagosAdapter`. El costo del cambio queda contenido en el perímetro externo del sistema, que es exactamente lo que la Dependency Rule de Clean Architecture busca lograr.
