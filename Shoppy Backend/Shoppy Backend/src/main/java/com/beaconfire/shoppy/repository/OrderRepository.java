package com.beaconfire.shoppy.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.beaconfire.shoppy.model.CustomerOrder;
import com.beaconfire.shoppy.model.OrderItem;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class OrderRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public CustomerOrder save(CustomerOrder order) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(order);
        return order;
    }

    public List<CustomerOrder> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from CustomerOrder", CustomerOrder.class).list();
    }

    public CustomerOrder findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(CustomerOrder.class, id);
    }

    public List<OrderItem> findOrderItemsByOrderId(Long orderId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from OrderItem where order.orderId = :orderId", OrderItem.class)
                .setParameter("orderId", orderId)
                .list();
    }

    public List<CustomerOrder> findAllByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from CustomerOrder where user.userId = :userId", CustomerOrder.class)
                .setParameter("userId", userId)
                .list();
    }

    public List<OrderItem> findOrderItemsByUserAndStatus(Long userId, String status) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "from OrderItem oi where oi.order.user.userId = :userId and oi.order.orderStatus = :status", OrderItem.class)
                .setParameter("userId", userId)
                .setParameter("status", status)
                .list();
    }

    public List<OrderItem> findOrderItemsByStatus(String status) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "from OrderItem oi where oi.order.orderStatus = :status", OrderItem.class)
                .setParameter("status", status)
                .list();
    }

    public List<OrderItem> findCompletedOrderItems() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "from OrderItem oi where oi.order.orderStatus = :status", OrderItem.class)
                .setParameter("status", "Completed")
                .list();
    }
}
