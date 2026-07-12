-- Corrección de acentos (datos dañados por importación sin UTF-8)
USE glasscore_db;

UPDATE empleado SET nombre='Carlos', apellido='Mejía' WHERE id=1;
UPDATE empleado SET nombre='Luis', apellido='Pineda' WHERE id=2;
UPDATE empleado SET nombre='María', apellido='López' WHERE id=3;
UPDATE empleado SET nombre='José', apellido='Hernández' WHERE id=4;
UPDATE empleado SET nombre='Ana', apellido='García' WHERE id=5;
UPDATE empleado SET nombre='Pedro', apellido='Ramírez' WHERE id=6;

UPDATE herramienta SET nombre='Nivel láser Bosch' WHERE codigo='HER-006';
UPDATE material SET nombre='Marco metálico galvanizado' WHERE tipo='METAL' LIMIT 1;

SELECT id, nombre, apellido FROM empleado;
