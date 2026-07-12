# GlassCore Logistics

Sistema de gestión para vidriería industrial GlassCore.
Desarrollado en Java con patrón DAO, interfaz Swing, MySQL y JasperReports.

## Requisitos

- JDK 17 o superior
- Apache NetBeans
- MySQL Server y MySQL Workbench
- Maven (incluido en NetBeans)

## Base de datos

1. Abrir MySQL Workbench
2. Ejecutar el script `sql/glasscore_db.sql`
3. Ajustar usuario y contraseña en `src/main/resources/db.properties`

```properties
db.url=jdbc:mysql://localhost:3306/glasscore_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Tegucigalpa&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci
db.user=root
db.password=
```

## Ejecución en NetBeans

1. File → Open Project → carpeta `GlassCoreLogistics`
2. Clic derecho en el proyecto → Run
3. Clase principal: `com.glasscore.Main`

## Reportes JasperReports

Ubicación: `src/main/resources/reportes/`

- `ComprobantePlanilla.jasper` / `.jrxml`
- `HojaRutaDespacho.jasper` / `.jrxml`

Para recompilar:

```bash
mvn compile exec:java -Dexec.mainClass=com.glasscore.util.CompilarReportes
```

## Módulos

| Módulo | Descripción |
|--------|-------------|
| Herramientas | Inventario y asignación a choferes/instaladores |
| Cotizaciones | Cálculo por medidas con alerta de compra si falta stock |
| Empleados / Planilla | CRUD de personal y cierre de planilla |
| Logística | Vehículos, ruta Tegucigalpa–Comayagua y control de mantenimiento |

## Estructura

```
com.glasscore
├── Main
├── conexion
├── modelo
├── dao / dao.impl
├── servicio
├── vista
└── util
```
