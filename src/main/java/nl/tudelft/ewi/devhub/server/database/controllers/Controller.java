package nl.tudelft.ewi.devhub.server.database.controllers;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.persist.Transactional;
import com.mysema.query.jpa.impl.JPAQuery;

public class Controller<T> {
	
	private final EntityManager entityManager;

	@Inject
	public Controller(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Transactional
	public T persist(T entity) {
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}
	
	@Transactional
	public T delete(T entity) {
		entityManager.remove(entity);
		entityManager.flush();
		return entity;
	}
	
	JPAQuery query() {
		return new JPAQuery(entityManager);
	}

}