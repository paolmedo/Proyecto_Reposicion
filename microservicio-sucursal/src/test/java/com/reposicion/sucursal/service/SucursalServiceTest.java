package com.reposicion.sucursal.service;

import com.reposicion.sucursal.dto.SucursalDTO;
import com.reposicion.sucursal.model.Sucursal;
import com.reposicion.sucursal.repository.SucursalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    @Test
    void guardarSucursal_DeberiaRetornarSucursalGuardada() {
        // Given (Dado un DTO y una entidad simulada)
        SucursalDTO dto = new SucursalDTO();
        dto.setNombre("Sucursal Centro");

        Sucursal sucursalSimulada = new Sucursal();
        sucursalSimulada.setId(1L);
        sucursalSimulada.setNombre("Sucursal Centro");

        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(sucursalSimulada);

        // When (Cuando se llama al método del servicio)
        Sucursal resultado = sucursalService.guardarSucursal(dto);

        // Then (Entonces el resultado debe ser válido y guardar en BD)
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Sucursal Centro", resultado.getNombre());
        verify(sucursalRepository, times(1)).save(any(Sucursal.class));
    }

    @Test
    void obtenerPorId_DeberiaRetornarSucursal_CuandoExiste() {
        // Given
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1L);
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));

        // When
        Sucursal resultado = sucursalService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(sucursalRepository, times(1)).findById(1L);
    }
    @Test
    void obtenerTodas_DeberiaRetornarListaDeSucursales() {
        // Given
        Sucursal s1 = new Sucursal();
        Sucursal s2 = new Sucursal();
        when(sucursalRepository.findAll()).thenReturn(java.util.Arrays.asList(s1, s2));

        // When
        java.util.List<Sucursal> resultado = sucursalService.obtenerTodas();

        // Then
        assertEquals(2, resultado.size());
        verify(sucursalRepository, times(1)).findAll();
    }

    @Test
    void actualizarSucursal_DeberiaRetornarActualizada_CuandoExiste() {
        // Given
        Sucursal existente = new Sucursal();
        existente.setId(1L);
        existente.setNombre("Antiguo Nombre");

        SucursalDTO dto = new SucursalDTO();
        dto.setNombre("Nuevo Nombre");

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(existente);

        // When
        Sucursal resultado = sucursalService.actualizarSucursal(1L, dto);

        // Then
        assertNotNull(resultado);
        assertEquals("Nuevo Nombre", resultado.getNombre());
        verify(sucursalRepository, times(1)).save(any(Sucursal.class));
    }

    @Test
    void eliminarSucursal_DeberiaRetornarTrue_CuandoExiste() {
        // Given
        when(sucursalRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sucursalRepository).deleteById(1L);

        // When
        boolean resultado = sucursalService.eliminarSucursal(1L);

        // Then
        assertTrue(resultado);
        verify(sucursalRepository, times(1)).deleteById(1L);
    }
}