CREATE TABLE sucursales (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            nombre VARCHAR(50) NOT NULL,
                            direccion VARCHAR(255) NOT NULL,
                            telefono INT NOT NULL,
                            ciudad VARCHAR(100) NOT NULL
);

INSERT INTO sucursales (nombre, direccion, telefono, ciudad)
VALUES
    ('Sucursal Viña del Mar', 'Avenida Libertad 123', 322123456, 'Viña del Mar'),
    ('Sucursal Valparaíso', 'Calle Condell 456', 322654321, 'Valparaíso');