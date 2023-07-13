package dao;

import model.Cidade;
import model.Estado;
import util.DaoException;

import java.util.ArrayList;
import java.util.List;

public class CidadeDao extends AbstractDao {

    public List<Cidade> findAllByEstado(Integer idEstado) {
        List<Cidade> cidades = new ArrayList<>();
        try {
            cidades = entityManager.createQuery("SELECT c FROM Cidade c WHERE c.estado.id = :idEstado ORDER BY c.nome ", Cidade.class)
                    .setParameter("idEstado", idEstado)
                    .getResultList();
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao buscar cidades, por favor contate o suporte para maiores detalhes.", error);
        }
        return cidades;
    }

}
