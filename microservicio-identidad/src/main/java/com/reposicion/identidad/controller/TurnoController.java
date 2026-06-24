package com.reposicion.identidad.controller;

import com.reposicion.identidad.dto.TurnoDTO;
import com.reposicion.identidad.excepciones.RespuestaError;
import com.reposicion.identidad.model.TurnoTrabajador;
import com.reposicion.identidad.service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/turnos")
@Tag(name = "Turnos", description = "Operaciones relacionadas con los turnos.")

public class TurnoController {

    private final TurnoService turnoService;

    // Crear turno
    @PostMapping
    @Operation(summary = "Crear un turno", description = "Crear un turno con un ID")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "201", description = "Turno creado con exito.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = TurnoTrabajador.class))),
            @ApiResponse(responseCode = "400", description = "Sintaxis no valida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
            @ApiResponse(responseCode = "409", description = "No puede haber un turno con el mismo horario de entrada y de salida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class)))
    })
    public ResponseEntity<TurnoTrabajador> turnoCreado(@RequestBody @Valid TurnoDTO turnoDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.crearTurno(turnoDTO));
    }

    // Listar turnos
    @GetMapping
    @Operation(summary = "Obtener todos los turnos", description = "Obtener todos los turnos en una lista.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Turnos listados con exito.",
                content = @Content(mediaType = "application/json",
                array = @ArraySchema( schema = @Schema(implementation = TurnoTrabajador.class))))
    })
    public ResponseEntity<List<TurnoTrabajador>> listarTurno(){
        return ResponseEntity.ok(turnoService.listarTodosTurnos());
    }

    // Listar un turno en especifico
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un turno", description = "Obtener un turno mediante su ID.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Turno obtenido con exito.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = TurnoTrabajador.class))),
            @ApiResponse(responseCode = "400", description = "Sintaxis no valida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
            @ApiResponse(responseCode = "404", description = "ID no encontrado",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class)))
    })
    public ResponseEntity<TurnoTrabajador> listarSoloUnTurno(@PathVariable Long id){
        return ResponseEntity.ok(turnoService.obtenerUnSoloTurno(id));
    }

    // Actualizar turno
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un turno", description = "Actualizar un turno mediante su ID.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Turno actualizado con exito.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = TurnoTrabajador.class))),
            @ApiResponse(responseCode = "400", description = "Sintaxis no valida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
            @ApiResponse(responseCode = "404", description = "ID no encontrado",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
            @ApiResponse(responseCode = "409", description = "No puede haber un turno con el mismo horario de entrada y de salida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class)))
    })
    public ResponseEntity<TurnoTrabajador> actualizarTurnos(@PathVariable Long id, @RequestBody @Valid TurnoDTO turnoDTO){
    TurnoTrabajador turnoGuardado =   turnoService.actualizarTurno(id,turnoDTO);
    return ResponseEntity.ok(turnoGuardado);
    }

    // Eliminar turno
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un turno", description = "Eliminar un turno mediante su ID.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "204", description = "Turno eliminado con exito."),
            @ApiResponse(responseCode = "400", description = "Sintaxis no valida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
            @ApiResponse(responseCode = "404", description = "ID no encontrado.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class)))
    })
    public ResponseEntity<Void> turnoEliminado(@PathVariable Long id){
        turnoService.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }
}
