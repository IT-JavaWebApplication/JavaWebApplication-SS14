package com.rikkei.bai2.config;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {

    @Getter
    private static final SessionFactory sessionFactory;

    static {

        try {

            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();

        } catch (Throwable ex) {

            System.err.println("Lỗi khởi tạo SessionFactory: " + ex);

            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {

        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}