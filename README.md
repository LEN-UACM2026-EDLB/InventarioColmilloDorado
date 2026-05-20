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

#SQL
CREATE DATABASE colmillo_dorado_db;
GO

USE colmillo_dorado_db;
GO

CREATE TABLE insumos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL CONSTRAINT CHK_InsumoTipo CHECK (tipo IN ('MALTA', 'LUPULO', 'LEVADURA')),
    cantidad_disponible DECIMAL(10,2) NOT NULL,
    unidad_medida VARCHAR(10) NOT NULL
);

CREATE TABLE material_empaque (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cantidad_disponible INT NOT NULL
);

CREATE TABLE producto_terminado (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre_cerveza VARCHAR(100) NOT NULL,
    estilo VARCHAR(50) NOT NULL,
    cantidad_botellas INT NOT NULL
);
GO

-- CARGA DE STOCK INICIAL REAL COLMILLO DORADO
INSERT INTO insumos (nombre, tipo, cantidad_disponible, unidad_medida) VALUES
('Malta Pilsen Rahr', 'MALTA', 10.00, 'kg'),
('Malta Trigo Acidificada', 'MALTA', 10.00, 'kg'),
('Malta Caramelo', 'MALTA', 5.00, 'kg'),
('Lupulo Amarillo', 'LUPULO', 500.00, 'g'),
('Lupulo Citrus', 'LUPULO', 500.00, 'g'),
('Lupulo Cascade', 'LUPULO', 400.00, 'g'),
('Levadura SafAle S-04', 'LEVADURA', 2.00, 'pz');

INSERT INTO material_empaque (nombre, quantity_disponible) VALUES
('Botella de vidrio 355ml', 200),
('Corcholatas', 1000);

INSERT INTO producto_terminado (nombre_cerveza, estilo, cantidad_botellas) VALUES
('Colmillo Dorado Blend Inicial', 'Por definir', 200);
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
