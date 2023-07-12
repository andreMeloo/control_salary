package config;

import util.EntityManagerFactoryUtil;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class MyServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        EntityManagerFactoryUtil.getEntityManagerFactory();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        EntityManagerFactoryUtil.closeEntityManagerFactory();
    }
}
