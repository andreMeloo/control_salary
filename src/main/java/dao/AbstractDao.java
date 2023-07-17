package dao;


import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.Setter;
import util.EntityManagerFactoryUtil;
import util.messagesSystem.ListMessagesSystemControl;

@Getter
@Setter
public class AbstractDao {

    private EntityManager entityManager;
    String mensagemErro = "";
    ListMessagesSystemControl msgControl;

    public void loadEntityManager() {
        msgControl = new ListMessagesSystemControl();
        entityManager = EntityManagerFactoryUtil.getEntityManagerFactory().createEntityManager();
    }

    public void close() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

}
