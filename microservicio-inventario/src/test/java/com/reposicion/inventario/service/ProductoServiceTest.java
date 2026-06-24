package com.reposicion.inventario.service;

import com.reposicion.inventario.dto.ProductoDTO;
import com.reposicion.inventario.excepciones.ExceptionConflict;
import com.reposicion.inventario.model.Categoria;
import com.reposicion.inventario.model.Producto;
import com.reposicion.inventario.repository.CategoriaRepository;
import com.reposicion.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    //--Test crear producto
    @Test
    @DisplayName("Debe registrar un producto si los datos son correctos.")
    void cuandoCrearProducto_entoncesRetornarProducto() {
//--GIVEN
        Categoria categoriaSimulada = crearCategoriaSimulada();
        ProductoDTO dtoEntrada = crearProductoDTOValido();
        Producto productoGuardado = crearProductoSimulado(categoriaSimulada);

        //--Simulamos que el código de barra no existe
        when(productoRepository.existsByCodigoBarra(dtoEntrada.getCodigoBarra())).thenReturn(false);
        //--Simulamos que encuentra la categoría
        when(categoriaRepository.findById(dtoEntrada.getCategoriaId())).thenReturn(Optional.of(categoriaSimulada));
        //--Simulamos el guardado
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

//--WHEN
        Producto resultado = productoService.crearProducto(dtoEntrada);

//--THEN
        assertNotNull(resultado, "El producto retornado no debería ser nulo.");
        assertEquals(1L, resultado.getId(), "El ID debe ser 1L.");
        assertEquals("123456789", resultado.getCodigoBarra(), "El código de barra no coincide.");
        assertEquals("Galletas", resultado.getNombreProducto(), "El nombre no coincide.");
        assertNotNull(resultado.getCategoria(), "La categoría asignada no debe ser nula.");

        Mockito.verify(productoRepository, Mockito.times(1)).save(any(Producto.class));
    }
    //--Test crear producto con codigo de barra repetido
    @Test
    @DisplayName("Debe lanzar ExceptionConflict si el código de barra ya existe al crear producto.")
    void cuandoCrearProducto_CodigoBarraExiste_entoncesLanzarException() {
//--GIVEN
        ProductoDTO dtoEntrada = crearProductoDTOValido();

        //--Simulacion de la base de datos responde que existe ese codigo de barra
        when(productoRepository.existsByCodigoBarra(dtoEntrada.getCodigoBarra())).thenReturn(true);

//--WHEN & THEN
        //--assertThrows para comprobar que el servicio detiene el proceso y lanza la excepcion
        assertThrows(ExceptionConflict.class, () -> productoService.crearProducto(dtoEntrada),
                "Deberia lanzar ExceptionConflict porque el codigo de barra ya existe.");

        //--Verificacion del metodo save nunca se haya ejecutado para proteger la base de datos
        Mockito.verify(productoRepository, Mockito.never()).save(any(Producto.class));
    }

    //--Test crear producto con categoria no encontrada
    @Test
    @DisplayName("Debe lanzar RuntimeException si la categoría no existe al crear producto.")
    void cuandoCrearProducto_CategoriaNoExiste_entoncesLanzarException() {
//--GIVEN
        ProductoDTO dtoEntrada = crearProductoDTOValido();

        //--Simulacion del codigo de barra esta libre
        when(productoRepository.existsByCodigoBarra(dtoEntrada.getCodigoBarra())).thenReturn(false);

        //--Simulamos que el repositorio de categorias devuelve vacio al buscar el ID
        when(categoriaRepository.findById(dtoEntrada.getCategoriaId())).thenReturn(Optional.empty());

//--WHEN & THEN
        //--Verificamos que lance la RuntimeException que configuraste
        assertThrows(RuntimeException.class, () -> productoService.crearProducto(dtoEntrada),
                "Deberia lanzar RuntimeException porque la categoria solicitada no existe.");

        //--Verificamos nuevamente que el producto nunca se guardó
        Mockito.verify(productoRepository, Mockito.never()).save(any(Producto.class));
    }

    //--Test listar todos los productos
    @Test
    @DisplayName("Debe listar todos los productos si los datos son correctos.")
    void cuandoListarTodosProducto_entoncesRetornarLista() {
//--GIVEN
        Categoria categoria = crearCategoriaSimulada();
        Producto prod1 = crearProductoSimulado(categoria);

        Producto prod2 = new Producto();
        prod2.setId(2L);
        prod2.setNombreProducto("Bebida");
        prod2.setCodigoBarra("987654321");

        List<Producto> listaSimulada = List.of(prod1, prod2);

        when(productoRepository.findAll()).thenReturn(listaSimulada);

//--WHEN
        List<Producto> resultado = productoService.listarTodosProducto();

//--THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "La lista debe contener exactamente 2 productos.");
        assertEquals("Galletas", resultado.get(0).getNombreProducto());
        assertEquals("Bebida", resultado.get(1).getNombreProducto());

        Mockito.verify(productoRepository, Mockito.times(1)).findAll();
    }

    //--Test listar un producto especifico
    @Test
    @DisplayName("Debe obtener un producto por su ID.")
    void cuandoListarUnSoloProducto_entoncesRetornarProducto() {
//--GIVEN
        Long idBuscado = 1L;
        Producto productoSimulado = crearProductoSimulado(crearCategoriaSimulada());

        when(productoRepository.findById(idBuscado)).thenReturn(Optional.of(productoSimulado));

//--WHEN
        Producto resultado = productoService.listarUnSoloProducto(idBuscado);

//--THEN
        assertNotNull(resultado);
        assertEquals(idBuscado, resultado.getId());
        assertEquals("123456789", resultado.getCodigoBarra());

        Mockito.verify(productoRepository, Mockito.times(1)).findById(idBuscado);
    }

    //--Test actualizar producto
    @Test
    @DisplayName("Debe actualizar un producto si los datos son correctos.")
    void cuandoActualizarProducto_entoncesRetornarProductoActualizado() {
//--GIVEN
        Long idExistente = 1L;
        Categoria categoria = crearCategoriaSimulada();

        Producto productoAntes = crearProductoSimulado(categoria);

        ProductoDTO dtoActualizacion = crearProductoDTOValido();
        dtoActualizacion.setNombreProducto("Galletas de Chocolate");
        dtoActualizacion.setStock(150);

        Producto productoDespues = crearProductoSimulado(categoria);
        productoDespues.setNombreProducto("Galletas de Chocolate");
        productoDespues.setStock(150);

        when(productoRepository.findById(idExistente)).thenReturn(Optional.of(productoAntes));
        when(productoRepository.existsByCodigoBarra(dtoActualizacion.getCodigoBarra())).thenReturn(false);
        when(categoriaRepository.findById(dtoActualizacion.getCategoriaId())).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoDespues);

//--WHEN
        Producto resultado = productoService.actualizarProducto(idExistente, dtoActualizacion);

//--THEN
        assertNotNull(resultado);
        assertEquals("Galletas de Chocolate", resultado.getNombreProducto(), "El nombre no se actualizó.");
        assertEquals(150, resultado.getStock(), "El stock no se actualizó.");

        Mockito.verify(productoRepository, Mockito.times(1)).save(any(Producto.class));
    }
    //--Test actualizar producto con producto no encontrado
    @Test
    @DisplayName("Debe lanzar RuntimeException si el producto a actualizar no existe.")
    void cuandoActualizarProducto_IdNoExiste_entoncesLanzarException() {
//--GIVEN
        Long idInexistente = 99L;
        ProductoDTO dto = crearProductoDTOValido();
        when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

//--WHEN & THEN
        assertThrows(RuntimeException.class, () -> productoService.actualizarProducto(idInexistente, dto));
        Mockito.verify(productoRepository, Mockito.never()).save(any(Producto.class));
    }

    //--Test actualizar producto con codigo de barra ya en uso por otro
    @Test
    @DisplayName("Debe lanzar ExceptionConflict si el código de barra ya pertenece a otro producto.")
    void cuandoActualizarProducto_CodigoBarraYaEnUso_entoncesLanzarException() {
//--GIVEN
        Long idExistente = 1L;
        Producto prodExistente = crearProductoSimulado(crearCategoriaSimulada());
        prodExistente.setCodigoBarra("ORIGINAL");

        ProductoDTO dtoActualizacion = crearProductoDTOValido();
        dtoActualizacion.setCodigoBarra("DUPLICADO");

        when(productoRepository.findById(idExistente)).thenReturn(Optional.of(prodExistente));
        //--Simulacion del nuevo codigo de barra ya existe en otro registro
        when(productoRepository.existsByCodigoBarra("DUPLICADO")).thenReturn(true);

//--WHEN & THEN
        assertThrows(ExceptionConflict.class, () -> productoService.actualizarProducto(idExistente, dtoActualizacion));
        Mockito.verify(productoRepository, Mockito.never()).save(any(Producto.class));
    }

    //--Test actualizar producto con Categoria no existente
    @Test
    @DisplayName("Debe lanzar RuntimeException si la nueva categoría no existe.")
    void cuandoActualizarProducto_CategoriaNoExiste_entoncesLanzarException() {
//--GIVEN
        Long idExistente = 1L;
        Producto prodExistente = crearProductoSimulado(crearCategoriaSimulada());

        ProductoDTO dtoActualizacion = crearProductoDTOValido();
        dtoActualizacion.setCategoriaId(999L); // ID inexistente

        when(productoRepository.findById(idExistente)).thenReturn(Optional.of(prodExistente));
        when(productoRepository.existsByCodigoBarra(any())).thenReturn(false);
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

//--WHEN & THEN
        assertThrows(RuntimeException.class, () -> productoService.actualizarProducto(idExistente, dtoActualizacion));
    }

    //--Test eliminar producto
    @Test
    @DisplayName("Debe eliminar un producto si el ID existe.")
    void cuandoEliminarProducto_entoncesEjecutarBorrado() {
//--GIVEN
        Long idAEliminar = 1L;
        when(productoRepository.existsById(idAEliminar)).thenReturn(true);

//--WHEN
        productoService.eliminarProducto(idAEliminar);

//--THEN
        Mockito.verify(productoRepository, Mockito.times(1)).deleteById(idAEliminar);
    }
    //--Test eliminar producto con ID no existente
    @Test
    @DisplayName("Debe lanzar RuntimeException si el ID del producto no existe al eliminar.")
    void cuandoEliminarProducto_IdNoExiste_entoncesLanzarException() {
//--GIVEN
        Long idInexistente = 99L;
        //--Simulamos que el repositorio responde 'false' (el producto no existe en BD)
        when(productoRepository.existsById(idInexistente)).thenReturn(false);

//--WHEN & THEN
        //--Verificacion del servicio lance la excepción correctamente
        assertThrows(RuntimeException.class, () -> productoService.eliminarProducto(idInexistente),
                "Deberia lanzar RuntimeException porque el producto no existe.");

        //--Verificacion: se asegura que no se llamo a deleteById
        Mockito.verify(productoRepository, Mockito.never()).deleteById(idInexistente);
    }

    //--Metodos helper
    private Categoria crearCategoriaSimulada() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombreCategoria("Abarrotes");
        return categoria;
    }

    private ProductoDTO crearProductoDTOValido() {
        ProductoDTO dto = new ProductoDTO();
        dto.setCodigoBarra("123456789");
        dto.setNombreProducto("Galletas");
        dto.setStock(100);
        dto.setDescripcionProducto("Galletas dulces");
        dto.setFechaEntradaProducto(LocalDate.now());
        dto.setCategoriaId(1L);
        return dto;
    }

    private Producto crearProductoSimulado(Categoria categoria) {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setCodigoBarra("123456789");
        producto.setNombreProducto("Galletas");
        producto.setStock(100);
        producto.setDescripcionProducto("Galletas dulces");
        producto.setFechaEntradaProducto(LocalDate.now());
        producto.setCategoria(categoria);
        return producto;
    }
}