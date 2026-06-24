package com.reposicion.identidad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reposicion.identidad.dto.TrabajadorDTO;
import com.reposicion.identidad.model.Trabajador;
import com.reposicion.identidad.service.TrabajadorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(TrabajadorController.class)
public class TrabajadorControllerTest {
    // 2. MockMvc es nuestro "Postman" interno. Con esto se hace el "CRUD".
    @Autowired
    private MockMvc mockMvc;

    // 3. Se usa @MockBean ya que estamos dentro del ecosistema de Spring.
    @MockBean
    private TrabajadorService trabajadorService;

    // 4. ObjectMapper transforma objetos (TrabajadorDTO) a texto JSON y viceversa.
    @Autowired
    private ObjectMapper objectMapper;


    //--Test listar TODOS los trabajadores (GET)
    @Test
    @org.junit.jupiter.api.DisplayName("Debe retornar 200 OK y la lista de trabajadores en formato JSON.")
    void cuandoListarTrabajadores_entoncesRetornar200Ok() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Trabajador trabajadorFalso = new Trabajador();
        trabajadorFalso.setId(1L);
        trabajadorFalso.setRut("20.123.456-7");
        trabajadorFalso.setNombre("Pavel");
        trabajadorFalso.setRol("Reponedor");
        trabajadorFalso.setEdad(21);

        java.util.List<Trabajador> listaFalsa = java.util.List.of(trabajadorFalso);

        //--Le decimos a nuestro Service simulado qué responder
        when(trabajadorService.listarTodosTrabajadores()).thenReturn(listaFalsa);

//--WHEN & THEN (Hacer la petición como en Postman y validar)
        mockMvc.perform(get("/api/trabajadores") // Simulamos un GET a tu URL real
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)) // Decimos que esperamos JSON

                //--Empezamos a validar la respuesta HTTP:
                .andExpect(status().isOk()) //--Esperamos un 200 OK (ResponseEntity.ok)

                //--JsonPath nos permite navegar por el JSON resultante usando el símbolo $
                //--$[0] significa "el primer elemento de la lista"
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Pavel"))
                .andExpect(jsonPath("$[0].rol").value("Reponedor"));
    }

    //--Test crear un trabajador (POST)
    @Test
    @org.junit.jupiter.api.DisplayName("Debe retornar 201 Created y el trabajador creado en JSON.")
    void cuandoCrearTrabajador_entoncesRetornar201Created() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        // 1. Armamos el DTO que "enviaría" el usuario desde el frontend o Postman
        TrabajadorDTO trabajadorEntrada = new TrabajadorDTO();
        trabajadorEntrada.setRut("20.123.456-7");
        trabajadorEntrada.setNombre("Pavel");
        trabajadorEntrada.setCorreo("correoPavel@gmail.com");
        trabajadorEntrada.setRol("Reponedor");
        trabajadorEntrada.setEdad(21);

        // 2. Entidad que devolvería la base de datos (ya con ID generado)
        Trabajador trabajadorGuardado = new Trabajador();
        trabajadorGuardado.setId(1L);
        trabajadorGuardado.setRut("20.123.456-7");
        trabajadorGuardado.setNombre("Pavel");
        trabajadorGuardado.setCorreo("correoPavel@gmail.com");
        trabajadorGuardado.setRol("Reponedor");
        trabajadorGuardado.setEdad(21);

        // Simulamos que el Service guarda con éxito cualquier DTO que le pasen y devuelve la entidad
        when(trabajadorService.guardarTrabajador(any(TrabajadorDTO.class))).thenReturn(trabajadorGuardado);

//--WHEN & THEN (Hacer la petición y validar)
        mockMvc.perform(post("/api/trabajadores") //--Simulamos un POST
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        //--objectMapper convierte tu DTO a un String en formato JSON
                        .content(objectMapper.writeValueAsString(trabajadorEntrada)))

                //--validacion de respuesta:
                .andExpect(status().isCreated()) // Esperamos un 201 Created

                //--No se usa $[0] porque no es una lista
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Pavel"))
                .andExpect(jsonPath("$.correo").value("correoPavel@gmail.com"))
                .andExpect(jsonPath("$.rol").value("Reponedor"));
    }

    //--Test obtener un trabajador en específico (GET /{id})
    @Test
    @org.junit.jupiter.api.DisplayName("Debe retornar 200 OK y un solo trabajador en formato JSON.")
    void cuandoListarUnTrabajador_entoncesRetornar200Ok() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idBuscado = 1L;

        Trabajador trabajadorFalso = new Trabajador();
        trabajadorFalso.setId(idBuscado);
        trabajadorFalso.setRut("20.123.456-7");
        trabajadorFalso.setNombre("Pavel");
        trabajadorFalso.setRol("Reponedor");
        trabajadorFalso.setEdad(21);

        //--Al buscar el ID 1, el Service lo encuentra y lo devuelve
        when(trabajadorService.obtenerUnSoloTrabajador(idBuscado)).thenReturn(trabajadorFalso);

//--WHEN & THEN
        //--Se pasa el idBuscado al final de la ruta: get("ruta/{id}", variable)
        mockMvc.perform(get("/api/trabajadores/{id}", idBuscado)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))

                //--Validacion de respuesta exitosa (200 OK)
                .andExpect(status().isOk())

                //--Validacion de contenido del JSON
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Pavel"))
                .andExpect(jsonPath("$.rut").value("20.123.456-7"));
    }
    //--Test actualizar trabajador (PUT)
    @Test
    @org.junit.jupiter.api.DisplayName("Debe retornar 200 OK y el trabajador actualizado en JSON.")
    void cuandoActualizarTrabajador_entoncesRetornar200Ok() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idExistente = 1L;

        //--El DTO con los datos nuevos enviados
        TrabajadorDTO dtoActualizacion = new TrabajadorDTO();
        dtoActualizacion.setRut("20.123.456-7");
        dtoActualizacion.setNombre("Pavel Modificado");
        dtoActualizacion.setCorreo("correoPavel@gmail.com");
        dtoActualizacion.setRol("Supervisor");
        dtoActualizacion.setEdad(21);

        //--La entidad que devolvera el Service
        Trabajador trabajadorActualizado = new Trabajador();
        trabajadorActualizado.setId(idExistente);
        trabajadorActualizado.setRut("20.123.456-7");
        trabajadorActualizado.setNombre("Pavel Modificado");
        trabajadorActualizado.setCorreo("correoPavel@gmail.com");
        trabajadorActualizado.setRol("Supervisor");
        trabajadorActualizado.setEdad(21);

        when(trabajadorService.actualizarTrabajador(org.mockito.ArgumentMatchers.eq(idExistente), any(TrabajadorDTO.class)))
                .thenReturn(trabajadorActualizado);

//--WHEN & THEN
        mockMvc.perform(put("/api/trabajadores/{id}", idExistente) // Simulamos un PUT
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizacion)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pavel Modificado"))
                .andExpect(jsonPath("$.rol").value("Supervisor"));
    }

    //--Test eliminar trabajador (DELETE)
    @Test
    @org.junit.jupiter.api.DisplayName("Debe retornar 204 No Content al eliminar un trabajador.")
    void cuandoEliminarTrabajador_entoncesRetornar204NoContent() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idAEliminar = 1L;
//--WHEN & THEN
        mockMvc.perform(delete("/api/trabajadores/{id}", idAEliminar)) // Simulamos un DELETE
                .andExpect(status().isNoContent()); // Tu controlador responde con 204 No Content
    }

    //--Test asignar turno a trabajador (PUT personalizado)
    @Test
    @org.junit.jupiter.api.DisplayName("Debe retornar 200 OK al asignar un turno.")
    void cuandoAsignarTurno_entoncesRetornar200Ok() throws Exception {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idTrabajador = 1L;
        Long idTurno = 2L;

        Trabajador trabajadorConTurno = new Trabajador();
        trabajadorConTurno.setId(idTrabajador);
        trabajadorConTurno.setNombre("Pavel");

        when(trabajadorService.asignarTurno(idTrabajador, idTurno)).thenReturn(trabajadorConTurno);

//--WHEN & THEN
        //--Ambas Urls
        mockMvc.perform(put("/api/trabajadores/{trabajadorId}/turno/{turnoId}", idTrabajador, idTurno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Pavel"));
    }
}