package com.example.demo.utils;

import com.example.demo.entity.Customer;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernateUtility {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Load application.properties
                Properties appProps = new Properties();
                try (InputStream input = HibernateUtility.class.getClassLoader()
                        .getResourceAsStream("application.properties")) {
                    if (input == null) {
                        throw new RuntimeException("Cannot find application.properties");
                    }
                    appProps.load(input);
                }

                // Set Hibernate configuration from appProps
                Properties settings = new Properties();
                settings.put(Environment.JAKARTA_JDBC_DRIVER, appProps.getProperty("db.driver"));
                settings.put(Environment.JAKARTA_JDBC_URL, appProps.getProperty("db.url"));
                settings.put(Environment.JAKARTA_JDBC_USER, appProps.getProperty("db.username"));
                settings.put(Environment.JAKARTA_JDBC_PASSWORD, appProps.getProperty("db.password"));
                settings.put(Environment.DIALECT, appProps.getProperty("hibernate.dialect"));
                settings.put(Environment.SHOW_SQL, appProps.getProperty("hibernate.show_sql"));
                settings.put(Environment.FORMAT_SQL, appProps.getProperty("hibernate.format_sql"));
                settings.put(Environment.HBM2DDL_AUTO, appProps.getProperty("hibernate.hbm2ddl.auto"));

                // Build Hibernate config
                Configuration configuration = new Configuration();
                configuration.setProperties(settings);
                configuration.addAnnotatedClass(Customer.class); // add more if needed

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (IOException e) {
                throw new RuntimeException("Could not load application.properties", e);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to build SessionFactory", e);
            }
        }
        return sessionFactory;
    }
}
