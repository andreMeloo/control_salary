package dao;


import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.Setter;
import util.EntityManagerFactoryUtil;

@Getter
@Setter
public class AbstractDao {

    private EntityManager entityManager;

    public void loadEntityManager() {
        entityManager = EntityManagerFactoryUtil.getEntityManagerFactory().createEntityManager();
    }

    public void close() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

}
