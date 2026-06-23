CREATE TABLE Usuario (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    NombreUsuario VARCHAR(50) NOT NULL UNIQUE,
    Contrasenia VARCHAR(255) NOT NULL,
    NombreCompleto VARCHAR(100) NOT NULL,
    Rol VARCHAR(20) NOT NULL DEFAULT 'VENDEDOR',
    Activo BIT DEFAULT 1,
    FechaCreacion DATETIME DEFAULT GETDATE()
);

-- Insertar usuario ADMIN (contraseña: admin123)
-- La contraseña se encriptará con BCrypt desde la aplicación
INSERT INTO Usuario (NombreUsuario, Contrasenia, NombreCompleto, Rol)
VALUES ('Lescano', '$2a$10$7jQpZqX3XqX3XqX3XqX3Xu', 'Jose Luis Lescano', 'ADMIN');

-- Insertar usuario VENDEDOR (contraseña: ventas123)
INSERT INTO Usuario (NombreUsuario, Contrasenia, NombreCompleto, Rol)
VALUES ('Ema', '$2a$10$8kRpZrY4YrY4YrY4YrY4Yu', 'Emanuel', 'VENDEDOR');