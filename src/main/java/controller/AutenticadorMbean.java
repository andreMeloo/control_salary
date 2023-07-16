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
        validaCampos();
        // criar logica de identificar erros

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
                        ListMessagesSystemControl messagesSystem = new ListMessagesSystemControl();
                        messagesSystem.addMensagem(new MensagemSistema(MensagemSistema.FALHA_AUTENTICACAO_SENHA_INCORRETA, TipoMensagem.ERROR));
                        return null;
                    }
                }
            }
        } catch (Exception erro) {
            throw new DaoException("Ocorreu um erro inesperado ao autenticar o usuário, contate o adiministrador do sistema para maiores informações", erro);
        } finally {
            if (userDao != null) {
                userDao.close();
            }
        }

        // mensagem de erro de nome de usuário incorreto
        System.out.println("Login incorreto");
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
            // Logica de gerar mensagens de erro
            System.out.println("Login: campo obrigatório");
        }
        if (ValidatorUtil.isEmpty(getSenhaForm())) {
            // Logica de gerar mensagens de erro
            System.out.println("Senha: campo obrigatório");
        }
    }
}
