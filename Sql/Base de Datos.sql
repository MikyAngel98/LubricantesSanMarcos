create dataBase LubricanteSanMarcos
go
use LubricanteSanMarcos

go
create table Categoria
(
	Id int identity(1,1) primary key,
	Nombre varchar(30) not null
);

go
create table Presentacion
(
	Id int identity(1,1) primary key,
	Nombre varchar(50),
	Litros Varchar(30)
);

go
create table Marca
(
	Id int identity(1,1) primary key,
	Nombre varchar(30) not null
);

go
create table Producto(
	Id int identity(1,1) primary key,
	Nombre varchar(30) not null,
	Precio decimal(10,2) not null,
	Stock decimal(10,2) not null,
	Detalle varchar(200) null,
	IdCategoria int not null,
	IdMarca int not null,
	foreign key (IdCategoria) references Categoria(Id),
	foreign key (IdMarca) references Marca(Id)
);

go
create table Foco
(
	Id int identity(1,1) primary key,
	Codigo varchar(20),
	IdProducto int not null,
	foreign key (IdProducto) references Producto(Id)
);

go
create table Filtro
(
	Id int identity(1,1) primary key,
	Codigo varchar(20),
	Rosca varchar(15),
	Uso Varchar(15),
	IdProducto int not null,
	foreign key (IdProducto) references Producto(Id)
);

go
create table Aceite
(
	Id int identity(1,1) primary key,
	Viscosidad varchar(20),
	TipoAceite varchar(15),
	Uso Varchar(30),
	EsAgrenel bit,
	IdPresentacion int not null,
	IdProducto int not null,
	foreign key (IdProducto) references Producto(Id),
	foreign key (IdPresentacion) references Presentacion(Id)
);

go
create table Contacto
(
	Id int identity(1,1) primary key,
	Celular varchar(20)
);

go
create table Persona
(
	Id int identity(1,1) primary key,
	Nombres varchar(50),
	Apellidos varchar(50),
	IdContacto int not null,
	foreign key(IdContacto) references Contacto(Id)
);

go
create table Cliente
(
	Id int identity(1,1) primary key,
	IdPersona int not null,
	foreign key (IdPersona) references Persona(Id)
);

go
create table Proveedor
(
	Id int identity(1,1) primary key,
	IdPersona int not null,
	Empresa varchar(20),
	foreign key (IdPersona) references Persona(Id)
);

go
create table Venta(
	Id int identity(1,1) primary key,
	Fecha date DEFAULT GETDATE(),
	Total decimal(10,2) not null,
	IdCliente int null,
	foreign key (IdCliente) references Cliente(Id)
);

go
create table DetalleVenta(
	Id int identity(1,1) primary key,
	Cantidad Decimal(10,2) not null,
	PrecioVenta decimal(10,2) not null,
	IdProducto int not null,
	IdVenta int not null,
	foreign key (IdProducto) references Producto(Id),
	foreign key (IdVenta) references Venta(Id),
	unique(IdVenta, IdProducto)
);

go
create table Compra(
	Id int identity(1,1) primary key,
	Fecha date DEFAULT GETDATE(),
	Total decimal(10,2) not null,
	IdProveedor int not null,
	foreign key (IdProveedor) references Proveedor(Id)
);

go
create table DetalleCompra(
	Id int identity(1,1) primary key,
	Cantidad Decimal(10,2) not null,
	PrecioCompra decimal(10,2) not null,
	IdProducto int not null,
	IdCompra int not null,
	foreign key (IdProducto) references Producto(Id),
	foreign key (IdCompra) references Compra(Id),
	unique(IdCompra, IdProducto)
);