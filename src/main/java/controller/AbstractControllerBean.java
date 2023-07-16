package controller;

import dao.AbstractDao;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;
import lombok.Getter;
import lombok.Setter;
import model.TipoUsuario;
import model.Usuario;
import util.ValidatorUtil;
import util.messagesSystem.ListMessagesSystemControl;
import util.messagesSystem.MensagemSistema;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AbstractControllerBean implements Serializable {

    // Caminhos
    protected static final String PAGINA_PESSOAS_SALARIOS = "/pessoas_salario.xhtml";
    protected static final String PAGINA_LOGIN = "/login.xhtml";

    // Botões
    protected static final String BOTAO_NAVEGACAO_LISTA_FUNCIONARIOS = "listagemFunc";


    private String botaoNavegacaoClicado;

    private Usuario usuarioSessao;

    private List<MensagemSistema> mensagensEmFila;

    public String getbotaoNavegacaoClicado() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        return (String) session.getAttribute("botaoClicado");
    }

    public void setbotaoNavegacaoClicado(String botaoClicado) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.setAttribute("botaoClicado", botaoClicado);
    }

    public void setUsuarioSessao(Usuario usuario) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        session.setAttribute("usuarioAutenticado", usuario);
    }


    public Usuario getUsuarioSessao() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        Usuario user = (Usuario) session.getAttribute("usuarioAutenticado");
        if (ValidatorUtil.isNotEmpty(user)) {
            return user;
        }
        return new Usuario();
    }


    public String redirectPage(String url) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        externalContext.redirect(externalContext.getRequestContextPath() + url);
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getMBean(String mbeanName) {
        FacesContext fc = FacesContext.getCurrentInstance();
        return (T) fc.getELContext().getELResolver().getValue(fc.getELContext(), null, mbeanName);
    }

    public boolean isAdministrador() {
        Usuario usuarioLogado = getUsuarioSessao();
        if (ValidatorUtil.isEmpty(usuarioLogado))
            return false;


        return usuarioLogado.getTipoUsuario().equals(TipoUsuario.ADMINISTRADOR);
    }

    public boolean isAdministrador(Usuario usuarioLogado) {
        if (ValidatorUtil.isEmpty(usuarioLogado))
            return false;

        return usuarioLogado.getTipoUsuario().equals(TipoUsuario.ADMINISTRADOR);
    }

    public <T> T getDao(Class<T> daoClass) {
        try {
            Constructor<T> constructor = daoClass.getConstructor();
            T daoInstance = constructor.newInstance();
            if (daoInstance instanceof AbstractDao abstractDao) {
                if (ValidatorUtil.isNotEmpty(abstractDao.getEntityManager())) {
                    if (!abstractDao.getEntityManager().isOpen())
                        abstractDao.loadEntityManager();
                } else {
                    abstractDao.loadEntityManager();
                }
            }
            return daoInstance;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTransacaoEmAndamento() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UserTransaction userTransaction = facesContext.getApplication().evaluateExpressionGet(facesContext, "#{transaction}", UserTransaction.class);

        try {
            if (ValidatorUtil.isNotEmpty(userTransaction)) {
                int transactionStatus = userTransaction.getStatus();
                return transactionStatus == Status.STATUS_ACTIVE || transactionStatus == Status.STATUS_MARKED_ROLLBACK;
            }
        } catch (Exception e) {
            // Tratar exceção
            return false;
        }
        return false;
    }

    // Seção de controle da lista de mesngaens de erros e avisos

    public boolean hasMessages() {
        ListMessagesSystemControl list = new ListMessagesSystemControl();
        return list.isMessagesSession();
    }

}
