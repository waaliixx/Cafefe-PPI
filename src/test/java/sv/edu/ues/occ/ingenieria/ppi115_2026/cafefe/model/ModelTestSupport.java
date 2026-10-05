package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

/** Contexto JSF simulado por prueba; se libera para no contaminar otras pruebas. */
@ExtendWith(MockitoExtension.class)
abstract class ModelTestSupport {
    protected FacesContext contexto;

    @BeforeEach
    void prepararContexto() {
        contexto = new FacesContextDePrueba();
        FacesContextDePrueba.establecer(contexto);
    }

    @AfterEach
    void liberarContexto() {
        FacesContextDePrueba.establecer(null);
    }

    protected void mensaje(FacesMessage.Severity severidad, String fragmento) {
        assertTrue(contexto.getMessageList().stream().anyMatch(m ->
                severidad.equals(m.getSeverity()) && m.getSummary().contains(fragmento)),
                () -> "No se encontró el mensaje esperado: " + fragmento);
    }

    protected static Date fecha(String dia) {
        return Date.from(LocalDate.parse(dia).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
