# Documentación Técnica — Sistema de Gestión Inmobiliaria

## 1. Resumen

Aplicación de escritorio en **Java 25 con Swing** para la administración integral de una inmobiliaria. Permite gestionar inmuebles (edificios, pisos y locales), inquilinos, contratos de alquiler, facturas de gastos y movimientos bancarios. La persistencia se maneja mediante archivos serializados (`.dat`).
La aplicación está organizada siguiendo una **arquitectura en capas**, típica de un diseño orientado a objetos bien estructurado:

- **Capa de modelo (`modelo`)**: contiene las entidades del dominio del negocio (Inmueble, Piso, Local, Edificio, Inquilino, Alquiler, Factura, MovimientoBancario).
- **Capa de persistencia (`repositorio`)**: encapsula el acceso y almacenamiento de los datos en disco mediante serialización de objetos Java.
- **Capa de servicio (`servicio`)**: concentra la lógica de negocio (reglas, validaciones de alto nivel, orquestación de operaciones entre entidades).
- **Capa de utilidades (`util`)**: validaciones reutilizables de bajo nivel sobre tipos de datos primitivos.
- **Capa de presentación (`vista`)**: interfaz gráfica Swing organizada en pestañas, cada una dedicada a un módulo funcional del sistema.

El punto de entrada de la aplicación es la clase `Main`, que inicializa el *look & feel* del sistema operativo y lanza la ventana principal (`VentanaPrincipal`) en el hilo de eventos de Swing (Event Dispatch Thread), como es la práctica recomendada para aplicaciones Swing.

---

## 2. Arquitectura del proyecto

```
src/main/java/com/inmobiliaria/
│
├── Main.java                         ← Punto de entrada. Aplica L&F, carga semilla, lanza la UI
│
├── modelo/                           ← Capa de dominio (entidades de negocio)
│   ├── Inmueble.java                 ← Clase abstracta base
│   ├── Edificio.java                 ← extends Inmueble
│   ├── Piso.java                     ← extends Inmueble
│   ├── Local.java                    ← extends Inmueble
│   ├── Inquilino.java               ← Datos personales, enums Sexo y TipoRespaldo
│   ├── Alquiler.java                ← Contrato entre inquilino e inmueble
│   ├── Factura.java                 ← Gasto asociado a un inmueble
│   └── MovimientoBancario.java      ← Ingreso o gasto asociado a un inmueble
│
├── repositorio/
│   └── RepositorioDatos.java         ← Singleton. CRUD + persistencia en .dat
│
├── servicio/
│   └── InmuebleServicio.java         ← Lógica de negocio. Orquesta validaciones y repo
│
├── util/
│   ├── Validador.java               ← Métodos estáticos de validación
│
└── vista/                            ← Interfaz gráfica (Swing)
    ├── VentanaPrincipal.java         ← JFrame con pestañas, header, botón salir
    ├── SwingUtil.java               ← Helpers: crearBoton, crearTextField, crearLabel
    ├── DialogoInmueble.java          ← JDialog para crear/editar inmuebles
    ├── PanelInmuebles.java           ← CRUD de inmuebles, búsqueda por dirección
    ├── PanelInquilinos.java          ← CRUD de inquilinos, búsqueda por nombre/cédula
    ├── PanelAlquileres.java          ← Alquilar y desalquilar inmuebles
    ├── PanelFacturas.java            ← Registrar y filtrar facturas
    └── PanelMovimientos.java         ← Registrar y filtrar movimientos bancarios
```
---
## 3. Explicación de las clases principales en detalle

### 3.1 `Inmueble` (clase abstracta — paquete `modelo`)

Es la **clase base** del sistema y el punto central de la jerarquía de herencia. Implementa `Serializable` para permitir su persistencia mediante serialización Java.

**Atributos encapsulados (privados, con getters/setters):**
`id`, `direccion`, `descripcion`, `codigoPostal`, `precioAlquiler`, `disponible` (booleano que indica si el inmueble está libre) e `inquilinoId` (referencia al inquilino actual, `null` si está disponible).

**Comportamiento clave:**
- Declara el método abstracto `getTipoInmueble()`, que cada subclase debe implementar devolviendo una cadena identificadora ("EDIFICIO", "PISO", "LOCAL"). Este método es la base del **polimorfismo** en el sistema: el código que recorre una lista de `Inmueble` puede invocar `getTipoInmueble()` sin conocer la subclase concreta, y obtiene en tiempo de ejecución el comportamiento correspondiente.
- Sobrescribe `toString()` para producir una representación textual uniforme, reutilizada por las subclases mediante `super.toString()` y extendida con sus propios atributos (otro ejemplo de polimorfismo combinado con reutilización de código vía herencia).
- Todo nuevo inmueble se construye con `disponible = true` e `inquilinoId = null`, garantizando un estado inicial coherente.
### 3.2 `Piso`, `Local` y `Edificio` (subclases de `Inmueble`)

Las tres heredan de `Inmueble` y representan especializaciones del concepto general:

- **`Piso`**: añade `numeroPiso`, `tipoEspacio` (Apartamento, Ático, Dúplex, etc.), `descripcionEspecifica` y `edificioId` (referencia opcional al edificio contenedor). Implementa `getTipoInmueble()` devolviendo `"PISO"`.
- **`Local`**: estructuralmente análogo a `Piso`, pero orientado a espacios comerciales/oficinas (`tipoLocal` en lugar de `tipoEspacio`). Implementa `getTipoInmueble()` devolviendo `"LOCAL"`.
- **`Edificio`**: añade `numeroPisos` (cantidad total de plantas) y `nombreEdificio`. Implementa `getTipoInmueble()` devolviendo `"EDIFICIO"`. A diferencia de `Piso` y `Local`, un `Edificio` no tiene `edificioId`, ya que actúa como contenedor de otros inmuebles, no como un inmueble contenido.
  Las tres subclases sobrescriben `toString()`, concatenando la representación heredada de `Inmueble` con sus atributos propios, lo que demuestra el uso combinado de **herencia** y **polimorfismo** de manera consistente en todo el modelo.

### 3.3 `Inquilino` (paquete `modelo`)

Representa a la persona que puede arrendar un inmueble. No hereda de ninguna clase del dominio (es una entidad independiente) pero contiene dos enumeraciones internas:

- **`Sexo`** (`MASCULINO`, `FEMENINO`, `OTRO`): cada valor lleva asociada una descripción legible en español, mostrada en la interfaz mediante `toString()` sobrescrito.
- **`TipoRespaldo`** (`NOMINA`, `AVAL_BANCARIO`, `CONTRATO_TRABAJO`, `AVAL_PERSONA`): describe el respaldo económico que presenta el inquilino para poder alquilar.
  Particularidad de diseño: el setter `setSexo(String)` valida que el valor recibido coincida con la descripción o el nombre de alguno de los valores del `enum Sexo`, lanzando `IllegalArgumentException` en caso contrario. Esto añade una capa de **validación de invariantes** directamente en el modelo, reforzando el encapsulamiento (no basta con tener atributos privados; también se protege la coherencia de su contenido).

### 3.4 `Alquiler` (paquete `modelo`)

Representa el contrato de arrendamiento entre un inquilino y un inmueble, mediante referencias por ID (`inquilinoId`, `inmuebleId`) en lugar de referencias directas a objetos. Contiene `fechaInicio`, `fechaFin` (nula mientras el contrato está vigente) y el indicador `activo`.

El método `finalizar(LocalDate fechaFin)` encapsula la regla de negocio de cierre de contrato: valida que la fecha de fin no sea nula ni anterior a la fecha de inicio, y solo entonces actualiza el estado a finalizado. De forma similar, el setter `setFechaInicio` valida coherencia con `fechaFin` si esta ya existe. Este es un buen ejemplo de cómo el modelo no es una simple bolsa de datos, sino que protege activamente sus propias reglas de consistencia temporal.

### 3.5 `Factura` y `MovimientoBancario` (paquete `modelo`)

Ambas clases representan eventos financieros asociados a un inmueble (por `inmuebleId`) y ambas usan un `enum` interno para tipificar el evento: `ConceptoFactura` en el caso de `Factura` (gastos de servicios y mantenimiento) y `TipoMovimiento` en el caso de `MovimientoBancario` (ingresos y egresos genéricos, incluyendo los generados automáticamente por el sistema al alquilar un inmueble). Esta decisión de diseño separa conceptualmente las "facturas" (documentos de gasto con proveedor) de los "movimientos bancarios" (flujo de caja general, que incluye también ingresos), permitiendo reportes financieros más completos sin mezclar ambos conceptos en una sola entidad.

### 3.6 `RepositorioDatos` (paquete `repositorio`)

Es el componente responsable de la **persistencia** y aplica el **patrón de diseño Singleton**: su constructor es privado y la única forma de obtener una instancia es a través del método estático `getInstance()`, que crea la instancia única la primera vez que se invoca (inicialización perezosa) y la reutiliza en todas las llamadas posteriores. Esto garantiza que toda la aplicación comparta un único punto de acceso a los datos en memoria, evitando inconsistencias por instancias duplicadas con datos divergentes.

Internamente mantiene:
- Dos colecciones indexadas por ID (`Map<String, Inmueble>` y `Map<String, Inquilino>`) para acceso rápido por clave.
- Tres listas (`List<Factura>`, `List<MovimientoBancario>`, `List<Alquiler>`) para entidades que no requieren acceso indexado frecuente por ID.
- Cinco contadores enteros independientes, uno por tipo de entidad, usados para generar identificadores legibles y únicos con formato `PREFIJO-00001`.
  Expone métodos de alta, baja, modificación y consulta (CRUD) para cada tipo de entidad, y tras cada operación de escritura invoca el método de guardado correspondiente, de forma que el estado en disco nunca queda desincronizado con el estado en memoria. También expone operaciones de "limpieza de huérfanos" (`eliminarFacturasDeInmueble`, `eliminarMovimientosDeInmueble`, `eliminarAlquileresDeInmueble`), utilizadas cuando se elimina un inmueble, para mantener la integridad referencial pese a no usar una base de datos relacional con claves foráneas.

### 3.7 `InmuebleServicio` (paquete `servicio`)

Constituye la **capa de lógica de negocio**, intermedia entre la interfaz gráfica y el repositorio. Mantiene una única referencia al repositorio (`RepositorioDatos.getInstance()`) y nunca expone directamente las estructuras internas del repositorio a la vista.

Responsabilidades principales:
- Orquestar operaciones que afectan a varias entidades a la vez. El ejemplo más representativo es `alquilarInmueble(...)`, que en una sola operación de negocio: valida la disponibilidad del inmueble, crea el `Alquiler`, actualiza el estado del `Inmueble`, y genera un `MovimientoBancario` de ingreso de forma automática.
- Aplicar validaciones de negocio antes de delegar en el repositorio (por ejemplo, comprobar que un `edificioId` indicado realmente exista y sea un `Edificio`, o que una cédula no esté duplicada antes de registrar un inquilino), apoyándose en la clase `Validador` para las validaciones de formato más básicas.
- Implementar reglas de integridad que protegen al usuario de eliminar datos que romperían la coherencia del sistema (no se puede eliminar un inmueble alquilado, ni un inquilino con alquileres activos).
  Esta clase es la única que la capa de vista debería conocer directamente; ninguna clase de `vista` accede a `RepositorioDatos` de forma directa, lo que respeta el principio de **separación de responsabilidades** y facilita las pruebas unitarias de la lógica de negocio de forma aislada de Swing.

### 3.8 `Validador` (paquete `util`)

Clase utilitaria compuesta exclusivamente por métodos `static`, sin estado (no se instancia). Centraliza reglas de validación de formato reutilizables en distintos puntos del sistema: cédula, edad, valores numéricos positivos, medio de contacto (email o teléfono) y código postal. Cada método lanza `IllegalArgumentException` con un mensaje descriptivo en español cuando la validación falla, mensaje que posteriormente es capturado y mostrado al usuario en la interfaz gráfica. Esta centralización evita duplicar lógica de validación en cada panel de la interfaz o en el servicio.

### 3.9 `VentanaPrincipal` y clases del paquete `vista`

`VentanaPrincipal` extiende `JFrame` y actúa como contenedor raíz de la interfaz. Define la paleta de colores corporativa y las fuentes tipográficas como constantes estáticas compartidas por todos los paneles, organiza el contenido en un `JTabbedPane` con pestañas verticales (una por cada módulo funcional) y gestiona el cierre seguro de la aplicación mediante confirmación del usuario.

Cada módulo funcional tiene su propio panel (`PanelInmuebles`, `PanelInquilinos`, `PanelAlquileres`, `PanelFacturas`, `PanelMovimientos`), todos extienden `JPanel` y siguen un patrón consistente: una `JTable` respaldada por un `DefaultTableModel` no editable directamente (las celdas no son editables desde la tabla; la edición se realiza mediante diálogos), formularios de alta/edición que usan `JComboBox` para los campos basados en `enum` (evitando errores de tipeo del usuario) y botones de acción que invocan a `InmuebleServicio`. `DialogoInmueble` es un `JDialog` reutilizado tanto para alta como edición de inmuebles, adaptando dinámicamente sus campos según el tipo seleccionado (Edificio/Piso/Local) mediante un `JComboBox` de tipo. `SwingUtil` es una clase utilitaria de la capa de vista que centraliza la creación de componentes Swing con estilo uniforme (botones, campos de texto, etiquetas) y la presentación de avisos, evitando duplicar código de estilizado en cada panel.
 
---

## 4. Los cuatro pilares de POO

### 4.1 Encapsulamiento

Todos los atributos de las clases del modelo son **privados** (`private`). El acceso se controla mediante getters y setters públicos. Esto protege el estado interno y permite validar antes de modificar.

**Ejemplos concretos:**

```java
// Inmueble.java — todos los campos privados
private String id;
private String direccion;
private boolean disponible;
// ...

// El estado solo se modifica a través de setters
public void setDisponible(boolean disponible) { this.disponible = disponible; }
```

```java
// Inquilino.java:75-85 — setter con validación interna
public void setSexo(String sexo) {
    boolean valido = false;
    for (Sexo s : Sexo.values()) {
        if (s.getDescripcion().equals(sexo) || s.name().equals(sexo)) {
            valido = true;
            break;
        }
    }
    if (!valido) throw new IllegalArgumentException("Sexo no válido.");
    this.sexo = sexo;
}
```

```java
// RepositorioDatos.java — colecciones privadas, acceso solo por métodos públicos
private Map<String, Inmueble> inmuebles = new HashMap<>();
private List<Factura> facturas = new ArrayList<>();

public List<Factura> getTodasFacturas() {
    return new ArrayList<>(facturas);  // devuelve copia, no la original
}
```

**También aplica en las vistas:** `DialogoInmueble.guardado` es `private` y solo se consulta mediante `isGuardado()`.

---

### 4.2 Abstracción

**`Inmueble`** es una **clase abstracta**. Define el contrato común para todos los inmuebles (ID, dirección, precio, disponibilidad) y declara `getTipoInmueble()` como **método abstracto** que cada subclase implementa.

```java
// Inmueble.java
public abstract class Inmueble implements Serializable {
    public abstract String getTipoInmueble();
    // implementación común de toString(), getters, setters...
}

// Piso.java
@Override
public String getTipoInmueble() { return "PISO"; }

// Edificio.java
@Override
public String getTipoInmueble() { return "EDIFICIO"; }
```

**También aplica en la capa de servicio:** `InmuebleServicio` abstrae la lógica de negocio. Los paneles de la vista nunca acceden directamente al repositorio; toda operación pasa por el servicio, que aplica validaciones y reglas de negocio.

```java
// PanelInmuebles.java — la vista depende del servicio, no del repositorio
servicio.eliminarInmueble(id);  // no sabe cómo se persiste
```

---

### 4.3 Herencia

La jerarquía de inmuebles es el ejemplo más claro:

```
Inmueble (abstracta)
├── Edificio    → agrega nombreEdificio, numeroPisos
├── Piso        → agrega numeroPiso, tipoEspacio, descripcionEspecifica, edificioId
└── Local       → agrega numeroPiso, tipoLocal, descripcionEspecifica, edificioId
```

Cada subclase comparte los atributos de `Inmueble` (dirección, precio, código postal, disponibilidad) y agrega los suyos propios. El constructor de cada subclase invoca `super()` pasando los parámetros comunes.

```java
// Edificio.java
public Edificio(...) {
    super(id, direccion, descripcion, codigoPostal, precioAlquiler);
    this.numeroPisos = numeroPisos;
    this.nombreEdificio = nombreEdificio;
}
```

También hay herencia en la capa de vista: `DialogoInmueble extends JDialog`, cada `Panel* extends JPanel`, y `VentanaPrincipal extends JFrame`.

---

### 4.4 Polimorfismo

**Polimorfismo por subtipado:** El repositorio almacena todos los inmuebles en un `Map<String, Inmueble>`. Puede contener indistintamente `Edificio`, `Piso` o `Local`. Cuando se itera la colección y se llama a `getTipoInmueble()` o `toString()`, Java resuelve en tiempo de ejecución qué implementación usar.

```java
// RepositorioDatos.java
private Map<String, Inmueble> inmuebles = new HashMap<>();

// En tiempo de ejecución, cada objeto responde con su propio toString() y getTipoInmueble()
for (Inmueble inm : inmuebles.values()) {
    System.out.println(inm.toString());
    // Edificio → "[EDIFICIO] Calle Mayor | ... | Nombre: Torre Azul | Pisos: 10"
    // Piso     → "[PISO] Calle Luna | ... | Piso Nº1 | Tipo: Duplex"
}
```

**Polimorfismo con `instanceof`:** El diálogo de edición y el servicio usan `instanceof` para tratar cada subclase según su tipo concreto:

```java
// DialogoInmueble.java
if (inm instanceof Edificio e) {
    servicio.modificarEdificio(e.getId(), pisos, nombre);
} else if (inm instanceof Piso p) {
    servicio.modificarPiso(p.getId(), nPiso, tEsp, dEsp, edId);
}

// InmuebleServicio.java
if (!(edificioRef instanceof Edificio)) {
    throw new IllegalArgumentException("El ID no existe o no es un edificio.");
}
```

**Polimorfismo paramétrico (genéricos):** El método `cargarArchivo()` en el repositorio usa un tipo genérico `<T>` para cargar cualquier tipo de colección serializada:

```java
private <T> T cargarArchivo(String ruta, T porDefecto) { ... }
// Se invoca con distintos tipos:
inmuebles   = cargarArchivo(ARCHIVO_INMUEBLES,   new HashMap<>());
facturas    = cargarArchivo(ARCHIVO_FACTURAS,    new ArrayList<>());
```

---

## 5. Principios SOLID

### 5.1 S — Single Responsibility (Responsabilidad única)

Cada clase tiene una sola razón para cambiar:

| Clase | Responsabilidad |
|-------|----------------|
| `Inmueble` / `Piso` / `Local` / `Edificio` | Representar una entidad del dominio |
| `Inquilino` | Representar datos personales de un inquilino |
| `Alquiler` | Representar un contrato de alquiler |
| `Factura` | Representar un gasto |
| `MovimientoBancario` | Representar un ingreso o egreso |
| `RepositorioDatos` | Persistir y recuperar datos (Singleton) |
| `InmuebleServicio` | Aplicar reglas de negocio y validaciones |
| `Validador` | Validar formatos y reglas de entrada |
| `SwingUtil` | Crear componentes Swing con el estilo corporativo |
| `PanelInmuebles` | Interfaz para gestionar inmuebles |
| `PanelInquilinos` | Interfaz para gestionar inquilinos |
| `PanelAlquileres` | Interfaz para alquilar/desalquilar |
| `PanelFacturas` | Interfaz para facturas |
| `PanelMovimientos` | Interfaz para movimientos bancarios |
| `DialogoInmueble` | Formulario modal para crear/editar un inmueble |
| `VentanaPrincipal` | Contenedor principal, pestañas, header |

Ninguna clase mezcla lógica de negocio con presentación, ni validación con persistencia.

---

### 5.2 O — Open/Closed (Abierto a extensión, cerrado a modificación)

**Agregar un nuevo tipo de inmueble** no requiere modificar `Inmueble`, `RepositorioDatos` ni los paneles existentes. Basta con crear una nueva subclase:

```java
public class Parking extends Inmueble {
    private int numeroPlaza;
    @Override public String getTipoInmueble() { return "PARKING"; }
    // ...
}
```

El repositorio ya maneja `Inmueble` de forma polimórfica — aceptará `Parking` sin tocar una línea del código existente.

**Agregar un nuevo tipo de validación** solo implica agregar un método estático en `Validador`. Los métodos existentes no se modifican.

**Agregar un nuevo concepto de factura** solo requiere agregar un valor al enum `ConceptoFactura`.

---

### 5.3 L — Liskov Substitution (Sustitución de Liskov)

Todas las subclases de `Inmueble` pueden usarse donde se espera un `Inmueble` sin romper el comportamiento:

```java
// El repositorio trata a todos como Inmueble
Inmueble inm = repo.buscarInmueblePorId("INM-00001");
// inm puede ser Edificio, Piso o Local — el comportamiento es correcto en todos los casos

// toString() devuelve representación adecuada según la subclase
// isDisponible() y getPrecioAlquiler() funcionan igual en todas
```

Las subclases no alteran las precondiciones ni postcondiciones de los métodos heredados. `getTipoInmueble()` siempre devuelve un string no nulo. `toString()` siempre incluye la información base más la específica (llama a `super.toString()` y concatena).

---

### 5.4 I — Interface Segregation (Segregación de interfaces)

Aunque el proyecto no define interfaces propias, el principio se manifiesta en el diseño de la capa de vista y servicio:

- **`PanelInmuebles`** solo conoce los métodos de `InmuebleServicio` que necesita para CRUD de inmuebles. No se le pasan métodos de facturas o alquileres si no los usa.
- **`PanelFacturas`** recibe el mismo `InmuebleServicio` pero solo invoca `registrarFactura()`, `consultarFacturasPorPeriodo()` y `getTodasFacturas()`.
- **`SwingUtil`** expone 4 métodos independientes. Un panel que solo necesita botones no está obligado a conocer `crearLabel()`.

Cada vista depende solo de los métodos del servicio que efectivamente utiliza, aunque el servicio ofrezca más.

---

### 5.5 D — Dependency Inversion (Inversión de dependencias)

**Las vistas dependen de una abstracción, no de una implementación concreta:**

```java
// PanelInmuebles recibe InmuebleServicio por constructor
public PanelInmuebles(InmuebleServicio servicio) { ... }

// InmuebleServicio depende del Singleton RepositorioDatos,
// pero siempre a través de su interfaz pública (métodos, no atributos)
private final RepositorioDatos repo = RepositorioDatos.getInstance();
```

Los paneles no conocen `RepositorioDatos`, no saben cómo se persisten los datos ni dónde. Si se cambiara la persistencia de archivos `.dat` a base de datos, solo habría que modificar `RepositorioDatos`; las vistas y el servicio no se tocan.

---

## 6. Funcionalidades — flujo detallado

Cada funcionalidad sigue la misma arquitectura en tres capas:

1. **Vista (Swing):** captura la acción del usuario, muestra formularios y resultados
2. **Servicio (`InmuebleServicio`):** valida los datos y aplica reglas de negocio
3. **Repositorio (`RepositorioDatos`):** persiste o recupera la información en archivos `.dat`

---

### 6.1 Registrar un inmueble (Edificio, Piso o Local)

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "＋ Nuevo Inmueble" en `PanelInmuebles` (color dorado `COLOR_ACENTO`) |
| **Vista** | `PanelInmuebles.abrirFormularioNuevo()` → abre `DialogoInmueble(servicio, null)` como modal |
| **Campos del formulario** | `DialogoInmueble.contruirUI()`: `JComboBox` de tipo (EDIFICIO/PISO/LOCAL), `JTextField` para dirección, descripción, código postal, precio. Según el tipo seleccionado, se muestran campos adicionales (nombre del edificio + número de pisos, o número de piso + tipo de espacio + descripción específica + ID de edificio opcional) |
| **Validación en UI** | `validar()` lanza `IllegalArgumentException` si los campos obligatorios están vacíos. `Validador.validarCodigoPostal()` si solo dígitos y entre 1000-99999. `Validador.validarPositivo()` para precio > 0 |
| **Validación de edificio** | Si el usuario ingresa un `edificioId`, el servicio valida que ese ID exista y sea `instanceof Edificio` (servicio línea 35-40, 53-58) |
| **Método del servicio** | `registrarEdificio()`, `registrarPiso()` o `registrarLocal()` según el tipo |
| **Repositorio** | `generarIdInmueble()` → ID autoincremental `INM-00001`. `agregarInmueble(inmueble)` escribe en `inmuebles.dat` |
| **Persistencia** | Cada alta persiste inmediatamente: `guardarInmuebles()` serializa todo el `HashMap` |
| **Éxito** | La tabla en `PanelInmuebles` se refresca con `cargarTabla(servicio.getTodosInmuebles())`. Mensaje "Inmueble guardado correctamente." |
| **Error** | `NumberFormatException` → "Ingrese valores numéricos válidos en Precio y Número de Piso." `IllegalArgumentException` → muestra el mensaje de validación (ej. "El campo X es obligatorio.") |

**Ejemplo de flujo completo — registrar un Piso:**

```
Usuario: hace clic en "＋ Nuevo Inmueble"
PanelInmuebles → DialogoInmueble(servicio, null)
Usuario: completa "Dirección: Calle Mayor",
         "Descripción: Piso luminoso", "Código Postal: 28001",
         "Precio: 850", tipo PISO → "Nº piso: 3", "Tipo: Apartamento",
         "Desc. específica: 2 habitaciones", "ID Edificio: INM-00001"
Usuario: clic "Guardar"
DialogoInmueble.guardar():
  1. validar() — todos los campos obligatorios rellenos ✅
  2. Validador.validarCodigoPostal("28001") — pasa ✅
  3. Validador.validarPositivo(850, "Precio") — pasa ✅
  4. servicio.registrarPiso(...):
     a. Validador.validarCodigoPostal() ✅
     b. repo.buscarInmueblePorId("INM-00001") → Edificio ✅
     c. repo.generarIdInmueble() → "INM-00004"
     d. new Piso(...) con todos los datos
     e. repo.agregarInmueble(piso) → guarda en inmuebles.dat
  5. Mensaje: "Inmueble guardado correctamente."
  6. dispose() — cierra el diálogo
PanelInmuebles: cargarTabla() — actualiza la tabla
```

---

### 6.2 Editar un inmueble

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "✏ Editar" en `PanelInmuebles` |
| **Selección previa** | El usuario debe seleccionar una fila en la tabla. Si no, se muestra "Seleccione un inmueble para editar." |
| **Vista** | `PanelInmuebles.editarSeleccionado()` → abre `DialogoInmueble(servicio, inmueble)` con sus datos precargados |
| **Precarga** | `DialogoInmueble.precargarDatos()` setea los `JTextField` con los valores actuales. El tipo de inmueble se bloquea (`cmbTipo.setEnabled(false)`) para evitar cambiar un piso a local |
| **Campos editables** | Todos: dirección, descripción, código postal, precio, y los campos específicos del tipo (si se edita un Piso, se puede cambiar el número de piso, tipo de espacio, descripción específica y edificio asociado) |
| **Servicio** | `servicio.modificarInmueble(id, direccion, descripcion, cp, precio)` actualiza los campos comunes. Luego según el tipo: `modificarPiso()`, `modificarLocal()` o `modificarEdificio()` |
| **Persistencia** | `actualizarInmueble()` reemplaza el objeto en el `HashMap` y persiste en `inmuebles.dat` |
| **Seguridad** | Si se edita el `edificioId` de un Piso/Local, se vuelve a validar que el ID referenciado exista y sea un `Edificio` |

---

### 6.3 Eliminar un inmueble

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "🗑 Eliminar" en `PanelInmuebles` |
| **Validación previa** | Solo se puede eliminar un inmueble si `isDisponible() == true` (no está ocupado). El servicio valida esto y retorna `false` si no se puede |
| **Confirmación** | Diálogo `YES_NO_OPTION`: "Se eliminarán también todas sus facturas, movimientos y alquileres asociados. ¿Desea continuar?" |
| **Limpieza de huérfanos** | Antes de eliminar, se llaman 3 métodos: `eliminarAlquileresDeInmueble(id)`, `eliminarFacturasDeInmueble(id)`, `eliminarMovimientosDeInmueble(id)`. Cada uno usa `removeIf()` sobre su lista correspondiente y persiste |
| **Servicio** | `eliminarInmueble(id)` → valida disponibilidad → limpia huérfanos → `repo.eliminarInmueble(id)` |
| **Éxito** | Mensaje "Inmueble eliminado correctamente." + refresco de tabla |
| **Error** | Si el inmueble está ocupado: "NO se puede eliminar: el inmueble está ocupado. Desalquile primero." |

---

### 6.4 Buscar inmuebles por dirección

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Campo de texto "Buscar por dirección:" + botón "Buscar" en `PanelInmuebles` |
| **Ejecución** | `PanelInmuebles.buscar()` → `servicio.consultarPorDireccion(texto)` → `repo.buscarInmueblesPorDireccion(direccion)` |
| **Búsqueda** | Case-insensitive: `inm.getDireccion().toLowerCase().contains(busq)` |
| **Resultado** | La tabla se carga solo con los inmuebles que coinciden. Etiqueta: "X resultado(s) para el texto." |
| **Ver Todos** | Botón "Ver Todos" → `cargarTabla(servicio.getTodosInmuebles())` |

---

### 6.5 Registrar un inquilino

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "＋ Registrar Inquilino" en `PanelInquilinos` |
| **Vista** | `PanelInquilinos.abrirFormulario()` → diálogo con `JOptionPane.showConfirmDialog()` con campos: nombre, cédula, edad, sexo (`JComboBox<Inquilino.Sexo>`), medio de contacto, tipo de respaldo (`JComboBox<Inquilino.TipoRespaldo>`) |
| **Validaciones** | 1. `req()`: nombre, cédula y contacto no pueden estar vacíos. 2. `Validador.validarCedula()`: solo dígitos, 4-15 caracteres. 3. `Integer.parseInt(edad)`: captura `NumberFormatException`. 4. `Validador.validarEdad()`: entre 18 y 120. 5. `Validador.validarContacto()`: email (contiene @ y .) o teléfono (solo dígitos, 8-15). 6. El sexo se selecciona de un `JComboBox`, siempre es válido |
| **Servicio** | `registrarInquilino(nombre, cedula, edad, sexo, contacto, respaldo)` → llama a `Validador.validarCedula()` + `repo.existeCedula()` (si ya existe: "Ya existe un inquilino con esa cédula.") → `generarIdInquilino()` (INQ-00001) → `agregarInquilino()` en `inquilinos.dat` |
| **Éxito** | Mensaje "Inquilino registrado con ID: INQ-00001" + refresco de tabla |
| **Error** | `NumberFormatException` → "La edad debe ser un número." `IllegalArgumentException` → muestra el mensaje de validación (cédula inválida, edad fuera de rango, contacto inválido) |

---

### 6.6 Eliminar un inquilino

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "🗑 Eliminar" en `PanelInquilinos` |
| **Validación** | `servicio.eliminarInquilino(id)` → si `repo.tieneAlquileresActivos(id)` retorna `false`. Solo permite eliminar inquilinos sin contratos activos |
| **Confirmación** | Diálogo `YES_NO_OPTION`: "¿Eliminar inquilino INQ-00001?" |
| **Éxito** | Mensaje "Inquilino eliminado correctamente." + refresco de tabla |
| **Error** | "NO se puede eliminar: tiene alquileres activos. Desalquile primero." |

---

### 6.7 Buscar inquilinos por nombre o cédula

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Campo "Buscar por nombre o cédula:" + botón "Buscar" en `PanelInquilinos` |
| **Ejecución** | `PanelInquilinos.buscar()` → filtra localmente sobre `servicio.getTodosInquilinos()`: `inq.getNombre().toLowerCase().contains(busq) \|\| inq.getCedula().contains(busq)` |
| **Resultado** | La tabla se recarga solo con los inquilinos que coinciden |
| **Ver Todos** | Botón "Ver Todos" → `actualizar()` recarga todos los inquilinos |

---

### 6.8 Alquilar un inmueble

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "Alquilar Inmueble" en `PanelAlquileres` |
| **Paso 1 — elegir inmueble** | Se muestran solo los inmuebles con `isDisponible() == true` en un `JComboBox` con formato `"INM-00001 — PISO \| Calle Mayor Nº1 \| $850"`. Si no hay disponibles: "No hay inmuebles disponibles actualmente." |
| **Paso 2 — elegir inquilino** | Se listan todos los inquilinos registrados en otro `JComboBox`. Si no hay: "No hay inquilinos registrados. Registre uno primero." |
| **Confirmación** | `showConfirmDialog` con los dos `JComboBox` |
| **Servicio** | `alquilarInmueble(inmuebleId, inquilinoId)` valida: ambos existen, inmueble disponible. Luego: |
| | 1. `generarIdAlquiler()` → "ALQ-00001" |
| | 2. `new Alquiler(id, inquilinoId, inmuebleId, LocalDate.now())` |
| | 3. `inm.setDisponible(false)` + `inm.setInquilinoId(inquilinoId)` |
| | 4. `actualizarInmueble(inm)` → persiste en `inmuebles.dat` |
| | 5. `agregarAlquiler(alq)` → persiste en `alquileres.dat` |
| | 6. **Movimiento bancario automático:** crea `MovimientoBancario` con `INGRESO_ALQUILER`, fecha actual, importe = `inm.getPrecioAlquiler()`, persona = nombre del inquilino |
| | 7. `agregarMovimiento(mov)` → persiste en `movimientos.dat` |
| **Éxito** | Mensaje "Alquiler registrado con ID: ALQ-00001. El inmueble ahora figura como OCUPADO." |
| **Error** | "No se pudo registrar el alquiler." (raro: ocurre solo si el inmueble dejó de estar disponible entre que se mostró el combo y se confirmó) |

**Ejemplo de flujo completo — Alquilar:**

```
Usuario: clic "Alquilar Inmueble"
PanelAlquileres:
  inmuebles = disp. stream().filter(isDisponible).toList()
  inquilinos = getTodosInquilinos()
  Muestra combos con opciones
Usuario: selecciona "INM-00003 — PISO | Calle Mayor Nº1 | $850"
         selecciona "INQ-00001 — Juan Pérez | 12345678"
         clic OK
servicio.alquilarInmueble("INM-00003", "INQ-00001"):
  1. inm = repo.buscar...("INM-00003") → existe y disponible ✅
  2. inq = repo.buscar...("INQ-00001") → existe ✅
  3. repo.generarIdAlquiler() → "ALQ-00001"
  4. new Alquiler(...) con LocalDate.now()
  5. inm.setDisponible(false)
  6. repo.actualizarInmueble(inm) → inmuebles.dat actualizado
  7. repo.agregarAlquiler(alq) → alquileres.dat actualizado
  8. new MovimientoBancario(INGRESO_ALQUILER, "INM-00003", hoy, 850.0, "Juan Pérez")
  9. repo.agregarMovimiento(mov) → movimientos.dat actualizado
  Retorna "ALQ-00001"
PanelAlquileres: mensaje de éxito, actualizar()
```

---

### 6.9 Desalquilar un inmueble

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "Desalquilar Inmueble" en `PanelAlquileres` |
| **Paso 1 — elegir inmueble** | Se muestran solo los inmuebles ocupados (`!isDisponible()`) en un `JComboBox`. Si no hay: "No hay inmuebles ocupados actualmente." |
| **Confirmación** | "¿Confirmar desalquiler del inmueble INM-00003?" |
| **Servicio** | `desalquilarInmueble(inmuebleId)` → valida que exista y no esté disponible. Luego: |
| | 1. Obtiene `getAlquileresActivosPorInmueble(inmuebleId)` |
| | 2. Para cada alquiler activo: `alq.finalizar(LocalDate.now())` — valida que la fecha actual sea posterior a fechaInicio (Fase 3.8) |
| | 3. `actualizarAlquiler(alq)` → persiste en `alquileres.dat` |
| | 4. `inm.setDisponible(true)` + `inm.setInquilinoId(null)` |
| | 5. `actualizarInmueble(inm)` → persiste en `inmuebles.dat` |
| **Éxito** | "Inmueble liberado y marcado DISPONIBLE." |
| **Error** | Si no se pudo desalquilar: mensaje de error (raro) |

---

### 6.10 Registrar una factura

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "＋ Registrar Factura" en `PanelFacturas` |
| **Vista** | `PanelFacturas.registrar()` → diálogo con `JComboBox<String>` de inmuebles, `JTextField` para fecha (precargada con hoy en formato `dd/MM/yyyy`), `JComboBox<ConceptoFactura>`, `JTextField` para proveedor y otro para costo |
| **Validaciones en UI** | 1. `tfProv.getText().trim().isBlank()` → "El proveedor es obligatorio." 2. `Double.parseDouble(costo)` → captura `NumberFormatException`. 3. `Validador.validarPositivo(costo, "Costo")` → mayor a cero. 4. `LocalDate.parse(fecha, FMT_VISTA)` → captura `DateTimeParseException` |
| **Servicio** | `registrarFactura(inmuebleId, fecha, concepto, proveedor, costo)` → `generarIdFactura()` (FAC-00001) → `new Factura(...)` → `agregarFactura()` en `facturas.dat` |
| **Éxito** | "Factura registrada con ID: FAC-00001." + refresco de tabla |
| **Errores** | `NumberFormatException` → "El costo debe ser un número válido y mayor a cero." `DateTimeParseException` → "Use dd/MM/yyyy (ej: 15/01/2024)." `IllegalArgumentException` → "El proveedor es obligatorio." |

---

### 6.11 Filtrar facturas por inmueble y período

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "Filtrar" en `PanelFacturas` |
| **Campos** | "Inmueble (ID o dirección):", "Desde (dd/MM/yyyy):", "Hasta (dd/MM/yyyy):" |
| **Ejecución** | `PanelFacturas.filtrar()`: si el campo inmueble está vacío, muestra todas. Si no: |
| | 1. Parsea fechas con `FMT_VISTA`. Si están vacías, usa fecha por defecto (desde = 2000-01-01, hasta = hoy) |
| | 2. Itera `servicio.getTodosInmuebles()` y compara: `inm.getId().toLowerCase().contains(texto) \|\| inm.getDireccion().toLowerCase().contains(texto)` |
| | 3. Para cada inmueble que coincide, suma sus facturas con `consultarFacturasPorPeriodo()` |
| **Repo** | `getFacturasPorInmuebleYPeriodo(inmuebleId, desde, hasta)` → filtra con `f.getInmuebleId().toLowerCase().contains(inmuebleId.toLowerCase())` y rango de fechas |
| **Ver Todas** | Botón "Ver Todas" → `cargar(servicio.getTodasFacturas())` |

---

### 6.12 Registrar un movimiento bancario

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "＋ Registrar Movimiento" en `PanelMovimientos` |
| **Vista** | `PanelMovimientos.registrar()` → diálogo con `JComboBox<String>` de inmuebles, `JComboBox<TipoMovimiento>`, fecha (hoy en `dd/MM/yyyy`), importe, persona/entidad |
| **Validaciones** | `Double.parseDouble(importe)` → `NumberFormatException`. `Validador.validarPositivo(importe, "Importe")`. `tfEntidad.isBlank()` → "La persona o entidad es obligatoria." |
| **Servicio** | `registrarMovimiento(inmuebleId, tipo, fecha, importe, entidad)` → `generarIdMovimiento()` (MOV-00001) → `new MovimientoBancario(...)` → `agregarMovimiento()` en `movimientos.dat` |
| **Éxito** | "Movimiento registrado con ID: MOV-00001." + refresco de tabla |
| **Errores** | Mismos que en facturas: `NumberFormatException` para importe, `DateTimeParseException` para fecha, `IllegalArgumentException` para campos vacíos |

---

### 6.13 Filtrar movimientos por inmueble y período

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | Botón "Filtrar por Inmueble y Período" en `PanelMovimientos` |
| **Campos** | "Inmueble (ID o dirección):", "Desde (dd/MM/yyyy):", "Hasta (dd/MM/yyyy):" |
| **Ejecución** | `PanelMovimientos.filtrar()`: mismo mecanismo que facturas: busca por ID o dirección, filtra por período |
| **Repo** | `getMovimientosPorInmuebleYPeriodo()` busca con `toLowerCase().contains()` y ordena por fecha ascendente (`Comparator.comparing(MovimientoBancario::getFecha)`) |
| **Ver Todos** | Botón "Ver Todos" → `cargar(servicio.getTodosMovimientos())` |


---

## 7. Validaciones implementadas

Centralizadas en la clase `Validador`:

| Método | Regla |
|--------|-------|
| `validarCedula(String)` | No vacío, solo dígitos, entre 4 y 15 caracteres |
| `validarEdad(int)` | Mínimo 18, máximo 120 |
| `validarPositivo(double, String)` | Valor estrictamente mayor a cero |
| `validarContacto(String)` | Email (contiene @ y .) o teléfono (solo dígitos, 8-15 caracteres) |
| `validarCodigoPostal(String)` | Solo dígitos, entre 1000 y 99999 |

Validaciones adicionales en el servicio y las vistas:

- **Cédula duplicada:** `RepositorioDatos.existeCedula()` verifica antes de registrar
- **Inquilino con alquileres activos:** no se puede eliminar
- **Inmueble ocupado:** no se puede eliminar
- **EdificioID referenciado:** al registrar piso/local, se valida que el edificio exista y sea realmente un `Edificio`
- **Fechas:** `finalizar()` exige fecha posterior a inicio. Constructor de alquiler exige `fechaInicio != null`
- **Sexo:** el setter valida contra `Sexo.values()`

---

## 8. Persistencia

Los datos se guardan en la carpeta `datos/` mediante **serialización Java**:

| Archivo | Contenido |
|---------|-----------|
| `inmuebles.dat` | `HashMap<String, Inmueble>` — todos los inmuebles serializados |
| `inquilinos.dat` | `HashMap<String, Inquilino>` |
| `facturas.dat` | `ArrayList<Factura>` |
| `movimientos.dat` | `ArrayList<MovimientoBancario>` |
| `alquileres.dat` | `ArrayList<Alquiler>` |
| `contadores.dat` | `int[]` con los 5 contadores autoincrementales |

**Manejo de errores de carga (Fase 4):** si algún archivo falla al deserializarse (por corrupción), se registra en el log, se activa la bandera `huboErrorCarga`, y al iniciar la aplicación se muestra una advertencia al usuario.

**Precaución:** Cambiar los tipos de los campos en las clases del modelo rompe la compatibilidad con archivos `.dat` existentes. Las fases 2 y 3 fueron diseñadas específicamente para **no modificar ningún tipo de dato serializado**.

**8.1 Ciclo de vida de los datos**

- **Al iniciar la aplicación**: el constructor privado de `RepositorioDatos` (invocado la primera vez que se llama a `getInstance()`) ejecuta `cargarTodo()`, que intenta deserializar cada archivo `.dat` mediante `ObjectInputStream`. Si un archivo no existe, se inicializa la colección correspondiente vacía (sistema nuevo); si existe pero está corrupto o no se puede leer (`IOException` o `ClassNotFoundException`), se captura la excepción, se registra una advertencia mediante `java.util.logging.Logger` y se marca internamente el indicador `huboErrorCarga = true`.
- **Aviso al usuario**: `VentanaPrincipal` consulta `servicio.isDatosCargadosCorrectamente()` justo después de construirse, y si algún archivo falló al cargar, muestra un `JOptionPane` de advertencia indicando que los datos pueden estar corruptos o faltantes, sin impedir el uso de la aplicación (esta continúa funcionando con las colecciones vacías o parcialmente cargadas).
- **Durante el uso**: cada operación de escritura en el repositorio (agregar, actualizar o eliminar cualquier entidad) invoca inmediatamente el método de guardado correspondiente (`guardarInmuebles()`, `guardarInquilinos()`, etc.), que serializa la colección completa actualizada mediante `ObjectOutputStream` y la sobrescribe en su archivo `.dat`. Esto implica que **no existe un botón de "Guardar" explícito**: la persistencia es transparente e inmediata tras cada cambio, minimizando el riesgo de pérdida de datos ante un cierre inesperado.
- **Generación de IDs**: cada vez que se genera un nuevo identificador (por ejemplo, `generarIdInmueble()`), el contador correspondiente se incrementa en memoria y **inmediatamente se persiste** el array completo de contadores mediante `guardarContadores()`, garantizando que, aunque la aplicación se cierre de forma abrupta, no se reutilicen IDs ya asignados en una sesión anterior.

**8.2 Justificación de la elección de diseño**
 
- Usar serialización de objetos en lugar de una base de datos relacional simplifica notablemente el proyecto (no requiere motor de base de datos, drivers JDBC ni mapeo objeto-relacional) y resulta apropiado para el alcance de un proyecto académico de POO orientado a una aplicación de escritorio de un único usuario. La contrapartida es que los archivos `.dat` no son legibles ni editables por fuera de la aplicación, no soportan acceso concurrente de múltiples procesos, y un cambio incompatible en la estructura de una clase del modelo (por ejemplo, añadir o quitar atributos) puede provocar errores de deserialización en archivos generados con una versión anterior del código, motivo por el cual `RepositorioDatos` maneja explícitamente esos fallos en lugar de dejar que la aplicación se caiga.
---
## 9. Descripción de la interfaz gráfica

La interfaz gráfica está construida íntegramente con **Java Swing**, sin frameworks externos, y sigue un estilo visual corporativo coherente definido centralmente en `VentanaPrincipal` (paleta de azul marino, azul medio, dorado/beige y blanco cálido, con tipografías Helvetica para texto general e Impact para la marca).

**Estructura general:**

La ventana principal (`VentanaPrincipal`) se organiza en un `BorderLayout` con dos zonas:
- Una **cabecera** (`NORTH`) con el nombre del sistema, un subtítulo descriptivo y un botón "Salir" que dispara una confirmación antes de cerrar la aplicación, evitando cierres accidentales con pérdida de contexto.
- Un **cuerpo central** (`CENTER`) ocupado por un `JTabbedPane` con pestañas ubicadas a la izquierda, cada una correspondiente a un módulo funcional: Inmuebles, Inquilinos, Alquileres, Facturas y Movimientos.
  **Patrón común de cada panel:**

Todos los paneles de módulo (`PanelInmuebles`, `PanelInquilinos`, `PanelAlquileres`, `PanelFacturas`, `PanelMovimientos`) siguen una estructura consistente, lo que facilita la curva de aprendizaje del usuario al moverse entre pestañas:
1. Una tabla (`JTable` sobre `DefaultTableModel`) que muestra el listado completo de registros del módulo, con celdas no editables directamente (se evita la edición "in place" para forzar el paso por las validaciones del formulario).
2. Botones de acción (alta, edición, eliminación, y acciones específicas como "Alquilar"/"Desalquilar") que abren diálogos modales (`JDialog`) o ejecutan la acción directamente sobre la fila seleccionada.
3. Formularios con campos de texto (`JTextField`) para datos libres y listas desplegables (`JComboBox`) para campos restringidos a un conjunto cerrado de valores (tipos de inmueble, sexo, tipo de respaldo, concepto de factura, tipo de movimiento), lo que reduce drásticamente errores de captura por parte del usuario.
4. Mensajes de error mostrados mediante `JOptionPane`, con el texto de la excepción capturada (`IllegalArgumentException`) traducido directamente al usuario, ya que los mensajes de las excepciones del modelo y del servicio están redactados en español y orientados al usuario final, no solo al desarrollador.
   
**Particularidades destacables:**
- `DialogoInmueble` es un único diálogo reutilizado tanto para registrar como para editar inmuebles, que cambia dinámicamente los campos visibles según el tipo de inmueble elegido en un `JComboBox`, evitando duplicar tres diálogos casi idénticos.
- Las tablas de inmuebles y movimientos usan renderizadores de celda personalizados (`getTableCellRendererComponent`) para resaltar visualmente ciertos estados (por ejemplo, disponibilidad del inmueble o tipo de movimiento), mejorando la legibilidad de los listados sin necesidad de columnas adicionales.
- `SwingUtil` centraliza la creación de botones, campos de texto y etiquetas con estilo uniforme, así como la presentación de avisos, lo que mantiene la coherencia visual entre paneles desarrollados de forma independiente.
---


## 10. Explicación de las relaciones más importantes del diagrama UML

*Sección actualizada a partir del diagrama UML final del proyecto (`InmobiliariaFinal.drawio`), que detalla con precisión las clases, multiplicidades y tipos de relación (herencia, agregación, asociación y dependencia) de todas las capas del sistema.*

### 10.1 Herencia: `Inmueble` ← `Piso`, `Local`, `Edificio`

Es la relación de **generalización/especialización** central del modelo, representada en el diagrama con flechas de punta triangular hueca ("Extends") desde `Piso`, `Local` y `Edificio` hacia la clase abstracta `«Inmueble»`. La clase base concentra los atributos comunes (`id`, `direccion`, `descripcion`, `codigoPostal`, `precioAlquiler`, `disponible`, `inquilinoId`) y los métodos `getTipoInmueble()` y `toString()`, que cada subclase hereda y, en el caso de `toString()`, sobrescribe. Esta relación permite que el resto del sistema trate de forma polimórfica una colección `List<Inmueble>` sin conocer la subclase concreta de cada elemento.

### 10.2 Agregación opcional: `Piso`/`Local` → `Edificio`

El diagrama representa explícitamente, mediante una flecha de **agregación** (diamante hueco en el extremo de `Edificio`) con multiplicidad **0...\*** del lado de `Piso`/`Local` y **1** del lado de `Edificio`, que un edificio puede agrupar conceptualmente a varios pisos y locales, aunque la relación se materializa en el código mediante el atributo `edificioId: String` (referencia por identificador, no por objeto). El **0** del lado de `Piso`/`Local` es coherente con que la relación es opcional: un `Piso` o `Local` puede existir de forma independiente, sin pertenecer a ningún `Edificio` registrado. El uso de IDs en lugar de referencias directas a objeto evita los problemas de serialización circular al persistir cada inmueble en el archivo `inmuebles.dat`.

### 10.3 Asociación `Inquilino` — `Alquiler` e `Inmueble` — `Alquiler`

El diagrama muestra dos **asociaciones simples** (líneas sin flecha en ninguno de los dos extremos, sin diamante):
- **`Inquilino` — `Alquiler`**: multiplicidad **1** del lado de `Inquilino` y **1...\*** del lado de `Alquiler` — un inquilino puede tener muchos alquileres a lo largo del tiempo.
- **`Inmueble` — `Alquiler`**: multiplicidad **1** en ambos extremos tal como está dibujado en el diagrama.
  `Alquiler` actúa así como una **clase de asociación** (entidad de enlace) entre `Inmueble` e `Inquilino`, aunque en el código la relación se implementa mediante referencias por ID (`inquilinoId`, `inmuebleId`) en lugar de objetos directos, por las mismas razones de simplicidad de persistencia mencionadas en 4.2. En la práctica, un mismo inmueble tiene múltiples `Alquiler` a lo largo del tiempo (uno por cada período en que fue arrendado) y la regla de negocio de que solo puede existir **un alquiler activo por inmueble en un momento dado** no se ve reflejada como multiplicidad en el diagrama, sino que es impuesta en tiempo de ejecución por `InmuebleServicio.alquilarInmueble()`.

### 10.4 Agregación `MovimientoBancario`/`Factura` → `Inmueble`

El diagrama representa con flechas de **agregación** (diamante hueco en el extremo de `Inmueble`) y multiplicidad **1...\*** las relaciones de `MovimientoBancario` y `Factura` hacia `Inmueble`: muchos movimientos bancarios y muchas facturas pueden estar agregados a un mismo inmueble (representado con multiplicidad **1** del lado de `Inmueble`). En el código esta relación se traduce en el atributo `inmuebleId: String` de cada `Factura` y cada `MovimientoBancario`, y es la base de la trazabilidad financiera por propiedad (reportes de ingresos y gastos filtrados por inmueble y por período).

### 10.5 Dependencias hacia las enumeraciones (`«enumeration»`)

El diagrama distingue claramente, mediante flechas discontinuas de **dependencia**, el uso que cada clase del modelo hace de sus enumeraciones internas:
- `Factura` **depende de** `«enumeration» ConceptoFactura`.
- `Inquilino` **depende de** `«enumeration» Sexo` y de `«enumeration» TipoRespaldo`.
- `MovimientoBancario` **depende de** `«enumeration» TipoMovimiento`.
  Estas dependencias (en lugar de asociaciones) reflejan que las enumeraciones son tipos de valor inmutables usados como atributo, no entidades con identidad propia ni ciclo de vida independiente.

### 10.6 Relación `InmuebleServicio` ↔ `RepositorioDatos` ↔ `Inmueble`/`Inquilino`

El diagrama muestra tres relaciones distintas que conviene diferenciar con precisión:
- **`InmuebleServicio` — `RepositorioDatos`**: **asociación simple** (línea sin flechas ni diamante), reflejo del atributo `-repo: RepositorioDatos` que el servicio obtiene vía `getInstance()`, con multiplicidad **1** en ambos extremos.
- **`InmuebleServicio` ···> `Inmueble`**: flecha discontinua de **dependencia** (no agregación): el servicio recibe, procesa y devuelve objetos `Inmueble` en sus métodos (`registrarEdificio()`, `getTodosInmuebles()`, `buscarPorId()`, etc.) sin mantener una referencia permanente ni ser su propietario.
- **`InmuebleServicio` ◇1 — 1...\* `Inquilino`**: flecha de **agregación** (diamante hueco del lado de `InmuebleServicio`), reflejando que el servicio opera sobre la colección completa de inquilinos.

En cualquier caso, la propiedad real de los datos reside siempre en `RepositorioDatos`: esta es la relación arquitectónica central de la capa de negocio, ya que `InmuebleServicio` no accede a los archivos `.dat` directamente, sino siempre a través de `RepositorioDatos`, que a su vez es la única clase que conoce y manipula las colecciones de `Inmueble`, `Inquilino`, `Factura`, `MovimientoBancario` y `Alquiler`.

### 10.7 Composición `VentanaPrincipal` ← Paneles e `InmuebleServicio`

El diagrama representa, mediante flechas de **composición** (diamante **relleno**, no hueco) desde cada uno de los cinco paneles (`PanelInmuebles`, `PanelInquilinos`, `PanelAlquileres`, `PanelFacturas`, `PanelMovimientos`) hacia `VentanaPrincipal`, el hecho de que la ventana principal contiene e integra estos paneles como pestañas de su `JTabbedPane`, y que su ciclo de vida depende exclusivamente de `VentanaPrincipal`. El diagrama incluye además una sexta flecha de composición, no siempre evidente a primera vista, entre `InmuebleServicio` y `VentanaPrincipal` (también con diamante relleno): la ventana principal es igualmente responsable de crear y poseer la única instancia de `InmuebleServicio` que luego comparte con cada panel. Cada panel, a su vez, se conecta mediante una **línea de asociación simple** (sin flechas) con `InmuebleServicio` (todos declaran `-servicio: InmuebleServicio`), confirmando en el diagrama que **ninguna clase de la capa `vista` se conecta directamente con `RepositorioDatos`**: la única vía de acceso a los datos desde la interfaz gráfica es a través del servicio.

### 10.8 Dependencias de la capa `vista` hacia el modelo y hacia `Validador`

El diagrama detalla, mediante flechas discontinuas de dependencia, qué clases concretas del modelo utiliza cada panel y diálogo, lo que documenta con precisión el acoplamiento real de la interfaz:
- `DialogoInmueble` depende de `Edificio`, `Piso`, `Local` (para construir y precargar el formulario dinámico) y de `Validador`.
- `PanelInmuebles` depende de `Inmueble` y de `DialogoInmueble` (lo invoca para alta/edición).
- `PanelInquilinos` depende de `Inquilino` y de `Validador`.
- `PanelAlquileres` depende de `Inmueble`, `Inquilino` y `Alquiler`.
- `PanelFacturas` depende de `Factura`, `Inmueble` y `Validador`.
- `PanelMovimientos` depende de `MovimientoBancario`, `Inmueble` y `Validador`.
  Esta es la confirmación gráfica de que `Validador` es efectivamente **reutilizado por múltiples paneles** (Inquilinos, Alquileres/Facturas/Movimientos), evitando duplicar las reglas de validación de formato en cada formulario.

### 10.9 Jerarquía de componentes Swing (no representada en este diagrama)

A diferencia de lo indicado en versiones anteriores de esta documentación, el diagrama UML **no incluye** nodos `JFrame`, `JPanel` ni `JDialog`, ni flechas de herencia ("Extends") entre la capa `vista` y clases de Swing: las 22 clases/enumeraciones del diagrama son exclusivamente las propias del proyecto (`modelo`, `repositorio`, `servicio`, `util` y `vista`). La relación de herencia con el framework existe únicamente en el código fuente, no en el diagrama: `VentanaPrincipal extends JFrame`, `DialogoInmueble extends JDialog`, y cada uno de los cinco paneles `extends JPanel`. Vale la pena mencionarlo igualmente porque dichas superclases son las responsables de todo el comportamiento gráfico (renderizado, eventos, ciclo de vida de la ventana) que la capa `vista` utiliza, mientras que las capas `modelo`, `repositorio` y `servicio` son completamente independientes de Swing.

### 10.10 Patrón Singleton aplicado a `RepositorioDatos`

Aunque no se representa con una flecha de relación entre dos clases distintas, el compartimento de atributos de `RepositorioDatos` en el diagrama incluye explícitamente `-instancia: RepositorioDatos` (una referencia estática a sí misma) y el compartimento de operaciones incluye `+getInstance(): RepositorioDatos`, evidenciando el patrón Singleton: una relación reflexiva de la clase consigo misma que garantiza un único punto de verdad para los datos en memoria durante toda la ejecución de la aplicación.

---
## 11. Dificultades encontradas durante el desarrollo

Durante el desarrollo del proyecto se identificaron y resolvieron varias dificultades típicas de una aplicación Java de escritorio con persistencia propia y modelo orientado a objetos con herencia:

1. **Modelado de la relación opcional Piso/Local–Edificio.** Decidir si la relación debía ser una referencia directa a objeto o una referencia por ID generó debate, ya que una referencia directa habría sido más "orientada a objetos" en apariencia, pero introducía el riesgo de grafos de objetos complejos al serializar (por ejemplo, qué ocurre si se serializa un Piso que referencia a un Edificio que a su vez podría referenciar de vuelta a sus pisos). Se optó finalmente por IDs de tipo `String`, resolviendo el problema de serialización a costa de tener que validar manualmente, en la capa de servicio, que el ID referenciado exista y corresponda efectivamente a un `Edificio`.
2. **Integridad referencial sin base de datos.** Al no contar con claves foráneas que el sistema de archivos pudiera imponer automáticamente, fue necesario implementar manualmente la limpieza en cascada de facturas, movimientos y alquileres asociados a un inmueble antes de poder eliminarlo, así como impedir la eliminación de inquilinos con alquileres activos. Olvidar alguna de estas comprobaciones habría dejado datos huérfanos en los archivos `.dat`, difíciles de detectar y corregir posteriormente.
3. **Manejo de archivos de datos corruptos o ausentes.** Dado que la persistencia depende de archivos binarios sensibles a cambios en la estructura de las clases serializadas, fue necesario anticipar el escenario en que un archivo `.dat` no exista (primera ejecución) o esté corrupto (ejecución interrumpida, edición manual accidental, incompatibilidad de versión de clase). Se resolvió mediante manejo explícito de excepciones en `cargarArchivo()` y `cargarContadores()`, con reinicio seguro a valores por defecto y notificación visual no bloqueante al usuario, en lugar de dejar que la aplicación termine abruptamente con una excepción no controlada.
4. **Consistencia de IDs autoincrementales entre sesiones.** Al usar contadores en memoria para generar identificadores legibles (en lugar de UUID), fue necesario asegurar que dichos contadores se persistieran de forma inmediata tras cada generación, para evitar que, tras cerrar y reabrir la aplicación, se reutilizaran IDs ya asignados a registros existentes, lo cual habría provocado colisiones de claves en los `HashMap` de inmuebles e inquilinos.
5. **Sincronización entre los formularios de la interfaz y las reglas de negocio del servicio.** Como las validaciones de formato (cédula, edad, contacto, código postal) están en `Validador` y las validaciones de negocio (disponibilidad de inmueble, duplicidad de cédula, existencia de edificio referenciado) están en `InmuebleServicio`, hubo que cuidar que los mensajes de `IllegalArgumentException` lanzados desde ambas capas fueran lo suficientemente descriptivos como para mostrarse directamente al usuario final en un `JOptionPane`, sin requerir traducción adicional ni exponer detalles técnicos internos.
6. **Adaptación dinámica del formulario de inmuebles.** Construir un único `DialogoInmueble` capaz de mostrar campos distintos según si se está creando un Edificio, un Piso o un Local (en lugar de tres diálogos separados) requirió gestionar cuidadosamente la visibilidad y el reseteo de campos al cambiar la selección en el `JComboBox` de tipo, así como validar que, al editar un inmueble existente, el diálogo precargue correctamente los valores específicos de su subtipo concreto.
---
## 12. Aportes recibidos de la IA generativa y forma en que fueron validados

Durante el desarrollo del proyecto se empleó un asistente de IA generativa como apoyo en distintas etapas del trabajo. A continuación se describen los aportes recibidos y el proceso seguido para validarlos antes de incorporarlos al proyecto final:

### 12.1 Diseño de la arquitectura en capas
Se consultó a la IA sobre cómo estructurar el proyecto siguiendo buenas prácticas de POO, lo que derivó en la separación en los paquetes `modelo`, `repositorio`, `servicio`, `util` y `vista`. Esta sugerencia fue validada verificando que efectivamente la capa de vista nunca invocara directamente a `RepositorioDatos`, sino siempre a través de `InmuebleServicio`, revisando manualmente los `import` de cada clase del paquete `vista` para confirmar que ninguna importara el paquete `repositorio`.

### 12.2 Aplicación de patrones de diseño
La implementación del patrón **Singleton** en `RepositorioDatos` (constructor privado + método estático `getInstance()`) fue sugerida y explicada por la IA, incluyendo la advertencia de que esta variante no es *thread-safe* (no usa sincronización), lo cual se consideró aceptable dado que la aplicación Swing es de un único usuario y las modificaciones de datos ocurren típicamente en el hilo de eventos. Esta decisión se validó probando manualmente que la instancia obtenida en distintos paneles de la interfaz fuera siempre la misma (comparando referencias durante pruebas con `System.identityHashCode`).

### 12.3 Estrategia de manejo de errores en la persistencia
La IA propuso el patrón de manejo de excepciones usado en `cargarArchivo()` y `cargarContadores()` (capturar `IOException`/`ClassNotFoundException`, devolver un valor por defecto y marcar un indicador de error en lugar de propagar la excepción). Esta sugerencia se validó de forma experimental: se renombró temporalmente uno de los archivos `.dat` y se corrompió su contenido para confirmar que la aplicación arrancaba correctamente mostrando el aviso de advertencia, en lugar de fallar con una excepción no controlada.

### 12.4 Redacción de Javadoc
Gran parte de los comentarios Javadoc (`@param`, `@return`, `@throws`) presentes en las clases del modelo, repositorio y servicio fueron generados con apoyo de la IA a partir de las firmas de los métodos ya escritos por el equipo. Cada comentario generado fue revisado manualmente, contrastando que las descripciones correspondieran exactamente al comportamiento real del método (por ejemplo, verificando que las condiciones documentadas en `@throws` coincidieran con las validaciones efectivamente implementadas en el código).

### 12.5 Validaciones de formato
Las expresiones regulares y rangos numéricos usados en `Validador` (formato de cédula, rango de edad, formato de contacto y de código postal) fueron discutidos con la IA para definir criterios razonables, pero los valores límite concretos (por ejemplo, 18-120 años, 4-15 dígitos para cédula, 1000-99999 para código postal) fueron decididos y ajustados por el equipo de desarrollo según los criterios propios del ejercicio académico, y se validaron mediante pruebas manuales ingresando valores límite y fuera de rango a través de la interfaz gráfica, confirmando que los mensajes de error se mostraran correctamente en cada caso.

### 12.6 Estilo visual de la interfaz Swing
La paleta de colores corporativa y la estructura general de la cabecera (`crearHeader()`) en `VentanaPrincipal` se refinaron con sugerencias de la IA sobre combinaciones de color y jerarquía tipográfica. La validación en este caso fue puramente visual: se ejecutó la aplicación repetidamente durante el desarrollo para confirmar legibilidad, contraste adecuado y coherencia entre todos los paneles.

---
## 13. Cómo ejecutar

```bash
# Requisitos: JDK 17+, Gradle 8+

# Compilar
./gradlew build

# Ejecutar (punto de entrada: com.inmobiliaria.Main)
java -cp build/classes/java/main com.inmobiliaria.Main
```

