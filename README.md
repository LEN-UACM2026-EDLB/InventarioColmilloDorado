# Sistema de Gestión de Inventario - Cervecería Colmillo Dorado

## Descripción

Este proyecto es una aplicación de escritorio desarrollada en Java para administrar el inventario de una cervecera local.

El sistema permite gestionar estilos de cerveza, productos terminados, insumos, movimientos de productos, movimientos de insumos y existencias. La información se almacena en una base de datos de SQL Server.

## Tecnologías utilizadas

- Java 17
- IntelliJ IDEA
- Swing
- SQL Server
- JDBC
- Maven
- Programación Orientada a Objetos
- Patrones de diseño

## Funcionalidades principales

- Administración de estilos de cerveza.
- Registro y edición de productos.
- Registro y edición de insumos.
- Registro de entradas y salidas de productos terminados.
- Registro de entradas y salidas de insumos.
- Consulta de existencias de productos.
- Consulta de historial de movimientos.
- Validación de cantidades para evitar salidas mayores a la existencia disponible.

## Estructura del proyecto

```text
src/main/java/cervecera/
│
├── Main.java
│
├── conexion/
│   └── ConexionBD.java
│
├── modelo/
│   ├── Estilo.java
│   ├── Producto.java
│   ├── Insumo.java
│   ├── MovimientoProducto.java
│   ├── EntradaProducto.java
│   ├── SalidaProducto.java
│   ├── MovimientoInsumo.java
│   ├── EntradaInsumo.java
│   ├── SalidaInsumo.java
│   ├── ExistenciaProducto.java
│   ├── HistorialMovimientoProducto.java
│   └── HistorialMovimientoInsumo.java
│
├── dao/
│   ├── EstiloDAO.java
│   ├── ProductoDAO.java
│   ├── InsumoDAO.java
│   ├── MovimientoProductoDAO.java
│   └── MovimientoInsumoDAO.java
│
├── fabrica/
│   ├── FabricaMovimientoProducto.java
│   └── FabricaMovimientoInsumo.java
│
└── vista/
    ├── VentanaPrincipal.java
    ├── PanelEstilos.java
    ├── PanelProductos.java
    ├── PanelInsumos.java
    ├── PanelMovimientosProductos.java
    ├── PanelMovimientosInsumos.java
    └── PanelExistencias.java
