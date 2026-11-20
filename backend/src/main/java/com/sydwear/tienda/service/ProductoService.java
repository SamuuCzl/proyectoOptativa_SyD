package com.sydwear.tienda.service;

import com.sydwear.tienda.model.Producto;
import com.sydwear.tienda.model.Categoria;
import com.sydwear.tienda.dto.*;
import com.sydwear.tienda.exception.conflictoException;
import com.sydwear.tienda.exception.noEncontradoException;
import com.sydwear.tienda.exception.peticionIncorrectaException;

import java.text.Normalizer;
import java.util.Locale;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ProductoService {

    private final CatalogoDatosService datos;

    public ProductoService(CatalogoDatosService datos) {
        this.datos = datos;
    }

    // Pasamos los datos del producto al DTO de resumen
    private ProductoResumenDto resumen(Producto producto) {

        return new ProductoResumenDto(
                producto.getId(),
                producto.getSku(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagenUrl(),
                producto.getCategoria().getId(),
                producto.getTalla(),
                producto.getColor(),
                producto.getActivo(),
                producto.getDestacado()
        );
    }

    // Pasamos todos los datos del producto al DTO de detalle
    private ProductoDetalleDto detalleDto(Producto producto) {

        Categoria categoria = producto.getCategoria();

        CategoriaDto categoriaDto = new CategoriaDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );

        return new ProductoDetalleDto(
                producto.getId(),
                producto.getSku(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagenUrl(),
                producto.getActivo(),
                producto.getDestacado(),
                producto.getFechaAlta(),
                categoriaDto,
                producto.getTalla(),
                producto.getColor()
        );
    }

    // Buscamos el producto por su id
    private Producto buscar(Long id) {

        for (Producto producto : datos.getProductos()) {

            if (producto.getId().equals(id)) {
                return producto;
            }
        }

        throw new noEncontradoException("Producto no encontrado");
    }

    // Buscamos la categoria a la que pertenece el producto
    private Categoria buscarCategoria(Long id) {

        for (Categoria categoria : datos.getCategorias()) {

            if (categoria.getId().equals(id)) {
                return categoria;
            }
        }

        throw new peticionIncorrectaException("La categoria no existe");
    }

    // Comprobamos que los datos introducidos sean correctos
    private void validar(ProductoCrearDto entrada, Long id) {

        if (entrada.getSku() == null || entrada.getSku().isBlank()) {
            throw new peticionIncorrectaException("Introduce un SKU");
        }

        if (entrada.getNombre() == null || entrada.getNombre().isBlank()) {
            throw new peticionIncorrectaException("Introduce un nombre");
        }

        if (entrada.getPrecio() == null ||
                entrada.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {

            throw new peticionIncorrectaException("El precio debe ser mayor que 0");
        }

        if (entrada.getStock() == null || entrada.getStock() < 0) {
            throw new peticionIncorrectaException("El stock no puede ser negativo ni estar vacio");
        }

        if (entrada.getCategoriaId() == null) {
            throw new peticionIncorrectaException("Introduce una categoria");
        }

        if (entrada.getTalla() == null || entrada.getTalla().isBlank()) {
            throw new peticionIncorrectaException("Introduce una talla");
        }

        if (entrada.getColor() == null || entrada.getColor().isBlank()) {
            throw new peticionIncorrectaException("Introduce un color");
        }

        // Comprobamos que no exista otro producto con el mismo SKU
        for (Producto producto : datos.getProductos()) {

            if (producto.getSku().equalsIgnoreCase(entrada.getSku().trim())
                    && !producto.getId().equals(id)) {

                throw new conflictoException("Ya existe un producto con ese SKU");
            }
        }
    }

    // Mostramos un producto siempre que este activo
    public ProductoDetalleDto detalle(Long id) {

        synchronized (datos) {

            Producto producto = buscar(id);

            if (!producto.getActivo()) {
                throw new noEncontradoException("Producto no encontrado");
            }

            return detalleDto(producto);
        }
    }

    // Mostramos los productos destacados de nuestra tienda
    public List<ProductoResumenDto> destacados() {

        synchronized (datos) {

            List<ProductoResumenDto> lista = new ArrayList<>();

            for (Producto producto : datos.getProductos()) {

                if (producto.getActivo() && producto.getDestacado()) {
                    lista.add(resumen(producto));
                }
            }

            return lista;
        }
    }

    // El administrador puede ver todos los productos
    public List<ProductoDetalleDto> listarAdmin() {

        synchronized (datos) {

            List<ProductoDetalleDto> lista = new ArrayList<>();

            for (Producto producto : datos.getProductos()) {
                lista.add(detalleDto(producto));
            }

            return lista;
        }
    }

    // Creamos un producto nuevo y lo añadimos a la lista
    public ProductoDetalleDto crear(ProductoCrearDto entrada) {

        synchronized (datos) {

            validar(entrada, null);

            Categoria categoria = buscarCategoria(entrada.getCategoriaId());

            Producto producto = new Producto(
                    datos.nuevoProducto(),
                    entrada.getSku().trim(),
                    entrada.getNombre().trim(),
                    entrada.getDescripcion(),
                    entrada.getPrecio(),
                    entrada.getStock(),
                    entrada.getImagenUrl(),
                    true,
                    entrada.getDestacado(),
                    LocalDateTime.now(),
                    categoria,
                    entrada.getTalla().trim(),
                    entrada.getColor().trim()
            );

            datos.getProductos().add(producto);

            return detalleDto(producto);
        }
    }

    // Buscamos el producto y modificamos sus datos
    public ProductoDetalleDto modificar(Long id, ProductoCrearDto entrada) {

        synchronized (datos) {

            Producto producto = buscar(id);

            validar(entrada, id);

            Categoria categoria = buscarCategoria(entrada.getCategoriaId());

            producto.setSku(entrada.getSku().trim());
            producto.setNombre(entrada.getNombre().trim());
            producto.setDescripcion(entrada.getDescripcion());
            producto.setPrecio(entrada.getPrecio());
            producto.setStock(entrada.getStock());
            producto.setImagenUrl(entrada.getImagenUrl());
            producto.setDestacado(entrada.getDestacado());
            producto.setCategoria(categoria);
            producto.setTalla(entrada.getTalla().trim());
            producto.setColor(entrada.getColor().trim());

            return detalleDto(producto);
        }
    }

    // Retiramos el producto sin eliminarlo de la lista
    public void eliminar(Long id) {

        synchronized (datos) {

            Producto producto = buscar(id);

            producto.setActivo(false);
        }
    }

    // Volvemos a activar un producto retirado
    public ProductoDetalleDto reactivar(Long id) {

        synchronized (datos) {

            Producto producto = buscar(id);

            producto.setActivo(true);

            return detalleDto(producto);
        }
    }

    // Aumentamos o disminuimos el stock del producto
    public ProductoDetalleDto modificarStock(Long id, StockDto entrada) {

        synchronized (datos) {

            Producto producto = buscar(id);

            if (entrada.getCantidad() == null) {
                throw new peticionIncorrectaException("Introduce una cantidad");
            }

            long nuevoStock = (long) producto.getStock() + entrada.getCantidad();

            if (nuevoStock < 0) {
                throw new conflictoException("No hay suficiente stock");
            }

            if (nuevoStock > Integer.MAX_VALUE) {
                throw new conflictoException("Limite de stock superado");
            }

            producto.setStock((int) nuevoStock);

            return detalleDto(producto);
        }
    }

    // Quitamos las tildes y pasamos el texto a minusculas
    private String normalizar(String texto) {

        if (texto == null) {
            return "";
        }

        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("\\p{M}", "");

        return texto.toLowerCase(Locale.ROOT);
    }

    // Comprobamos que los filtros introducidos sean correctos
    private void validarFiltros(ProductoFiltroDto filtro) {

        if (filtro.getPrecioMin() != null &&
                filtro.getPrecioMin().compareTo(BigDecimal.ZERO) < 0) {

            throw new peticionIncorrectaException("Precio minimo incorrecto");
        }

        if (filtro.getPrecioMax() != null &&
                filtro.getPrecioMax().compareTo(BigDecimal.ZERO) < 0) {

            throw new peticionIncorrectaException("Precio maximo incorrecto");
        }

        if (filtro.getPrecioMin() != null && filtro.getPrecioMax() != null) {

            if (filtro.getPrecioMin().compareTo(filtro.getPrecioMax()) > 0) {

                throw new peticionIncorrectaException("El precio minimo supera al maximo");
            }
        }

        if (!"NOMBRE".equals(filtro.getOrden()) &&
                !"PRECIO".equals(filtro.getOrden()) &&
                !"NOVEDAD".equals(filtro.getOrden())) {

            throw new peticionIncorrectaException("Orden incorrecto");
        }

        if (!"ASC".equals(filtro.getDireccion()) &&
                !"DESC".equals(filtro.getDireccion())) {

            throw new peticionIncorrectaException("Direccion incorrecta");
        }
    }

    // Buscamos los productos que cumplen los filtros
    public List<ProductoResumenDto> listar(ProductoFiltroDto filtro) {

        synchronized (datos) {

            validarFiltros(filtro);

            List<Producto> productosEncontrados = new ArrayList<>();

            for (Producto producto : datos.getProductos()) {

                // No mostramos los productos retirados
                if (!producto.getActivo()) {
                    continue;
                }

                // Buscamos por categoria
                if (filtro.getCategoriaId() != null &&
                        !producto.getCategoria().getId().equals(filtro.getCategoriaId())) {
                    continue;
                }

                // Buscamos por nombre
                if (filtro.getQ() != null &&
                        !normalizar(producto.getNombre()).contains(normalizar(filtro.getQ()))) {
                    continue;
                }

                // Comprobamos el precio minimo
                if (filtro.getPrecioMin() != null &&
                        producto.getPrecio().compareTo(filtro.getPrecioMin()) < 0) {
                    continue;
                }

                // Comprobamos el precio maximo
                if (filtro.getPrecioMax() != null &&
                        producto.getPrecio().compareTo(filtro.getPrecioMax()) > 0) {
                    continue;
                }

                // Comprobamos si tiene stock
                if (Boolean.TRUE.equals(filtro.getSoloDisponibles()) &&
                        producto.getStock() == 0) {
                    continue;
                }

                // Buscamos por talla
                if (filtro.getTalla() != null &&
                        !normalizar(producto.getTalla()).equals(normalizar(filtro.getTalla()))) {
                    continue;
                }

                // Buscamos por color
                if (filtro.getColor() != null &&
                        !normalizar(producto.getColor()).equals(normalizar(filtro.getColor()))) {
                    continue;
                }

                productosEncontrados.add(producto);
            }

            // Ordenamos los productos encontrados
            ordenar(productosEncontrados, filtro.getOrden(), filtro.getDireccion());

            List<ProductoResumenDto> lista = new ArrayList<>();

            for (Producto producto : productosEncontrados) {
                lista.add(resumen(producto));
            }

            return lista;
        }
    }

    // Ordenamos los productos segun lo que haya elegido el usuario
    private void ordenar(List<Producto> productos, String orden, String direccion) {

        for (int i = 0; i < productos.size(); i++) {

            for (int j = i + 1; j < productos.size(); j++) {

                Producto primero = productos.get(i);
                Producto segundo = productos.get(j);

                int resultado = 0;

                switch (orden) {

                    case "NOMBRE":
                        resultado = normalizar(primero.getNombre())
                                .compareTo(normalizar(segundo.getNombre()));
                        break;

                    case "PRECIO":
                        resultado = primero.getPrecio()
                                .compareTo(segundo.getPrecio());
                        break;

                    case "NOVEDAD":
                        resultado = primero.getFechaAlta()
                                .compareTo(segundo.getFechaAlta());
                        break;
                }

                // Cambiamos el orden de los productos si es necesario
                if ((direccion.equals("ASC") && resultado > 0) ||
                        (direccion.equals("DESC") && resultado < 0)) {

                    productos.set(i, segundo);
                    productos.set(j, primero);
                }
            }
        }
    }

    // Mostramos los productos de una categoria
    public List<ProductoResumenDto> productosCategoria(Long id) {

        synchronized (datos) {

            boolean encontrada = false;

            for (Categoria categoria : datos.getCategorias()) {

                if (categoria.getId().equals(id)) {
                    encontrada = true;
                    break;
                }
            }

            if (!encontrada) {
                throw new noEncontradoException("Categoria no encontrada");
            }

            ProductoFiltroDto filtro = new ProductoFiltroDto();

            filtro.setCategoriaId(id);

            return listar(filtro);
        }
    }

    // Buscamos un producto por su SKU
    public ProductoDetalleDto buscarSku(String sku) {

        synchronized (datos) {

            for (Producto producto : datos.getProductos()) {

                if (producto.getSku().equalsIgnoreCase(sku) &&
                        producto.getActivo()) {

                    return detalleDto(producto);
                }
            }

            throw new noEncontradoException("Producto no encontrado");
        }
    }

    // Mostramos los productos que tienen poco stock
    public List<ProductoResumenDto> stockBajo(int umbral) {

        synchronized (datos) {

            if (umbral < 0) {
                throw new peticionIncorrectaException("Umbral incorrecto");
            }

            List<ProductoResumenDto> lista = new ArrayList<>();

            for (Producto producto : datos.getProductos()) {

                if (producto.getStock() < umbral) {
                    lista.add(resumen(producto));
                }
            }

            return lista;
        }
    }

}