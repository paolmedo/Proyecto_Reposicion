package com.reposicion.ventas;

import com.reposicion.ventas.client.SucursalClient;
import com.reposicion.ventas.dto.SucursalDTO;
import com.reposicion.ventas.dto.VentasDTO;
import com.reposicion.ventas.model.Ventas;
import com.reposicion.ventas.repository.VentasRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Inicialización de Mockito
class VentasServiceTest {

    @Mock
    private VentasRepository ventasRepository;

    @Mock
    private SucursalClient sucursalClient;

    @InjectMocks
    private VentasService ventasService;

    @Test
    void registrarVenta_Exito() {
        // Given (Prepara datos)
        VentasDTO requestDTO = new VentasDTO();
        requestDTO.setSucursalId(1L);
        requestDTO.setProducto("Monitor");
        requestDTO.setCantidad(2);
        requestDTO.setTotal(250000.0);

        Ventas ventaGuardada = new Ventas();
        ventaGuardada.setId(10L);
        ventaGuardada.setSucursalId(1L);
        ventaGuardada.setProducto("Monitor");
        ventaGuardada.setCantidad(2);
        ventaGuardada.setTotal(250000.0);
        ventaGuardada.setEstado("ACTIVA");
        ventaGuardada.setFechaVenta(LocalDateTime.now());

        SucursalDTO sucursalMock = new SucursalDTO();
        when(sucursalClient.obtenerSucursal(1L)).thenReturn(sucursalMock);
        when(ventasRepository.save(any(Ventas.class))).thenReturn(ventaGuardada);

        // When (Ejecuta el método)
        VentasDTO resultado = ventasService.registrarVenta(requestDTO);

        // Then (Verifica los resultados)
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("ACTIVA", resultado.getEstado());
        verify(sucursalClient, times(1)).obtenerSucursal(1L);
        verify(ventasRepository, times(1)).save(any(Ventas.class));
    }

    @Test
    void eliminarVenta_Exito() {
        Ventas ventaActiva = new Ventas();
        ventaActiva.setId(1L);
        ventaActiva.setEstado("ACTIVA");

        when(ventasRepository.findById(1L)).thenReturn(java.util.Optional.of(ventaActiva));
        when(ventasRepository.save(any(Ventas.class))).thenReturn(ventaActiva);

        boolean resultado = ventasService.eliminarVenta(1L);

        assertTrue(resultado);
        assertEquals("ANULADA", ventaActiva.getEstado());
        verify(ventasRepository, times(1)).findById(1L);
        verify(ventasRepository, times(1)).save(ventaActiva);
    }

    @Test
    void registrarVenta_SucursalNoExiste_LanzaExcepcion() {
        // Given (Prepara datos con un ID que no existe)
        VentasDTO requestDTO = new VentasDTO();
        requestDTO.setSucursalId(99L);
        requestDTO.setProducto("Teclado");

        // Simulamos que el Feign Client devuelve null
        when(sucursalClient.obtenerSucursal(99L)).thenReturn(null);

        // When & Then (Verificamos que lance la excepción y no guarde nada)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> ventasService.registrarVenta(requestDTO));

        assertEquals("Sucursal no encontrada", exception.getMessage());
        verify(ventasRepository, never()).save(any(Ventas.class));
    }

    @Test
    void eliminarVenta_NoExiste() {
        // Simulamos que la base de datos no encuentra el ID 99
        when(ventasRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        boolean resultado = ventasService.eliminarVenta(99L);

        // Verificamos que retorna false y nunca intenta guardar nada
        assertFalse(resultado);
        verify(ventasRepository, never()).save(any(Ventas.class));
    }
}