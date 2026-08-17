# GlassCore Logistics

Plataforma web de la vidriería GlassCore: cotización a medida, facturación SAR, clientes/proveedores, planilla (14vo y aguinaldo), inventario, herramientas, logística A→B y reportería Jasper. PWA instalable.

Demo: [https://glasscorelogistics.onrender.com](https://glasscorelogistics.onrender.com)

## Stack

- Java 17, Spring Boot 3.3, Thymeleaf, Spring Security
- MySQL 8, patrón DAO (JDBC, sin JPA)
- JasperReports (planilla, hoja de ruta, ventas contables)
- Maven / NetBeans
- PWA (`manifest.json` + `sw.js`): interfaz en caché y consulta offline de hojas de ruta

## Roles (RBAC)

| Rol | Acceso |
| --- | --- |
| ADMIN | Todos los módulos, fiscal SAR y usuarios |
| CONTADOR | Reportes, histórico de planilla, facturación emitida |
| CAJERO | Clientes, cotizaciones y conversión a venta |
| OPERADOR | Inventario, herramientas y logística de campo |

## Cómo correrla en local

1. Instalar JDK 17+ y MySQL.
2. Crear la base (Workbench o consola):

```sql
SOURCE sql/glasscore_db.sql;
```

3. Revisar usuario/clave de MySQL en `src/main/resources/application.properties`.
4. En NetBeans: **Run**. O en consola: `mvn spring-boot:run`
5. Abrir [http://localhost:8080](http://localhost:8080)

Si la base está vacía, al arrancar se crean tablas, parámetros fiscales demo y los 12 módulos.

## Módulos

1. **Acceso y roles** — autenticación y perfiles Admin / Contador / Cajero
2. **Fiscal SAR** — RTN, CAI, fecha límite, rango y correlativo automático
3. **Cotizaciones** — ancho × alto, vigencia 2–15 días hábiles, alerta de compra sin bloquear
4. **Facturación** — orden vigente → factura, descargo de inventario, estado `CONVERTIDA_A_VENTA`
5. **Clientes** — contacto completo, unicidad por identidad / RTN
6. **Proveedores** — varios teléfonos y agentes comerciales
7. **Empleados** — identidad única, ingreso y vacaciones por antigüedad
8. **Planilla** — neto + checkbox 14vo/aguinaldo proporcional + PDF
9. **Inventario** — vidrio (m²), aluminio y metal (m lineales)
10. **Herramientas** — DISPONIBLE → ASIGNADA al entregarse
11. **Logística** — ruta A→B ida/vuelta, combustible Lps, bloqueo por mantenimiento
12. **Reportes** — ventas mensual/trimestral/anual (ejecutivo y contable), planilla y hoja de ruta

## Despliegue (Render + Aiven)

Servicio Docker. Variables: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`. Health check: `/login`.
