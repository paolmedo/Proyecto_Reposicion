package com.reposicion.ventas.dto;

import lombok.Data;

@Data
public class SucursalDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private Integer telefono;
    private String ciudad;
}