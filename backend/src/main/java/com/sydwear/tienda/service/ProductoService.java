package com.sydwear.tienda.service;

import com.sydwear.tienda.model.Producto;
import com.sydwear.tienda.model.Categoria;
import com.sydwear.tienda.dto.*;
import com.sydwear.tienda.exception.conflictoException;
import com.sydwear.tienda.exception.noEncontradoException;
import com.sydwear.tienda.exception.peticionIncorrectaException;

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
}