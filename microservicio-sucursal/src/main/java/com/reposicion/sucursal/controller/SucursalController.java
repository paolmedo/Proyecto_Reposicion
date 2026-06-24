package com.reposicion.sucursal.controller;

import com.reposicion.sucursal.dto.SucursalDTO;
import com.reposicion.sucursal.model.Sucursal;
import com.reposicion.sucursal.service.SucursalService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Imports de Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
@RestController
@CrossOrigin
@RequestMapping("/api/sucursales")
@Tag(name = "Gestión de Sucursales", description = "Endpoints para administrar las sucursales")
public class SucursalController {

    private static final Logger logger = LoggerFactory.getLogger(SucursalController.class);

    @Autowired
    private SucursalService sucursalService;

    @Operation(summary = "Crear una nueva sucursal")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sucursal creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación en los datos")
    })
    @PostMapping
    public ResponseEntity<Sucursal> crear(@Valid @RequestBody SucursalDTO sucursalDTO) {
        logger.info("Controller: Petición POST recibida en '/api/sucursales'");
        Sucursal nueva = sucursalService.guardarSucursal(sucursalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @Operation(summary = "Actualizar una sucursal existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal actualizada"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Sucursal> actualizar(
            @Parameter(description = "ID de la sucursal a actualizar") @PathVariable Long id,
            @Valid @RequestBody SucursalDTO sucursalDTO) {
        logger.info("Controller: Petición PUT recibida para actualizar ID: {}", id);
        Sucursal actualizada = sucursalService.actualizarSucursal(id, sucursalDTO);

        if (actualizada != null) {
            return ResponseEntity.ok(actualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar todas las sucursales")
    @ApiResponse(responseCode = "200", description = "Lista de sucursales obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<Sucursal>> listar() {
        logger.info("Controller: Petición GET recibida en '/api/sucursales'");
        return ResponseEntity.ok(sucursalService.obtenerTodas());
    }

    @Operation(summary = "Buscar sucursal por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal encontrada"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Sucursal> obtenerPorId(
            @Parameter(description = "ID de la sucursal a buscar") @PathVariable Long id) {
        logger.info("Controller: Buscando sucursal ID: {}", id);
        Sucursal sucursal = sucursalService.obtenerPorId(id);
        if (sucursal != null) {
            return ResponseEntity.ok(sucursal);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una sucursal por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sucursal eliminada con éxito"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSucursal(
            @Parameter(description = "ID de la sucursal a eliminar") @PathVariable Long id){
        logger.info("Controller: Petición DELETE recibida para ID: {}", id);
        boolean eliminado = sucursalService.eliminarSucursal(id);
        if (eliminado){
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}