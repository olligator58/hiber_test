package dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public abstract class AbstractHibernateDao<T> {
    private final Class<T> clazz;
    private SessionFactory sessionFactory;

    public AbstractHibernateDao(final Class<T> clazzToSet)   {
        this.clazz = clazzToSet;
        init();
    }

    private void init() {
        sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
    }

    public T getById(final int id) {
        try (Session session = sessionFactory.openSession()) {
            return (T) session.get(clazz, id);
        }
    }

    public List<T> getItems(int from, int count) {
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery("from " + clazz.getName(), clazz);
            query.setFirstResult(from);
            query.setMaxResults(count);
            return query.list();
        }
    }

    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from " + clazz.getName(), clazz).list();
        }
    }

    public T create(final T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return entity;
        }
    }

    public T update(final T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
            return entity;
        }
    }

    public void delete(final T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        }
    }

    public void deleteById(final int entityId) {
        final T entity = getById(entityId);
        delete(entity);
    }
}
