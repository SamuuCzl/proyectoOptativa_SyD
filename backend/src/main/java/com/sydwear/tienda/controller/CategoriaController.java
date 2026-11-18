package com.sydwear.tienda.controller;

import com.sydwear.tienda.dto.CategoriaDto;
import com.sydwear.tienda.dto.CategoriaCrearDto;
import com.sydwear.tienda.service.CategoriaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoriaController {

    private final CategoriaService servicio;

    public CategoriaController(CategoriaService servicio) {
        this.servicio = servicio;
    }

    // Mostramos todas las categorias
    @GetMapping({"/api/categorias", "/api/admin/categorias"})
    public ResponseEntity<List<CategoriaDto>> listar() {

        return ResponseEntity.ok(servicio.listar());
    }

    // Buscamos una categoria por su id
    @GetMapping("/api/categorias/{id}")
    public ResponseEntity<CategoriaDto> detalle(@PathVariable Long id) {

        return ResponseEntity.ok(servicio.detalle(id));
    }

    // Creamos una categoria nueva
    @PostMapping("/api/admin/categorias")
    public ResponseEntity<CategoriaDto> crear(@RequestBody CategoriaCrearDto dto) {

        return ResponseEntity.status(201).body(servicio.crear(dto));
    }

    // Modificamos los datos de una categoria
    @PutMapping("/api/admin/categorias/{id}")
    public ResponseEntity<CategoriaDto> modificar(@PathVariable Long id,
                                                  @RequestBody CategoriaCrearDto dto) {

        return ResponseEntity.ok(servicio.modificar(id, dto));
    }

    // Eliminamos la categoria seleccionada
    @DeleteMapping("/api/admin/categorias/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        servicio.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}