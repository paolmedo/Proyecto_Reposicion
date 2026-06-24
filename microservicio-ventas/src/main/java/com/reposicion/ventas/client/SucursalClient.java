package com.reposicion.ventas.client;

import com.reposicion.ventas.dto.SucursalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SUCURSAL-SERVICE")
public interface SucursalClient {

    @GetMapping("/api/sucursales/{id}")
    SucursalDTO obtenerSucursal(@PathVariable Long id);
}
