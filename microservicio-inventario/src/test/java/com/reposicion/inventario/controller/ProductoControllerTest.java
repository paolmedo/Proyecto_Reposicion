package com.reposicion.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reposicion.inventario.dto.ProductoDTO;
import com.reposicion.inventario.model.Producto;
import com.reposicion.inventario.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    //--Test crear producto (POST)
    @Test
    @DisplayName("Debe retornar 201 Created al registrar un producto.")
    void cuandoCrearProducto_entoncesRetornar201Created() throws Exception {
//-- GIVEN
        ProductoDTO dtoEntrada = new ProductoDTO();
        dtoEntrada.setCodigoBarra("123456789");
        dtoEntrada.setNombreProducto("Galletas");
        dtoEntrada.setStock(100);
        dtoEntrada.setDescripcionProducto("Galletas dulces");
        dtoEntrada.setFechaEntradaProducto(LocalDate.now());
        dtoEntrada.setCategoriaId(1L);

        Producto productoGuardado = new Producto();
        productoGuardado.setId(1L);
        productoGuardado.setCodigoBarra("123456789");
        productoGuardado.setNombreProducto("Galletas");
        productoGuardado.setStock(100);

        when(productoService.crearProducto(any(ProductoDTO.class))).thenReturn(productoGuardado);

//--WHEN & THEN
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigoBarra").value("123456789"))
                .andExpect(jsonPath("$.nombreProducto").value("Galletas"))
                .andExpect(jsonPath("$.stock").value(100));
    }

    //-- Test listar todos los productos (GET)
    @Test
    @DisplayName("Debe retornar 200 OK y la lista de productos.")
    void cuandoListarProductos_entoncesRetornar200Ok() throws Exception {
//--GIVEN
        Producto prod1 = new Producto();
        prod1.setId(1L);
        prod1.setNombreProducto("Galletas");

        Producto prod2 = new Producto();
        prod2.setId(2L);
        prod2.setNombreProducto("Bebida");

        List<Producto> listaProductos = List.of(prod1, prod2);

        when(productoService.listarTodosProducto()).thenReturn(listaProductos);

//--WHEN & THEN
        mockMvc.perform(get("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nombreProducto").value("Galletas"))
                .andExpect(jsonPath("$[1].nombreProducto").value("Bebida"));
    }

    //--Test listar un solo producto (GET /{id})
    @Test
    @DisplayName("Debe retornar 200 OK y el producto solicitado.")
    void cuandoListarUnSoloProducto_entoncesRetornar200Ok() throws Exception {
//--GIVEN
        Long idBuscado = 1L;
        Producto productoSimulado = new Producto();
        productoSimulado.setId(idBuscado);
        productoSimulado.setNombreProducto("Galletas");
        productoSimulado.setCodigoBarra("123456789");

        when(productoService.listarUnSoloProducto(idBuscado)).thenReturn(productoSimulado);

//--WHEN & THEN
        mockMvc.perform(get("/api/productos/{id}", idBuscado)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombreProducto").value("Galletas"))
                .andExpect(jsonPath("$.codigoBarra").value("123456789"));
    }

    //--Test Actualizar Producto (PUT /{id})
    @Test
    @DisplayName("Debe retornar 200 OK al actualizar un producto.")
    void cuandoActualizarProducto_entoncesRetornar200Ok() throws Exception {
//--GIVEN
        Long idExistente = 1L;

        ProductoDTO dtoActualizacion = new ProductoDTO();
        dtoActualizacion.setCodigoBarra("123456789");
        dtoActualizacion.setNombreProducto("Galletas de Chocolate");
        dtoActualizacion.setStock(150);
        dtoActualizacion.setDescripcionProducto("Galletas dulces con chispas");

        Producto productoActualizado = new Producto();
        productoActualizado.setId(idExistente);
        productoActualizado.setCodigoBarra("123456789");
        productoActualizado.setNombreProducto("Galletas de Chocolate");
        productoActualizado.setStock(150);
        dtoActualizacion.setFechaEntradaProducto(LocalDate.now());
        dtoActualizacion.setCategoriaId(1L);
        when(productoService.actualizarProducto(eq(idExistente), any(ProductoDTO.class))).thenReturn(productoActualizado);

//--WHEN & THEN
        mockMvc.perform(put("/api/productos/{id}", idExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombreProducto").value("Galletas de Chocolate"))
                .andExpect(jsonPath("$.stock").value(150));

    }

    //-- Test eliminar producto (DELETE /{id})
    @Test
    @DisplayName("Debe retornar 204 No Content al eliminar un producto.")
    void cuandoEliminarProducto_entoncesRetornar204NoContent() throws Exception {
//--GIVEN
        Long idAEliminar = 1L;
//--Metodo void en el Service, no necesitamos 'when'

//--WHEN & THEN
        mockMvc.perform(delete("/api/productos/{id}", idAEliminar))
                .andExpect(status().isNoContent()); // Status 204
    }
}