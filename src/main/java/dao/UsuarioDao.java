package dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import model.Pessoa;
import model.PessoaSalario;
import model.Usuario;
import util.DaoException;
import util.messagesSystem.MensagemSistema;
import util.messagesSystem.TipoMensagem;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDao extends AbstractDao {

    public void alterar(Usuario usuario) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(usuario);
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            mensagemErro = "Erro ao tentar alterar o usuário, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
    }

    public Usuario findById(Integer id) {
        Usuario usuario = null;
        try {
            usuario = getEntityManager().find(Usuario.class, id);
        } catch (Exception error) {
            mensagemErro = "Erro ao buscar um usuário, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return usuario;
    }

    public Usuario findByUsername(String login) {
        Usuario usuario = new Usuario();
        try {
            String sql = "SELECT u FROM Usuario u WHERE u.login = :login";
            usuario = getEntityManager().createQuery(sql, Usuario.class)
                    .setParameter("login", login)
                    .getSingleResult();
        } catch (NoResultException nre) {
            usuario = null;
        } catch (Exception error) {
            mensagemErro = "Erro ao localizar o login no sistema, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return usuario;
    }

    public Usuario findByPessoa(Integer idPessoa) {
        Usuario usuario = new Usuario();
        try {
            String sql = "SELECT u FROM Usuario u WHERE u.pessoa.id = :idPessoa";
            usuario = getEntityManager().createQuery(sql, Usuario.class)
                    .setParameter("idPessoa", idPessoa)
                    .getSingleResult();
        } catch (NoResultException nre) {
            usuario = null;
        } catch (Exception error) {
            mensagemErro = "Erro ao realizar a busca, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return usuario;
    }
}
