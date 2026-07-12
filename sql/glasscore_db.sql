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
('Carlos', 'Mejía',     'CHOFER',         12500.00, '9876-1101'),
('Luis',   'Pineda',    'CHOFER',         12000.00, '9876-1102'),
('María',  'López',     'INSTALADOR',     11000.00, '9876-2201'),
('José',   'Hernández', 'INSTALADOR',     10800.00, '9876-2202'),
('Ana',    'García',    'SUPERVISOR',     16000.00, '9876-3301'),
('Pedro',  'Ramírez',   'ADMINISTRATIVO', 9500.00,  '9876-4401');

INSERT INTO herramienta (codigo, nombre, tipo, estado) VALUES
('HER-001', 'Taladro industrial Bosch',    'TALADRO',   'DISPONIBLE'),
('HER-002', 'Cortadora de vidrio Diamond', 'CORTADORA', 'DISPONIBLE'),
('HER-003', 'Ventosa industrial 200kg',    'VENTOSA',   'DISPONIBLE'),
('HER-004', 'Ventosa industrial 150kg',    'VENTOSA',   'DISPONIBLE'),
('HER-005', 'Amoladora Makita',            'AMOLADORA', 'DISPONIBLE'),
('HER-006', 'Nivel láser Bosch',           'NIVEL',     'DISPONIBLE');

INSERT INTO material (nombre, tipo, unidad, stock, precio_unitario) VALUES
('Vidrio templado 6mm',       'VIDRIO',   'M2', 25.000, 450.00),
('Vidrio laminado 8mm',       'VIDRIO',   'M2', 18.000, 620.00),
('Perfil aluminio natural',   'ALUMINIO', 'ML', 80.000, 95.00),
('Perfil aluminio negro',     'ALUMINIO', 'ML', 60.000, 110.00),
('Marco metálico galvanizado','METAL',    'ML', 50.000, 75.00);

INSERT INTO vehiculo (placa, marca, km_actual, km_limite_mantenimiento, chofer_id) VALUES
('HND-4521', 'Toyota Hilux',    98450, 100000, 1),
('HND-3388', 'Nissan Frontier', 45200,  60000, 2),
('HND-9910', 'Isuzu D-Max',     19800,  25000, NULL);

SELECT 'Base de datos glasscore_db creada correctamente' AS mensaje;