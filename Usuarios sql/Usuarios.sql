USE LubricanteSanMarcos;
GO

-- Crear login (nivel servidor)
CREATE LOGIN app_lubricantes
WITH PASSWORD = 'adminLubricantes123!';
GO

-- Crear usuario dentro de la BD
CREATE USER app_lubricantes
FOR LOGIN app_lubricantes;
GO

-- Dar permisos de administrador sobre la BD
ALTER ROLE db_owner ADD MEMBER app_lubricantes;
GO