package com.beaconfire.shoppy.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.beaconfire.shoppy.model.Product;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
@Transactional("hibernateTransactionManager")
public class ProductRepository {

    @Autowired
    private SessionFactory sessionFactory;

//    public List<Product> findAllInStock() {
//        Session session = sessionFactory.getCurrentSession();
//        return session.createQuery("from Product where quantity > 0", Product.class).list();
//    }

    public List<Product> findAllInStock() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);

        Root<Product> product = cq.from(Product.class);
        cq.select(product).where(cb.gt(product.get("quantity"), 0));

        return session.createQuery(cq).getResultList();
    }

    public List<Product> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Product", Product.class).list();
    }

    public Product findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Product.class, id);
    }

    public void save(Product product) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(product);
    }

    public Product findByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Product product = session.createQuery("from Product where name = :name", Product.class)
                .setParameter("name", name)
                .uniqueResult();

        System.out.println("findByName: Product found: " + product); // Debugging log
        return product;
    }
}