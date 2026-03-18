USE LubricanteSanMarcos;
GO

-- =========================
-- PRODUCTO
-- =========================

CREATE INDEX idx_producto_categoria
ON Producto(IdCategoria);
GO

CREATE INDEX idx_producto_marca
ON Producto(IdMarca);
GO

-- Búsqueda por nombre (MUY IMPORTANTE)
CREATE INDEX idx_producto_nombre
ON Producto(Nombre);
GO


-- =========================
-- DETALLE VENTA
-- =========================

-- Para ver detalles por venta
CREATE INDEX idx_detalleventa_venta
ON DetalleVenta(IdVenta);
GO

-- Para consultas por producto (estadísticas, stock)
CREATE INDEX idx_detalleventa_producto
ON DetalleVenta(IdProducto);
GO


-- =========================
-- DETALLE COMPRA
-- =========================

-- Para ver detalles por compra
CREATE INDEX idx_detallecompra_compra
ON DetalleCompra(IdCompra);
GO

-- Para consultas por producto
CREATE INDEX idx_detallecompra_producto
ON DetalleCompra(IdProducto);
GO


-- =========================
-- VENTA
-- =========================

-- Consultas por cliente
CREATE INDEX idx_venta_cliente
ON Venta(IdCliente);
GO


-- =========================
-- COMPRA
-- =========================

-- Consultas por proveedor
CREATE INDEX idx_compra_proveedor
ON Compra(IdProveedor);
GO