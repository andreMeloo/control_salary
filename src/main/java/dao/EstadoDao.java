package dao;

import model.Estado;
import util.DaoException;
import util.messagesSystem.MensagemSistema;
import util.messagesSystem.TipoMensagem;

import java.util.ArrayList;
import java.util.List;

public class EstadoDao extends AbstractDao{

    public List<Estado> findAll() {
        List<Estado> estados = new ArrayList<>();
        try {
            estados = getEntityManager().createQuery("SELECT est FROM Estado est ORDER BY est.nome", Estado.class).getResultList();
        } catch (Exception error) {
            mensagemErro = "Erro ao buscar estados, por favor contate o suporte para maiores detalhes.";
            msgControl.addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        }
        return estados;
    }

}
