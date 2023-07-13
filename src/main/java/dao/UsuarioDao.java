package dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import model.Pessoa;
import model.Usuario;
import org.hibernate.Session;
import util.DaoException;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDao extends AbstractDao {

    public void criarPessoaUsuario(Usuario usuario) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(usuario.getPessoa());
            entityManager.persist(usuario);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao criar um usuário, por favor contate o suporte para maiores detalhes.", error);
        }
    }

    public void alterar(Usuario usuario) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(usuario);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao tentar alterar o usuário, por favor contate o suporte para maiores detalhes.", error);
        }
    }

    public Usuario findById(Integer id) {
        Usuario usuario = null;
        try {
            usuario = entityManager.find(Usuario.class, id);
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao buscar um usuário, por favor contate o suporte para maiores detalhes.", error);
        }
        return usuario;
    }

    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            usuarios = entityManager.createQuery("SELECT u FROM Usuario u ORDER BY u.pessoa.nome", Usuario.class).getResultList();
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
            usuario = entityManager.createQuery(sql, Usuario.class)
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
