package com.reposicion.ventas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasDTO {

    private Long id;

    @JsonProperty("sucursalId")
    @NotNull(message = "El ID de la sucursal es obligatorio.")
    private Long sucursalId;

    @JsonProperty("producto")
    @NotBlank(message = "El nombre del producto no puede estar vacío.")
    private String producto;

    @JsonProperty("cantidad")
    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad mínima a vender debe ser 1.")
    private Integer cantidad;

    @JsonProperty("total")
    @NotNull(message = "El total de la venta es obligatorio.")
    @Min(value = 0, message = "El total no puede ser un monto negativo.")
    private Double total;

    private String estado;
}