package com.reposicion.identidad.service;

import com.reposicion.identidad.dto.TurnoDTO;
import com.reposicion.identidad.model.TurnoTrabajador;
import com.reposicion.identidad.repository.TurnoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TurnoServiceTest {

    //--Crear un service del turno falso
    @InjectMocks
    private TurnoService service;
    //--Crear un repository del turno falso
    @Mock
    private TurnoRepository repository;

    //--Test crear turno
    @Test
    @DisplayName("Debe registrar un turno si los datos son correctos.")
    void cuandoCrearTurno_entoncesRetornarTurnoTrabajador(){
//--GIVEN(Dado que: Se preparan los datos de entrada)
    TurnoTrabajador turnoTrabajadorSimulado = crearTurnoSimulado(1L);
    TurnoDTO turnoDTOSimulado = crearTurnoDTOValido();
        //--Configuración del Mock
        //--El repositorio falso intentara guardar cualquier Turno y debe devolver el simulado
        Mockito.when(repository.save(Mockito.any(TurnoTrabajador.class))).thenReturn(turnoTrabajadorSimulado);

//--WHEN(Cuando: Se ejecuta el metodo real en el service)
        TurnoTrabajador resultado = service.crearTurno(turnoDTOSimulado);

//--THEN(Entonces: Se validan los resultados precisos)
        org.junit.jupiter.api.Assertions.assertNotNull(resultado, "El turno retornado no debería ser nulo.");
        org.junit.jupiter.api.Assertions.assertEquals(1L, resultado.getId(), "El ID generado debería ser 1L.");
        org.junit.jupiter.api.Assertions.assertEquals("Mañana", resultado.getTipoTurno(), "El tipo de turno no coincide con el ingresado.");

        //--Verificamos la trazabilidad: que el metodo save se llamo exactamente una vez
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(TurnoTrabajador.class));

    }

    //--Test listar todos los turno
    @Test
    @DisplayName("Debe listar todos los turno si los datos son correctos.")
        void cuandoListarTodosTurnos_entoncesTurnosListados(){
//--GIVEN(Dado que: Se preparan los datos de entrada)
        TurnoTrabajador turno1 = crearTurnoSimulado(1L);

        //--Creacion de turno extra
        TurnoTrabajador turno2 = new TurnoTrabajador();
        turno2.setId(2L);
        turno2.setTipoTurno("Tarde");
        turno2.setHoraInicio(java.time.LocalTime.of(16, 0));
        turno2.setHoraTermino(java.time.LocalTime.of(23, 59));

        //--Agrupados ambos en una lista
        java.util.List<TurnoTrabajador> listaSimulada = java.util.List.of(turno1, turno2);

        //--Configuración del Mock
        Mockito.when(repository.findAll()).thenReturn(listaSimulada);

//--WHEN(Cuando: Se ejecuta el metodo real en el service)
        java.util.List<TurnoTrabajador> resultado = service.listarTodosTurnos();

//--THEN(Entonces: Se validan los resultados precisos)
        org.junit.jupiter.api.Assertions.assertNotNull(resultado, "La lista retornada no debería ser nula.");
        org.junit.jupiter.api.Assertions.assertEquals(2, resultado.size(), "La lista debería contener exactamente 2 turnos.");

        //--Validamos que el contenido sea exactamente el simulado
        org.junit.jupiter.api.Assertions.assertEquals("Mañana", resultado.get(0).getTipoTurno(), "El primer turno debe ser de Mañana.");
        org.junit.jupiter.api.Assertions.assertEquals("Tarde", resultado.get(1).getTipoTurno(), "El segundo turno debe ser de Tarde.");

        //--Verificamos que se interactuo con el repositorio exactamente una vez
        Mockito.verify(repository, Mockito.times(1)).findAll();

        }
    //--Test obtener un turno en especifico
    @Test
    @DisplayName("Debe obtener un turno si los datos son correctos.")
    void cuandoObtenerUnSoloTurno_entoncesTurnoObtenido(){
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idBuscado = 1L;
        TurnoTrabajador turnoSimulado = crearTurnoSimulado(idBuscado);

        //--Configuración del Mock
        //--Simulacion que al buscar por el ID 1L, la base de datos lo encuentra y lo devuelve dentro de un Optional
        Mockito.when(repository.findById(idBuscado)).thenReturn(java.util.Optional.of(turnoSimulado));

//--WHEN(Cuando: Se ejecuta el metodo real en el service)
        TurnoTrabajador resultado = service.obtenerUnSoloTurno(idBuscado);

        //--THEN (Entonces: Se validan los resultados precisos)
        org.junit.jupiter.api.Assertions.assertNotNull(resultado, "El turno retornado no debería ser nulo.");
        org.junit.jupiter.api.Assertions.assertEquals(idBuscado, resultado.getId(), "El ID del turno retornado no coincide con el buscado.");
        org.junit.jupiter.api.Assertions.assertEquals("Mañana", resultado.getTipoTurno(), "El tipo de turno no coincide con el esperado.");

        //--Verificamos la trazabilidad: que el metodo findById se llamó exactamente una vez
        Mockito.verify(repository, Mockito.times(1)).findById(idBuscado);

    }
    //--Test actualizar turno
    @Test
    @DisplayName("Debe actualizar un turno si los datos son correctos.")
    void cuandoActualizarTurnos_entoncesTurnoActualizado(){
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idExistente = 1L;
        //--Creamos el turno original
        TurnoTrabajador turnoAntesDeModificar = crearTurnoSimulado(idExistente);

        //--Creamos el DTO con los nuevos datos
        TurnoDTO dtoActualizacion = new TurnoDTO();
        dtoActualizacion.setTipoTurno("Tarde");
        dtoActualizacion.setHoraInicio(java.time.LocalTime.of(14, 0));
        dtoActualizacion.setHoraTermino(java.time.LocalTime.of(22, 0));

        //--Como deberia quedar el turno
        TurnoTrabajador turnoModificado = new TurnoTrabajador();
        turnoModificado.setId(idExistente);
        turnoModificado.setTipoTurno(dtoActualizacion.getTipoTurno());
        turnoModificado.setHoraInicio(dtoActualizacion.getHoraInicio());
        turnoModificado.setHoraTermino(dtoActualizacion.getHoraTermino());

        //--Configuración del Mock
        Mockito.when(repository.findById(idExistente)).thenReturn(java.util.Optional.of(turnoAntesDeModificar));
        Mockito.when(repository.save(Mockito.any(TurnoTrabajador.class))).thenReturn(turnoModificado);

//--WHEN
        TurnoTrabajador resultado = service.actualizarTurno(idExistente, dtoActualizacion);

//--THEN
        org.junit.jupiter.api.Assertions.assertNotNull(resultado, "El turno modificado no debería ser nulo.");
        org.junit.jupiter.api.Assertions.assertEquals(idExistente, resultado.getId(), "El ID no debe cambiar.");
        org.junit.jupiter.api.Assertions.assertEquals("Tarde", resultado.getTipoTurno(), "El tipo de turno no se actualizó.");

        Mockito.verify(repository, Mockito.times(1)).findById(idExistente);
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(TurnoTrabajador.class));

    }
    //--Test eliminar turno
    @Test
    @DisplayName("Debe eliminar un turno si los datos son correctos.")
    void cuandoEliminarTurno_entoncesTurnoEliminado(){
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idAEliminar = 1L;
        // Simulamos que el turno si existe para que no arroje excepción
        Mockito.when(repository.existsById(idAEliminar)).thenReturn(true);
//--WHEN
        service.eliminarTurno(idAEliminar);
//--THEN
        // Verificacion de ejecutó de la orden de borrado una vez
        Mockito.verify(repository, Mockito.times(1)).deleteById(idAEliminar);

    }

    //--Metodos helper
    private TurnoTrabajador crearTurnoSimulado(Long id) {
        TurnoTrabajador turno = new TurnoTrabajador();
        turno.setId(1L);
        turno.setTipoTurno("Mañana");
        turno.setHoraInicio(java.time.LocalTime.of(8, 0));
        turno.setHoraTermino(java.time.LocalTime.of(16, 0));
        return turno;
    }

    private TurnoDTO crearTurnoDTOValido() {
        TurnoDTO dto = new TurnoDTO();
        dto.setTipoTurno("Mañana");
        dto.setHoraInicio(java.time.LocalTime.of(8, 0));
        dto.setHoraTermino(java.time.LocalTime.of(16, 0));
        return dto;
    }
}
