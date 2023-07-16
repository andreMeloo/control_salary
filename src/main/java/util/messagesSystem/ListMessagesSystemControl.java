package util.messagesSystem;

import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import util.ValidatorUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Named
public class ListMessagesSystemControl {

    public void addMensagem(MensagemSistema mensagemSistema) {
        List<MensagemSistema> mensagensEmFila = new ArrayList<>();
        limpaMessagesSystem();

        if (ValidatorUtil.isNotEmpty(mensagemSistema)) {
            mensagensEmFila.add(mensagemSistema);
            setMessagesSession(mensagensEmFila);
        }
    }

    public void addMensagem(List<MensagemSistema> mensagensSistema) {
        limpaMessagesSystem();

        if (ValidatorUtil.isNotEmpty(mensagensSistema)) {
            setMessagesSession(mensagensSistema);
        }
    }

    private void setMessagesSession(List<MensagemSistema> messages) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        session.setAttribute("MessagesSystem", messages);
    }

    public static void limpaMessagesSystem() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);

        Object messagesSystem = session.getAttribute("MessagesSystem");
        if (ValidatorUtil.isNotEmpty(messagesSystem)) {
            session.removeAttribute("MessagesSystem");
        }
    }

    public boolean isMessagesSession() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);

        Object atributeMessage = session.getAttribute("MessagesSystem");

        if (ValidatorUtil.isNotEmpty(atributeMessage)) {
            return true;
        } else {
            return false;
        }
    }

    public List<MensagemSistema> getMessagesSystem() {
        List<MensagemSistema> mensagensEmFila = new ArrayList<>();
        if (isMessagesSession()) {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(true);

            Object atributeMessage = session.getAttribute("MessagesSystem");
            if (ValidatorUtil.isNotEmpty(atributeMessage)) {
                mensagensEmFila = (List<MensagemSistema>) atributeMessage;
            }
        }
        return mensagensEmFila;
    }

    public void fechaBlocoMensagens() {
        limpaMessagesSystem();
    }
}
