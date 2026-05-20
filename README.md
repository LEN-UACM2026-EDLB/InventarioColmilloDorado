# Sistema de Gestión de Inventario - Cervecería Colmillo Dorado

### Proyecto Final: Programación Orientada a Objetos y Patrones de Diseño
**Profesor:** Dr. Quiroz Fabián  
**Plantel:** San Lorenzo Tezonco, UACM  
**Ciclo Académico:** 2026-1  

---

## 1. Descripción del Proyecto

Este sistema es una solución de software ejecutable diseñada para resolver el problema real de control de almacén, insumos y lotes de producción de la **Cervecería Colmillo Dorado**. La aplicación demuestra el dominio avanzado de los pilares de la Programación Orientada a Objetos (POO) y la correcta implementación de patrones de diseño arquitectónicos, evitando clases vacías y garantizando un modelado no trivial.

### Cumplimiento de Requisitos Técnicos
* **Encapsulamiento:** Ocultamiento estricto de atributos (`private`) con acceso controlado mediante getters/setters que integran validaciones de negocio (evitando inventarios negativos).
* **Herencia y Polimorfismo:** Jerarquía coherente de entidades a partir de clases abstractas e interfaces para la gestión de componentes genéricos del inventario.
* **Composición y Agregación:** Modelado de relaciones complejas (ej. un Lote de producción se compone de recetas e insumos específicos).
* **Patrones de Diseño Implementados:**
    1.  **Creacional (Singleton):** Aplicado en la clase `ConexionDB` para garantizar una única instancia de conexión activa hacia Microsoft SQL Server, optimizando los recursos del sistema.
    2.  **Estructural/Comportamiento (DAO - Data Access Object):** Desacoplamiento total entre la lógica de negocio y la persistencia de datos.

---

##  2. Requisitos Previos e Infraestructura

Antes de iniciar, asegúrate de tener instalado en tu entorno local:
* **Java Development Kit (JDK):** Versión 17 o superior.
* **IDE:** IntelliJ IDEA (Community o Ultimate).
* **Gestor de Dependencias:** Maven (integrado en IntelliJ).
* **Base de Datos:** Microsoft SQL Server y SQL Server Management Studio (SSMS).

---

##  3. Guía de Levantamiento de la Base de Datos (SSMS)

Debido a que el proyecto utiliza **Microsoft SQL Server**, es obligatorio habilitar los protocolos de red local para permitir la comunicación con el driver JDBC de Java. Sigue estos pasos de manera estricta:

### Paso 3.1: Configurar los puertos en Windows
1. Abre el **Administrador de configuración de SQL Server** (*SQL Server Configuration Manager*).
2. Ve a **Configuración de red de SQL Server** -> **Protocolos de MYSQL** (o el nombre de tu instancia local).
3. Da clic derecho en **TCP/IP** y selecciona **Habilitar** (*Enable*).
4. Entra a las **Propiedades de TCP/IP** y dirígete a la pestaña **Direcciones IP** (*IP Addresses*).
5. Baja hasta el final de la lista a la sección **IPAll**:
   * Deja el campo **Puertos dinámicos TCP** (*TCP Dynamic Ports*) **completamente vacío** (borra el cero si existe).
   * En **Puerto TCP** (*TCP Port*) escribe exactamente: `1433`.
6. En el panel izquierdo, haz clic en **Servicios de SQL Server**, selecciona tu servicio activo (ej. `SQL Server (MYSQL)`) y dale **Reiniciar**.

### Paso 3.2: Montar el Esquema y Datos Iniciales
Abre **SQL Server Management Studio (SSMS)**, conéctate a tu instancia local (`HP\MYSQL` o `localhost`), abre una **Nueva consulta (New Query)** y ejecuta el siguiente script para cargar el inventario inicial real provisto por la administración:

#SQL query


IF DB_ID('CerveceraInventarioDB') IS NOT NULL
BEGIN
    ALTER DATABASE CerveceraInventarioDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE CerveceraInventarioDB;
END
GO

CREATE DATABASE CerveceraInventarioDB;
GO

USE CerveceraInventarioDB;
GO


CREATE TABLE Estilos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NULL,
    activo BIT NOT NULL DEFAULT 1,

    CONSTRAINT UQ_Estilos_Nombre UNIQUE (nombre)
);
GO


CREATE TABLE Productos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255) NULL,
    precio DECIMAL(12,2) NOT NULL,
    estilo_id INT NOT NULL,
    activo BIT NOT NULL DEFAULT 1,

    CONSTRAINT FK_Productos_Estilos
        FOREIGN KEY (estilo_id) REFERENCES Estilos(id),

    CONSTRAINT CK_Productos_Precio
        CHECK (precio >= 0)
);
GO



CREATE TABLE Insumos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    cantidad_disponible DECIMAL(12,3) NOT NULL DEFAULT 0,
    unidad_medida VARCHAR(50) NOT NULL,

    CONSTRAINT CK_Insumos_CantidadDisponible
        CHECK (cantidad_disponible >= 0)
);
GO



CREATE TABLE MovimientosInsumos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    insumo_id INT NOT NULL,
    tipo_movimiento CHAR(1) NOT NULL,
    cantidad DECIMAL(12,3) NOT NULL,
    fecha_movimiento DATETIME NOT NULL DEFAULT GETDATE(),
    observaciones VARCHAR(255) NULL,

    CONSTRAINT FK_MovimientosInsumos_Insumos
        FOREIGN KEY (insumo_id) REFERENCES Insumos(id),

    CONSTRAINT CK_MovimientosInsumos_TipoMovimiento
        CHECK (tipo_movimiento IN ('E', 'S')),

    CONSTRAINT CK_MovimientosInsumos_Cantidad
        CHECK (cantidad > 0)
);
GO

CREATE TABLE MovimientosProductos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    producto_id INT NOT NULL,
    tipo_movimiento CHAR(1) NOT NULL,
    cantidad DECIMAL(12,3) NOT NULL,
    fecha_movimiento DATETIME NOT NULL DEFAULT GETDATE(),
    observaciones VARCHAR(255) NULL,

    CONSTRAINT FK_MovimientosProductos_Productos
        FOREIGN KEY (producto_id) REFERENCES Productos(id),

    CONSTRAINT CK_MovimientosProductos_TipoMovimiento
        CHECK (tipo_movimiento IN ('E', 'S')),

    CONSTRAINT CK_MovimientosProductos_Cantidad
        CHECK (cantidad > 0)
);
GO


INSERT INTO Estilos (nombre, descripcion)
VALUES
('IPA', 'Cerveza con amargor marcado y aroma intenso a lúpulo.'),
('Porter', 'Cerveza oscura con notas tostadas y sabor robusto.'),
('Stout', 'Cerveza oscura con notas a café, chocolate o malta tostada.'),
('Lager', 'Cerveza de fermentación baja, limpia y refrescante.'),
('Pale Ale', 'Cerveza clara con equilibrio entre malta y lúpulo.');
GO


INSERT INTO Productos (nombre, descripcion, precio, estilo_id)
VALUES
('Lúpulo Salvaje', 'Cerveza artesanal estilo IPA en botella de 355 ml.', 85.00, 1),
('Noche Oscura', 'Cerveza artesanal estilo Stout en botella de 355 ml.', 90.00, 3),
('Puerto Negro', 'Cerveza artesanal estilo Porter en botella de 355 ml.', 88.00, 2),
('Clara del Valle', 'Cerveza artesanal estilo Lager en botella de 355 ml.', 75.00, 4),
('Ámbar Local', 'Cerveza artesanal estilo Pale Ale en botella de 355 ml.', 80.00, 5);
GO


INSERT INTO Insumos (nombre, tipo, cantidad_disponible, unidad_medida)
VALUES
('Malta Pale Ale', 'Malta', 50.000, 'kg'),
('Malta Chocolate', 'Malta', 20.000, 'kg'),
('Lúpulo Cascade', 'Lúpulo', 5.000, 'kg'),
('Lúpulo Citra', 'Lúpulo', 3.500, 'kg'),
('Levadura Ale', 'Levadura', 2.000, 'kg'),
('Levadura Lager', 'Levadura', 1.500, 'kg'),
('Botella 355 ml', 'Envase', 500.000, 'pieza'),
('Tapa corona', 'Envase', 500.000, 'pieza'),
('Etiqueta adhesiva', 'Empaque', 500.000, 'pieza');
GO

INSERT INTO MovimientosInsumos (insumo_id, tipo_movimiento, cantidad, observaciones)
VALUES
(1, 'E', 50.000, 'Compra inicial de malta Pale Ale.'),
(2, 'E', 20.000, 'Compra inicial de malta Chocolate.'),
(3, 'E', 5.000, 'Compra inicial de lúpulo Cascade.'),
(4, 'E', 3.500, 'Compra inicial de lúpulo Citra.'),
(5, 'E', 2.000, 'Compra inicial de levadura Ale.'),
(6, 'E', 1.500, 'Compra inicial de levadura Lager.'),
(7, 'E', 500.000, 'Compra inicial de botellas.'),
(8, 'E', 500.000, 'Compra inicial de tapas corona.'),
(9, 'E', 500.000, 'Compra inicial de etiquetas.');
GO

INSERT INTO MovimientosProductos (producto_id, tipo_movimiento, cantidad, observaciones)
VALUES
(1, 'E', 120.000, 'Producción inicial de Lúpulo Salvaje.'),
(2, 'E', 80.000, 'Producción inicial de Noche Oscura.'),
(3, 'E', 90.000, 'Producción inicial de Puerto Negro.'),
(4, 'E', 150.000, 'Producción inicial de Clara del Valle.'),
(5, 'E', 100.000, 'Producción inicial de Ámbar Local.'),

(1, 'S', 12.000, 'Venta inicial.'),
(2, 'S', 8.000, 'Venta inicial.'),
(4, 'S', 20.000, 'Venta inicial.');
GO

CREATE VIEW vwExistenciasProductos AS
SELECT
    P.id,
    P.nombre,
    P.descripcion,
    P.precio,
    E.nombre AS estilo,
    ISNULL(SUM(
        CASE MP.tipo_movimiento
            WHEN 'E' THEN MP.cantidad
            WHEN 'S' THEN -MP.cantidad
            ELSE 0
        END
    ), 0) AS existencia
FROM Productos P
INNER JOIN Estilos E ON P.estilo_id = E.id
LEFT JOIN MovimientosProductos MP ON P.id = MP.producto_id
WHERE P.activo = 1
GROUP BY
    P.id,
    P.nombre,
    P.descripcion,
    P.precio,
    E.nombre;
GO

CREATE VIEW vwHistorialMovimientosProductos AS
SELECT
    MP.id,
    P.nombre AS producto,
    E.nombre AS estilo,
    CASE MP.tipo_movimiento
        WHEN 'E' THEN 'Entrada'
        WHEN 'S' THEN 'Salida'
    END AS tipo_movimiento,
    MP.cantidad,
    MP.fecha_movimiento,
    MP.observaciones
FROM MovimientosProductos MP
INNER JOIN Productos P ON MP.producto_id = P.id
INNER JOIN Estilos E ON P.estilo_id = E.id;
GO

CREATE VIEW vwHistorialMovimientosInsumos AS
SELECT
    MI.id,
    I.nombre AS insumo,
    I.tipo,
    CASE MI.tipo_movimiento
        WHEN 'E' THEN 'Entrada'
        WHEN 'S' THEN 'Salida'
    END AS tipo_movimiento,
    MI.cantidad,
    I.unidad_medida,
    MI.fecha_movimiento,
    MI.observaciones
FROM MovimientosInsumos MI
INNER JOIN Insumos I ON MI.insumo_id = I.id;
GO

SELECT * FROM Estilos;
SELECT * FROM Productos;
SELECT * FROM Insumos;

SELECT * 
FROM vwExistenciasProductos
ORDER BY nombre;

SELECT *
FROM vwHistorialMovimientosProductos
ORDER BY fecha_movimiento DESC;

SELECT *
FROM vwHistorialMovimientosInsumos
ORDER BY fecha_movimiento DESC;
GO

---

## 4. Reglas básicas para subir código:

* **Nunca** trabajes ni hagas commits directamente sobre la rama `main`.
* Antes de iniciar una nueva tarjeta (actividad), posiciónate en `main` y jala lo último del servidor:
  
      git checkout main
      git pull origin main
* Crea una rama secundaria para tu tarea específica:
  
    `git checkout -b feature/nombre-de-tu-tarea`
* Desarrolla tu código, y al terminar realiza tus commits y súbelos a GitHub:

    `git add .`
  
    `git commit -m "feat: descripcion corta de lo que implementaste"`
  
    `git push origin feature/nombre-de-tu-tarea`
  
* Ve a la interfaz web de GitHub y abre un Pull Request (PR) para que se fusione tus cambios con la rama main debera ser aprobado por un integrante diferente.
