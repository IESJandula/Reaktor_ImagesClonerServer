# ImagesClonerServer

Servidor Spring Boot (**Reaktor**) que persiste en **MySQL** el cat�logo de im�genes Clonezilla del servidor PXE: estados (`DESACTIVADA`, `PENDIENTE`, `ACTIVADA`), acci�n post-restore (`poweroff`, `reboot`) y sincronizaci�n con las carpetas detectadas en disco.

- **Artifact Maven:** `es.iesjandula.reaktor:ImagesClonerServer`
- **Puerto HTTP:** `8094` (`server.port` en `application.yaml` / `application-VPS.yaml`)
- **Tabla JPA:** `imagen_clonezilla` (entidad `ImagenClonezilla`)
- **Estados y acciones:** constantes en `Constants.java` (no hay enum `EstadoImagen` en este proyecto)

## Requisitos

| Requisito | Detalle |
| --- | --- |
| Build | Java, Maven |
| Parent POM | `es.iesjandula.reaktor:Dependencies:1.0.0` |
| Dependencias locales | `BaseServer` y `BaseClient` `1.0.0` (JWT, seguridad, auditor�a) |
| Base de datos | MySQL, esquema `reaktor_imagesclonerserver` |
| Mensajer�a | RabbitMQ (auditor�a Reaktor) |
| Autenticaci�n | JWT firmado con clave p�blica `reaktor.publicKeyFile` |

## Configuraci�n

### Desarrollo � `src/main/resources/application.yaml`

| Propiedad | Valor en repo |
| --- | --- |
| `spring.application.name` | `images-cloner` |
| `server.port` | `8094` |
| `spring.jpa.hibernate.ddl-auto` | `update` |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/reaktor_imagesclonerserver?createDatabaseIfNotExist=true` |
| `reaktor.clientId` | `images-cloner` |

Fragmento relevante:

```yaml
spring:
  application:
    name: images-cloner
  jpa:
    hibernate:
      ddl-auto: update
  datasource:
    url: jdbc:mysql://localhost:3306/reaktor_imagesclonerserver?createDatabaseIfNotExist=true
    username: root
    password: toor
server:
  port: 8094
reaktor:
  publicKeyFile: C:\claves\public_key.pem
  clientId: "images-cloner"
```

### Producci�n / VPS � `src/main/resources/application-VPS.yaml`

| Propiedad | Valor en repo |
| --- | --- |
| `spring.jpa.hibernate.ddl-auto` | `validate` |
| `logging.file.name` | `/tmp/reaktor_imagesClonerServer.log` |
| Placeholders | `${DB_PASSWORD}`, `${RABBITMQ_USERNAME}`, `${RABBITMQ_PASSWORD}`, `${PUBLIC_KEY_FILE}`, `${CLIENT_ID}` |

### Filtrado Maven � `application-VPS-filter.properties`

Archivo local (en `.gitignore`). El `pom.xml` filtra `application-VPS.yaml` con estas claves:

```properties
DB_PASSWORD=***
PUBLIC_KEY_FILE=/ruta/public_key.pem
CLIENT_ID=images-cloner
RABBITMQ_USERNAME=***
RABBITMQ_PASSWORD=***
```

Build CI / VPS:

```bash
mvn clean install -Dspring.config.location=src/main/resources/application-VPS.yaml
```

## Seguridad (JWT)

Todas las rutas REST documentadas exigen `Authorization: Bearer <token>`.

Los roles se comprueban con `@PreAuthorize` y las constantes de `es.iesjandula.reaktor.base.utils.BaseConstants` (dependencia `BaseServer`):

| �mbito | Expresi�n en c�digo | Constantes referenciadas |
| --- | --- | --- |
| Admin | `hasAnyRole(...)` | `ROLE_ADMINISTRADOR`, `ROLE_DIRECCION` |
| Cliente PXE | `hasRole(...)` | `ROLE_CLIENTE_CLONADOR_IMAGENES` |

El claim de rol en el JWT debe coincidir con lo que espera `BaseServer` para esas constantes. `reaktor.clientId` identifica la aplicaci�n ante el emisor (`images-cloner` en desarrollo; `${CLIENT_ID}` en VPS).

## Modelo de datos

Entidad `ImagenClonezilla` (`imagen_clonezilla`):

| Campo | Tipo | Notas |
| --- | --- | --- |
| `nombreImagen` | `String` (PK) | Nombre de carpeta de imagen |
| `estado` | `String` | `DESACTIVADA`, `PENDIENTE`, `ACTIVADA` (`Constants`) |
| `accion` | `String` | `poweroff` o `reboot` (`Constants`) |

DTO de listado admin `ImagenClonezillaDto`: la consulta `buscarTodasLasImagenes()` proyecta solo **`nombreImagen`** y **`estado`** (el campo `accion` del DTO no se rellena en el listado).

## Reglas de negocio (comportamiento real del c�digo)

1. **POST admin (imagen por defecto):** primero `desactivarTodasLasImagenes()` ? **todas** pasan a `DESACTIVADA`; luego la imagen indicada (debe existir en BD) pasa a `PENDIENTE` con la `accion` normalizada.
2. **DELETE admin:** todas las filas a `DESACTIVADA` (`habilitarMenuCompletoTodasLasImagenes`).
3. **Acci�n (`accion` cabecera en POST admin):** si falta o est� en blanco ? `poweroff`; si viene informada ? `trim` + min�sculas; solo acepta `poweroff` o `reboot` (c�digo **9403** si no).
4. **PUT cliente:** `ponerImagenActivada(nombreImagen)` ? esa fila queda `ACTIVADA` (sin comprobar que estuviera `PENDIENTE`; no desactiva otras filas).
5. **POST cliente (sync):** `DELETE` de filas cuyo `nombreImagen` **no** est� en el array enviado; por cada nombre del array, si no existe en BD se construye un `ImagenClonezilla` en memoria (`DESACTIVADA`, `poweroff`) y se devuelve en la respuesta � **el controlador no llama a `save()`** sobre esas altas.
6. **Listas vac�as / nulas en POST cliente:** cuerpo `null` ? error **9401**; elementos `null` o en blanco ? error **9402**.

No hay restricci�n en base de datos que garantice una sola fila `ACTIVADA`; el flujo operativo previsto es: admin deja una `PENDIENTE` ? cliente confirma con PUT ? `ACTIVADA`.

## API REST

Base URL local: `http://localhost:8094`

### Admin � `AdminRestController` ? `/images_cloner/admin`

| M�todo | Ruta completa | Roles | Entrada | Respuesta exitosa |
| --- | --- | --- | --- | --- |
| `GET` | `/images_cloner/admin/` | `ROLE_ADMINISTRADOR` o `ROLE_DIRECCION` | � | `200` + `List<ImagenClonezillaDto>` (`nombreImagen`, `estado`) |
| `POST` | `/images_cloner/admin/` | idem | Cabeceras `nombreImagen` (obligatoria), `accion` (opcional) | `200` + lista actualizada; `400` cuerpo `{codigo,mensaje}` |
| `DELETE` | `/images_cloner/admin/` | idem | � | `200` + lista actualizada |

C�digos de error propios (`Constants`): **9402** nombre vac�o, **9403** acci�n inv�lida, **9404** imagen no encontrada en POST.

### Cliente � `ClientRestController` ? `/images_cloner/client`

| M�todo | Ruta completa | Rol | Entrada | Respuesta exitosa |
| --- | --- | --- | --- | --- |
| `POST` | `/images_cloner/client/` | `ROLE_CLIENTE_CLONADOR_IMAGENES` | `Content-Type: application/json` � cuerpo `List<String>` | `200` + `List<ImagenClonezilla>` |
| `PUT` | `/images_cloner/client/` | idem | Cabecera `nombreImagen` (obligatoria) | `200` sin cuerpo |

Errores cliente: **9401** lista nula, **9402** nombre vac�o en lista o en PUT.

### Ejemplos de cuerpo

Listado admin (sin `accion` en JSON):

```json
[
  { "nombreImagen": "demo-win10", "estado": "PENDIENTE" }
]
```

Sync cliente:

```json
["demo-win10", "ubuntu-lab"]
```

Respuesta sync (entidad completa; altas nuevas solo en respuesta si a�n no estaban en BD):

```json
[
  {
    "nombreImagen": "nueva-carpeta",
    "estado": "DESACTIVADA",
    "accion": "poweroff"
  }
]
```

Error de negocio:

```json
{
  "codigo": 9404,
  "mensaje": "La imagen no existe: win-inexistente"
}
```

## Ejemplos `curl`

```bash
# GET � listar (admin)
curl -s -H "Authorization: Bearer TOKEN" \
  http://localhost:8094/images_cloner/admin/

# POST � establecer imagen por defecto (admin)
curl -s -X POST \
  -H "Authorization: Bearer TOKEN" \
  -H "nombreImagen: demo-win10" \
  -H "accion: reboot" \
  http://localhost:8094/images_cloner/admin/

# DELETE � desactivar todas (admin)
curl -s -X DELETE -H "Authorization: Bearer TOKEN" \
  http://localhost:8094/images_cloner/admin/

# POST � sincronizar carpetas en disco (cliente)
curl -s -X POST \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '["demo-win10","ubuntu-lab"]' \
  http://localhost:8094/images_cloner/client/

# PUT � marcar ACTIVADA tras restore (cliente)
curl -s -X PUT \
  -H "Authorization: Bearer TOKEN" \
  -H "nombreImagen: demo-win10" \
  http://localhost:8094/images_cloner/client/
```

## Arranque

**Local** (MySQL accesible, perfil por defecto `application.yaml`):

```bash
mvn spring-boot:run
```

**JAR** (classifier del `pom.xml`):

```bash
mvn clean package
java -jar target/ImagesClonerServer-*-jar-with-dependencies.jar
```

Clase principal: `es.iesjandula.reaktor.images_cloner_server.ReaktorImagesClonerServerApplication`.

**VPS:** mismo JAR tras `mvn clean install` con `application-VPS.yaml` y `application-VPS-filter.properties` generado o copiado manualmente.

## CI

Workflow [`.github/workflows/maven.yml`](.github/workflows/maven.yml):

| Paso | Acci�n |
| --- | --- |
| Trigger | `push` a rama `main` |
| Runner | `self-hosted`, matriz `linux` |
| Secretos | `DB_PASSWORD`, `PUBLIC_KEY_FILE`, `CLIENT_ID`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD` |
| Build | Escribe `application-VPS-filter.properties` y ejecuta `mvn clean install -Dspring.config.location=src/main/resources/application-VPS.yaml` |
| Extra | `advanced-security/maven-dependency-submission-action` |
