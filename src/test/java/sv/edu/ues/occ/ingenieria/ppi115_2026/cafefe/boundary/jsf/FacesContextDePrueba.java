package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseStream;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.lifecycle.Lifecycle;
import jakarta.faces.render.RenderKit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Contexto mínimo para capturar mensajes sin depender de la implementación del servidor. */
final class FacesContextDePrueba extends FacesContext {
    private final List<FacesMessage> mensajes = new ArrayList<>();

    static void establecer(FacesContext contexto) {
        setCurrentInstance(contexto);
    }

    @Override
    public void addMessage(String cliente, FacesMessage mensaje) {
        mensajes.add(mensaje);
    }

    @Override public List<FacesMessage> getMessageList() { return mensajes; }
    @Override public Iterator<FacesMessage> getMessages() { return mensajes.iterator(); }
    @Override public Iterator<FacesMessage> getMessages(String cliente) { return getMessages(); }
    @Override public void release() { setCurrentInstance(null); }

    // Los models probados solo utilizan el contexto actual y addMessage.
    @Override public Application getApplication() { throw new UnsupportedOperationException(); }
    @Override public Iterator<String> getClientIdsWithMessages() { throw new UnsupportedOperationException(); }
    @Override public Lifecycle getLifecycle() { throw new UnsupportedOperationException(); }
    @Override public ExternalContext getExternalContext() { throw new UnsupportedOperationException(); }
    @Override public FacesMessage.Severity getMaximumSeverity() { throw new UnsupportedOperationException(); }
    @Override public RenderKit getRenderKit() { throw new UnsupportedOperationException(); }
    @Override public boolean getRenderResponse() { throw new UnsupportedOperationException(); }
    @Override public boolean getResponseComplete() { throw new UnsupportedOperationException(); }
    @Override public ResponseStream getResponseStream() { throw new UnsupportedOperationException(); }
    @Override public void setResponseStream(ResponseStream stream) { throw new UnsupportedOperationException(); }
    @Override public ResponseWriter getResponseWriter() { throw new UnsupportedOperationException(); }
    @Override public void setResponseWriter(ResponseWriter writer) { throw new UnsupportedOperationException(); }
    @Override public UIViewRoot getViewRoot() { throw new UnsupportedOperationException(); }
    @Override public void setViewRoot(UIViewRoot vista) { throw new UnsupportedOperationException(); }
    @Override public void renderResponse() { throw new UnsupportedOperationException(); }
    @Override public void responseComplete() { throw new UnsupportedOperationException(); }
}
