package dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public abstract class AbstractHibernateDao<T> {
    private final Class<T> clazz;
    private SessionFactory sessionFactory;

    public AbstractHibernateDao(final Class<T> clazzToSet)   {
        this.clazz = clazzToSet;
    }

    private void init() {
        sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
    }

    public T getById(final long id) {
        return (T) getCurrentSession().get(clazz, id);
    }

    public List<T> getItems(int from, int count) {
        Query query = getCurrentSession().createQuery("from " + clazz.getName(), clazz);
        query.setFirstResult(from);
        query.setMaxResults(count);
        return query.list();
    }

    public List<T> findAll() {
        return getCurrentSession().createQuery("from " + clazz.getName(), clazz).list();
    }

    public T create(final T entity) {
        getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    public T update(final T entity) {
        return (T) getCurrentSession().merge(entity);
    }

    public void delete(final T entity) {
        getCurrentSession().delete(entity);
    }

    public void deleteById(final long entityId) {
        final T entity = getById(entityId);
        delete(entity);
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
