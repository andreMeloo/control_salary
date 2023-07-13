package dao;

import jakarta.persistence.PersistenceException;

import model.Pessoa;
import util.DaoException;
import controller.PessoaSalarioMbean.ParametrosBusca;
import controller.PessoaSalarioMbean.RestricoesBusca;

import java.util.ArrayList;
import java.util.List;

public class PessoaDao extends AbstractDao {

    public void remover(Pessoa pessoa) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(pessoa);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao remover a pessoa, por favor contate o suporte para maiores detalhes.", error);
        }
    }

    public void alterar(Pessoa pessoa) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(pessoa);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao tentar alterar a pessoa, por favor contate o suporte para maiores detalhes.", error);
        }
    }

    public Pessoa findById(Integer id) {
        Pessoa pessoa = new Pessoa();
        try {
            String sql = "SELECT p FROM Pessoa p WHERE p.id = :id";
            pessoa = entityManager.createQuery(sql, Pessoa.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao buscar uma pessoa, por favor contate o suporte para maiores detalhes.", error);
        }
        return pessoa;
    }

    public List<Pessoa> findAll() {
        List<Pessoa> pessoas = new ArrayList<>();
        try {
            pessoas = entityManager.createQuery("SELECT p FROM Pessoa p ORDER BY p.nome", Pessoa.class).getResultList();
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao buscar Pessoas, por favor contate o suporte para maiores detalhes.", error);
        }
        return pessoas;
    }

    public List<Pessoa> findByNameAndUserAndCargoAndEstado(ParametrosBusca parametros, RestricoesBusca restricoes) {
        List<Pessoa> pessoas = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT p FROM Pessoa ");
            sql.append(" JOIN Usuario u ON u.pessoa = p ");
            sql.append(" WHERE 1 = 1 ");

            if (restricoes.isNome() && !parametros.getNome().isEmpty())
                sql.append(" AND sem_acento(upper(p.nome)) LIKE sem_acento(upper(:nome)) ");

            if (restricoes.isUsername() && !parametros.getUsername().isEmpty())
                sql.append(" AND u.login =  ").append(" :login ");

            if (restricoes.isCargo() && (parametros.getCargo() != null || parametros.getCargo() > 0))
                sql.append(" AND p.cargo.id = ").append(" :idCargo ");

            if (restricoes.isEstado() && (parametros.getEstado() != null || parametros.getCargo() > 0))
                sql.append(" AND p.endereco.cidade.estado.id = ").append(" :idEstado ");

            sql.append(" ORDER BY p.nome, p.cargo.id ");

            pessoas = entityManager.createQuery(sql.toString(), Pessoa.class)
                    .setParameter("nome", parametros.getNome())
                    .setParameter("login", parametros.getUsername())
                    .setParameter("idCargo", parametros.getCargo())
                    .setParameter("idEstado", parametros.getEstado())
                    .getResultList();
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao buscar pessoas, por favor contate o suporte para maiores detalhes.", error);
        }
        return pessoas;
    }

}
