package dao;

import jakarta.persistence.PersistenceException;
import model.Estado;
import model.Pessoa;
import model.PessoaSalario;
import model.Usuario;
import util.DaoException;

import java.util.ArrayList;
import java.util.List;

public class PessoaSalarioDao extends AbstractDao{

    public void criar(PessoaSalario pessoaSalario) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(pessoaSalario);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao criar um usuário, por favor contate o suporte para maiores detalhes.", error);
        }
    }

    public void remover(PessoaSalario pessoaSalario) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(pessoaSalario);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao remover a pessoaSalario, por favor contate o suporte para maiores detalhes.", error);
        }
    }

    public PessoaSalario findByPessoa(Integer idPessoa) {
        PessoaSalario pessoaSalario = new PessoaSalario();
        try {
            String sql = "SELECT ps FROM PessoaSalario ps WHERE ps.pessoa.id = :idPessoa";
            pessoaSalario = entityManager.createQuery(sql, PessoaSalario.class)
                    .setParameter("idPessoa", idPessoa)
                    .getSingleResult();
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao realizar a busca, por favor contate o suporte para maiores detalhes.", error);
        }
        return pessoaSalario;
    }

    public List<PessoaSalario> findByPessoas(List<Pessoa> pessoas) {
        // Lógica responsável por gerar o stringIn
        StringBuilder stringIn = new StringBuilder();
        stringIn.append(" ( ");

        int t = pessoas.size();
        for (int i = 0; i < t; i++) {
            stringIn.append(pessoas.get(i).getId());
            if (i < (t - 1)) {
                stringIn.append(",");
            }
        }
        stringIn.append(" )");

        List<PessoaSalario> PessoasSalarios = new ArrayList<>();
        try {
            PessoasSalarios = entityManager.createQuery("SELECT ps FROM PessoaSalario ps WHERE ps.pessoa.id IN " + stringIn.toString() + " ORDER BY ps.pessoa.nome", PessoaSalario.class).getResultList();
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao realizar a busca, por favor contate o suporte para maiores detalhes.", error);
        }
        return PessoasSalarios;

    }

    public Integer countPessoasSemSlarioCalculado() {
        Integer numPessoas = 0;
        try {
            numPessoas = (Integer) entityManager.createQuery("SELECT count(p) FROM Pessoa p WHERE NOT EXISTS ( SELECT 1 FROM PessoaSalario ps WHERE ps.pessoa.id = p.id)")
                    .getSingleResult();

        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao realizar a busca, por favor contate o suporte para maiores detalhes.", error);
        }
        return numPessoas;
    }

}
