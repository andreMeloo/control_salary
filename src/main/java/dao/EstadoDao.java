package dao;

import model.Estado;
import util.DaoException;

import java.util.ArrayList;
import java.util.List;

public class EstadoDao extends AbstractDao{

    public List<Estado> findAll() {
        List<Estado> estados = new ArrayList<>();
        try {
            estados = getEntityManager().createQuery("SELECT est FROM Estado est ORDER BY est.nome", Estado.class).getResultList();
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao buscar estados, por favor contate o suporte para maiores detalhes.", error);
        }
        return estados;
    }

}
