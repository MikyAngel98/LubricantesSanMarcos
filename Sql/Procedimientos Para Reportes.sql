-- ==================== VENTAS ====================

CREATE PROCEDURE sp_ReporteVentas
    @FechaInicio DATE,
    @FechaFin DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        v.Id,
        v.Fecha,
        ISNULL(p.Nombres + ' ' + p.Apellidos, 'CLIENTE OCASIONAL') AS Cliente,
        COUNT(DISTINCT dv.IdProducto) AS TiposProductos,
        v.Total,
        v.MetodoPago
    FROM Venta v
    LEFT JOIN Cliente c ON v.IdCliente = c.Id
    LEFT JOIN Persona p ON c.IdPersona = p.Id
    INNER JOIN DetalleVenta dv ON v.Id = dv.IdVenta
    WHERE v.Fecha BETWEEN @FechaInicio AND @FechaFin
    GROUP BY v.Id, v.Fecha, p.Nombres, p.Apellidos, v.Total, v.MetodoPago
    ORDER BY v.Fecha DESC
END
GO

CREATE PROCEDURE sp_DetalleVenta
    @IdVenta INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        p.Nombre AS Producto,
        CASE 
            WHEN a.Id IS NOT NULL THEN a.Viscosidad
            ELSE NULL
        END AS Viscosidad,
        CASE 
            WHEN f.Id IS NOT NULL THEN f.Codigo
            WHEN fo.Id IS NOT NULL THEN fo.Codigo
            ELSE NULL
        END AS Codigo,
        m.Nombre AS Marca,
        c.Nombre AS Categoria,
        dv.Cantidad,
        dv.PrecioVenta,
        (dv.Cantidad * dv.PrecioVenta) AS Subtotal
    FROM DetalleVenta dv
    INNER JOIN Producto p ON dv.IdProducto = p.Id
    LEFT JOIN Aceite a ON p.Id = a.IdProducto
    LEFT JOIN Filtro f ON p.Id = f.IdProducto
    LEFT JOIN Foco fo ON p.Id = fo.IdProducto
    INNER JOIN Marca m ON p.IdMarca = m.Id
    INNER JOIN Categoria c ON p.IdCategoria = c.Id
    WHERE dv.IdVenta = @IdVenta
    ORDER BY p.Nombre
END
GO

-- ==================== COMPRAS ====================

CREATE PROCEDURE sp_HistorialCompras
    @FechaInicio DATE,
    @FechaFin DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        c.Id,
        c.Fecha,
        pr.Empresa AS Proveedor,
        COUNT(DISTINCT dc.IdProducto) AS TiposProductos,
        c.Total
    FROM Compra c
    INNER JOIN Proveedor pr ON c.IdProveedor = pr.Id
    INNER JOIN DetalleCompra dc ON c.Id = dc.IdCompra
    WHERE c.Fecha BETWEEN @FechaInicio AND @FechaFin
    GROUP BY c.Id, c.Fecha, pr.Empresa, c.Total
    ORDER BY c.Fecha DESC
END
GO

CREATE PROCEDURE sp_DetalleCompra
    @IdCompra INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        p.Nombre AS Producto,
        CASE 
            WHEN a.Id IS NOT NULL THEN a.Viscosidad
            ELSE NULL
        END AS Viscosidad,
        CASE 
            WHEN f.Id IS NOT NULL THEN f.Codigo
            WHEN fo.Id IS NOT NULL THEN fo.Codigo
            ELSE NULL
        END AS Codigo,
        m.Nombre AS Marca,
        c.Nombre AS Categoria,
        dc.Cantidad,
        dc.PrecioCompra,
        (dc.Cantidad * dc.PrecioCompra) AS Subtotal
    FROM DetalleCompra dc
    INNER JOIN Producto p ON dc.IdProducto = p.Id
    LEFT JOIN Aceite a ON p.Id = a.IdProducto
    LEFT JOIN Filtro f ON p.Id = f.IdProducto
    LEFT JOIN Foco fo ON p.Id = fo.IdProducto
    INNER JOIN Marca m ON p.IdMarca = m.Id
    INNER JOIN Categoria c ON p.IdCategoria = c.Id
    WHERE dc.IdCompra = @IdCompra
    ORDER BY p.Nombre
END
GO

-- ==================== REPORTES ADICIONALES ====================

CREATE PROCEDURE sp_ProductosMasVendidos
    @FechaInicio DATE,
    @FechaFin DATE,
    @Top INT = 10
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP (@Top)
        p.Id,
        p.Nombre,
        c.Nombre AS Categoria,
        m.Nombre AS Marca,
        SUM(dv.Cantidad) AS TotalVendido,
        SUM(dv.Cantidad * dv.PrecioVenta) AS TotalFacturado
    FROM Producto p
    INNER JOIN DetalleVenta dv ON p.Id = dv.IdProducto
    INNER JOIN Venta v ON dv.IdVenta = v.Id
    INNER JOIN Categoria c ON p.IdCategoria = c.Id
    INNER JOIN Marca m ON p.IdMarca = m.Id
    WHERE v.Fecha BETWEEN @FechaInicio AND @FechaFin
    GROUP BY p.Id, p.Nombre, c.Nombre, m.Nombre
    ORDER BY TotalVendido DESC
END
GO

CREATE PROCEDURE sp_ProductosBajoStock
    @StockMinimo DECIMAL(10,2) = 5
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        p.Id,
        p.Nombre,
        c.Nombre AS Categoria,
        m.Nombre AS Marca,
        p.Stock,
        p.Precio
    FROM Producto p
    INNER JOIN Categoria c ON p.IdCategoria = c.Id
    INNER JOIN Marca m ON p.IdMarca = m.Id
    WHERE p.Stock <= @StockMinimo
    ORDER BY p.Stock ASC
END
GO