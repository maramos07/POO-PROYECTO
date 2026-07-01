# Sistema de Gestión Inmobiliaria

Proyecto de Programación Orientada a Objetos. Aplicación de escritorio
en Java con Swing para administrar inmuebles, inquilinos, alquileres,
facturas y movimientos bancarios.

## Requisitos

- JDK 17 o superior
- Gradle 8.x (incluye wrapper `gradlew`)

## Compilar y ejecutar

```bash
./gradlew build      # compila
./gradlew run         # ejecuta (si se configura el plugin application)
# o directo:
java -cp build/classes/java/main com.inmobiliaria.Main
```
## Cómo abrir el proyecto en un IDE

El proyecto usa **Gradle** como sistema de build. Lo más simple es abrir la carpeta raíz del proyecto (donde está `build.gradle`) directamente, sin crear un proyecto Java nuevo desde cero:

- **IntelliJ IDEA**: `File → Open` sobre la carpeta raíz y listo — IntelliJ detecta el `build.gradle` automáticamente (soporte Gradle incluido de fábrica) y sincroniza el proyecto sin pasos adicionales.
- **NetBeans** (12+): `File → Open Project...` y selecciona la carpeta raíz. NetBeans también reconoce el `build.gradle` automáticamente y descarga las dependencias.
- **Eclipse**: necesita el plugin *Buildship* para reconocer proyectos Gradle (viene incluido en "Eclipse IDE for Java Developers", pero si no lo tienes, instálalo desde `Help → Eclipse Marketplace`). Luego: `File → Import → Gradle → Existing Gradle Project`.

**Importante:** no crees un "Java Project" / "Java Application" en blanco y copies el código adentro — el IDE no resolverá las dependencias ni respetará la estructura `src/main/java` correctamente. Siempre se debe **abrir/importar como proyecto Gradle existente**.

Requisitos previos: JDK 17 o superior configurado en el IDE, y conexión a internet la primera vez (para que Gradle descargue el wrapper y las dependencias).

## Estructura del proyecto

```
src/main/java/com/inmobiliaria/
├── Main.java                  ← punto de entrada
├── modelo/                    ← clases del dominio (POO)
│   ├── Inmueble.java          ← abstracta, base de piso/local/edificio
│   ├── Piso.java
│   ├── Local.java
│   ├── Edificio.java
│   ├── Inquilino.java
│   ├── Alquiler.java
│   ├── Factura.java
│   └── MovimientoBancario.java
├── repositorio/
│   └── RepositorioDatos.java  ← Singleton, persistencia en .dat
├── servicio/
│   └── InmuebleServicio.java  ← lógica de negocio
├── util/
│   └── Validador.java         ← validaciones estáticas
└── vista/                     ← interfaz Swing
    ├── VentanaPrincipal.java
    ├── SwingUtil.java
    ├── DialogoInmueble.java
    ├── PanelInmuebles.java
    ├── PanelInquilinos.java
    ├── PanelAlquileres.java
    ├── PanelFacturas.java
    └── PanelMovimientos.java
```

## Persistencia

Los datos se guardan en la carpeta `datos/` como archivos
serializados (.dat):

```
inmuebles.dat, inquilinos.dat, facturas.dat,
movimientos.dat, alquileres.dat, contadores.dat
```

## Principios POO aplicados

- **Herencia**: `Inmueble` → `Piso`, `Local`, `Edificio`
- **Encapsulamiento**: campos privados con getters/setters validados
- **Polimorfismo**: `getTipoInmueble()` abstracto, `toString()`
- **Patrón Singleton**: `RepositorioDatos`
- **Validación centralizada**: clase `Validador`
