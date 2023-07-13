package dao;

import jakarta.persistence.PersistenceException;
import model.Cargo;
import util.DaoException;

import java.util.ArrayList;
import java.util.List;

public class CargoDao extends AbstractDao{

    public void criar(Cargo cargo) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(cargo);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao criar um cargo, por favor contate o suporte para maiores detalhes.", error);
        }
    }

    public void alterar(Cargo cargo) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(cargo);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao tentar alterar o cargo, por favor contate o suporte para maiores detalhes.", error);
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    public void remover(Cargo cargo) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(cargo);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            error.printStackTrace();
            throw new DaoException("Erro ao remover o cargo, por favor contate o suporte para maiores detalhes.", error);
        }finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public List<Cargo> findAll() {
        List<Cargo> cargos = new ArrayList<>();
        try {
            cargos = entityManager.createQuery("SELECT cr FROM Cargo cr ORDER BY cr.nome", Cargo.class).getResultList();
        } catch (Exception error) {
            error.printStackTrace();
            throw new DaoException("Erro ao buscar cargos, por favor contate o suporte para maiores detalhes.", error);
        } finally {
            entityManager.close();
        }
        return cargos;
    }

}
