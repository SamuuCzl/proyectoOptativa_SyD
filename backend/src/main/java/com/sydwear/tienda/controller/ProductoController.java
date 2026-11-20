package com.sydwear.tienda.controller;

import com.sydwear.tienda.dto.*;
import com.sydwear.tienda.service.ProductoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductoController {

    private final ProductoService servicio;

    public ProductoController(ProductoService servicio) {
        this.servicio = servicio;
    }

    // Mostramos los productos aplicando los filtros
    @GetMapping("/api/productos")
    public ResponseEntity<List<ProductoResumenDto>> listar(
            @ModelAttribute ProductoFiltroDto filtro) {

        return ResponseEntity.ok(servicio.listar(filtro));
    }

    // Buscamos un producto por su id
    @GetMapping("/api/productos/{id}")
    public ResponseEntity<ProductoDetalleDto> detalle(
            @PathVariable Long id) {

        return ResponseEntity.ok(servicio.detalle(id));
    }

    // Mostramos los productos destacados
    @GetMapping("/api/productos/destacados")
    public ResponseEntity<List<ProductoResumenDto>> destacados() {

        return ResponseEntity.ok(servicio.destacados());
    }

    // Mostramos todos los productos al administrador
    @GetMapping("/api/admin/productos")
    public ResponseEntity<List<ProductoDetalleDto>> listarAdmin() {

        return ResponseEntity.ok(servicio.listarAdmin());
    }

    // Creamos un producto nuevo
    @PostMapping("/api/admin/productos")
    public ResponseEntity<ProductoDetalleDto> crear(
            @RequestBody ProductoCrearDto dto) {

        return ResponseEntity.status(201).body(servicio.crear(dto));
    }

    // Modificamos los datos del producto
    @PutMapping("/api/admin/productos/{id}")
    public ResponseEntity<ProductoDetalleDto> modificar(
            @PathVariable Long id,
            @RequestBody ProductoCrearDto dto) {

        return ResponseEntity.ok(servicio.modificar(id, dto));
    }

    // Retiramos el producto de la venta
    @DeleteMapping("/api/admin/productos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        servicio.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    // Aumentamos o disminuimos el stock
    @PutMapping("/api/admin/productos/{id}/stock")
    public ResponseEntity<ProductoDetalleDto> modificarStock(
            @PathVariable Long id,
            @RequestBody StockDto dto) {

        return ResponseEntity.ok(servicio.modificarStock(id, dto));
    }

    // Volvemos a activar el producto
    @PutMapping("/api/admin/productos/{id}/reactivar")
    public ResponseEntity<ProductoDetalleDto> reactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(servicio.reactivar(id));
    }

    // Mostramos los productos de una categoria
    @GetMapping("/api/categorias/{id}/productos")
    public ResponseEntity<List<ProductoResumenDto>> productosCategoria(
            @PathVariable Long id) {

        return ResponseEntity.ok(servicio.productosCategoria(id));
    }

    // Buscamos un producto por su SKU
    @GetMapping("/api/productos/sku/{sku}")
    public ResponseEntity<ProductoDetalleDto> buscarSku(
            @PathVariable String sku) {

        return ResponseEntity.ok(servicio.buscarSku(sku));
    }

    // Mostramos los productos que tienen poco stock
    @GetMapping("/api/admin/productos/stock-bajo")
    public ResponseEntity<List<ProductoResumenDto>> stockBajo(
            @RequestParam(defaultValue = "5") int umbral) {

        return ResponseEntity.ok(servicio.stockBajo(umbral));
    }
}