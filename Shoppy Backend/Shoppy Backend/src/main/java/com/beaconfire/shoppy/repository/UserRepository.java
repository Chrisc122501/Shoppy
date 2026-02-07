package com.beaconfire.shoppy.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.beaconfire.shoppy.model.User;

//import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional("hibernateTransactionManager")
public class UserRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public void saveUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.save(user);
    }

    public User findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(User.class, id);
    }

    public User findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from User where username = :username", User.class)
                .setParameter("username", username)
                .uniqueResult();
    }

    public User findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from User where email = :email", User.class)
                .setParameter("email", email)
                .uniqueResult();
    }

    public List<User> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from User", User.class).list();
    }

    public void deleteUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(user);
    }
}