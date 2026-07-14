-- GlassCore Logistics
-- Script de base de datos para MySQL Workbench

DROP DATABASE IF EXISTS glasscore_db;
CREATE DATABASE glasscore_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE glasscore_db;

CREATE TABLE empleado (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    cargo           ENUM('CHOFER','INSTALADOR','ADMINISTRATIVO','SUPERVISOR') NOT NULL,
    salario_base    DECIMAL(12,2) NOT NULL,
    telefono        VARCHAR(20),
    activo          TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE herramienta (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(50) NOT NULL UNIQUE,
    nombre          VARCHAR(100) NOT NULL,
    tipo            VARCHAR(50) NOT NULL,
    estado          ENUM('DISPONIBLE','ASIGNADA','MANTENIMIENTO') NOT NULL DEFAULT 'DISPONIBLE',
    empleado_id     INT NULL,
    CONSTRAINT fk_herramienta_empleado
        FOREIGN KEY (empleado_id) REFERENCES empleado(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE material (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    tipo             ENUM('VIDRIO','ALUMINIO','METAL') NOT NULL,
    unidad           ENUM('M2','ML') NOT NULL,
    stock            DECIMAL(12,3) NOT NULL DEFAULT 0,
    precio_unitario  DECIMAL(12,2) NOT NULL
);

CREATE TABLE cotizacion (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    cliente           VARCHAR(150) NOT NULL,
    tipo_estructura   VARCHAR(50) NOT NULL,
    ancho             DECIMAL(10,3) NOT NULL,
    alto              DECIMAL(10,3) NOT NULL,
    area_vidrio       DECIMAL(12,3) NOT NULL,
    metros_aluminio   DECIMAL(12,3) NOT NULL,
    metros_metal      DECIMAL(12,3) NOT NULL,
    subtotal          DECIMAL(12,2) NOT NULL,
    alerta_compra     TEXT NULL,
    fecha             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE planilla (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    empleado_id   INT NOT NULL,
    salario_base  DECIMAL(12,2) NOT NULL,
    horas_extras  DECIMAL(12,2) NOT NULL DEFAULT 0,
    viaticos      DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_neto    DECIMAL(12,2) NOT NULL,
    fecha_pago    DATE NOT NULL,
    CONSTRAINT fk_planilla_empleado
        FOREIGN KEY (empleado_id) REFERENCES empleado(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE vehiculo (
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    placa                    VARCHAR(20) NOT NULL UNIQUE,
    marca                    VARCHAR(50) NOT NULL,
    km_actual                INT NOT NULL DEFAULT 0,
    km_limite_mantenimiento  INT NOT NULL,
    chofer_id                INT NULL,
    CONSTRAINT fk_vehiculo_chofer
        FOREIGN KEY (chofer_id) REFERENCES empleado(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE viaje (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    vehiculo_id          INT NOT NULL,
    chofer_id            INT NOT NULL,
    ruta                 VARCHAR(100) NOT NULL DEFAULT 'Tegucigalpa - Comayagua',
    es_redondo           TINYINT(1) NOT NULL DEFAULT 0,
    kilometros           INT NOT NULL,
    factor_rendimiento   DECIMAL(8,4) NOT NULL,
    precio_combustible   DECIMAL(10,2) NOT NULL,
    litros_estimados     DECIMAL(10,3) NOT NULL,
    gasto_combustible    DECIMAL(12,2) NOT NULL,
    fecha_salida         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_viaje_vehiculo
        FOREIGN KEY (vehiculo_id) REFERENCES vehiculo(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_viaje_chofer
        FOREIGN KEY (chofer_id) REFERENCES empleado(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO empleado (nombre, apellido, cargo, salario_base, telefono) VALUES
('Carlos',  CONVERT(UNHEX('4D656AC3AD61') USING utf8mb4), 'CHOFER', 12500.00, '9876-1101'),
('Luis',    'Pineda', 'CHOFER', 12000.00, '9876-1102'),
(CONVERT(UNHEX('4D6172C3AD61') USING utf8mb4), CONVERT(UNHEX('4CC3B370657A') USING utf8mb4), 'INSTALADOR', 11000.00, '9876-2201'),
(CONVERT(UNHEX('4A6F73C3A9') USING utf8mb4), CONVERT(UNHEX('4865726EC3A16E64657A') USING utf8mb4), 'INSTALADOR', 10800.00, '9876-2202'),
('Ana', CONVERT(UNHEX('47617263C3AD61') USING utf8mb4), 'SUPERVISOR', 16000.00, '9876-3301'),
('Pedro', CONVERT(UNHEX('52616DC3AD72657A') USING utf8mb4), 'ADMINISTRATIVO', 9500.00, '9876-4401'),
('Roberto', 'Castillo', 'CHOFER', 11800.00, '9876-1103'),
(CONVERT(UNHEX('536F66C3AD61') USING utf8mb4), CONVERT(UNHEX('4D617274C3AD6E657A') USING utf8mb4), 'INSTALADOR', 11200.00, '9876-2203'),
('Diego', 'Flores', 'INSTALADOR', 10500.00, '9876-2204'),
('Elena', 'Vargas', 'ADMINISTRATIVO', 9800.00, '9876-4402'),
(CONVERT(UNHEX('416E6472C3A973') USING utf8mb4), 'Morales', 'SUPERVISOR', 15500.00, '9876-3302'),
('Karla', 'Santos', 'INSTALADOR', 10900.00, '9876-2205');

INSERT INTO herramienta (codigo, nombre, tipo, estado, empleado_id) VALUES
('HER-001', 'Taladro industrial Bosch', 'TALADRO', 'ASIGNADA', 3),
('HER-002', 'Cortadora de vidrio Diamond', 'CORTADORA', 'DISPONIBLE', NULL),
('HER-003', 'Ventosa industrial 200kg', 'VENTOSA', 'ASIGNADA', 1),
('HER-004', 'Ventosa industrial 150kg', 'VENTOSA', 'DISPONIBLE', NULL),
('HER-005', 'Amoladora Makita', 'AMOLADORA', 'ASIGNADA', 4),
('HER-006', CONVERT(UNHEX('4E6976656C206CC3A173657220426F736368') USING utf8mb4), 'NIVEL', 'DISPONIBLE', NULL),
('HER-007', 'Taladro percutor Dewalt', 'TALADRO', 'DISPONIBLE', NULL),
('HER-008', CONVERT(UNHEX('436F727461646F726120656CC3A9637472696361204D696C7761756B6565') USING utf8mb4), 'CORTADORA', 'ASIGNADA', 8),
('HER-009', 'Ventosa doble 300kg', 'VENTOSA', 'DISPONIBLE', NULL),
('HER-010', 'Esmeril angular Makita', 'AMOLADORA', 'DISPONIBLE', NULL),
('HER-011', CONVERT(UNHEX('4E6976656C206CC3A17365722033363020426F736368') USING utf8mb4), 'NIVEL', 'ASIGNADA', 3),
('HER-012', 'Pistola de silicona industrial', 'SILICONA', 'DISPONIBLE', NULL),
('HER-013', CONVERT(UNHEX('43696E7461206DC3A97472696361206CC3A1736572204C65696361') USING utf8mb4), 'MEDICION', 'DISPONIBLE', NULL),
('HER-014', 'Martillo de goma 5lb', 'GOLPE', 'ASIGNADA', 9),
('HER-015', 'Escuadra de aluminio 1.2m', 'MEDICION', 'DISPONIBLE', NULL);

INSERT INTO material (nombre, tipo, unidad, stock, precio_unitario) VALUES
('Vidrio templado 6mm', 'VIDRIO', 'M2', 12.000, 450.00),
('Vidrio laminado 8mm', 'VIDRIO', 'M2', 8.000, 620.00),
('Vidrio reflectivo 10mm', 'VIDRIO', 'M2', 6.000, 780.00),
('Perfil aluminio natural', 'ALUMINIO', 'ML', 80.000, 95.00),
('Perfil aluminio negro', 'ALUMINIO', 'ML', 60.000, 110.00),
('Perfil aluminio bronce', 'ALUMINIO', 'ML', 45.000, 125.00),
(CONVERT(UNHEX('4D6172636F206D6574C3A16C69636F2067616C76616E697A61646F') USING utf8mb4), 'METAL', 'ML', 50.000, 75.00),
(CONVERT(UNHEX('4D6172636F206D6574C3A16C69636F206E6567726F') USING utf8mb4), 'METAL', 'ML', 35.000, 90.00);

INSERT INTO cotizacion (cliente, tipo_estructura, ancho, alto, area_vidrio, metros_aluminio, metros_metal, subtotal, alerta_compra, fecha) VALUES
('Residencial Los Pinos', 'VENTANA', 1.200, 1.500, 1.800, 5.400, 5.400, 2142.00, NULL, '2026-06-10 09:15:00'),
('Comercial MetroMall', 'PUERTA', 2.100, 2.400, 5.040, 9.000, 9.000, 4693.80, NULL, '2026-06-18 11:40:00'),
('Hotel Plaza Real', 'BALCON', 3.500, 2.800, 9.800, 12.600, 12.600, 7959.00, NULL, '2026-07-01 14:20:00'),
('Torre Centro', 'VENTANA', 8.000, 4.000, 32.000, 24.000, 24.000, 21240.00,
 'ALERTA DE COMPRA REQUERIDA: adquirir 6.000 m2 de vidrio (necesario 32.000, stock 26.000)',
 '2026-07-08 16:05:00'),
(CONVERT(UNHEX('436CC3AD6E6963612053616E7461204D6172C3AD61') USING utf8mb4), 'PUERTA', 1.800, 2.200, 3.960, 8.000, 8.000, 3702.00, NULL, '2026-07-12 10:30:00');

INSERT INTO planilla (empleado_id, salario_base, horas_extras, viaticos, total_neto, fecha_pago) VALUES
(1, 12500.00, 800.00, 350.00, 13650.00, '2026-06-15'),
(2, 12000.00, 500.00, 200.00, 12700.00, '2026-06-15'),
(3, 11000.00, 1200.00, 450.00, 12650.00, '2026-06-15'),
(4, 10800.00, 600.00, 300.00, 11700.00, '2026-06-15'),
(5, 16000.00, 0.00, 0.00, 16000.00, '2026-06-15'),
(1, 12500.00, 950.00, 400.00, 13850.00, '2026-07-15'),
(3, 11000.00, 700.00, 250.00, 11950.00, '2026-07-15'),
(8, 11200.00, 400.00, 150.00, 11750.00, '2026-07-15'),
(6, 9500.00, 200.00, 100.00, 9800.00, '2026-07-15'),
(9, 10500.00, 850.00, 500.00, 11850.00, '2026-07-15');

INSERT INTO vehiculo (placa, marca, km_actual, km_limite_mantenimiento, chofer_id) VALUES
('HND-4521', 'Toyota Hilux', 99850, 100000, 1),
('HND-3388', 'Nissan Frontier', 45200, 60000, 2),
('HND-9910', 'Isuzu D-Max', 19800, 25000, NULL),
('HND-7744', 'Mitsubishi L200', 32100, 50000, 7),
('HND-2205', 'Ford Ranger', 67800, 70000, NULL);

INSERT INTO viaje (vehiculo_id, chofer_id, ruta, es_redondo, kilometros, factor_rendimiento, precio_combustible, litros_estimados, gasto_combustible, fecha_salida) VALUES
(2, 2, 'Tegucigalpa - Comayagua (Simple)', 0, 85, 0.12, 32.50, 10.200, 331.50, '2026-06-20 07:30:00'),
(4, 7, 'Tegucigalpa - Comayagua (Redondo)', 1, 170, 0.12, 32.50, 20.400, 663.00, '2026-07-02 08:00:00'),
(2, 2, 'Tegucigalpa - Comayagua (Simple)', 0, 85, 0.12, 32.50, 10.200, 331.50, '2026-07-10 06:45:00');

SELECT 'Base de datos glasscore_db creada correctamente' AS mensaje;