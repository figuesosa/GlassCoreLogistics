CREATE TABLE IF NOT EXISTS cliente (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    identidad_rtn   VARCHAR(20) NOT NULL UNIQUE,
    telefono        VARCHAR(30),
    email           VARCHAR(120),
    direccion       VARCHAR(250),
    activo          TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS proveedor (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    rtn             VARCHAR(20) NOT NULL UNIQUE,
    direccion       VARCHAR(250),
    activo          TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS proveedor_telefono (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id    INT NOT NULL,
    telefono        VARCHAR(30) NOT NULL,
    CONSTRAINT fk_prov_tel FOREIGN KEY (proveedor_id) REFERENCES proveedor(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS proveedor_contacto (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id    INT NOT NULL,
    nombre          VARCHAR(120) NOT NULL,
    cargo           VARCHAR(80),
    telefono        VARCHAR(30),
    email           VARCHAR(120),
    CONSTRAINT fk_prov_con FOREIGN KEY (proveedor_id) REFERENCES proveedor(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS configuracion_fiscal (
    id                   INT PRIMARY KEY,
    rtn_empresa          VARCHAR(20) NOT NULL,
    cai                  VARCHAR(50) NOT NULL,
    fecha_limite_emision DATE NOT NULL,
    rango_inicial        INT NOT NULL,
    rango_final          INT NOT NULL,
    correlativo_actual   INT NOT NULL
);

CREATE TABLE IF NOT EXISTS configuracion_cotizacion (
    id                    INT PRIMARY KEY,
    dias_habiles_default  INT NOT NULL
);

CREATE TABLE IF NOT EXISTS venta (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    cotizacion_id   INT NOT NULL,
    numero_factura  VARCHAR(30) NOT NULL UNIQUE,
    cai             VARCHAR(50) NOT NULL,
    subtotal        DECIMAL(12,2) NOT NULL,
    isv             DECIMAL(12,2) NOT NULL,
    total           DECIMAL(12,2) NOT NULL,
    retencion       DECIMAL(12,2) NOT NULL DEFAULT 0,
    fecha           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_venta_cot FOREIGN KEY (cotizacion_id) REFERENCES cotizacion(id)
);

INSERT IGNORE INTO configuracion_fiscal (id, rtn_empresa, cai, fecha_limite_emision, rango_inicial, rango_final, correlativo_actual)
VALUES (1, '08019002222260', 'A1B2-C3D4-E5F6-G7H8-I9J0-K1L2', '2026-12-31', 1, 1000, 1);

INSERT IGNORE INTO configuracion_cotizacion (id, dias_habiles_default) VALUES (1, 5);

INSERT IGNORE INTO cliente (nombre, identidad_rtn, telefono, email, direccion) VALUES
('Residencial Los Pinos', '0801-1990-01234', '2234-1100', 'pinos@correo.hn', 'Col. Los Pinos, Tegucigalpa'),
('Comercial MetroMall', '08019995432110', '2234-2200', 'metro@correo.hn', 'Blvd. Comunidad Económica'),
('Hotel Plaza Real', '08019991112220', '2234-3300', 'plaza@correo.hn', 'Blvd. Morazán');
