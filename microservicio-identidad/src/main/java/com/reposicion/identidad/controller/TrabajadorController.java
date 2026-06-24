package com.reposicion.identidad.controller;

import com.reposicion.identidad.dto.TrabajadorDTO;
import com.reposicion.identidad.excepciones.RespuestaError;
import com.reposicion.identidad.model.Trabajador;
import com.reposicion.identidad.service.TrabajadorService;
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
@RequestMapping("/api/trabajadores")
@Tag(name = "Trabajadores", description = "Operaciones relacionadas con los trabajadores.")

public class TrabajadorController {

    private final TrabajadorService trabajadorService;

   // Crear un trabajador
   @PostMapping
   @Operation(summary = "Crear un trabajador", description = "Crear un trabajador con ID automatico.")
   @ApiResponses(value ={
           @ApiResponse(responseCode = "201", description = "Trabajador creado con exito.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Trabajador.class))),
           @ApiResponse(responseCode = "400", description = "Sintaxis no valida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
           @ApiResponse(responseCode = "409", description = "RUT o CORREO ya en uso.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class)))
   })
   public ResponseEntity<Trabajador> crearTrabajador(@RequestBody @Valid TrabajadorDTO trabajadorDTO){
       return ResponseEntity.status(HttpStatus.CREATED).body(trabajadorService.guardarTrabajador(trabajadorDTO));
   }

   // Listar TODOS los trabajadores registrados
   @GetMapping
   @Operation(summary = "Obtener todos los trabajadores", description = "Obtener todos los trabajadores en una lista.")
   @ApiResponses(value ={
           @ApiResponse(responseCode = "200", description = "Trabajadores listados con exito.",
                content = @Content(mediaType = "application/json",
                array = @ArraySchema( schema = @Schema(implementation = Trabajador.class))))
   })
   public ResponseEntity<List<Trabajador>> listarTrabajador(){
       return ResponseEntity.ok(trabajadorService.listarTodosTrabajadores());
   }

   // Listar un trabajador en especifico
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un trabajador", description = "Obtener un trabajador mediante su ID.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Trabajador obtenido con exito.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Trabajador.class))),
            @ApiResponse(responseCode = "400", description = "Sintaxis no valida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
            @ApiResponse(responseCode = "404", description = "ID no encontrado",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class)))
    })
    public ResponseEntity<Trabajador> listarUnTrabajador(@PathVariable Long id){
       return ResponseEntity.ok(trabajadorService.obtenerUnSoloTrabajador(id));
    }

   // Actualizar trabajador
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un trabajador", description = "Actualizar un trabajador mediante su ID.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Trabajador actualizado con exito.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Trabajador.class))),
            @ApiResponse(responseCode = "400", description = "Sintaxis no valida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
            @ApiResponse(responseCode = "404", description = "ID no encontrado",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
            @ApiResponse(responseCode = "409", description = "El RUT o CORREO le pertenecen a otro trabajador.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class)))
    })
    public ResponseEntity<Trabajador> actualizarTrabajadores(@PathVariable Long id,@RequestBody @Valid TrabajadorDTO trabajadorDTO ){
       Trabajador trabajadorGuardado = trabajadorService.actualizarTrabajador(id,trabajadorDTO );
    return ResponseEntity.ok(trabajadorGuardado);
    }

    // Eliminar trabajador
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un trabajador", description = "Eliminar un trabajador mediante su ID.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "204", description = "Trabajador eliminado con exito."),
            @ApiResponse(responseCode = "400", description = "Sintaxis no valida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
            @ApiResponse(responseCode = "404", description = "ID no encontrado.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class)))
    })
    public ResponseEntity<Void> eliminarTrabajador(@PathVariable Long id){
    trabajadorService.eliminarTrabajador(id);
    return ResponseEntity.noContent().build();
    }

    // Asignar turno a trabajador
    @PutMapping("/{trabajadorId}/turno/{turnoId}")
    @Operation(summary = "Cambiar un turno", description = "Cambiar un turno a un trabajador. Se necesita el ID del turno y el ID del trabajador.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Turno asignado con exito.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Trabajador.class))),
            @ApiResponse(responseCode = "400", description = "Sintaxis no valida.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class))),
            @ApiResponse(responseCode = "404", description = "Trabajador no encontrado o turno no encontrado.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespuestaError.class)))
    })
    public ResponseEntity<Trabajador> asignarTurnoTrabajador(@PathVariable Long trabajadorId, @PathVariable Long turnoId){
       Trabajador trabajadorActualizado = trabajadorService.asignarTurno(trabajadorId, turnoId);
       return ResponseEntity.ok(trabajadorActualizado);
    }
}
