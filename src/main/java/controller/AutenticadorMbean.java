package controller;

import dao.AbstractDao;
import dao.UsuarioDao;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import model.Usuario;
import util.DaoException;
import util.PasswordControl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
@Getter
@Setter
public class AutenticadorMbean implements Serializable {

    private static final String PAGINA_INICIAL = "pessoas_salario.xhtml";
    private static final String PAGINA_LOGIN = "login.xhtml";
    private String loginForm;
    private String senhaForm;
    private Usuario usuarioAutenticado;

    public void init() {
        usuarioAutenticado = new Usuario();
        loginForm = "";
        senhaForm = "";
    }

    public String autenticar() {
        if (!validaCampos())
            return null;
        // criar logica de identificar erros

        UsuarioDao userDao = AbstractDao.getDao(UsuarioDao.class);
        try {
            if (userDao != null) {
                usuarioAutenticado = userDao.findByUsername(loginForm);
                if (usuarioAutenticado != null) {
                    boolean autenticado = PasswordControl.verifyPassword(senhaForm, usuarioAutenticado.getSenha());
                    if (autenticado) {
                        defineUsuarioSessao();
                        usuarioAutenticado = new Usuario();
                        FacesContext.getCurrentInstance().getExternalContext().redirect(PAGINA_INICIAL);
                        return null;
                    } else {
                        // Mensagem de erro senha incorreta
                        System.out.println("Senha incorreta");
                        usuarioAutenticado = new Usuario();
                        return PAGINA_LOGIN;
                    }
                }
            }
        } catch (Exception erro) {
            throw new DaoException("Ocorreu um erro inesperado ao autenticar o usuário, contate o adiministrador do sistema para maiores informações", erro);
        } finally {
            if (userDao != null) {
                userDao.closeEntityManager();
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

        if (session != null) {
            // Invalidar a sessão para remover o usuário autenticado
            session.invalidate();
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(PAGINA_LOGIN);
        return null;
    }

    private Boolean validaCampos() {
        if (getLoginForm().isEmpty()) {
            // Logica de gerar mensagens de erro
            System.out.println("Login: campo obrigatório");
            return false;
        }
        if (getSenhaForm().isEmpty()) {
            // Logica de gerar mensagens de erro
            System.out.println("Senha: campo obrigatório");
            return false;
        }
        return true;
    }

    private void defineUsuarioSessao() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        session.setAttribute("usuarioAutenticado", usuarioAutenticado);
    }

    private Usuario getUsuarioSessao() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        Usuario user = (Usuario) session.getAttribute("usuarioAutenticado");
        if (user != null) {
            return user;
        }
        return new Usuario();
    }
}
