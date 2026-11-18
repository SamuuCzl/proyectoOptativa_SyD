package com.sydwear.tienda.service;

import com.sydwear.tienda.model.Categoria;
import com.sydwear.tienda.model.Producto;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class CatalogoDatosService {

    private final List<Categoria> categorias = new ArrayList<>();
    private final List<Producto> productos = new ArrayList<>();

    private long siguienteCategoria = 4;
    private long siguienteProducto = 13;

    public CatalogoDatosService() {

        // Creamos las tres categorias de nuestra tienda
        categorias.add(new Categoria(1L, "Camisetas", "Ropa de manga corta"));
        categorias.add(new Categoria(2L, "Sudaderas", "Ropa de abrigo casual"));
        categorias.add(new Categoria(3L, "Pantalones", "Vaqueros y cargos"));

        // Añadimos las camisetas
        agregar(1L, "CAM-01-M", "Camiseta básica blanca",
                "Camiseta de manga corta", "19.99", 12,
                "https://upload.wikimedia.org/wikipedia/commons/a/a7/White-tshirt.jpg",
                true, true, 1, "M", "Blanco");

        agregar(2L, "CAM-01-L", "Camiseta básica blanca",
                "Camiseta de manga corta", "19.99", 12,
                "https://upload.wikimedia.org/wikipedia/commons/a/a7/White-tshirt.jpg",
                true, false, 1, "L", "Blanco");

        agregar(3L, "CAM-02-M", "Camiseta básica negra",
                "Camiseta casual de algodón", "19.99", 12,
                "https://upload.wikimedia.org/wikipedia/commons/8/81/Camiseta-negra.jpg",
                true, false, 1, "M", "Negro");

        agregar(4L, "CAM-02-L", "Camiseta básica negra",
                "Camiseta casual de algodón", "19.99", 0,
                "https://upload.wikimedia.org/wikipedia/commons/8/81/Camiseta-negra.jpg",
                true, false, 1, "L", "Negro");

        // Añadimos las sudaderas
        agregar(5L, "SUD-01-M", "Sudadera gris",
                "Sudadera de capucha urbana", "34.95", 12,
                "https://upload.wikimedia.org/wikipedia/commons/9/91/Faceless_%28Unsplash%29.jpg",
                true, true, 2, "M", "Gris");

        agregar(6L, "SUD-01-L", "Sudadera gris",
                "Sudadera de capucha urbana", "34.95", 12,
                "https://upload.wikimedia.org/wikipedia/commons/9/91/Faceless_%28Unsplash%29.jpg",
                true, false, 2, "L", "Gris");

        agregar(7L, "SUD-02-M", "Sudadera negra",
                "Sudadera streetwear con capucha", "39.95", 12,
                "https://upload.wikimedia.org/wikipedia/commons/c/c2/Hoodie_m7agar.jpg",
                true, false, 2, "M", "Negro");

        agregar(8L, "SUD-02-L", "Sudadera negra",
                "Sudadera streetwear con capucha", "39.95", 12,
                "https://upload.wikimedia.org/wikipedia/commons/c/c2/Hoodie_m7agar.jpg",
                true, false, 2, "L", "Negro");

        // Añadimos los pantalones
        agregar(9L, "PAN-01-M", "Vaquero casual",
                "Pantalón vaquero de diario", "44.90", 12,
                "https://upload.wikimedia.org/wikipedia/commons/5/57/Pantalones_jeans_%28versi%C3%B3n_2%29.jpg",
                true, true, 3, "M", "Azul");

        agregar(10L, "PAN-01-L", "Vaquero casual",
                "Pantalón vaquero de diario", "44.90", 3,
                "https://upload.wikimedia.org/wikipedia/commons/5/57/Pantalones_jeans_%28versi%C3%B3n_2%29.jpg",
                true, false, 3, "L", "Azul");

        agregar(11L, "PAN-02-M", "Pantalón cargo",
                "Pantalón de bolsillos laterales", "39.90", 12,
                "https://upload.wikimedia.org/wikipedia/commons/c/cd/Cargo_pants_001.jpg",
                true, false, 3, "M", "Verde");

        agregar(12L, "PAN-02-L", "Pantalón cargo",
                "Pantalón de bolsillos laterales", "39.90", 12,
                "https://upload.wikimedia.org/wikipedia/commons/c/cd/Cargo_pants_001.jpg",
                false, false, 3, "L", "Verde");
    }

    // Creamos el producto y lo añadimos a la lista
    private void agregar(Long id, String sku, String nombre,
                         String descripcion, String precio,
                         int stock, String imagen, boolean activo,
                         boolean destacado, int categoria,
                         String talla, String color) {

        productos.add(new Producto(
                id, sku, nombre, descripcion,
                new BigDecimal(precio), stock, imagen,
                activo, destacado,
                LocalDateTime.now().minusDays(13 - id),
                categorias.get(categoria - 1),
                talla, color
        ));
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public long nuevaCategoria() {
        return siguienteCategoria++;
    }

    public long nuevoProducto() {
        return siguienteProducto++;
    }
}
