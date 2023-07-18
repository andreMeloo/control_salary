package dao;

import jakarta.persistence.NoResultException;
import model.Cidade;
import model.Pessoa;
import util.DaoException;
import util.messagesSystem.MensagemSistema;
import util.messagesSystem.TipoMensagem;

import java.util.ArrayList;
import java.util.List;

public class CidadeDao extends AbstractDao {

    public List<Cidade> findAllByEstado(Integer idEstado) {
        List<Cidade> cidades = new ArrayList<>();
        try {
            cidades = getEntityManager().createQuery("SELECT c FROM Cidade c WHERE c.estado.id = :idEstado ORDER BY c.nome ", Cidade.class)
                    .setParameter("idEstado", idEstado)
                    .getResultList();
        } catch (Exception error) {
            mensagemErro = "Erro ao buscar cidades, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return cidades;
    }

    public Cidade findById(Integer id) {
        Cidade cidade = new Cidade();
        try {
            String sql = "SELECT c FROM Cidade c WHERE c.id = :id";
            cidade = getEntityManager().createQuery(sql, Cidade.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException nre) {
            cidade = new Cidade();
        } catch (Exception error) {
            mensagemErro = "Erro ao buscar um municipio, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return cidade;
    }

}
