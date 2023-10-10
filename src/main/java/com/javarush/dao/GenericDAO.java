package com.javarush.dao;

import org.hibernate.SessionFactory;
import org.hibernate.Session;

import java.util.List;
import java.lang.reflect.TypeVariable;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public abstract class GenericDAO<T> {

    private final Class<T> clazz;
    private SessionFactory sessionFactory;

    public GenericDAO(final Class<T> clazzToSet, SessionFactory sessionFactory) {
        this.clazz = clazzToSet;
        this.sessionFactory = sessionFactory;
    }

    public T getById(int id) {
        TypeVariable<Class<T>>[] typeParameters = clazz.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            System.out.println(typeParameters[i].getName() + " : " + typeParameters[i].getTypeName());
        }
        return sessionFactory.openSession().get(clazz, id);
    }

    public List<T> getAll() {
        Query<T> query = sessionFactory.openSession().createQuery("from " + clazz.getName(), clazz);
        return query.getResultList();
    }

    public List<T> getItems(int offset, int count)
    {
        Query<T> query = sessionFactory.getCurrentSession().createQuery("from " + clazz.getName(), clazz);
        query.setFirstResult(offset);
        query.setMaxResults(count);
        return query.getResultList();
    }


    public void removeById(int id) {
        try (Session session = sessionFactory.openSession()) {
            T t = session.get(clazz, id);
            Transaction transaction = session.beginTransaction();
            session.remove(t);
            transaction.commit();
        }
    }

    public void remove(T t) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(t);
            transaction.commit();
        }
    }

    public void save(T t) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(t);
            transaction.commit();
        }
    }

    public void update(T t) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(t);
            transaction.commit();
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
