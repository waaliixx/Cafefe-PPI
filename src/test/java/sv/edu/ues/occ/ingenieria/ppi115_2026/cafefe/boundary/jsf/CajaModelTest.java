/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CajaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caja;

/**
 *
 * @author walixx
 */
/** Pruebas de comportamiento de CajaModel, con DAO y contexto JSF simulados. */
public class CajaModelTest extends ModelTestSupport{
    
    @Mock CajaDAO cajaDAO;
    @InjectMocks CajaModel model;

    private Caja caja(String nombre) {
        Caja c = new Caja(UUID.randomUUID());
        c.setNombre(nombre);
        c.setActivo(true);
        return c;
    }

    @Test
    void usaElDAOInyectado() {
        assertSame(cajaDAO, model.getDAO());
    }

    @Test
    void nuevoRegistroGeneraIdentificadoresDiferentes() {
        Caja primera = model.nuevoRegistro();
        Caja segunda = model.nuevoRegistro();
        assertNotNull(primera.getIdCaja());
        assertNotEquals(primera.getIdCaja(), segunda.getIdCaja());
        assertEquals(primera.getIdCaja(), model.getIdRegistro(primera));
    }

    @Test
    void idDeRegistroNuloEsNulo() {
        assertNull(model.getIdRegistro(null));
    }

    @Test
    void nuevaCajaNaceActiva() {
        assertTrue(model.nuevoRegistro().getActivo());
    }

    @Test
    void cancelarVuelveAlListadoYLimpiaSeleccion() {
        model.setSeleccionado(model.nuevoRegistro());
        model.nuevo();
        model.cancelar();
        assertNull(model.getRegistro());
        assertNull(model.getSeleccionado());
        assertTrue(model.isListado());
    }

    @Test
    void registroNuloNoEsValido() {
        assertFalse(model.validarRegistro());
    }

    @Test
    void nombreNuloOVacioOEnBlancoImpideGuardar() {
        for (String nombre : new String[]{null, "", "   "}) {
            model.nuevo();
            model.getRegistro().setNombre(nombre);
            model.guardar();
            assertTrue(model.isCreando());
            mensaje(FacesMessage.SEVERITY_WARN, "obligatorio");
            model.cancelar();
        }
        verify(cajaDAO, never()).crear(any());
        verify(cajaDAO, never()).modificar(any());
    }

    @Test
    void nombreDemasiadoLargoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("x".repeat(CajaModel.MAX_NOMBRE + 1));
        model.guardar();
        verify(cajaDAO, never()).crear(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "155");
    }

    @Test
    void nombreConLongitudMaximaSeAcepta() {
        model.nuevo();
        model.getRegistro().setNombre("x".repeat(CajaModel.MAX_NOMBRE));
        when(cajaDAO.crear(model.getRegistro())).thenReturn(true);
        model.guardar();
        assertTrue(model.isListado());
    }

    @Test
    void nombreRepetidoImpideGuardarSinImportarMayusculasNiEspacios() {
        when(cajaDAO.findAll()).thenReturn(List.of(caja("Caja 1")));
        model.nuevo();
        model.getRegistro().setNombre("  caja 1 ");
        model.guardar();
        verify(cajaDAO, never()).crear(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_ERROR, "Ya existe");
    }

    @Test
    void editarUnaCajaPuedeConservarSuPropioNombre() {
        Caja original = caja("Caja 1");
        when(cajaDAO.buscarPorId(original.getIdCaja())).thenReturn(original);
        when(cajaDAO.findAll()).thenReturn(List.of(original));
        when(cajaDAO.modificar(original)).thenReturn(true);
        model.editar(original);
        model.guardar();
        verify(cajaDAO).modificar(original);
        assertTrue(model.isListado());
    }

    @Test
    void cajaConNombreNuloEnLaListaNoRompeLaValidacion() {
        when(cajaDAO.findAll()).thenReturn(List.of(caja(null)));
        model.nuevo();
        model.getRegistro().setNombre("Caja 2");
        when(cajaDAO.crear(model.getRegistro())).thenReturn(true);
        model.guardar();
        assertTrue(model.isListado());
    }

    @Test
    void registroValidoSeGuardaConNombreRecortado() {
        model.nuevo();
        Caja nueva = model.getRegistro();
        nueva.setNombre("  Caja principal  ");
        when(cajaDAO.crear(nueva)).thenReturn(true);
        model.guardar();
        assertEquals("Caja principal", nueva.getNombre());
        assertTrue(model.isListado());
        mensaje(FacesMessage.SEVERITY_INFO, "guardado");
    }

    @Test
    void desactivarUnaCajaEstaPermitido() {
        Caja original = caja("Caja 1");
        when(cajaDAO.buscarPorId(original.getIdCaja())).thenReturn(original);
        when(cajaDAO.modificar(original)).thenReturn(true);
        model.editar(original);
        original.setActivo(false);
        model.guardar();
        verify(cajaDAO).modificar(original);
    }

    // ---------- eliminar ----------
    @Test
    void noSeEliminaUnaCajaConFacturas() {
        Caja c = caja("Caja 1");
        when(cajaDAO.contarFacturas(c.getIdCaja())).thenReturn(3L);
        model.eliminar(c);
        verify(cajaDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "3 facturas");
    }

    @Test
    void mensajeEnSingularConUnaFactura() {
        Caja c = caja("Caja 1");
        when(cajaDAO.contarFacturas(c.getIdCaja())).thenReturn(1L);
        model.eliminar(c);
        mensaje(FacesMessage.SEVERITY_ERROR, "1 factura.");
    }

    @Test
    void seEliminaUnaCajaSinFacturasYSeLimpiaLaSeleccion() {
        Caja c = caja("Caja 1");
        model.setSeleccionado(c);
        when(cajaDAO.contarFacturas(c.getIdCaja())).thenReturn(0L);
        when(cajaDAO.eliminar(c)).thenReturn(true);
        model.eliminar(c);
        verify(cajaDAO).eliminar(c);
        assertNull(model.getSeleccionado());
        mensaje(FacesMessage.SEVERITY_INFO, "eliminado");
    }

    @Test
    void siNoSePuedeContarFacturasNoSeElimina() {
        Caja c = caja("Caja 1");
        when(cajaDAO.contarFacturas(c.getIdCaja())).thenThrow(new PersistenceException("Fallo"));
        model.eliminar(c);
        verify(cajaDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "verificar");
    }

    @Test
    void eliminarNuloAvisaSinConsultar() {
        model.eliminar(null);
        verify(cajaDAO, never()).contarFacturas(any());
        verify(cajaDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "Selecciona");
    }
}
