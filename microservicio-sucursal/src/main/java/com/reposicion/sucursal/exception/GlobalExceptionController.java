package com.reposicion.sucursal.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

    // 1. Atrapa datos inválidos (Ej: @Valid falla por nombre vacío)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        logger.warn("Exception: Datos inválidos en Sucursales.");
        ErrorResponse error = new ErrorResponse();
        error.setMensaje("Datos inválidos en la petición");
        error.setDetalle(ex.getBindingResult().getFieldError().getDefaultMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 2. Atrapa errores de formato (Ej: Letras en campo de teléfono)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleFormatError(HttpMessageNotReadableException ex) {
        logger.warn("Exception: Error de formato en los datos de entrada de Sucursales.");
        ErrorResponse error = new ErrorResponse();
        error.setMensaje("Formato de dato incorrecto");
        error.setDetalle("Se ingresó texto en un campo numérico o el JSON está mal formado.");
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 3. Atrapa cualquier otro error inesperado (Error 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        logger.error("Exception: Error interno en Sucursales - {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse();
        error.setMensaje("Error interno en el servidor");
        error.setDetalle(ex.getMessage());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}