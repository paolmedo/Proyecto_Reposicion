package com.reposicion.inventario.service;

import com.reposicion.inventario.dto.CategoriaDTO;
import com.reposicion.inventario.model.Categoria;
import com.reposicion.inventario.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    //--Test crear categoria
    @Test
    @DisplayName("Debe registrar una categoria si los datos son correctos.")
    void cuandoCrearCategoria_entoncesRetornarCategoria() {
//--GIVEN
        CategoriaDTO dtoEntrada = crearCategoriaDTOValido();
        Categoria categoriaGuardada = crearCategoriaSimulada();

        //--Simulamos que el nombre de la categoría aún no existe
        when(categoriaRepository.existsByNombreCategoria(dtoEntrada.getNombreCategoria())).thenReturn(false);
        //--Simulamos el guardado exitoso
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaGuardada);

//--WHEN
        Categoria resultado = categoriaService.crearCategoria(dtoEntrada);
//--THEN
        assertNotNull(resultado, "La categoria retornada no debería ser nula.");
        assertEquals(1L, resultado.getId(), "El ID debe ser 1L.");
        assertEquals("Lácteos", resultado.getNombreCategoria(), "El nombre de la categoria no coincide.");
        assertEquals("Productos derivados de la leche", resultado.getDescripcionCategoria(), "La descripción no coincide.");

        Mockito.verify(categoriaRepository, Mockito.times(1)).save(any(Categoria.class));
    }

    //--Test listar todas las categorias
    @Test
    @DisplayName("Debe listar todas las categorias si los datos son correctos.")
    void cuandoListarCategorias_entoncesRetornarLista() {
//--GIVEN
        Categoria cat1 = crearCategoriaSimulada();

        Categoria cat2 = new Categoria();
        cat2.setId(2L);
        cat2.setNombreCategoria("Carnes");
        cat2.setDescripcionCategoria("Cortes de vacuno y pollo");

        List<Categoria> listaSimulada = List.of(cat1, cat2);

        when(categoriaRepository.findAll()).thenReturn(listaSimulada);

//--WHEN
        List<Categoria> resultado = categoriaService.listarCategorias();

//--THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "La lista debe contener exactamente 2 categorias.");
        assertEquals("Lácteos", resultado.get(0).getNombreCategoria());
        assertEquals("Carnes", resultado.get(1).getNombreCategoria());

        Mockito.verify(categoriaRepository, Mockito.times(1)).findAll();
    }

    //--Test listar una categoria especifica
    @Test
    @DisplayName("Debe obtener una categoria por su ID.")
    void cuandoListarUnaCategoria_entoncesRetornarCategoria() {
//--GIVEN
        Long idBuscado = 1L;
        Categoria categoriaSimulada = crearCategoriaSimulada();

        when(categoriaRepository.findById(idBuscado)).thenReturn(Optional.of(categoriaSimulada));

//--WHEN
        Categoria resultado = categoriaService.listarUnaCategoria(idBuscado);
//--THEN
        assertNotNull(resultado);
        assertEquals(idBuscado, resultado.getId());
        assertEquals("Lácteos", resultado.getNombreCategoria());

        Mockito.verify(categoriaRepository, Mockito.times(1)).findById(idBuscado);
    }

    //-- Test actualizar categoria
    @Test
    @DisplayName("Debe actualizar una categoria si los datos son correctos.")
    void cuandoActualizarUnaCategoria_entoncesRetornarCategoriaActualizada() {
//--GIVEN
        Long idExistente = 1L;

        Categoria categoriaAntes = crearCategoriaSimulada();

        CategoriaDTO dtoActualizacion = new CategoriaDTO();
        dtoActualizacion.setNombreCategoria("Lácteos y Quesos");
        dtoActualizacion.setDescripcionCategoria("Todo tipo de derivados lácteos");

        Categoria categoriaDespues = new Categoria();
        categoriaDespues.setId(idExistente);
        categoriaDespues.setNombreCategoria(dtoActualizacion.getNombreCategoria());
        categoriaDespues.setDescripcionCategoria(dtoActualizacion.getDescripcionCategoria());

        when(categoriaRepository.findById(idExistente)).thenReturn(Optional.of(categoriaAntes));
        when(categoriaRepository.existsByNombreCategoria(dtoActualizacion.getNombreCategoria())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaDespues);

//--WHEN
        Categoria resultado = categoriaService.actualizarUnaCategoria(idExistente, dtoActualizacion);

//--THEN
        assertNotNull(resultado);
        assertEquals("Lácteos y Quesos", resultado.getNombreCategoria(), "El nombre no se actualizó.");
        assertEquals("Todo tipo de derivados lácteos", resultado.getDescripcionCategoria(), "La descripción no se actualizó.");

        Mockito.verify(categoriaRepository, Mockito.times(1)).save(any(Categoria.class));
    }

    //--Test eliminar categoría
    @Test
    @DisplayName("Debe eliminar una categoria si el ID existe.")
    void cuandoEliminarCategoria_entoncesEjecutarBorrado() {
//--GIVEN
        Long idAEliminar = 1L;
        when(categoriaRepository.existsById(idAEliminar)).thenReturn(true);

//--WHEN
        categoriaService.eliminarCategoria(idAEliminar);

//--THEN
        Mockito.verify(categoriaRepository, Mockito.times(1)).deleteById(idAEliminar);
    }

    //--Metodos helper
    private CategoriaDTO crearCategoriaDTOValido() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombreCategoria("Lácteos");
        dto.setDescripcionCategoria("Productos derivados de la leche");
        return dto;
    }

    private Categoria crearCategoriaSimulada() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombreCategoria("Lácteos");
        categoria.setDescripcionCategoria("Productos derivados de la leche");
        return categoria;
    }
}