package dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import model.Usuario;
import util.DaoException;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDao extends AbstractDao {

    public void criarPessoaUsuario(Usuario usuario) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(usuario.getPessoa());
            getEntityManager().persist(usuario);
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao criar um usuário, por favor contate o suporte para maiores detalhes.", error);
        }
    }

    public void alterar(Usuario usuario) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(usuario);
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao tentar alterar o usuário, por favor contate o suporte para maiores detalhes.", error);
        }
    }

    public Usuario findById(Integer id) {
        Usuario usuario = null;
        try {
            usuario = getEntityManager().find(Usuario.class, id);
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao buscar um usuário, por favor contate o suporte para maiores detalhes.", error);
        }
        return usuario;
    }

    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            usuarios = getEntityManager().createQuery("SELECT u FROM Usuario u ORDER BY u.pessoa.nome", Usuario.class).getResultList();
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao buscar usuários, por favor contate o suporte para maiores detalhes.", error);
        }
        return usuarios;
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
            error.printStackTrace();
            throw new DaoException("Erro ao buscar um usuário pelo login, por favor contate o suporte para maiores detalhes.", error);
        }
        return usuario;
    }
}
