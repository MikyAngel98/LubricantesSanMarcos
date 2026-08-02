# 🛢️ Lubricantes San Marcos

Sistema de gestión para una lubricadora y tienda de accesorios automotrices. Permite registrar **ventas**, **compras**, controlar el **inventario** y generar **reportes**, con autenticación de usuarios por roles.

Aplicación de escritorio construida con **JavaFX**, **Hibernate (JPA)** y **SQL Server**.

---

## ✨ Características

- 🔐 **Inicio de sesión** con contraseñas encriptadas (BCrypt) y roles de usuario.
- 🛒 **Módulo de Ventas**:
  - Selección de productos por categorías (Aceites, Filtros, Focos y Productos).
  - Carrito de compra con cantidades ajustables (incluye aceite a granel en presentaciones de 0.25 L).
  - Control de **stock en tiempo real**: el stock mostrado descuenta lo que ya está en el carrito.
  - Registro de venta con descuento del stock en la base de datos.
  - Impresión de **recibo** en PDF.
- 📦 **Módulo de Compras**:
  - Registro de compras a proveedores con precios de compra.
  - Actualización automática del stock al registrar la compra.
- 📋 **Inventario**: vista de productos, filtros por nombre y alerta de productos con stock bajo.
- 📊 **Reportes**: consulta por período de ventas y compras, con detalle y exportación a PDF.
- 👥 **Gestión de usuarios** (solo ADMIN): alta, baja, edición y activación/desactivación.
- 🔍 **Buscadores** por nombre en todas las tablas de productos.

## 🧩 Módulos principales

| Módulo | Descripción |
|---|---|
| Ventas | Registro de ventas con carrito, stock en vivo y recibo PDF |
| Compras | Registro de compras a proveedores con control de precios |
| Inventario | Consulta de productos y alertas de stock bajo |
| Reportes | Ventas/compras por período con detalle y PDF |
| Configuración | Gestión de usuarios y roles |

## 👥 Roles de usuario

- **ADMIN**: acceso completo, incluye gestión de usuarios y configuración.
- **VENDEDOR**: operación del punto de venta (ventas, compras, inventario, reportes).

---

## 🛠️ Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| JavaFX | 21 |
| Hibernate (JPA) | 5.6 |
| Microsoft SQL Server | Express (compatible con cualquier edición) |
| Maven | 3.9 |
| JDBC Driver (mssql-jdbc) | 12.6 |
| jBCrypt | 0.4 |
| OpenPDF | 1.3 |

---

## 📁 Estructura del proyecto

El proyecto principal es la carpeta `SanMarcos/`.

```
SanMarcos/
├── src/main/java/org/example/
│   ├── Modelo/          # Entidades JPA (Aceite, Filtro, Foco, Producto, Usuario, ...)
│   ├── DAO/             # Acceso a datos (JDBC y JPA)
│   ├── Servicio/        # Lógica de negocio (Venta, Compra, Producto, Reporte, PDF...)
│   ├── DTO/             # Objetos de transferencia para detalle de ventas/compras
│   ├── utils/           # Utilidades y configuración
│   └── Vista/           # Controladores JavaFX y pantallas (FXML)
├── src/main/resources/
│   ├── fxml/            # Vistas JavaFX (Login, Ventas, Compras, Reportes, ...)
│   └── META-INF/
│       └── persistence.xml   # Configuración de conexión a la base de datos
├── pom.xml              # Dependencias y plugins de Maven
└── .gitignore
```

Otras carpetas del repositorio:

- `Sql/` — Scripts de creación de la base de datos y datos de prueba.
- `Usuarios sql/` — Script de ejemplo para la tabla de usuarios.
- `VistaLubricantes/`, `VistaLubricantes2/`, `PruebaFx/` — Prototipos/versiones antiguas (sin uso actual).

## 🗄️ Modelo de datos

La base de datos `LubricanteSanMarcos` contiene las siguientes tablas:

`Aceite`, `Categoria`, `Cliente`, `Compra`, `Contacto`, `DetalleCompra`, `DetalleVenta`, `Filtro`, `Foco`, `Marca`, `Persona`, `Presentacion`, `Producto`, `Proveedor`, `Usuario`, `Venta`.

Los scripts para crear y poblar la base de datos están en la carpeta `Sql/`:

- `Base de Datos.sql` — creación de tablas.
- `Datos de Prueba.sql` — datos de ejemplo.

---

## 🚀 Requisitos previos

- **JDK 21** instalado (se recomienda Liberica Full u Oracle JDK, que incluyen `jpackage`).
- **Maven 3.9+**.
- **SQL Server** (Express es suficiente) con una base de datos `LubricanteSanMarcos`.

## ⚙️ Configuración de la base de datos

La conexión se configura en `SanMarcos/src/main/resources/META-INF/persistence.xml`:

```xml
<property name="javax.persistence.jdbc.url"
          value="jdbc:sqlserver://localhost:1433;databaseName=LubricanteSanMarcos;encrypt=true;trustServerCertificate=true"/>
<property name="javax.persistence.jdbc.user" value="app_lubricantes"/>
<property name="javax.persistence.jdbc.password" value="adminLubricantes123!"/>
```

Ajusta la URL, usuario y contraseña según tu instancia de SQL Server.

## ▶️ Cómo ejecutar

Desde la carpeta `SanMarcos/`:

```bash
mvn javafx:run
```

## 📦 Cómo empaquetar (ejecutable independiente)

Para generar un **JAR con todas las dependencias**:

```bash
mvn package -DskipTests
```

Para crear una **carpeta portable** (aplicación + Java incluido, no requiere instalación en la PC destino):

```bash
jpackage --type app-image \
  --name LubricanteSanMarcos \
  --input target \
  --main-jar SanMarcos-1.0-SNAPSHOT.jar \
  --main-class org.example.Vista.MainApp \
  --dest dist
```

El resultado queda en `dist/LubricanteSanMarcos/` y se ejecuta con doble clic en `LubricanteSanMarcos.exe`.

> Nota: la PC donde se ejecute debe poder conectarse a la instancia de SQL Server indicada en `persistence.xml` (por ejemplo `localhost:1433`).

---

## 🔑 Usuarios

Los usuarios se administran desde **Configuración → Usuarios** (solo ADMIN). Las contraseñas se almacenan encriptadas con **BCrypt**, por lo que no se guardan en texto plano.

---

## 🗒️ Licencia

Proyecto de uso interno para **Lubricantes San Marcos**.

---

Hecho con ❤️ por **Miky Angel**.
