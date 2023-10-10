package com.javarush.dao;

import com.javarush.domain.City;
import com.javarush.domain.Rental;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class RentalDAO extends GenericDAO<Rental> {
    public RentalDAO(SessionFactory sessionFactory) {
        super(Rental.class, sessionFactory);
    }


    public Rental getAnyUnreturned() {

        Query<Rental> query = getSessionFactory().getCurrentSession().createQuery("select r from Rental r where r.rentalDate is null ", Rental.class);
        query.setMaxResults(1);
        return query.getSingleResult();

    }


}