# Contrato API SyD Wear V1

URL base: http://localhost:8080

Los datos de entrada y salida utilizan JSON.

Ambos: cliente y administrador autenticados.

Todas las rutas excepto registro y login necesitaran
un token JWT. Las rutas de administracion solo estaran
disponibles para usuarios con rol ADMIN.

Actualmente la API se encuentra en desarrollo.

## Endpoints

| Metodo | Ruta | Rol | Entrada | Salida | HTTP |
|---|---|---|---|---|---|
| GET | /api/categorias | Ambos | - | List<CategoriaDto> | 200 |
| GET | /api/categorias/{id} | Ambos | - | CategoriaDto | 200, 404 |
| GET | /api/admin/categorias | ADMIN | - | List<CategoriaDto> | 200 |
| POST | /api/admin/categorias | ADMIN | CategoriaCrearDto | CategoriaDto | 201, 400, 409 |
| PUT | /api/admin/categorias/{id} | ADMIN | CategoriaCrearDto | CategoriaDto | 200, 400, 404, 409 |
| DELETE | /api/admin/categorias/{id} | ADMIN | - | - | 204, 404, 409 |
| GET | /api/productos/{id} | Ambos | - | ProductoDetalleDto | 200, 404 |
| GET | /api/productos/destacados | Ambos | - | List<ProductoResumenDto> | 200 |
| GET | /api/admin/productos | ADMIN | - | List<ProductoDetalleDto> | 200 |
| POST | /api/admin/productos | ADMIN | ProductoCrearDto | ProductoDetalleDto | 201, 400, 409 |
| PUT | /api/admin/productos/{id} | ADMIN | ProductoCrearDto | ProductoDetalleDto | 200, 400, 404, 409 |
| DELETE | /api/admin/productos/{id} | ADMIN | - | - | 204, 404 |
| PUT | /api/admin/productos/{id}/stock | ADMIN | StockDto | ProductoDetalleDto | 200, 400, 404, 409 |
| PUT | /api/admin/productos/{id}/reactivar | ADMIN | - | ProductoDetalleDto | 200, 404 |
| GET | /api/productos | Ambos | ProductoFiltroDto | List<ProductoResumenDto> | 200, 400 |
| GET | /api/categorias/{id}/productos | Ambos | - | List<ProductoResumenDto> | 200, 404 |
| GET | /api/productos/sku/{sku} | Ambos | - | ProductoDetalleDto | 200, 404 |
| GET | /api/admin/productos/stock-bajo | ADMIN | umbral | List<ProductoResumenDto> | 200, 400 |
| POST | /api/auth/registro | Publico | RegistroDto | TokenDto | 201, 400, 409 |
| POST | /api/auth/login | Publico | LoginDto | TokenDto | 200, 401 |
| GET | /api/usuarios/me | Ambos | - | UsuarioDto | 200 |
| PUT | /api/usuarios/me | Ambos | PerfilDto | UsuarioDto | 200, 400 |
| GET | /api/admin/usuarios | ADMIN | - | List<UsuarioDto> | 200 |
| PUT | /api/admin/usuarios/{id}/rol | ADMIN | RolDto | UsuarioDto | 200, 400, 404, 409 |
| GET | /api/carrito | Ambos (propio) | - | CarritoDto | 200 |
| POST | /api/carrito/lineas | Ambos (propio) | LineaCrearDto | CarritoDto | 200, 400, 404, 409 |
| PUT | /api/carrito/lineas/{productoId} | Ambos (propio) | CantidadDto | CarritoDto | 200, 400, 404, 409 |
| DELETE | /api/carrito/lineas/{productoId} | Ambos (propio) | - | CarritoDto | 200, 404 |
| DELETE | /api/carrito | Ambos (propio) | - | - | 204 |
| POST | /api/pedidos | Ambos (propio) | PedidoCrearDto opcional | PedidoDto | 201, 400, 409 |
| GET | /api/pedidos | Ambos (propio) | estado (query) | List<PedidoDto> | 200, 400 |
| GET | /api/pedidos/{id} | Ambos (propio) | - | PedidoDto | 200, 404 |
| PUT | /api/pedidos/{id}/cancelar | Ambos (propio) | - | PedidoDto | 200, 404, 409 |
| GET | /api/admin/pedidos | ADMIN | estado, desde, hasta, email (query) | List<PedidoDto> | 200, 400 |
| GET | /api/admin/pedidos/{id} | ADMIN | - | PedidoDto | 200, 404 |
| PUT | /api/admin/pedidos/{id}/estado | ADMIN | EstadoPedidoDto | PedidoDto | 200, 400, 404, 409 |

## Filtros de productos

Los parametros de busqueda seran:

- categoriaId
- q
- precioMin
- precioMax
- soloDisponibles
- orden
- direccion
- talla
- color

La busqueda no tendra en cuenta mayusculas ni tildes.

Los tipos de ordenacion seran NOMBRE, PRECIO y NOVEDAD.
La direccion podra ser ASC o DESC.

Por defecto se utilizara NOMBRE ASC.

El umbral de stock bajo sera 5 por defecto.
La consulta administrativa incluira productos retirados.

## Stock

StockDto recibe la cantidad que queremos sumar o restar
al stock actual.

Retirar un producto no supone eliminarlo.
Reactivar un producto no modifica su stock.

## Usuarios y autenticacion

- PerfilDto: nombre y direccionEnvio.
- RolDto: CLIENTE o ADMIN.
- RegistroDto: email, nombre, direccionEnvio y password.
- LoginDto: email y password.
- TokenDto: token JWT.

El administrador inicial se creara al arrancar utilizando
el email configurado en tienda.admins y una contraseña
guardada en una variable de entorno.

Las contraseñas nunca se devolveran en las respuestas.

## Carrito

Cada linea contiene productoId y cantidad.

El maximo sera de 5 unidades por producto.

El subtotal se calculara con el precio actual del producto.

Los gastos de envio seran de 4,95 euros.
A partir de 60 euros el envio sera gratuito.
Si el carrito esta vacio los gastos seran 0 euros.

## Pedidos

PedidoCrearDto permite indicar direccionEnvio.
Si no se indica se utilizara la direccion del usuario.

EstadoPedidoDto contiene el nuevo estado del pedido.

Las fechas de los filtros utilizaran el formato yyyy-MM-dd.
Se incluiran los dias inicial y final.

Cada usuario solo podra consultar su propio carrito y
sus propios pedidos, incluido el administrador cuando
utilice las rutas de cliente.

La gestion global se realizara mediante /api/admin.

## Codigos HTTP

- 200: Peticion correcta.
- 201: Recurso creado correctamente.
- 204: Operacion correcta sin contenido.
- 400: Datos incorrectos.
- 401: Usuario no autenticado o token invalido.
- 403: Usuario sin permisos.
- 404: Recurso no encontrado.
- 409: Conflicto con los datos actuales.

No se mostraran trazas de errores en las respuestas.

## Documentacion

En esta version utilizaremos este archivo Markdown
para documentar la API. Swagger se incorporara en V2.