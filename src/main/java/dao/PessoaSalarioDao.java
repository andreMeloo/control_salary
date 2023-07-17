package dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import model.Pessoa;
import model.PessoaSalario;
import util.DaoException;
import util.SqlUtil;
import util.messagesSystem.MensagemSistema;
import util.messagesSystem.TipoMensagem;

import java.util.ArrayList;
import java.util.List;

public class PessoaSalarioDao extends AbstractDao{

    public void contabilizarSalarios(List<PessoaSalario> pessoaSalarios) {
        try {
            getEntityManager().getTransaction().begin();
            for (PessoaSalario pessoaSalario : pessoaSalarios) {
                getEntityManager().persist(pessoaSalario);
            }
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            mensagemErro = "Erro ao criar um usuário, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
    }

    public void remover(PessoaSalario pessoaSalario) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().remove(pessoaSalario);
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            mensagemErro = "Erro ao remover a pessoaSalario, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
    }

    public PessoaSalario findByPessoa(Integer idPessoa) {
        PessoaSalario pessoaSalario = new PessoaSalario();
        try {
            String sql = "SELECT ps FROM PessoaSalario ps WHERE ps.pessoa.id = :idPessoa";
            pessoaSalario = getEntityManager().createQuery(sql, PessoaSalario.class)
                    .setParameter("idPessoa", idPessoa)
                    .getSingleResult();
        } catch (NoResultException nre) {
            pessoaSalario = null;
        } catch (Exception error) {
            mensagemErro = "Erro ao realizar a busca, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return pessoaSalario;
    }

    public List<PessoaSalario> findByPessoas(List<Pessoa> pessoas) {
        List<PessoaSalario> PessoasSalarios = new ArrayList<>();
        try {
            PessoasSalarios = getEntityManager().createQuery("SELECT ps FROM PessoaSalario ps WHERE ps.pessoa.id IN " + SqlUtil.gerarStringInSQL(pessoas) + " ORDER BY ps.pessoa.nome", PessoaSalario.class).getResultList();
        } catch (Exception error) {
            mensagemErro = "Erro ao buscar pessoas, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return PessoasSalarios;
    }

    public Integer countPessoasSemSalarioCalculado(List<Pessoa> pessoas) {
        Long numPessoas = 0L;
        try {
            numPessoas = (Long) getEntityManager().createQuery("SELECT count(distinct p) FROM Pessoa p WHERE p.id IN " + SqlUtil.gerarStringInSQL(pessoas) + " AND NOT EXISTS ( SELECT 1 FROM PessoaSalario ps WHERE ps.pessoa.id = p.id)")
                    .getSingleResult();

        } catch (NoResultException nre) {
            numPessoas = 0L;
        } catch (Exception error) {
            mensagemErro = "Erro ao realizar a busca, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return numPessoas.intValue();
    }

}
