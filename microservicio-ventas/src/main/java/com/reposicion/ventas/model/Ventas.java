package com.reposicion.ventas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
@Data
public class Ventas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de la sucursal es obligatorio.")
    private Long sucursalId;

    @NotBlank(message = "El código o nombre del producto no puede estar vacío.")
    private String producto;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad mínima de venta debe ser 1.")
    private Integer cantidad;

    @NotNull(message = "El total de la venta es obligatorio.")
    @Min(value = 0, message = "El total no puede ser negativo.")
    private Double total;

    private String estado= "ACTIVA";

    private LocalDateTime fechaVenta = LocalDateTime.now();
}