# Librería — Práctica 3 · TDI

Aplicación web para administrar el catálogo de una librería: registrar, listar, buscar/filtrar, editar y eliminar libros.
Es la evolución de la Práctica 2 (Servlets/JSP) hacia **Spring Boot (Spring MVC)** con arquitectura en capas.

**Autor:** Dylan Enrique Juárez Martínez — Tecnologías para Desarrollos en Internet, Facultad de Ciencias, UNAM

---

## 1. Requisitos previos

| Software | Versión usada | Para qué |
|---|---|---|
| Java (JDK) | **21** (OpenJDK 21) | Compilar y ejecutar |
| MySQL Server | 8.x | Base de datos |
| Maven | no hace falta instalarlo: el proyecto trae el *wrapper* `./mvnw` | Descargar dependencias y compilar |
| IDE (opcional) | Spring Tools for Eclipse 5.4 | Ejecutar desde el IDE |
| Navegador | cualquiera moderno, con internet | Bootstrap, jQuery y DataTables se cargan desde CDN |

Comprobar versiones:

```bash
java -version      # debe decir 21
mysql --version
```

## 2. Tecnologías y versiones

| Componente | Versión |
|---|---|
| Spring Boot | 4.1.2 |
| Spring Framework | 7.0.x |
| Tomcat embebido | 11.0.x |
| MySQL Connector/J | 9.x |
| HikariCP (pool de conexiones) | 7.x |
| Bootstrap | 5.3.3 (CDN) |
| jQuery | 3.7.1 (CDN) |
| DataTables | 2.3.8 + Buttons 3.2.6 + Select 3.1.3 + ColumnControl 1.2.1 (CDN) |
| Material Design Icons | 7.4.47 (CDN) |
| Empaquetado | `war` |

## 3. Dependencias (`pom.xml`)

El `pom.xml` completo viene en el proyecto; Maven descarga todo solo. Para referencia:

**Generadas por Spring Initializr** (Java 21, Maven, packaging **War**):

| Dependencia en Initializr | Para qué |
|---|---|
| Spring Web | Spring MVC, controladores y Tomcat embebido |
| JDBC API | `JdbcTemplate` |
| MySQL Driver | Conexión a MySQL (`com.mysql:mysql-connector-j`) |
| Validation | `@NotBlank`, `@Size`, `@NotNull`, `@DecimalMin` |

**Agregadas a mano** dentro de `<dependencies>` (sin versión: la maneja Spring Boot):

```xml
<!-- Anotaciones @Entity, @Table, @Id (solo anotaciones; el acceso a datos es con JdbcTemplate) -->
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
</dependency>

<!-- Motor que compila las JSP (el Tomcat embebido no lo trae) -->
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <scope>provided</scope>
</dependency>

<!-- JSTL: <c:...>, <fn:...> (API + implementación) -->
<dependency>
    <groupId>jakarta.servlet.jsp.jstl</groupId>
    <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
</dependency>
<dependency>
    <groupId>org.glassfish.web</groupId>
    <artifactId>jakarta.servlet.jsp.jstl</artifactId>
</dependency>
```

## 4. Base de datos

El script `library.sql` (raíz del proyecto) crea el esquema `library`, la tabla `books` y carga 50 libros.
Se ejecuta **una vez**:

```bash
mysql -u root -p < library.sql
```

Verificar:

```bash
mysql -u root -p library -e "SELECT COUNT(*) FROM books;"    # debe dar 50
```

Tabla:

```sql
CREATE TABLE books (
  id     INT AUTO_INCREMENT PRIMARY KEY,
  name   VARCHAR(200)  NOT NULL,
  author VARCHAR(150)  NOT NULL,
  price  DECIMAL(10,2) NOT NULL CHECK (price >= 0)
);
```

> Volver a correr `library.sql` borra la tabla y la deja con los 50 libros originales.

## 5. Configuración

Editar `src/main/resources/application.properties` y poner el usuario y la contraseña de **tu** MySQL:

```properties
spring.application.name=library

server.port=8080

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/library?serverTimezone=America/Mexico_City&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp

spring.mvc.hiddenmethod.filter.enabled=true
```

| Propiedad | Para qué |
|---|---|
| `spring.datasource.*` | Conexión a MySQL; Spring Boot crea el `DataSource` y el `JdbcTemplate` con estos datos |
| `spring.mvc.view.prefix/suffix` | `return "books/list"` → `/WEB-INF/jsp/books/list.jsp` |
| `spring.mvc.hiddenmethod.filter.enabled` | Permite PUT y DELETE desde formularios HTML (campo oculto `_method`) |
| `server.port` | Puerto de la aplicación |

## 6. Cómo ejecutarlo

### Opción A — Terminal

Desde la raíz del proyecto (donde está `pom.xml`):

```bash
chmod +x mvnw            # solo la primera vez, en Linux/Mac
./mvnw spring-boot:run
```

En Windows: `mvnw.cmd spring-boot:run`

### Opción B — Spring Tools for Eclipse

1. *File → Import → Maven → Existing Maven Projects* → elegir la carpeta del proyecto.
2. Esperar a que Maven descargue las dependencias.
3. Clic derecho en **`LibraryApplication.java`** → **Run As → Spring Boot App**.
   (Sobre el archivo, no sobre el proyecto, y como *Spring Boot App*, no *Java Application*.)

### Opción C — Generar el `.war` y ejecutarlo

```bash
./mvnw clean package
java -jar target/library-0.0.1-SNAPSHOT.war
```

### Abrir

La aplicación está lista cuando la consola muestra `Tomcat started on port 8080`.

**http://localhost:8080/books**

## 7. Problemas comunes

| Error | Causa | Solución |
|---|---|---|
| `Access denied for user 'root'@'localhost'` | Usuario o contraseña incorrectos | Revisar `spring.datasource.username/password` |
| `Unknown database 'library'` | No se corrió el script | `mysql -u root -p < library.sql` |
| `Communications link failure` | MySQL no está encendido | `sudo systemctl start mysql` |
| `Port 8080 was already in use` / `La dirección ya se está usando` | Otra instancia (u otro Tomcat) usa el puerto | `lsof -i :8080` y `kill <PID>`, o cambiar `server.port` |
| `AssertionViolatedException ... main` al ejecutar | El IDE eligió otra clase `main` | Ejecutar `LibraryApplication` con *Run As → Spring Boot App* |
| 404 al abrir `/books` | Faltan las JSP o están fuera de `WEB-INF` | Deben estar en `src/main/webapp/WEB-INF/jsp/books/` |
| Página sin estilos | Sin internet (CDN) | Conectarse; los estilos y scripts se cargan desde jsDelivr |

## 8. Arquitectura

Monolito en capas con patrón MVC:

```
Navegador (JSP + Bootstrap + jQuery/DataTables)
        │
BookController   @Controller   rutas CRUD + endpoint JSON para DataTables
        │
BookService      @Service      reglas de negocio (insertar vs. actualizar, límites de paginación)
        │
BookRepository   @Repository   acceso a datos con JdbcTemplate (sustituye al DAO de la Práctica 2)
        │
MySQL            tabla books
```

```
library/
├── pom.xml
├── mvnw · mvnw.cmd · .mvn/
├── library.sql
├── README.md
└── src/main/
    ├── java/mx/unam/ciencias/library/
    │   ├── LibraryApplication.java     clase principal
    │   ├── ServletInitializer.java     para desplegar el .war en un Tomcat externo
    │   ├── config/        WebConfig: MessageSource, LocaleResolver, interceptor de idioma, validador
    │   ├── controller/    BookController
    │   ├── model/         Book (@Entity + validaciones), BookFilter (petición de DataTables)
    │   ├── repository/    BookRepository + BookRepositoryImpl (JdbcTemplate)
    │   └── service/       BookService + BookServiceImpl
    ├── resources/
    │   ├── application.properties
    │   ├── messages.properties        español (por defecto)
    │   ├── messages_en.properties     inglés
    │   └── static/  css/styles.css · js/app.js
    └── webapp/WEB-INF/jsp/
        ├── fragments/   header.jspf · footer.jspf
        └── books/       list.jsp · form.jsp
```

## 9. Rutas

| Método | URL | Acción |
|---|---|---|
| GET | `/books` | Catálogo |
| GET | `/books/data` | JSON para DataTables (paginación, búsqueda, orden y filtros en el servidor) |
| GET | `/books/new` | Formulario de registro |
| POST | `/books` | Registrar |
| GET | `/books/{id}/edit` | Formulario de edición |
| PUT | `/books/{id}` | Actualizar |
| DELETE | `/books/{id}` | Eliminar |

Cambiar idioma: agregar `?lang=es` o `?lang=en` a cualquier URL (o usar los botones ES / EN).

## 10. Funcionalidades

- **CRUD completo** con validación en el servidor (`@NotBlank`, `@Size`, `@NotNull`, `@DecimalMin`); los errores se
  muestran debajo de cada campo.
- **DataTables con paginación del lado del servidor**: búsqueda general en vivo, orden por columna y menú por columna
  con búsqueda por condición (contiene, empieza con, mayor que…) y lista de autores.
- **Selección de filas** con barra de acciones (Agregar, Editar, Eliminar) y **vista** automática, de tarjetas o de tabla.
- **Modal de confirmación** al eliminar y **alertas descartables** con el resultado de cada operación.
- **Internacionalización** español / inglés, incluidos los mensajes de validación.
- Diseño **responsivo** con Bootstrap 5 (grid, navbar, footer, formularios, tabla, modal).
