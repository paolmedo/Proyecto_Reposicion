package com.reposicion.identidad.service;

import com.reposicion.identidad.dto.TrabajadorDTO;
import com.reposicion.identidad.excepciones.ExceptionConflict;
import com.reposicion.identidad.model.Trabajador;
import com.reposicion.identidad.model.TurnoTrabajador;
import com.reposicion.identidad.repository.TrabajadorRepository;
import com.reposicion.identidad.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final TurnoRepository turnoRepository;

    // Crear un trabajador
    public Trabajador guardarTrabajador(TrabajadorDTO trabajadorDTO){
        log.info("Iniciando creación de nuevo trabajador con RUT {}", trabajadorDTO.getRut());
        if (trabajadorRepository.existsByRut(trabajadorDTO.getRut())){
            throw new ExceptionConflict("El RUT '" + trabajadorDTO.getRut() + "' Ya pertenece a un trabajador registrado.");
        }
        if (trabajadorRepository.existsByCorreo(trabajadorDTO.getCorreo())){
            throw new ExceptionConflict("El CORREO '" + trabajadorDTO.getCorreo() + "' Ya pertenece a un trabajador registrado.");
        }
        Trabajador nuevoTrabajador = new Trabajador();
        nuevoTrabajador.setRut(trabajadorDTO.getRut());
        nuevoTrabajador.setNombre(trabajadorDTO.getNombre());
        nuevoTrabajador.setCorreo(trabajadorDTO.getCorreo());
        nuevoTrabajador.setRol(trabajadorDTO.getRol());
        nuevoTrabajador.setEdad(trabajadorDTO.getEdad());
        if (trabajadorDTO.getTurnoId() != null){
            TurnoTrabajador turno = turnoRepository.findById(trabajadorDTO.getTurnoId()).orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + trabajadorDTO.getTurnoId()));
            nuevoTrabajador.setTurno(turno);
        }
        return trabajadorRepository.save(nuevoTrabajador);
    }
    // Listar TODOS los trabajadores registrados
    public  List<Trabajador> listarTodosTrabajadores() {
        log.debug("Iniciando busqueda de todos los trabajadores");
        return trabajadorRepository.findAll();
    }
    // Obtener un trabajador en específico
    public Trabajador obtenerUnSoloTrabajador(Long id){
        log.debug("Inicando busqueda de trabajador con ID {}", id);
        return trabajadorRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }
    // Actualizar trabajador
    public Trabajador actualizarTrabajador(Long id, TrabajadorDTO trabajadorDTO){
        log.info("Iniciando actualizacion de trabajador con ID {}", id);
        Trabajador trabajadorExistente = trabajadorRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        if(trabajadorRepository.existsByRut(trabajadorDTO.getRut())
        && !trabajadorExistente.getRut().equals(trabajadorDTO.getRut())){
            throw new ExceptionConflict("El RUT '" + trabajadorDTO.getRut() + "' ya pertenece a otro trabajador registrado.");
        }
        if (trabajadorRepository.existsByCorreo(trabajadorDTO.getCorreo())
        && !trabajadorExistente.getCorreo().equals(trabajadorDTO.getCorreo())){
            throw new ExceptionConflict("El CORREO '" + trabajadorDTO.getCorreo() + "' ya pertenece a otro trabajador registrado.");
        }
        trabajadorExistente.setRut(trabajadorDTO.getRut());
        trabajadorExistente.setNombre(trabajadorDTO.getNombre());
        trabajadorExistente.setCorreo(trabajadorDTO.getCorreo());
        trabajadorExistente.setRol(trabajadorDTO.getRol());
        trabajadorExistente.setEdad(trabajadorDTO.getEdad());
        return trabajadorRepository.save(trabajadorExistente);
    }
    // Eliminar trabajador
    public void eliminarTrabajador(Long id){
        log.info("Iniciando eliminacion de trabajador con ID {}", id);
        if (!trabajadorRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        trabajadorRepository.deleteById(id);
    }
    // Asignar turno a trabajador
    public Trabajador asignarTurno(Long trabajadorId, Long turnoId){
        log.info("Iniciando asignacion vinculo de  turno ID {} al trabajador con ID {}", turnoId, trabajadorId);
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId).orElseThrow(() -> new RuntimeException("Trabajador no encontrado con ID: " + trabajadorId));
        TurnoTrabajador turno = turnoRepository.findById(turnoId).orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnoId));
        trabajador.setTurno(turno);
        return trabajadorRepository.save(trabajador);
    }
}