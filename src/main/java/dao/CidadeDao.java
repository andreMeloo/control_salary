package dao;

import model.Cidade;
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

}
