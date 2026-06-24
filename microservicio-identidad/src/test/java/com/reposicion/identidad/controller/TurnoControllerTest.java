package com.reposicion.identidad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reposicion.identidad.dto.TurnoDTO;
import com.reposicion.identidad.model.TurnoTrabajador;
import com.reposicion.identidad.service.TurnoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TurnoController.class)
public class TurnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TurnoService turnoService;

    @Autowired
    private ObjectMapper objectMapper;

    //--Test Crear Turno (POST)
    @Test
    @DisplayName("Debe retornar 201 Created al registrar un turno.")
    void cuandoCrearTurno_entoncesRetornar201Created() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        TurnoDTO turnoDTO = new TurnoDTO();
        turnoDTO.setTipoTurno("Mañana");
        turnoDTO.setHoraInicio(LocalTime.of(8, 0));
        turnoDTO.setHoraTermino(LocalTime.of(16, 0));

        TurnoTrabajador turnoGuardado = new TurnoTrabajador();
        turnoGuardado.setId(1L);
        turnoGuardado.setTipoTurno("Mañana");
        turnoGuardado.setHoraInicio(LocalTime.of(8, 0));
        turnoGuardado.setHoraTermino(LocalTime.of(16, 0));

        when(turnoService.crearTurno(any(TurnoDTO.class))).thenReturn(turnoGuardado);

//--WHEN & THEN
        mockMvc.perform(post("/api/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipoTurno").value("Mañana"));
    }

    //--Test Listar Todos los Turnos (GET)
    @Test
    @DisplayName("Debe retornar 200 OK y la lista de turnos.")
    void cuandoListarTurnos_entoncesRetornar200Ok() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        TurnoTrabajador turno1 = new TurnoTrabajador();
        turno1.setId(1L);
        turno1.setTipoTurno("Mañana");

        TurnoTrabajador turno2 = new TurnoTrabajador();
        turno2.setId(2L);
        turno2.setTipoTurno("Tarde");

        List<TurnoTrabajador> listaTurnos = List.of(turno1, turno2);

        when(turnoService.listarTodosTurnos()).thenReturn(listaTurnos);

//-- WHEN & THEN
        mockMvc.perform(get("/api/turnos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].tipoTurno").value("Mañana"))
                .andExpect(jsonPath("$[1].tipoTurno").value("Tarde"));
    }

    //-- Test Listar Un Solo Turno (GET /{id})
    @Test
    @DisplayName("Debe retornar 200 OK y el turno solicitado.")
    void cuandoListarSoloUnTurno_entoncesRetornar200Ok() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idBuscado = 1L;
        TurnoTrabajador turno = new TurnoTrabajador();
        turno.setId(idBuscado);
        turno.setTipoTurno("Noche");

        when(turnoService.obtenerUnSoloTurno(idBuscado)).thenReturn(turno);

//-- WHEN & THEN
        mockMvc.perform(get("/api/turnos/{id}", idBuscado)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipoTurno").value("Noche"));
    }

    //-- Test Actualizar Turno (PUT /{id})
    @Test
    @DisplayName("Debe retornar 200 OK al actualizar un turno.")
    void cuandoActualizarTurnos_entoncesRetornar200Ok() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idExistente = 1L;

        TurnoDTO dtoActualizacion = new TurnoDTO();
        dtoActualizacion.setTipoTurno("Tarde");
        dtoActualizacion.setHoraInicio(LocalTime.of(14, 0));
        dtoActualizacion.setHoraTermino(LocalTime.of(22, 0));

        TurnoTrabajador turnoActualizado = new TurnoTrabajador();
        turnoActualizado.setId(idExistente);
        turnoActualizado.setTipoTurno("Tarde");
        turnoActualizado.setHoraInicio(LocalTime.of(14, 0));
        turnoActualizado.setHoraTermino(LocalTime.of(22, 0));

        when(turnoService.actualizarTurno(eq(idExistente), any(TurnoDTO.class))).thenReturn(turnoActualizado);

//-- WHEN & THEN
        mockMvc.perform(put("/api/turnos/{id}", idExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipoTurno").value("Tarde"));
    }

    //-- Test Eliminar Turno (DELETE /{id})
    @Test
    @DisplayName("Debe retornar 204 No Content al eliminar un turno.")
    void cuandoEliminarTurno_entoncesRetornar204NoContent() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idAEliminar = 1L;
        //--Al ser void en el Controller y Service no necesita when.

//-- WHEN & THEN
        mockMvc.perform(delete("/api/turnos/{id}", idAEliminar))
                .andExpect(status().isNoContent());
    }
}