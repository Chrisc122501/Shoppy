package com.beaconfire.shoppy.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.beaconfire.shoppy.model.Watchlist;
import com.beaconfire.shoppy.model.WatchlistId;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class WatchlistRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(Watchlist watchlist) {
        sessionFactory.getCurrentSession().saveOrUpdate(watchlist);
    }

    public void delete(Watchlist watchlist) {
        sessionFactory.getCurrentSession().delete(watchlist);
    }

    public Watchlist findById(WatchlistId id) {
        return sessionFactory.getCurrentSession().get(Watchlist.class, id);
    }

    public List<Watchlist> findAllByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Watchlist w where w.user.userId = :userId and w.product.quantity > 0", Watchlist.class)
                .setParameter("userId", userId)
                .list();
    }
}
