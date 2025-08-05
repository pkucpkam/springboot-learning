package com.example.demo.repository.impl;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.utils.HibernateUtility;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
    public void insert(Customer customer) {
        Transaction transaction = null;
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(customer);
            transaction.commit();
        } catch (Exception e) {
            if (Optional.ofNullable(transaction).isPresent());
            e.printStackTrace();
        }
    }

    public Customer selectById(Long id) {
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            return session.get(Customer.class, id);
        }
    }

    public Customer selectByEmail(String email) {
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            String hql = "from Customer where email = :email";
            return session.createQuery(hql, Customer.class).setParameter("email", email).uniqueResult();
        }
    }

    public List<Customer> selectAll() {
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            return session.createQuery("from Customer").list();
        }
    }

    public void update(Customer customer) {
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(customer);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Customer customer) {
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(customer);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
