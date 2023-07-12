package util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerFactoryUtil {
    private static EntityManagerFactory entityManagerFactory;

    private EntityManagerFactoryUtil() {}

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("funcionarios");
        }
        return entityManagerFactory;
    }

    public static synchronized void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
