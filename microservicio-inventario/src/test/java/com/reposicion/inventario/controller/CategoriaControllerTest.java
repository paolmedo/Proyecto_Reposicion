package com.reposicion.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reposicion.inventario.dto.CategoriaDTO;
import com.reposicion.inventario.model.Categoria;
import com.reposicion.inventario.service.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    //--Test Crear Categoría (POST)
    @Test
    @DisplayName("Debe retornar 201 Created al registrar una categoria.")
    void cuandoCrearCategoria_entoncesRetornar201Created() throws Exception {
//--GIVEN
        CategoriaDTO dtoEntrada = new CategoriaDTO();
        dtoEntrada.setNombreCategoria("Lácteos");
        dtoEntrada.setDescripcionCategoria("Productos derivados de la leche");

        Categoria categoriaGuardada = new Categoria();
        categoriaGuardada.setId(1L);
        categoriaGuardada.setNombreCategoria("Lácteos");
        categoriaGuardada.setDescripcionCategoria("Productos derivados de la leche");

        when(categoriaService.crearCategoria(any(CategoriaDTO.class))).thenReturn(categoriaGuardada);

        //--WHEN & THEN
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombreCategoria").value("Lácteos"))
                .andExpect(jsonPath("$.descripcionCategoria").value("Productos derivados de la leche"));
    }

    //--Test Listar Todas las Categorías (GET)
    @Test
    @DisplayName("Debe retornar 200 OK y la lista de categorias.")
    void cuandoListarCategorias_entoncesRetornar200Ok() throws Exception {
//--GIVEN
        Categoria cat1 = new Categoria();
        cat1.setId(1L);
        cat1.setNombreCategoria("Lácteos");

        Categoria cat2 = new Categoria();
        cat2.setId(2L);
        cat2.setNombreCategoria("Carnes");

        List<Categoria> listaCategorias = List.of(cat1, cat2);

        when(categoriaService.listarCategorias()).thenReturn(listaCategorias);

//--WHEN & THEN
        mockMvc.perform(get("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nombreCategoria").value("Lácteos"))
                .andExpect(jsonPath("$[1].nombreCategoria").value("Carnes"));
    }

    //--Test Listar Una Sola Categoría (GET /{id})
    @Test
    @DisplayName("Debe retornar 200 OK y la categoria solicitada.")
    void cuandoListarUnaCategoria_entoncesRetornar200Ok() throws Exception {
//--GIVEN
        Long idBuscado = 1L;
        Categoria categoriaSimulada = new Categoria();
        categoriaSimulada.setId(idBuscado);
        categoriaSimulada.setNombreCategoria("Lácteos");

        when(categoriaService.listarUnaCategoria(idBuscado)).thenReturn(categoriaSimulada);

//--WHEN & THEN
        mockMvc.perform(get("/api/categorias/{id}", idBuscado)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombreCategoria").value("Lácteos"));
    }

    //--Test Actualizar Categoría (PUT /{id})
    @Test
    @DisplayName("Debe retornar 200 OK al actualizar una categoria.")
    void cuandoActualizarCategoria_entoncesRetornar200Ok() throws Exception {
//--GIVEN
        Long idExistente = 1L;

        CategoriaDTO dtoActualizacion = new CategoriaDTO();
        dtoActualizacion.setNombreCategoria("Lácteos y Quesos");
        dtoActualizacion.setDescripcionCategoria("Todo tipo de derivados lácteos");

        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setId(idExistente);
        categoriaActualizada.setNombreCategoria("Lácteos y Quesos");
        categoriaActualizada.setDescripcionCategoria("Todo tipo de derivados lácteos");

        when(categoriaService.actualizarUnaCategoria(eq(idExistente), any(CategoriaDTO.class))).thenReturn(categoriaActualizada);

//--WHEN & THEN
        mockMvc.perform(put("/api/categorias/{id}", idExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombreCategoria").value("Lácteos y Quesos"))
                .andExpect(jsonPath("$.descripcionCategoria").value("Todo tipo de derivados lácteos"));
    }

    //--Test Eliminar Categoría (DELETE /{id})
    @Test
    @DisplayName("Debe retornar 204 No Content al eliminar una categoria.")
    void cuandoEliminarCategoria_entoncesRetornar204NoContent() throws Exception {
//--GIVEN
        Long idAEliminar = 1L;

//--WHEN & THEN
        mockMvc.perform(delete("/api/categorias/{id}", idAEliminar))
                .andExpect(status().isNoContent()); // Status 204
    }
}