# Modelo SyD Wear

Las clases principales de nuestra tienda son Categoria,
Producto, Usuario, Carrito, LineaCarrito, Pedido y
LineaPedido. Tambien tendremos los enumerados Rol y
EstadoPedido.

Cada SKU identifica una prenda con una talla y un color.
Cada producto pertenece a una categoria.

## Relaciones

- Una categoria puede tener varios productos.
- Un usuario tiene un carrito.
- Un carrito puede tener varias lineas de carrito.
- Un usuario puede realizar varios pedidos.
- Un pedido puede tener varias lineas de pedido.
- Cada linea hace referencia a un producto.

Los pedidos guardaran el nombre y el precio del producto
en el momento de realizar la compra.

## Tipos de datos

- Las fechas se guardaran con LocalDateTime.
- Los importes se guardaran con BigDecimal.
- Las contraseñas se almacenaran mediante BCrypt.
- Los datos se guardaran en memoria.

## Organizacion

CatalogoDatosService almacenara las categorias y los
productos que utilizaran los distintos servicios.

Los servicios de carrito y pedidos compartiran el monitor
del catalogo para controlar las modificaciones del stock.

En esta primera version no utilizaremos una base de datos.
Los datos se reiniciaran al cerrar la aplicacion.