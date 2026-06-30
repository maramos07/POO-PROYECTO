# Documentación Completa — Sistema de Gestión Inmobiliaria

## 1. Resumen

Aplicación de escritorio en **Java 25 con Swing** para la administración integral de una inmobiliaria. Permite gestionar inmuebles (edificios, pisos y locales), inquilinos, contratos de alquiler, facturas de gastos y movimientos bancarios. La persistencia se maneja mediante archivos serializados (`.dat`) y la arquitectura sigue el patrón de **tres capas**: modelo, servicio/repositorio y vista.

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

## 3. Diagrama de clases simplificado

```
                    ┌──────────────┐
                    │   Inmueble   │  (abstracta, Serializable)
                    │──────────────│
                    │ - id         │
                    │ - direccion  │
                    │ - numero     │
                    │ - descripcion│
                    │ - codPostal  │
                    │ - precioAlq  │
                    │ - disponible │
                    │ - inquilinoId│
                    │──────────────│
                    │ + getTipoInmueble() ── abstracto
                    └──────┬───────┘
           ┌───────────────┼───────────────┐
           │               │               │
    ┌──────┴──────┐ ┌──────┴──────┐ ┌──────┴──────┐
    │  Edificio   │ │    Piso     │ │   Local     │
    │─────────────│ │─────────────│ │─────────────│
    │ - numPisos  │ │ - numPiso   │ │ - numPiso   │
    │ - nombreEdif│ │ - tipoEspacio│ │ - tipoLocal │
    │             │ │ - descEsp   │ │ - descEsp   │
    │             │ │ - edificioId│ │ - edificioId│
    └─────────────┘ └─────────────┘ └─────────────┘


┌────────────────┐     ┌──────────────────┐     ┌─────────────────────┐
│   Inquilino    │     │     Alquiler     │     │      Factura        │
│────────────────│     │──────────────────│     │─────────────────────│
│ - id           │     │ - id             │     │ - id                │
│ - nombre       │     │ - inquilinoId    │     │ - fechaEmision      │
│ - cedula       │     │ - inmuebleId     │     │ - inmuebleId        │
│ - edad         │     │ - fechaInicio    │     │ - concepto (enum)   │
│ - sexo (String)│     │ - fechaFin       │     │ - proveedor         │
│ - medioContacto│     │ - activo         │     │ - costo             │
│ - tipoRespaldo │     │──────────────────│     └─────────────────────┘
│────────────────│     │ + finalizar()    │
│ enum Sexo      │     │ + setFechaInicio │     ┌─────────────────────┐
│ enum TipoResp. │     └──────────────────┘     │ MovimientoBancario  │
└────────────────┘                              │─────────────────────│
                                                │ - id                │
                                                │ - tipoMovim (enum)  │
        ┌─────────────────────┐                 │ - inmuebleId        │
        │   InmuebleServicio  │                 │ - fecha             │
        │─────────────────────│                 │ - importe           │
        │ - repo (Singleton)  │                 │ - personaEntidad    │
        │─────────────────────│                 └─────────────────────┘
        │ + registrar*()      │
        │ + modificar*()      │
        │ + eliminar*()       │         ┌────────────────────┐
        │ + alquilar()        │         │ RepositorioDatos   │
        │ + desalquilar()     │         │────────────────────│
        │ + consultar*()      │         │ + getInstance()    │
        └─────────────────────┘         │ + CRUD inmuebles   │
                                        │ + CRUD inquilinos  │
                                        │ + CRUD facturas    │
                                        │ + CRUD movimientos │
                ┌──────────────┐        │ + CRUD alquileres  │
                │  Validador   │        │ + limpieza huérfanos│
                │──────────────│        │ + carga/guardado   │
                │ + validarX() │        └────────────────────┘
                └──────────────┘
```

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
| **Campos del formulario** | `DialogoInmueble.contruirUI()`: `JComboBox` de tipo (EDIFICIO/PISO/LOCAL), `JTextField` para dirección, código interno, descripción, código postal, precio. Según el tipo seleccionado, se muestran campos adicionales (nombre del edificio + número de pisos, o número de piso + tipo de espacio + descripción específica + ID de edificio opcional) |
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
Usuario: completa "Dirección: Calle Mayor", "Código Interno: 10",
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
| **Campos editables** | Todos: dirección, código interno, descripción, código postal, precio, y los campos específicos del tipo (si se edita un Piso, se puede cambiar el número de piso, tipo de espacio, descripción específica y edificio asociado) |
| **Servicio** | `servicio.modificarInmueble(id, direccion, numero, descripcion, cp, precio)` actualiza los campos comunes. Luego según el tipo: `modificarPiso()`, `modificarLocal()` o `modificarEdificio()` |
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

### 6.14 Carga inicial de datos de prueba (`SeedData`)

| Aspecto | Detalle |
|---------|---------|
| **Disparador** | `Main.main()` → `SeedData.cargarSiVacio(new InmuebleServicio())` |
| **Condición** | Solo ejecuta si `servicio.getTodosInmuebles().isEmpty()`. Si ya hay datos, no hace nada |
| **Qué crea** | 2 edificios, 4 pisos, 2 locales, 4 inquilinos, 2 alquileres, 5 facturas, movimientos automáticos de los alquileres + 1 movimiento extra |
| **Cómo asocia** | Busca inmuebles e inquilinos por dirección/cédula usando streams y `filter()` |
| **Mensaje** | "Datos de prueba cargados: X inmuebles, Y inquilinos, Z alquileres, W facturas, V movimientos." |

Para reiniciar los datos de prueba, borrar los archivos de la carpeta `datos/` y reiniciar la aplicación.

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

**Precaución:** Cambiar los tipos de los campos en las clases del modelo rompe la compatibilidad con archivos `.dat` existentes. Las fases 2 y 3 fueron diseñadas específicamente para **no modificar ningún tipo de dato serializado** (ver sección 9).

---


## 10. Cómo ejecutar

```bash
# Requisitos: JDK 17+, Gradle 8+

# Compilar
./gradlew build

# Ejecutar (punto de entrada: com.inmobiliaria.Main)
java -cp build/classes/java/main com.inmobiliaria.Main
```

Al primer inicio, si no hay datos, `SeedData` carga automáticamente:

- **2 edificios** (Torre Azul, Centro Comercial Norte)
- **4 pisos** (apartamento, ático, dúplex, estudio)
- **2 locales** (comercial, oficina)
- **4 inquilinos** (Juan, María, Carlos, Laura)
- **2 alquileres activos** (Juan → apartamento, María → dúplex)
- **5 facturas** registradas
- **Movimientos bancarios** generados automáticamente por los alquileres + 1 adicional

Los datos de prueba se pueden borrar eliminando los archivos de la carpeta `datos/`. La próxima ejecución los volverá a crear.
