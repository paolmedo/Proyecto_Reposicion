package com.reposicion.identidad.service;

import com.reposicion.identidad.dto.TrabajadorDTO;
import com.reposicion.identidad.excepciones.ExceptionConflict;
import com.reposicion.identidad.model.Trabajador;
import com.reposicion.identidad.model.TurnoTrabajador;
import com.reposicion.identidad.repository.TrabajadorRepository;
import com.reposicion.identidad.repository.TurnoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class TrabajadorServiceTest {

    //--Crear repositorio falso del trabajador.
    @Mock
    private TrabajadorRepository trabajadorRepository;
    //--Crear repositorio falso del turno.
    @Mock
    private TurnoRepository turnoRepository;
    //--Crear service falso del trabajador.
    @InjectMocks
    private TrabajadorService trabajadorService;

    //--Test crear un trabajador
    @Test
    @DisplayName("Debe registrar un trabajador si los datos son correctos y validos.")
    void cuandoGuardarTrabajador_entoncesRetornarTrabajador() {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        TurnoTrabajador turnoSimulado = crearTurnoSimulado();
        TrabajadorDTO trabajadorEntradaDTO = crearTrabajadorDTOSimulado();
        Trabajador trabajadorGuardado = crearTrabajadorSimulado(turnoSimulado);
//--Configuracion del Mock
        Mockito.when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoSimulado));
//--Indicamos al repositorio falso (trabajadorService) que cuando reciba cualquier objeto de Trabajador en metodo .save(), devuelva 'trabajadorGuardado'.
        Mockito.when(trabajadorRepository.save(Mockito.any(Trabajador.class))).thenReturn(trabajadorGuardado);
//--WHEN(Cuando: Se ejecuta el metodo real en el service)
        Trabajador resultado = trabajadorService.guardarTrabajador(trabajadorEntradaDTO);
//--THEN(Entonces: se valida que el resultado cumpla cumpla con la estructura de la entidad Trabajador)
        assertNotNull(resultado, "El TRABAJADOR retornado no deberia ser nulo.");
        assertNotNull(resultado.getId(), "El ID generado no deberia ser nulo.");
        assertEquals(1L, resultado.getId(), "El ID generado deberia ser 1L.");
        assertEquals("20.123.456-7", resultado.getRut(), "El RUT almacenado no coincide con el ingresado.");
        assertEquals("Pavel", resultado.getNombre(), "El NOMBRE almacenado no coicide con el ingresado.");
        assertEquals("correoPavel@gmail.com", resultado.getCorreo(), "El CORREO almacenado no coicide con el ingresado.");
        assertEquals("Reponedor", resultado.getRol(), "El ROL almacenado no coicide con el ingresado.");
        assertEquals(30, resultado.getEdad(), "La EDAD almacenada no coicide con la ingresada.");
//--Relacion del turno
        assertNotNull(resultado.getTurno(), "El turno asignado no debe ser nulo.");
        assertEquals(1L, resultado.getTurno().getId(), "El ID del turno debe ser 1L.");

    }
    //--Test crear un trabajador con conflicto de rut
    @Test
    @DisplayName("No debe registrar un trabajador por conflictos con el rut.")
    void cuandoGuardarTrabajador_conRutExistente_entoncesLanzarExceptionConflict(){
//--GIVEN(Dado que: Se preparan los datos de entrada)
        TrabajadorDTO trabajadorDTO = crearTrabajadorDTOSimulado();
        // Le decimos a Mockito que simule que el RUT YA EXISTE en la base de datos
        Mockito.when(trabajadorRepository.existsByRut(trabajadorDTO.getRut())).thenReturn(true);

//--WHEN & THEN
        // assertThrows verifica que al intentar guardar, efectivamente explote con la excepción correcta
        ExceptionConflict excepcion = org.junit.jupiter.api.Assertions.assertThrows(
                ExceptionConflict.class, () -> trabajadorService.guardarTrabajador(trabajadorDTO)
        );

        // Validamos que el mensaje de la excepción sea exactamente el que programaste en el Service
        org.junit.jupiter.api.Assertions.assertEquals("El RUT '" + trabajadorDTO.getRut() + "' Ya pertenece a un trabajador registrado.", excepcion.getMessage());

        // Verificamos que el repositorio JAMÁS haya intentado hacer un .save()
        Mockito.verify(trabajadorRepository, Mockito.never()).save(Mockito.any(Trabajador.class));
    }

    //--Test crear un trabajador con conclifto de correo
    @Test
    @DisplayName("No debe registrar un trabajador por conflictos con el correo.")
    void cuandoGuardarTrabajador_conCorreoExistente_entoncesLanzarExceptionConflict(){
//--GIVEN(Dado que: Se preparan los datos de entrada)
        TrabajadorDTO trabajadorDTO = crearTrabajadorDTOSimulado();
        // Por defecto Mockito devuelve false para existsByRut, así que saltará al siguiente if.
        // Le decimos que simule que el CORREO YA EXISTE.
        Mockito.when(trabajadorRepository.existsByCorreo(trabajadorDTO.getCorreo())).thenReturn(true);

        //--WHEN & THEN
        ExceptionConflict excepcion = org.junit.jupiter.api.Assertions.assertThrows(
                ExceptionConflict.class,
                () -> trabajadorService.guardarTrabajador(trabajadorDTO)
        );

        org.junit.jupiter.api.Assertions.assertEquals("El CORREO '" + trabajadorDTO.getCorreo() + "' Ya pertenece a un trabajador registrado.", excepcion.getMessage());
        Mockito.verify(trabajadorRepository, Mockito.never()).save(Mockito.any(Trabajador.class));
    }
    //--Test crear un trabajador con turno no encontrado
    @Test
    @DisplayName("No debe registrar un trabajador porque no hay turno para vincular.")
    void cuandoGuardarTrabajador_conTurnoNoEncontrado_entoncesLanzarRunTimeException(){
//--GIVEN(Dado que: Se preparan los datos de entrada)
        TrabajadorDTO trabajadorDTO = crearTrabajadorDTOSimulado();
        // Simulamos que al buscar el turno en la BD, no encuentra nada (Optional.empty)
        Mockito.when(turnoRepository.findById(trabajadorDTO.getTurnoId())).thenReturn(Optional.empty());

        //--WHEN & THEN
        RuntimeException excepcion = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class, () -> trabajadorService.guardarTrabajador(trabajadorDTO)
        );

        org.junit.jupiter.api.Assertions.assertEquals("Turno no encontrado con ID: " + trabajadorDTO.getTurnoId(), excepcion.getMessage());
        Mockito.verify(trabajadorRepository, Mockito.never()).save(Mockito.any(Trabajador.class));

    }

    //--Test listar todos los trabajadores registrados
    @Test
    @DisplayName("Debe listar todos los trabajadores si los datos son correctos.")
    void cuandoListarTodosTrabajadores_entoncesRetornarTodosLosTrabajadores() {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        TurnoTrabajador turnoSimulado = crearTurnoSimulado();
        Trabajador trabajador1 = crearTrabajadorSimulado(turnoSimulado);
        Trabajador trabajador2 = new Trabajador();
        trabajador2.setId(2L);
        trabajador2.setRut("20.123.123-4");
        trabajador2.setNombre("Juan");
        trabajador2.setCorreo("correoJuan@gmail.com");
        trabajador2.setRol("Jefe de sala");
        trabajador2.setEdad(35);
//--Se agrupa trabajado1 y trabajador2 en una lista
        java.util.List<Trabajador> listaSimulada = java.util.List.of(trabajador1, trabajador2);
//--Configuracion del mock
        Mockito.when(trabajadorRepository.findAll()).thenReturn(listaSimulada);
//--WHEN(Cuando: Se ejecuta el metodo real del service)
        java.util.List<Trabajador> resultado = trabajadorService.listarTodosTrabajadores();
//--THEN(Entonces: se valida que la lista cumpla con las condiciones esperadas)
        assertNotNull(resultado, "La lista retornada no debería ser nula.");
        assertEquals(2, resultado.size(), "La lista debería contener exactamente 2 trabajadores.");
//--Aserciones precisas sobre el contenido para asegurar el porcentaje de logro (IE 3.1.1)
        assertEquals("Pavel", resultado.get(0).getNombre(), "El primer trabajador de la lista debe ser Pavel.");
        assertEquals("Juan", resultado.get(1).getNombre(), "El segundo trabajador de la lista debe ser Juan.");
//--Verificamos que se interactuó con el repositorio exactamente una vez
        Mockito.verify(trabajadorRepository, Mockito.times(1)).findAll();
    }

    //--Test obtener un trabajador en especifico
    @Test
    @DisplayName("Debe obtener un solo trabajador si los datos son correctos.")
    void cuandoObtenerUnSoloTrabajador_entoncesRetornarUnSoloTrabajador() {
        TurnoTrabajador turnoTrabajador = crearTurnoSimulado();
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idBuscado = 1L;
        Trabajador trabajador = crearTrabajadorSimulado(turnoTrabajador);
//--Configuracion del mock
        Mockito.when(trabajadorRepository.findById(idBuscado)).thenReturn(Optional.of(trabajador));
//--WHEN(Cuando: Se ejecuta el metodo real del service)
        Trabajador resultado = trabajadorService.obtenerUnSoloTrabajador(idBuscado);
//--THEN(Entonces: Se valida
        assertNotNull(resultado, "El TRABAJADOR retornado no deberia ser nulo.");
        assertEquals(idBuscado, resultado.getId(), "El ID del trabajador retornado debe ser 1L.");
        assertEquals("20.123.456-7", resultado.getRut(), "El RUT del trabajador no coincide.");
        assertEquals("Pavel", resultado.getNombre(), "El NOMBRE del trabajador no coincide.");
        assertEquals("correoPavel@gmail.com", resultado.getCorreo(), "El CORREO del trabajador no coincide.");
        assertEquals("Reponedor", resultado.getRol(), "El ROL del trabajador no coincide.");
        assertEquals(30, resultado.getEdad(), "La EDAD del trabajador no coincide.");
        assertNotNull(resultado.getTurno(), "El turno asociado no debería ser nulo.");
        assertEquals(1L, resultado.getTurno().getId(), "El ID del turno asociado debe ser 1L.");
        Mockito.verify(trabajadorRepository, Mockito.times(1)).findById(idBuscado);
    }

    //--Test actualizar trabajador
    @Test
    @DisplayName("Debe actualizar un trabajador si los datos son correctos.")
    void cuandoActualizarTrabajador_entoncesRetornarActualizarTrabajador() {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idExistente = 1L;
        TurnoTrabajador turnoSimulado = crearTurnoSimulado();

        Trabajador trabajadorAntesDeModificar = crearTrabajadorSimulado(turnoSimulado);

        TrabajadorDTO dtoActualizacion = crearTrabajadorDTOSimulado();
        dtoActualizacion.setNombre("Pavel Modificado");
        dtoActualizacion.setRol("Supervisor");

        Trabajador trabajadorModificado = crearTrabajadorSimulado(turnoSimulado);
        trabajadorModificado.setNombre(dtoActualizacion.getNombre());
        trabajadorModificado.setRol(dtoActualizacion.getRol());
//--Configuracion del mock
        Mockito.when(trabajadorRepository.findById(idExistente)).thenReturn(Optional.of(trabajadorAntesDeModificar));
        Mockito.when(trabajadorRepository.save(Mockito.any(Trabajador.class))).thenReturn(trabajadorModificado);
        Trabajador resultado = trabajadorService.actualizarTrabajador(idExistente, dtoActualizacion);
//--THEN(Entonces: validamos con asserts precisos que los datos cambiaron correctamente)
        assertNotNull(resultado, "El TRABAJADOR modificado no debería ser nulo.");
        assertEquals(idExistente, resultado.getId(), "El ID del trabajador debe seguir siendo 1L.");
// Verificamos que los campos realmente mutaron a los valores del DTO
        assertEquals("Pavel Modificado", resultado.getNombre(), "El NOMBRE no se actualizó correctamente.");
        assertEquals("Supervisor", resultado.getRol(), "El ROL no se actualizó correctamente.");
// Verificamos que los campos que no se debían tocar mantengan su consistencia
        assertEquals("20.123.456-7", resultado.getRut(), "El RUT no debería cambiar en una actualización.");
        assertEquals(30, resultado.getEdad(), "La EDAD no debería haber cambiado.");
// Verificaciones de Mockito para asegurar la trazabilidad del proceso
        Mockito.verify(trabajadorRepository, Mockito.times(1)).findById(idExistente);
        Mockito.verify(trabajadorRepository, Mockito.times(1)).save(Mockito.any(Trabajador.class));
    }
    //--Test actualizar trabajador con trabajador no encontrado
    @Test
    @org.junit.jupiter.api.DisplayName("Debe lanzar RuntimeException si el trabajador a actualizar no existe.")
    void cuandoActualizarTrabajador_IdNoExiste_entoncesLanzarException() {
//--GIVEN
        Long idInexistente = 99L;
        TrabajadorDTO dtoActualizacion = crearTrabajadorDTOSimulado();

        //--Simulamos que la base de datos no encuentra a nadie con ese ID
        Mockito.when(trabajadorRepository.findById(idInexistente)).thenReturn(java.util.Optional.empty());

//--WHEN & THEN
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> trabajadorService.actualizarTrabajador(idInexistente, dtoActualizacion),
                "Deberia lanzar RuntimeException al no encontrar el ID.");

        Mockito.verify(trabajadorRepository, Mockito.never()).save(Mockito.any(Trabajador.class));
    }

    //--Test actualizar trabajador con RUT ya en uso por otro trabajador
    @Test
    @org.junit.jupiter.api.DisplayName("Debe lanzar ExceptionConflict si el nuevo RUT ya pertenece a otro trabajador.")
    void cuandoActualizarTrabajador_RutYaEnUso_entoncesLanzarException() {
//--GIVEN
        Long idExistente = 1L;
        //--El trabajador en la base de datos tiene un RUT original
        Trabajador trabajadorAntes = crearTrabajadorSimulado(crearTurnoSimulado());
        trabajadorAntes.setRut("11.111.111-1");

        //--El DTO trae un RUT diferente ("20.123.456-7")
        TrabajadorDTO dtoActualizacion = crearTrabajadorDTOSimulado();

        Mockito.when(trabajadorRepository.findById(idExistente)).thenReturn(java.util.Optional.of(trabajadorAntes));

        //--Simulacion del nuevo RUT del DTO YA existe en la base de datos
        Mockito.when(trabajadorRepository.existsByRut(dtoActualizacion.getRut())).thenReturn(true);

//--WHEN & THEN
        org.junit.jupiter.api.Assertions.assertThrows(com.reposicion.identidad.excepciones.ExceptionConflict.class,
                () -> trabajadorService.actualizarTrabajador(idExistente, dtoActualizacion),
                "Debería lanzar ExceptionConflict porque el RUT ya está en uso.");

        Mockito.verify(trabajadorRepository, Mockito.never()).save(Mockito.any(Trabajador.class));
    }

    //--Test actualizar trabajador con CORREO ya en uso por otro trabajador
    @Test
    @org.junit.jupiter.api.DisplayName("Debe lanzar ExceptionConflict si el nuevo CORREO ya pertenece a otro trabajador.")
    void cuandoActualizarTrabajador_CorreoYaEnUso_entoncesLanzarException() {
//--GIVEN
        Long idExistente = 1L;
        //--El trabajador original
        Trabajador trabajadorAntes = crearTrabajadorSimulado(crearTurnoSimulado());
        trabajadorAntes.setRut("11.111.111-1");
        trabajadorAntes.setCorreo("correoOriginal@gmail.com");

        TrabajadorDTO dtoActualizacion = crearTrabajadorDTOSimulado();
        dtoActualizacion.setRut("11.111.111-1");
        dtoActualizacion.setCorreo("correoNuevoEnUso@gmail.com"); //--Diferente correo

        Mockito.when(trabajadorRepository.findById(idExistente)).thenReturn(java.util.Optional.of(trabajadorAntes));
        //--El RUT no es problema porque es igual
        Mockito.when(trabajadorRepository.existsByRut(dtoActualizacion.getRut())).thenReturn(false);
        //--El correo si existe en la base de datos
        Mockito.when(trabajadorRepository.existsByCorreo(dtoActualizacion.getCorreo())).thenReturn(true);

//--WHEN & THEN
        org.junit.jupiter.api.Assertions.assertThrows(com.reposicion.identidad.excepciones.ExceptionConflict.class, () -> trabajadorService.actualizarTrabajador(idExistente, dtoActualizacion),
                "Debería lanzar ExceptionConflict porque el CORREO ya está en uso.");

        Mockito.verify(trabajadorRepository, Mockito.never()).save(Mockito.any(Trabajador.class));
    }

    //--Test eliminar trabajador
    @Test
    @DisplayName("Debe eliminar un trabajador si los datos son correctos.")
    void cuandoEliminarTrabajador_entoncesEliminarTrabajador() {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idAEliminar = 1L;
        // Solo necesitamos simular que el ID sí existe en la base de datos
        Mockito.when(trabajadorRepository.existsById(idAEliminar)).thenReturn(true);
//--WHEN
        trabajadorService.eliminarTrabajador(idAEliminar);
//--THEN
        // Verificamos que se ejecutó la orden de borrado EXACTAMENTE una vez usando deleteById
        Mockito.verify(trabajadorRepository, Mockito.times(1)).deleteById(idAEliminar);
    }

    //--Test asignar turno a trabajador
    @Test
    @DisplayName("Debe asignar un turno a un trabajador si los datos son correctos.")
    void cuandoAsignarTurno_entoncesTurnoAsignado() {
//--GIVEN(Dado que: Se preparan los datos de entrada)
        Long idTrabajador = 1L;
        Long idNuevoTurno = 2L; // Un ID distinto para probar el cambio real
        //--Metodos helper
        TurnoTrabajador turnoOriginal = crearTurnoSimulado(); // Tiene ID 1L por defecto
        Trabajador trabajadorSimulado = crearTrabajadorSimulado(turnoOriginal);

        //--Creacion de turno
        TurnoTrabajador turnoNuevo = new TurnoTrabajador();
        turnoNuevo.setId(idNuevoTurno);
        turnoNuevo.setTipoTurno("Tarde");
        //--Configuramos los Mocks para que el servicio encuentre ambas entidades
        Mockito.when(trabajadorRepository.findById(idTrabajador)).thenReturn(Optional.of(trabajadorSimulado));
        Mockito.when(turnoRepository.findById(idNuevoTurno)).thenReturn(Optional.of(turnoNuevo));
        //--Simulacion del guardado final
        Mockito.when(trabajadorRepository.save(Mockito.any(Trabajador.class))).thenReturn(trabajadorSimulado);
//--WHEN(Cuando: Se ejecuta la lógica en el service)
        Trabajador resultado = trabajadorService.asignarTurno(idTrabajador, idNuevoTurno);
//--THEN(Entonces: validacion que la llave foranea se haya actualizado correctamente)
        assertNotNull(resultado, "El TRABAJADOR con el nuevo turno no deberia ser nulo.");
        //--Verificamos que el trabajador sigue siendo el mismo
        assertEquals(idTrabajador, resultado.getId(), "El ID del trabajador no debio cambiar.");
        assertEquals("Pavel", resultado.getNombre(), "El nombre del trabajador debe mantenerse íntegro.");
        //--Validacion que el ID del turno asociado ahora es 2L y no 1L
        assertNotNull(resultado.getTurno(), "El nuevo turno asignado no deberia ser nulo.");
        assertEquals(idNuevoTurno, resultado.getTurno().getId(), "El ID del turno asociado no se actualizo al nuevo valor de 2L.");
        //--Verificacion de la trazabilidad de los repositorios
        Mockito.verify(trabajadorRepository, Mockito.times(1)).findById(idTrabajador);
        Mockito.verify(turnoRepository, Mockito.times(1)).findById(idNuevoTurno);
        Mockito.verify(trabajadorRepository, Mockito.times(1)).save(trabajadorSimulado);
    }


    //--Metodos helper (Reutilizacion de datos)
    private TurnoTrabajador crearTurnoSimulado() {
        TurnoTrabajador turno = new TurnoTrabajador();
        turno.setId(1L);
        return turno;
    }

    private TrabajadorDTO crearTrabajadorDTOSimulado() {
        TrabajadorDTO trabajadorDTO = new TrabajadorDTO();
        trabajadorDTO.setRut("20.123.456-7");
        trabajadorDTO.setNombre("Pavel");
        trabajadorDTO.setCorreo("correoPavel@gmail.com");
        trabajadorDTO.setRol("Reponedor");
        trabajadorDTO.setEdad(30);
        trabajadorDTO.setTurnoId(1L);
        return trabajadorDTO;
    }

    private Trabajador crearTrabajadorSimulado(TurnoTrabajador turno) {
        Trabajador trabajador = new Trabajador();
        trabajador.setId(1L);
        trabajador.setRut("20.123.456-7");
        trabajador.setNombre("Pavel");
        trabajador.setCorreo("correoPavel@gmail.com");
        trabajador.setRol("Reponedor");
        trabajador.setEdad(30);
        trabajador.setTurno(turno);
        return trabajador;
    }
}