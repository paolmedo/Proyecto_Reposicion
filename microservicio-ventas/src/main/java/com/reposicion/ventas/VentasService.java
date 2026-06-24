package com.reposicion.ventas;

import com.reposicion.ventas.client.SucursalClient;
import com.reposicion.ventas.dto.SucursalDTO;
import com.reposicion.ventas.dto.VentasDTO;
import com.reposicion.ventas.model.Ventas;
import com.reposicion.ventas.repository.VentasRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentasService {
    private static final Logger logger = LoggerFactory.getLogger(VentasService.class);

    @Autowired
    private VentasRepository ventaRepository;

    @Autowired
    private SucursalClient sucursalClient;

    public VentasDTO registrarVenta(VentasDTO ventasDTO) {
        logger.info("Validando existencia de sucursal ID: {}", ventasDTO.getSucursalId());

        SucursalDTO sucursal = sucursalClient.obtenerSucursal(ventasDTO.getSucursalId());
        if (sucursal == null) {
            throw new RuntimeException("Sucursal no encontrada");
        }
        logger.info("Sucursal validada correctamente.");

        Ventas venta = new Ventas();
        venta.setSucursalId(ventasDTO.getSucursalId());
        venta.setProducto(ventasDTO.getProducto());
        venta.setCantidad(ventasDTO.getCantidad());
        venta.setTotal(ventasDTO.getTotal());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado("ACTIVA");

        Ventas guardada = ventaRepository.save(venta);
        logger.info("Venta registrada con éxito. Ticket ID: {}", guardada.getId());
        return convertirADto(guardada);
    }

    public VentasDTO actualizarVenta(Long id, VentasDTO ventasDTO) {
        logger.info("Intentando actualizar venta con ID: {}", id);
        return ventaRepository.findById(id)
                .map(ventaExistente -> {
                    ventaExistente.setSucursalId(ventasDTO.getSucursalId());
                    ventaExistente.setProducto(ventasDTO.getProducto());
                    ventaExistente.setCantidad(ventasDTO.getCantidad());
                    ventaExistente.setTotal(ventasDTO.getTotal());

                    Ventas actualizada = ventaRepository.save(ventaExistente);
                    logger.info("Venta actualizada con ID: {}", id);
                    return convertirADto(actualizada); // Devolvemos el DTO
                })
                .orElse(null);
    }

    public boolean eliminarVenta(Long id) {
        logger.info("Intentando anular lógicamente la venta ID: {}", id);
        return ventaRepository.findById(id)
                .map(ventaExistente -> {
                    ventaExistente.setEstado("ANULADA");
                    ventaRepository.save(ventaExistente);
                    logger.info("Venta ID {} anulada correctamente.", id);
                    return true;
                })
                .orElseGet(() -> {
                    logger.warn("No existe venta con ID: {}", id);
                    return false;
                });
    }

    public List<VentasDTO> listarTodas() {
        logger.info("Listando todas las ventas");
        return ventaRepository.findAll()
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    private VentasDTO convertirADto(Ventas entidad) {
        VentasDTO dto = new VentasDTO();
        dto.setId(entidad.getId());
        dto.setSucursalId(entidad.getSucursalId());
        dto.setProducto(entidad.getProducto());
        dto.setCantidad(entidad.getCantidad());
        dto.setTotal(entidad.getTotal());
        dto.setEstado(entidad.getEstado());
        return dto;
    }
}