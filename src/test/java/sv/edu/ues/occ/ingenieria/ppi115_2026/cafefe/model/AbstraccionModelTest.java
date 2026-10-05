package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.application.FacesMessage;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Pruebas de comportamiento de AbstraccionModel, con DAO y contexto JSF simulados. */
class AbstraccionModelTest extends ModelTestSupport {

    @Mock ProductoDAO dao;

    private Modelo modelo() {
        return new Modelo(dao);
    }

    private static class Modelo extends AbstraccionModel<Producto> {
        private final ProductoDAO dao;
        boolean valido = true;
        Modelo(ProductoDAO dao) { this.dao = dao; }
        @Override protected AbstraccionDAO<Producto> getDAO() { return dao; }
        @Override protected Producto nuevoRegistro() { return new Producto(UUID.randomUUID()); }
        @Override protected Object getIdRegistro(Producto p) { return p.getIdProducto(); }
        @Override protected boolean validarRegistro() { return valido && super.validarRegistro(); }
    }

    @Test
    void estadoInicialEsListado() {
        Modelo model = modelo();
        assertTrue(model.getRegistros().isEmpty());
        assertNull(model.getRegistro());
        assertEquals(Estado.NINGUNO, model.getEstado());
        assertTrue(model.isListado());
        assertFalse(model.isCreando());
        assertFalse(model.isEditando());
        assertFalse(model.isMostrarFormulario());
    }

    @Test
    void inicializarCargaCopiaMutable() {
        Modelo model = modelo();
        Producto p = new Producto(UUID.randomUUID());
        List<Producto> originales = List.of(p);
        when(dao.findAll()).thenReturn(originales);
        model.inicializar();
        assertEquals(originales, model.getRegistros());
        assertNotSame(originales, model.getRegistros());
        model.getRegistros().clear();
        assertEquals(1, originales.size());
        verify(dao).findAll();
    }

    @Test
    void cargaFallidaConservaListadoAnterior() {
        Modelo model = modelo();
        Producto p = new Producto(UUID.randomUUID());
        when(dao.findAll()).thenReturn(List.of(p)).thenThrow(new PersistenceException("Fallo"));
        model.cargarRegistros();
        model.cargarRegistros();
        assertEquals(List.of(p), model.getRegistros());
        mensaje(FacesMessage.SEVERITY_ERROR, "cargar");
    }

    @Test
    void nuevoAbreFormularioDeCreacion() {
        Modelo model = modelo();
        model.nuevo();
        assertNotNull(model.getRegistro().getIdProducto());
        assertEquals(Estado.CREAR, model.getEstado());
        assertTrue(model.isCreando());
        assertTrue(model.isMostrarFormulario());
        assertFalse(model.isListado());
        assertFalse(model.isEditando());
        verifyNoInteractions(dao);
    }

    @Test
    void editarRecuperaInstanciaDesdeDAO() {
        Modelo model = modelo();
        Producto fila = new Producto(UUID.randomUUID());
        Producto recuperado = new Producto(fila.getIdProducto());
        when(dao.buscarPorId(fila.getIdProducto())).thenReturn(recuperado);
        model.editar(fila);
        assertSame(recuperado, model.getRegistro());
        assertTrue(model.isEditando());
        assertFalse(model.isCreando());
        assertFalse(model.isListado());
        assertTrue(model.isMostrarFormulario());
    }

    @Test
    void editarNuloNoConsulta() {
        Modelo model = modelo();
        model.editar(null);
        verifyNoInteractions(dao);
        assertTrue(model.isListado());
        mensaje(FacesMessage.SEVERITY_WARN, "Selecciona");
    }

    @Test
    void eliminarNuloNoConsulta() {
        Modelo model = modelo();
        model.eliminar(null);
        verifyNoInteractions(dao);
        assertTrue(model.isListado());
        mensaje(FacesMessage.SEVERITY_WARN, "Selecciona");
    }

    @Test
    void editarSinIdNoConsulta() {
        Modelo model = modelo();
        model.editar(new Producto());
        verifyNoInteractions(dao);
        assertTrue(model.isListado());
        mensaje(FacesMessage.SEVERITY_WARN, "Selecciona");
    }

    @Test
    void eliminarSinIdNoConsulta() {
        Modelo model = modelo();
        model.eliminar(new Producto());
        verifyNoInteractions(dao);
        assertTrue(model.isListado());
        mensaje(FacesMessage.SEVERITY_WARN, "Selecciona");
    }

    @Test
    void editarNoEncontradoConservaFormulario() {
        Modelo model = modelo();
        model.nuevo();
        Producto anterior = model.getRegistro();
        Producto fila = new Producto(UUID.randomUUID());
        when(dao.buscarPorId(fila.getIdProducto())).thenReturn(null);
        model.editar(fila);
        assertSame(anterior, model.getRegistro());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_ERROR, "recuperar");
    }

    @Test
    void editarConErrorConservaFormulario() {
        Modelo model = modelo();
        model.nuevo();
        Producto anterior = model.getRegistro();
        Producto fila = new Producto(UUID.randomUUID());
        when(dao.buscarPorId(fila.getIdProducto())).thenThrow(new PersistenceException("Fallo"));
        model.editar(fila);
        assertSame(anterior, model.getRegistro());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_ERROR, "recuperar");
    }

    @Test
    void editarSinArgumentoUsaRegistroSeleccionado() {
        Modelo model = modelo();
        Producto p = new Producto(UUID.randomUUID());
        model.setRegistro(p);
        when(dao.buscarPorId(p.getIdProducto())).thenReturn(p);
        model.editar();
        assertSame(p, model.getRegistro());
        assertTrue(model.isEditando());
    }

    @Test
    void crearExito() {
        Modelo model = modelo();
        model.nuevo();
        Producto editado = model.getRegistro();
        when(dao.crear(editado)).thenReturn(true);
        model.guardar();
        verify(dao).crear(editado);
        assertNull(model.getRegistro());
        assertTrue(model.isListado());
        verify(dao).findAll();
        mensaje(FacesMessage.SEVERITY_INFO, "guardado");
    }

    @Test
    void crearFalse() {
        Modelo model = modelo();
        model.nuevo();
        Producto editado = model.getRegistro();
        when(dao.crear(editado)).thenReturn(false);
        model.guardar();
        verify(dao).crear(editado);
        assertSame(editado, model.getRegistro());
        verify(dao, never()).findAll();
        mensaje(FacesMessage.SEVERITY_ERROR, "guardar");
    }

    @Test
    void crearExcepcion() {
        Modelo model = modelo();
        model.nuevo();
        Producto editado = model.getRegistro();
        when(dao.crear(editado)).thenThrow(new PersistenceException("Fallo"));
        model.guardar();
        verify(dao).crear(editado);
        assertSame(editado, model.getRegistro());
        verify(dao, never()).findAll();
        mensaje(FacesMessage.SEVERITY_ERROR, "guardar");
    }

    @Test
    void modificarExito() {
        Modelo model = modelo();
        Producto fila = new Producto(UUID.randomUUID());
        when(dao.buscarPorId(fila.getIdProducto())).thenReturn(fila);
        model.editar(fila);
        Producto editado = model.getRegistro();
        when(dao.modificar(editado)).thenReturn(true);
        model.guardar();
        verify(dao).modificar(editado);
        assertNull(model.getRegistro());
        assertTrue(model.isListado());
        verify(dao).findAll();
        mensaje(FacesMessage.SEVERITY_INFO, "guardado");
    }

    @Test
    void modificarFalse() {
        Modelo model = modelo();
        Producto fila = new Producto(UUID.randomUUID());
        when(dao.buscarPorId(fila.getIdProducto())).thenReturn(fila);
        model.editar(fila);
        Producto editado = model.getRegistro();
        when(dao.modificar(editado)).thenReturn(false);
        model.guardar();
        verify(dao).modificar(editado);
        assertSame(editado, model.getRegistro());
        verify(dao, never()).findAll();
        mensaje(FacesMessage.SEVERITY_ERROR, "guardar");
    }

    @Test
    void modificarExcepcion() {
        Modelo model = modelo();
        Producto fila = new Producto(UUID.randomUUID());
        when(dao.buscarPorId(fila.getIdProducto())).thenReturn(fila);
        model.editar(fila);
        Producto editado = model.getRegistro();
        when(dao.modificar(editado)).thenThrow(new PersistenceException("Fallo"));
        model.guardar();
        verify(dao).modificar(editado);
        assertSame(editado, model.getRegistro());
        verify(dao, never()).findAll();
        mensaje(FacesMessage.SEVERITY_ERROR, "guardar");
    }

    @Test
    void guardarSinFormularioNoInvocaDAO() {
        Modelo model = modelo();
        model.guardar();
        model.setRegistro(new Producto(UUID.randomUUID()));
        model.guardar();
        verifyNoInteractions(dao);
        mensaje(FacesMessage.SEVERITY_WARN, "Abre");
    }

    @Test
    void validacionFallidaConservaRegistro() {
        Modelo model = modelo();
        model.nuevo();
        model.valido = false;
        Producto p = model.getRegistro();
        model.guardar();
        assertSame(p, model.getRegistro());
        assertTrue(model.isCreando());
        verifyNoInteractions(dao);
    }

    @Test
    void eliminarExito() {
        Modelo model = modelo();
        model.nuevo();
        Producto p = model.getRegistro();
        when(dao.eliminar(p)).thenReturn(true);
        model.eliminar();
        verify(dao).eliminar(p);
        assertTrue(model.isListado());
        assertNull(model.getRegistro());
        verify(dao).findAll();
        mensaje(FacesMessage.SEVERITY_INFO, "eliminado");
    }

    @Test
    void eliminarFalse() {
        Modelo model = modelo();
        model.nuevo();
        Producto p = model.getRegistro();
        when(dao.eliminar(p)).thenReturn(false);
        model.eliminar();
        verify(dao).eliminar(p);
        assertSame(p, model.getRegistro());
        assertTrue(model.isCreando());
        verify(dao, never()).findAll();
        mensaje(FacesMessage.SEVERITY_ERROR, "eliminar");
    }

    @Test
    void eliminarExcepcion() {
        Modelo model = modelo();
        model.nuevo();
        Producto p = model.getRegistro();
        when(dao.eliminar(p)).thenThrow(new PersistenceException("Fallo"));
        model.eliminar();
        verify(dao).eliminar(p);
        assertSame(p, model.getRegistro());
        assertTrue(model.isCreando());
        verify(dao, never()).findAll();
        mensaje(FacesMessage.SEVERITY_ERROR, "eliminar");
    }

    @Test
    void cancelarDescartaFormularioSinPersistir() {
        Modelo model = modelo();
        model.nuevo();
        model.cancelar();
        assertNull(model.getRegistro());
        assertTrue(model.isListado());
        verifyNoInteractions(dao);
    }
}
