-- =====================================================
-- DATOS DE PRUEBA PARA LUBRICANTE SAN MARCOS
-- =====================================================

USE LubricanteSanMarcos;
GO

-- =====================================================
-- 1. LIMPIAR DATOS EXISTENTES (OPCIONAL)
-- =====================================================
-- Comenta estas líneas si NO quieres eliminar datos existentes

DELETE FROM DetalleCompra;
DELETE FROM DetalleVenta;
DELETE FROM Compra;
DELETE FROM Venta;
DELETE FROM Proveedor;
DELETE FROM Cliente;
DELETE FROM Persona;
DELETE FROM Contacto;
DELETE FROM Aceite;
DELETE FROM Filtro;
DELETE FROM Foco;
DELETE FROM Producto;
DELETE FROM Categoria;
DELETE FROM Marca;
DELETE FROM Presentacion;
DBCC CHECKIDENT ('Categoria', RESEED, 0);
DBCC CHECKIDENT ('Marca', RESEED, 0);
DBCC CHECKIDENT ('Presentacion', RESEED, 0);
DBCC CHECKIDENT ('Producto', RESEED, 0);
DBCC CHECKIDENT ('Contacto', RESEED, 0);
DBCC CHECKIDENT ('Persona', RESEED, 0);
DBCC CHECKIDENT ('Cliente', RESEED, 0);
DBCC CHECKIDENT ('Proveedor', RESEED, 0);


-- =====================================================
-- 2. CATEGORÍAS (5)
-- =====================================================
INSERT INTO Categoria (Nombre) VALUES ('Aceites');
INSERT INTO Categoria (Nombre) VALUES ('Filtros');
INSERT INTO Categoria (Nombre) VALUES ('Focos');
INSERT INTO Categoria (Nombre) VALUES ('Lubricantes');
INSERT INTO Categoria (Nombre) VALUES ('Aditivos');
GO

-- =====================================================
-- 3. MARCAS (5)
-- =====================================================
INSERT INTO Marca (Nombre) VALUES ('Castrol');
INSERT INTO Marca (Nombre) VALUES ('Mobil');
INSERT INTO Marca (Nombre) VALUES ('Bosch');
INSERT INTO Marca (Nombre) VALUES ('Shell');
INSERT INTO Marca (Nombre) VALUES ('Valvoline');
GO

-- =====================================================
-- 4. PRESENTACIONES (5)
-- =====================================================
INSERT INTO Presentacion (Nombre, Litros) VALUES ('Botella', '1L');
INSERT INTO Presentacion (Nombre, Litros) VALUES ('Galón', '4L');
INSERT INTO Presentacion (Nombre, Litros) VALUES ('Bidón', '20L');
INSERT INTO Presentacion (Nombre, Litros) VALUES ('Tambor', '200L');
INSERT INTO Presentacion (Nombre, Litros) VALUES ('Sobre', '1/4L');
GO

-- =====================================================
-- 5. PRODUCTOS (5 base)
-- =====================================================
-- Aceite 20W-50
INSERT INTO Producto (Nombre, Precio, Stock, Detalle, IdCategoria, IdMarca) 
VALUES ('Aceite 20W-50', 85.00, 100, 'Aceite mineral para motor gasolinero', 1, 1);
INSERT INTO Aceite (Viscosidad, TipoAceite, Uso, EsAgrenel, IdPresentacion, IdProducto)
VALUES ('20W-50', 'Mineral', 'Motor', 0, 1, SCOPE_IDENTITY());

-- Aceite 15W-40
INSERT INTO Producto (Nombre, Precio, Stock, Detalle, IdCategoria, IdMarca) 
VALUES ('Aceite 15W-40', 90.00, 80, 'Aceite para motor diesel', 1, 2);
INSERT INTO Aceite (Viscosidad, TipoAceite, Uso, EsAgrenel, IdPresentacion, IdProducto)
VALUES ('15W-40', 'Sintético', 'Diesel', 0, 1, SCOPE_IDENTITY());

-- Filtro de Aceite
INSERT INTO Producto (Nombre, Precio, Stock, Detalle, IdCategoria, IdMarca) 
VALUES ('Filtro de Aceite', 35.00, 50, 'Filtro de alta eficiencia', 2, 3);
INSERT INTO Filtro (Codigo, Rosca, Uso, IdProducto)
VALUES ('FL-910', '3/4-16', 'Aceite', SCOPE_IDENTITY());

-- Filtro de Aire
INSERT INTO Producto (Nombre, Precio, Stock, Detalle, IdCategoria, IdMarca) 
VALUES ('Filtro de Aire', 45.00, 40, 'Filtro de aire para motor', 2, 3);
INSERT INTO Filtro (Codigo, Rosca, Uso, IdProducto)
VALUES ('FA-281', 'N/A', 'Aire', SCOPE_IDENTITY());

-- Foco Halógeno H4
INSERT INTO Producto (Nombre, Precio, Stock, Detalle, IdCategoria, IdMarca) 
VALUES ('Foco Halógeno H4', 28.50, 60, 'Luz blanca 12V 60/55W', 3, 3);
INSERT INTO Foco (Codigo, IdProducto)
VALUES ('H4-12V', SCOPE_IDENTITY());
GO

-- =====================================================
-- 6. CONTACTOS (5)
-- =====================================================
INSERT INTO Contacto (Celular) VALUES ('777-10001');
INSERT INTO Contacto (Celular) VALUES ('777-10002');
INSERT INTO Contacto (Celular) VALUES ('777-10003');
INSERT INTO Contacto (Celular) VALUES ('777-10004');
INSERT INTO Contacto (Celular) VALUES ('777-10005');
GO

-- =====================================================
-- 7. PERSONAS (5)
-- =====================================================
INSERT INTO Persona (Nombres, Apellidos, IdContacto) VALUES ('Juan', 'Perez', 1);
INSERT INTO Persona (Nombres, Apellidos, IdContacto) VALUES ('Maria', 'Lopez', 2);
INSERT INTO Persona (Nombres, Apellidos, IdContacto) VALUES ('Carlos', 'Mamani', 3);
INSERT INTO Persona (Nombres, Apellidos, IdContacto) VALUES ('Ana', 'Flores', 4);
INSERT INTO Persona (Nombres, Apellidos, IdContacto) VALUES ('Luis', 'Torrez', 5);
GO

-- =====================================================
-- 8. CLIENTES (3)
-- =====================================================
INSERT INTO Cliente (IdPersona) VALUES (1);
INSERT INTO Cliente (IdPersona) VALUES (2);
INSERT INTO Cliente (IdPersona) VALUES (3);
GO

-- =====================================================
-- 9. PROVEEDORES (2)
-- =====================================================
INSERT INTO Proveedor (IdPersona, Empresa) VALUES (4, 'Lubricantes del Sur S.R.L.');
INSERT INTO Proveedor (IdPersona, Empresa) VALUES (5, 'Importadora AutoParts S.A.');
GO

-- =====================================================
-- 10. COMPRAS (2 compras con detalles)
-- =====================================================
-- Compra 1: Aceite 20W-50 y Filtro de Aceite
INSERT INTO Compra (Fecha, Total, IdProveedor) VALUES (GETDATE(), 0, 1);
DECLARE @IdCompra1 INT = SCOPE_IDENTITY();

INSERT INTO DetalleCompra (Cantidad, PrecioCompra, IdProducto, IdCompra) VALUES (20, 70.00, 1, @IdCompra1);
INSERT INTO DetalleCompra (Cantidad, PrecioCompra, IdProducto, IdCompra) VALUES (15, 28.00, 3, @IdCompra1);

-- Actualizar total de compra 1
UPDATE Compra SET Total = (
    SELECT SUM(Cantidad * PrecioCompra) FROM DetalleCompra WHERE IdCompra = @IdCompra1
) WHERE Id = @IdCompra1;

-- Actualizar stock de productos comprados
UPDATE Producto SET Stock = Stock + 20 WHERE Id = 1;
UPDATE Producto SET Stock = Stock + 15 WHERE Id = 3;

-- Compra 2: Aceite 15W-40 y Foco
INSERT INTO Compra (Fecha, Total, IdProveedor) VALUES (GETDATE(), 0, 2);
DECLARE @IdCompra2 INT = SCOPE_IDENTITY();

INSERT INTO DetalleCompra (Cantidad, PrecioCompra, IdProducto, IdCompra) VALUES (25, 75.00, 2, @IdCompra2);
INSERT INTO DetalleCompra (Cantidad, PrecioCompra, IdProducto, IdCompra) VALUES (30, 22.00, 5, @IdCompra2);

-- Actualizar total de compra 2
UPDATE Compra SET Total = (
    SELECT SUM(Cantidad * PrecioCompra) FROM DetalleCompra WHERE IdCompra = @IdCompra2
) WHERE Id = @IdCompra2;

-- Actualizar stock de productos comprados
UPDATE Producto SET Stock = Stock + 25 WHERE Id = 2;
UPDATE Producto SET Stock = Stock + 30 WHERE Id = 5;
GO

-- =====================================================
-- 11. VENTAS (2 ventas con detalles)
-- =====================================================
-- Venta 1: Aceite 20W-50 y Filtro de Aire
INSERT INTO Venta (Fecha, Total, IdCliente) VALUES (GETDATE(), 0, 1);
DECLARE @IdVenta1 INT = SCOPE_IDENTITY();

INSERT INTO DetalleVenta (Cantidad, PrecioVenta, IdProducto, IdVenta) VALUES (5, 95.00, 1, @IdVenta1);
INSERT INTO DetalleVenta (Cantidad, PrecioVenta, IdProducto, IdVenta) VALUES (3, 55.00, 4, @IdVenta1);

-- Actualizar total de venta 1
UPDATE Venta SET Total = (
    SELECT SUM(Cantidad * PrecioVenta) FROM DetalleVenta WHERE IdVenta = @IdVenta1
) WHERE Id = @IdVenta1;

-- Actualizar stock de productos vendidos (restar)
UPDATE Producto SET Stock = Stock - 5 WHERE Id = 1;
UPDATE Producto SET Stock = Stock - 3 WHERE Id = 4;

-- Venta 2: Aceite 15W-40 y Foco
INSERT INTO Venta (Fecha, Total, IdCliente) VALUES (GETDATE(), 0, 2);
DECLARE @IdVenta2 INT = SCOPE_IDENTITY();

INSERT INTO DetalleVenta (Cantidad, PrecioVenta, IdProducto, IdVenta) VALUES (4, 105.00, 2, @IdVenta2);
INSERT INTO DetalleVenta (Cantidad, PrecioVenta, IdProducto, IdVenta) VALUES (6, 35.00, 5, @IdVenta2);

-- Actualizar total de venta 2
UPDATE Venta SET Total = (
    SELECT SUM(Cantidad * PrecioVenta) FROM DetalleVenta WHERE IdVenta = @IdVenta2
) WHERE Id = @IdVenta2;

-- Actualizar stock de productos vendidos (restar)
UPDATE Producto SET Stock = Stock - 4 WHERE Id = 2;
UPDATE Producto SET Stock = Stock - 6 WHERE Id = 5;
GO

-- =====================================================
-- 12. VERIFICAR DATOS INSERTADOS
-- =====================================================
PRINT '=== VERIFICACIÓN DE DATOS ===';

SELECT 'Categorías' as Tabla, COUNT(*) as Cantidad FROM Categoria;
SELECT 'Marcas' as Tabla, COUNT(*) as Cantidad FROM Marca;
SELECT 'Presentaciones' as Tabla, COUNT(*) as Cantidad FROM Presentacion;
SELECT 'Productos' as Tabla, COUNT(*) as Cantidad FROM Producto;
SELECT 'Contactos' as Tabla, COUNT(*) as Cantidad FROM Contacto;
SELECT 'Personas' as Tabla, COUNT(*) as Cantidad FROM Persona;
SELECT 'Clientes' as Tabla, COUNT(*) as Cantidad FROM Cliente;
SELECT 'Proveedores' as Tabla, COUNT(*) as Cantidad FROM Proveedor;
SELECT 'Compras' as Tabla, COUNT(*) as Cantidad FROM Compra;
SELECT 'Ventas' as Tabla, COUNT(*) as Cantidad FROM Venta;

-- Mostrar productos con su stock actual
PRINT '=== STOCK ACTUAL DE PRODUCTOS ===';
SELECT Id, Nombre, Stock, Precio FROM Producto ORDER BY Id;
GO