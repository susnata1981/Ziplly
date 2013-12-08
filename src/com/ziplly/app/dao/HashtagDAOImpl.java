package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ziplly.app.model.Hashtag;
import com.ziplly.app.model.HashtagDTO;

public class HashtagDAOImpl implements HashtagDAO {

	@Override
	public void create(Hashtag hashtag) {
		if (hashtag == null) {
			throw new IllegalArgumentException();
		}
		
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		em.persist(hashtag);
		em.getTransaction().commit();
		em.flush();
		em.close();
	}

	@Override
	public HashtagDTO findByName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Invalid argument");
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("from Hashtag where tag = :name");
		query.setParameter("name", name);
		Hashtag result = (Hashtag) query.getSingleResult();
		em.getTransaction().commit();
		HashtagDTO resp = EntityUtil.clone(result);
		em.close();
		return resp;
	}

	@Override
	public List<HashtagDTO> findAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery("from Hashtag");
		@SuppressWarnings("unchecked")
		List<Hashtag> result = query.getResultList();
		em.close();
		return EntityUtil.cloneHashtahList(result);
	}

	@Override
	public List<HashtagDTO> findTopHashtag(int n) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNativeQuery("select * from hashtag h where h.id in (select max(id) "
				+ "from tweet_hashtag group by id order by count(tweet_id))");
		@SuppressWarnings("unchecked")
		List<Hashtag> result = query.getResultList();
		em.close();
		return EntityUtil.cloneHashtahList(result);
	}
}
