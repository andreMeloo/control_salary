package dao;

import controller.PessoaSalarioMbean.ParametrosBusca;
import controller.PessoaSalarioMbean.RestricoesBusca;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import model.Pessoa;
import model.Usuario;
import util.DaoException;
import util.messagesSystem.MensagemSistema;
import util.messagesSystem.TipoMensagem;

import java.util.ArrayList;
import java.util.List;

public class PessoaDao extends AbstractDao {

    public void criarPessoaUsuario(Pessoa pessoa) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(pessoa);

            pessoa.getUsuario().setPessoa(pessoa);

            getEntityManager().persist(pessoa.getUsuario());
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            mensagemErro = "Erro ao cadastrar uma pessoa, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
    }

    public void removerPessoaUsuario(Pessoa pessoa) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().remove(pessoa.getUsuario());
            getEntityManager().remove(pessoa);
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            mensagemErro = "Erro ao realizar a remoção, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
    }

    public void alterarPessoaUsuario(Pessoa pessoa) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(pessoa);
            getEntityManager().merge(pessoa.getUsuario());
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            mensagemErro = "Erro ao tentar alterar a pessoa, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
    }

    public Pessoa findById(Integer id) {
        Pessoa pessoa = new Pessoa();
        try {
            String sql = "SELECT p FROM Pessoa p WHERE p.id = :id";
            pessoa = getEntityManager().createQuery(sql, Pessoa.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException nre) {
            pessoa = new Pessoa();
        } catch (Exception error) {
            mensagemErro = "Erro ao buscar uma pessoa, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return pessoa;
    }

    public List<Pessoa> findByNameAndCargoAndEstado(ParametrosBusca parametros, RestricoesBusca restricoes) {
        List<Pessoa> pessoas = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT p FROM Pessoa p ");
            sql.append(" JOIN Usuario u ON u.pessoa = p ");
            sql.append(" WHERE 1 = 1 ");

            if (restricoes.isNome() && !parametros.getNome().isEmpty())
                sql.append(" AND sem_acento(UPPER(p.nome)) LIKE sem_acento(UPPER(CONCAT('%', :nome, '%'))) ");

            if (restricoes.isCargo() && (parametros.getCargo() != null || parametros.getCargo() > 0))
                sql.append(" AND p.cargo.id = ").append(" :idCargo ");

            if (restricoes.isEstado() && (parametros.getEstado() != null || parametros.getCargo() > 0))
                sql.append(" AND p.endereco.cidade.estado.id = ").append(" :idEstado ");

            sql.append(" ORDER BY p.nome, p.cargo.id ");

            TypedQuery<Pessoa> query = getEntityManager().createQuery(sql.toString(), Pessoa.class);
            if (restricoes.isNome() && !parametros.getNome().isEmpty())
                query.setParameter("nome", parametros.getNome());

            if (restricoes.isCargo() && (parametros.getCargo() != null || parametros.getCargo() > 0))
                query.setParameter("idCargo", parametros.getCargo());

            if (restricoes.isEstado() && (parametros.getEstado() != null || parametros.getCargo() > 0))
                query.setParameter("idEstado", parametros.getEstado());

            pessoas = query.getResultList();
        } catch (Exception error) {
            mensagemErro = "Erro ao buscar pessoas, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return pessoas;
    }

    public List<Pessoa> findAll() {
        List<Pessoa> pessoas = new ArrayList<>();
        try {
            pessoas = getEntityManager().createQuery("SELECT p FROM Pessoa p ORDER BY p.nome", Pessoa.class).getResultList();
        } catch (Exception error) {
            mensagemErro = "Erro ao buscar Pessoas, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return pessoas;
    }

}
