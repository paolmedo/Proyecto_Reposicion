package com.reposicion.ventas.controller;

import com.reposicion.ventas.dto.VentasDTO;
import com.reposicion.ventas.VentasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/ventas")
@Tag(name = "Gestión de Ventas", description = "Endpoints para registrar, actualizar, anular y listar ventas")
public class VentasController {

    private static final Logger logger = LoggerFactory.getLogger(VentasController.class);

    @Autowired
    private VentasService ventasService;

    @PostMapping
    @Operation(summary = "Registrar una nueva venta", description = "Crea un registro de venta validando la existencia de la sucursal")
    @ApiResponse(responseCode = "201", description = "Venta registrada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<VentasDTO> registrarVenta(@Valid @RequestBody VentasDTO ventasDTO) {
        logger.info("Controller: Solicitud recibida para registrar venta");
        VentasDTO nuevaVenta = ventasService.registrarVenta(ventasDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una venta", description = "Modifica los datos de una venta existente según su ID")
    @ApiResponse(responseCode = "200", description = "Venta actualizada correctamente")
    @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    public ResponseEntity<VentasDTO> actualizarVenta(@PathVariable Long id, @Valid @RequestBody VentasDTO ventasDTO) {
        logger.info("Controller: Solicitud recibida para actualizar venta ID: {}", id);
        VentasDTO ventaActualizada = ventasService.actualizarVenta(id, ventasDTO);
        if (ventaActualizada != null) {
            return ResponseEntity.ok(ventaActualizada);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Anular una venta", description = "Realiza una anulación lógica cambiando el estado de la venta a ANULADA")
    @ApiResponse(responseCode = "204", description = "Venta anulada correctamente")
    @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        logger.info("Controller: Solicitud recibida para anular venta ID: {}", id);
        boolean eliminada = ventasService.eliminarVenta(id);
        if (eliminada) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Listar todas las ventas", description = "Retorna una lista con todas las ventas registradas en el sistema")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    public ResponseEntity<List<VentasDTO>> listarTodas() {
        logger.info("Controller: Solicitud recibida para listar todas las ventas");
        List<VentasDTO> ventas = ventasService.listarTodas();
        return ResponseEntity.ok(ventas);
    }
}