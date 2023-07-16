package dao;

import jakarta.persistence.PersistenceException;
import model.Cargo;
import util.DaoException;

import java.util.ArrayList;
import java.util.List;

public class CargoDao extends AbstractDao{

    public void criar(Cargo cargo) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(cargo);
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao criar um cargo, por favor contate o suporte para maiores detalhes.", error);
        }
    }

    public void alterar(Cargo cargo) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(cargo);
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao tentar alterar o cargo, por favor contate o suporte para maiores detalhes.", error);
        } finally {
            assert getEntityManager() != null;
            getEntityManager().close();
        }
    }

    public void remover(Cargo cargo) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().remove(cargo);
            getEntityManager().getTransaction().commit();
        } catch (PersistenceException error) {
            if (getEntityManager() != null && getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao remover o cargo, por favor contate o suporte para maiores detalhes.", error);
        }finally {
            if (getEntityManager() != null) {
                getEntityManager().close();
            }
        }
    }

    public List<Cargo> findAll() {
        List<Cargo> cargos = new ArrayList<>();
        try {
            cargos = getEntityManager().createQuery("SELECT cr FROM Cargo cr ORDER BY cr.nome", Cargo.class).getResultList();
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao buscar cargos, por favor contate o suporte para maiores detalhes.", error);
        } finally {
            getEntityManager().close();
        }
        return cargos;
    }

}
