package controller;

import dao.UsuarioDao;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.PartialViewContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import model.Usuario;
import util.DaoException;
import util.PasswordControl;
import util.ValidatorUtil;
import util.messagesSystem.ListMessagesSystemControl;
import util.messagesSystem.MensagemSistema;
import util.messagesSystem.TipoMensagem;

import java.io.IOException;
import java.io.Serializable;

@Named
@RequestScoped
@Getter
@Setter
public class AutenticadorMbean extends AbstractControllerBean implements Serializable {
    private String loginForm;
    private String senhaForm;
    private Usuario usuarioAutenticado;

    public AutenticadorMbean() {
        usuarioAutenticado = new Usuario();
        loginForm = "";
        senhaForm = "";
    }

    public String autenticar() {
        setMessagesSystem(new ListMessagesSystemControl());
        validaCampos();
        if (hasMessages())
            return null;

        UsuarioDao userDao = getDao(UsuarioDao.class);
        try {
            if (userDao != null) {
                usuarioAutenticado = userDao.findByUsername(loginForm);
                if (ValidatorUtil.isNotEmpty(usuarioAutenticado)) {
                    boolean autenticado = PasswordControl.verifyPassword(senhaForm, usuarioAutenticado.getSenha());
                    if (autenticado) {
                        setUsuarioSessao(usuarioAutenticado);
                        usuarioAutenticado = new Usuario();
                        PessoaSalarioMbean mbean = getMBean("pessoaSalarioMbean");
                        return mbean.entrarListagemFuncionarios();
                    } else {
                        getMessagesSystem().addMensagem(new MensagemSistema(MensagemSistema.FALHA_AUTENTICACAO_SENHA_INCORRETA, TipoMensagem.ERROR));
                        return null;
                    }
                }
            }
        } catch (Exception erro) {
            String mensagemErro = "Ocorreu um erro inesperado ao autenticar o usuário, contate o adiministrador do sistema para maiores informações";
            getMessagesSystem().addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, erro);
        } finally {
            if (userDao != null) {
                userDao.close();
            }
        }

        getMessagesSystem().addMensagem(new MensagemSistema(MensagemSistema.FALHA_AUTENTICACAO_LOGIN_INCORRETO, TipoMensagem.ERROR));
        usuarioAutenticado = new Usuario();
        return null;
    }

    public String logout() throws IOException {
        // Obter a sessão HTTP atual
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);

        if (ValidatorUtil.isNotEmpty(session)) {
            // Invalidar a sessão para remover o usuário autenticado
            session.invalidate();
        }
        return redirectPage(PAGINA_LOGIN);
    }

    private void validaCampos() {
        if (ValidatorUtil.isEmpty(getLoginForm())) {
            getMessagesSystem().addMensagem(new MensagemSistema("Login: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        }
        if (ValidatorUtil.isEmpty(getSenhaForm())) {
            getMessagesSystem().addMensagem(new MensagemSistema("Senha: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        }
    }
}
