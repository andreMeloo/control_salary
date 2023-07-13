package dao;


import jakarta.persistence.EntityManager;
import util.EntityManagerFactoryUtil;

import java.lang.reflect.Constructor;

public class AbstractDao {

    protected EntityManager entityManager;

    protected void loadEntityManager() {
        entityManager = EntityManagerFactoryUtil.getEntityManagerFactory().createEntityManager();
    }

    public void closeEntityManager() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    public static <T> T getDao(Class<T> daoClass) {
        try {
            Constructor<T> constructor = daoClass.getConstructor();
            T daoInstance = constructor.newInstance();
            if (daoInstance instanceof AbstractDao) {
                AbstractDao abstractDao = (AbstractDao) daoInstance;
                abstractDao.loadEntityManager();
            }
            return daoInstance;
        } catch (Exception e) {
            // Tratar exceção, se necessário
            return null;
        }
    }

}
