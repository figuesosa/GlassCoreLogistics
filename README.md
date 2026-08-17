# GlassCore Logistics

Aplicación web para una vidriería: cotizaciones por medida, inventario de herramientas, planilla, usuarios y logística Tegucigalpa–Comayagua.

Demo: [https://glasscorelogistics.onrender.com](https://glasscorelogistics.onrender.com)

## Stack

- Java 17, Spring Boot 3.3, Thymeleaf, Spring Security
- MySQL 8
- JasperReports (PDF de planilla y hoja de ruta)
- Maven / NetBeans

El admin ve planilla y usuarios. El operador no.

## Cómo correrla en local

1. Instalar JDK 17+ y MySQL.
2. Crear la base (Workbench o consola):

```sql
SOURCE sql/glasscore_db.sql;
```

Eso crea `glasscore_db` con datos de prueba (stock de vidrio 26 m², Hilux cerca de mantenimiento, etc.).

3. Revisar usuario/clave de MySQL en `src/main/resources/application.properties`. Por defecto usa `root` en `localhost:3306`.

4. Abrir el proyecto en NetBeans y **Run**. O en consola:

```
mvn spring-boot:run
```

5. Abrir [http://localhost:8080](http://localhost:8080)

Si la base está vacía (por ejemplo Aiven `defaultdb`), al arrancar se crean las tablas y se cargan los datos demo solos.

## Módulos

- **Inicio** — stock bajo, flota cerca de mantenimiento, cotizaciones recientes, clima TGU y tipo de cambio
- **Herramientas** — alta, edición, asignación y devolución
- **Cotizaciones** — ancho × alto; si el vidrio no alcanza, guarda igual y marca alerta de compra
- **Planilla** — empleados, cierre de pago y PDF Jasper (solo admin)
- **Usuarios** — crear / editar / borrar cuentas y cambiar contraseña (solo admin)
- **Logística** — vehículos, autorización de salida (85 km / 170 km redondo) y PDF de hoja de ruta

## Despliegue (Render + Aiven)

Servicio Docker. El `Dockerfile` está en la raíz. Variables de entorno:

- `SPRING_DATASOURCE_URL` — JDBC de Aiven (`defaultdb`, `sslMode=REQUIRED`)
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

No definir `PORT`: Render lo inyecta. Health check: `/login`.

## Estructura

```
src/main/java/com/glasscore/   controladores, servicios, DAO
src/main/resources/templates/  páginas Thymeleaf
src/main/resources/static/     CSS, JS, iconos PWA
sql/glasscore_db.sql           script para MySQL local
```
