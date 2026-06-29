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
