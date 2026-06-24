package com.reposicion.identidad.service;


import com.reposicion.identidad.dto.TurnoDTO;
import com.reposicion.identidad.excepciones.ExceptionConflict;
import com.reposicion.identidad.model.TurnoTrabajador;
import com.reposicion.identidad.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;

    // Crear turno
    public TurnoTrabajador crearTurno(TurnoDTO turnoDTO){
        log.info("Iniciando creacion de turno... Horario solicitado: {} - {}", turnoDTO.getHoraInicio(), turnoDTO.getHoraTermino());
        if (turnoDTO.getHoraTermino().isBefore(turnoDTO.getHoraInicio()) ||
            turnoDTO.getHoraTermino().equals(turnoDTO.getHoraInicio())){
            log.warn("Se rechazo la creacion del turno: La hora de término ({}) es anterior o igual a la de inicio ({}) ", turnoDTO.getHoraTermino(), turnoDTO.getHoraInicio());
            throw  new ExceptionConflict("La hora de termino no puede ser anterior o igual a la hora de inicio.");
        }
        if (turnoRepository.existsByTipoTurnoAndHoraInicioAndHoraTermino(
                turnoDTO.getTipoTurno(), turnoDTO.getHoraInicio(), turnoDTO.getHoraTermino())){
            throw new ExceptionConflict("Ya existe un turno " + turnoDTO.getTipoTurno() + " con el mismo horario de entrada y de salida.");
        }
        TurnoTrabajador nuevoTurno = new TurnoTrabajador();
        nuevoTurno.setTipoTurno(turnoDTO.getTipoTurno());
        nuevoTurno.setHoraInicio(turnoDTO.getHoraInicio());
        nuevoTurno.setHoraTermino(turnoDTO.getHoraTermino());
        TurnoTrabajador guardado = turnoRepository.save(nuevoTurno);
        log.info("Turno creado exitosamente en la base de datos con ID: {}", guardado.getTipoTurno());
    return guardado;
    }

    // Listar todos los turnos
    public List<TurnoTrabajador> listarTodosTurnos(){
        log.debug("Iniciando busqueda de todos los turnos");
        return turnoRepository.findAll();
    }
    // Obtener un turno en especifico
    public TurnoTrabajador obtenerUnSoloTurno(Long id){
        log.debug("Iniciando busqueda de turno con ID {}", id);
        return turnoRepository.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado " + id));
    }
    // Actualizar turno
    public TurnoTrabajador actualizarTurno(Long id, TurnoDTO turnoDTO){
        log.info("Iniciando actualizacion de turno con ID {}", id);
        TurnoTrabajador turnoExistente = turnoRepository.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + id));
        if (turnoDTO.getHoraTermino().isBefore(turnoDTO.getHoraInicio()) ||
                turnoDTO.getHoraTermino().equals(turnoDTO.getHoraInicio())) {
            throw new ExceptionConflict("La hora de termino no puede ser anterior o igual a la hora de inicio.");
        }
        TurnoTrabajador turnoDuplicado = turnoRepository.findByTipoTurnoAndHoraInicioAndHoraTermino(
                turnoDTO.getTipoTurno(), turnoDTO.getHoraInicio(), turnoDTO.getHoraTermino());
        if (turnoDuplicado != null && !turnoDuplicado.getId().equals(id)) {
            throw new ExceptionConflict("Ya existe otro turno '" + turnoDTO.getTipoTurno() + "' con el mismo horario de entrada y salida.");
        }
        turnoExistente.setTipoTurno(turnoDTO.getTipoTurno());
        turnoExistente.setHoraInicio(turnoDTO.getHoraInicio());
        turnoExistente.setHoraTermino(turnoDTO.getHoraTermino());
        return turnoRepository.save(turnoExistente);
    }
    // Eliminar turno
    public void eliminarTurno(Long id){
        log.info("Iniciando eliminacion de turno con ID {}", id);
        if (!turnoRepository.existsById(id)){
            throw new RuntimeException("Turno no encontrado con ID: " + id);
        }
        turnoRepository.deleteById(id);
    }
}
