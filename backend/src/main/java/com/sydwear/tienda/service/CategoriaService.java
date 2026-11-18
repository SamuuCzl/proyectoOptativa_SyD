package com.sydwear.tienda.service;

import com.sydwear.tienda.model.Categoria;
import com.sydwear.tienda.model.Producto;
import com.sydwear.tienda.dto.CategoriaDto;
import com.sydwear.tienda.dto.CategoriaCrearDto;
import com.sydwear.tienda.exception.conflictoException;
import com.sydwear.tienda.exception.noEncontradoException;
import com.sydwear.tienda.exception.peticionIncorrectaException;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoriaService {

    private final CatalogoDatosService datos;

    public CategoriaService(CatalogoDatosService datos) {
        this.datos = datos;
    }

    // Pasamos los datos de la categoria al DTO
    private CategoriaDto dto(Categoria categoria) {

        return new CategoriaDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }

    // Buscamos una categoria por su id
    private Categoria buscar(Long id) {

        for (Categoria categoria : datos.getCategorias()) {

            if (categoria.getId().equals(id)) {
                return categoria;
            }
        }

        throw new noEncontradoException("Categoria no encontrada");
    }

    // Comprobamos que el nombre sea correcto y no este repetido
    private void validar(CategoriaCrearDto entrada, Long id) {

        if (entrada.getNombre() == null || entrada.getNombre().isBlank()) {
            throw new peticionIncorrectaException("Introduce un nombre");
        }

        for (Categoria categoria : datos.getCategorias()) {

            if (categoria.getNombre().equalsIgnoreCase(entrada.getNombre().trim())
                    && !categoria.getId().equals(id)) {

                throw new conflictoException("Ya existe esa categoria");
            }
        }
    }

    // Mostramos todas las categorias de nuestra tienda
    public List<CategoriaDto> listar() {

        synchronized (datos) {

            List<CategoriaDto> lista = new ArrayList<>();

            for (Categoria categoria : datos.getCategorias()) {
                lista.add(dto(categoria));
            }

            return lista;
        }
    }

    // Mostramos la categoria que hemos buscado
    public CategoriaDto detalle(Long id) {

        synchronized (datos) {
            return dto(buscar(id));
        }
    }

    // Creamos una categoria nueva y la añadimos a la lista
    public CategoriaDto crear(CategoriaCrearDto entrada) {

        synchronized (datos) {

            validar(entrada, null);

            Categoria categoria = new Categoria(
                    datos.nuevaCategoria(),
                    entrada.getNombre().trim(),
                    entrada.getDescripcion()
            );

            datos.getCategorias().add(categoria);

            return dto(categoria);
        }
    }

    // Buscamos la categoria y cambiamos sus datos
    public CategoriaDto modificar(Long id, CategoriaCrearDto entrada) {

        synchronized (datos) {

            Categoria categoria = buscar(id);

            validar(entrada, id);

            categoria.setNombre(entrada.getNombre().trim());
            categoria.setDescripcion(entrada.getDescripcion());

            return dto(categoria);
        }
    }

    // Eliminamos la categoria si no tiene productos
    public void eliminar(Long id) {

        synchronized (datos) {

            Categoria categoria = buscar(id);

            for (Producto producto : datos.getProductos()) {

                if (producto.getCategoria().getId().equals(id)) {

                    throw new conflictoException("La categoria tiene productos");
                }
            }

            datos.getCategorias().remove(categoria);
        }
    }
}